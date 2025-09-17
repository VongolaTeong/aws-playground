terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  required_version = ">= 1.3"
}

provider "aws" {
  region     = "ap-southeast-2"
  access_key = "test"
  secret_key = "test"

  # Point Terraform to LocalStack (running via docker compose)
  endpoints {
    ec2 = "http://localhost:4566"
    sts = "http://localhost:4566"
  }
}

# Minimal VPC
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "umamusume-vpc"
  }
}

# --- Public Subnet ---
resource "aws_subnet" "public_1" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "ap-southeast-1a"
  map_public_ip_on_launch = true

  tags = {
    Name = "umamusume-public-subnet-1"
  }
}

# --- Internet Gateway ---
resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "umamusume-igw"
  }
}

# --- Route Table for Public Subnet ---
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }

  tags = {
    Name = "umamusume-public-rt"
  }
}

# --- Associate Public Subnet with Route Table ---
resource "aws_route_table_association" "public_assoc" {
  subnet_id      = aws_subnet.public_1.id
  route_table_id = aws_route_table.public.id
}

# --- Private Subnet ---
resource "aws_subnet" "private_1" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "ap-southeast-1a"
  map_public_ip_on_launch = false

  tags = {
    Name = "umamusume-private-subnet-1"
  }
}

# --- Private Route Table ---
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id

  # No internet route here (keeps it private)
  tags = {
    Name = "umamusume-private-rt"
  }
}

# --- Associate Private Subnet with Private Route Table ---
resource "aws_route_table_association" "private_assoc" {
  subnet_id      = aws_subnet.private_1.id
  route_table_id = aws_route_table.private.id
}

# Allocate Elastic IP for NAT Gateway
resource "aws_eip" "nat_eip" {
  domain = "vpc"
  tags = {
    Name = "umamusume-nat-eip"
  }
}

# NAT Gateway in the public subnet
resource "aws_nat_gateway" "nat" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = aws_subnet.public_1.id
  tags = {
    Name = "umamusume-nat-gateway"
  }
  depends_on = [aws_internet_gateway.gw]
}

# Update private route table to send internet traffic through NAT
resource "aws_route" "private_nat_gateway" {
  route_table_id         = aws_route_table.private.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.nat.id
}

# ------------------------------
# Security Groups
# ------------------------------

# Public SG (allow SSH + HTTP from your IP / internet)
resource "aws_security_group" "public_sg" {
  name        = "umamusume-public-sg"
  description = "Allow SSH and HTTP from internet"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "umamusume-public-sg" }
}

# Private SG (allow traffic ONLY from public SG)
resource "aws_security_group" "private_sg" {
  name        = "umamusume-private-sg"
  description = "Allow app traffic from public SG"
  vpc_id      = aws_vpc.main.id

  ingress {
    description      = "App traffic from public SG"
    from_port        = 8080
    to_port          = 8080
    protocol         = "tcp"
    security_groups  = [aws_security_group.public_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "umamusume-private-sg" }
}

# ------------------------------
# EC2 Instances
# ------------------------------

# Public EC2 (bastion/web)
resource "aws_instance" "public_ec2" {
  ami           = "ami-0c02fb55956c7d316" # Amazon Linux 2 AMI (example for ap-southeast-2)
  instance_type = "t2.micro"

  subnet_id              = aws_subnet.public_1.id
  vpc_security_group_ids = [aws_security_group.public_sg.id]
  associate_public_ip_address = true

  tags = { Name = "umamusume-public-ec2" }
}

# Private EC2 (app server)
resource "aws_instance" "private_ec2" {
  ami           = "ami-0c02fb55956c7d316"
  instance_type = "t2.micro"

  subnet_id              = aws_subnet.private_1.id
  vpc_security_group_ids = [aws_security_group.private_sg.id]
  associate_public_ip_address = false

  tags = { Name = "umamusume-private-ec2" }
}

# ------------------------------
# DB Subnet Group
# ------------------------------
resource "aws_db_subnet_group" "umamusume_db_subnets" {
  count      = 0
  name       = "umamusume-db-subnet-group"
  subnet_ids = [aws_subnet.private_1.id]

  tags = {
    Name = "umamusume-db-subnet-group"
  }
}

# ------------------------------
# Security Group for DB
# ------------------------------
resource "aws_security_group" "db_sg" {
  name        = "umamusume-db-sg"
  description = "Allow DB access only from app SG"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "Postgres access from private app SG"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.private_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "umamusume-db-sg" }
}

# ------------------------------
# RDS Instance
# ------------------------------
resource "aws_db_instance" "umamusume_db" {
  count                  = 0
  identifier             = "umamusume-db"
  allocated_storage      = 20
  engine                 = "postgres"
  engine_version         = "15.3"
  instance_class         = "db.t3.micro"
  username               = "admin"
  password               = "changeme123"
  skip_final_snapshot    = true

  db_subnet_group_name   = aws_db_subnet_group.umamusume_db_subnets[0].name
  vpc_security_group_ids = [aws_security_group.db_sg.id]

  publicly_accessible    = false
  multi_az               = false
  storage_type           = "gp2"

  tags = { Name = "umamusume-rds" }
}

resource "aws_security_group" "app_sg" {
  name   = "umamusume-app-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port       = 80
    to_port         = 80
    protocol        = "tcp"
    security_groups = [aws_security_group.alb_sg.id] # only allow ALB traffic
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "umamusume-app-sg" }
}

resource "aws_instance" "app" {
  ami           = "ami-12345678" # pick a valid AMI in ap-southeast-2
  instance_type = "t3.micro"
  subnet_id     = aws_subnet.private_1.id
  vpc_security_group_ids = [aws_security_group.app_sg.id]

  tags = { Name = "umamusume-app" }
}

# --------------------------------
# Security Group for ALB
# --------------------------------
resource "aws_security_group" "alb_sg" {
  name        = "umamusume-alb-sg"
  description = "Allow HTTP traffic from the internet"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "Allow HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "umamusume-alb-sg" }
}

# --------------------------------
# ALB
# --------------------------------
resource "aws_lb" "umamusume_alb" {
  name               = "umamusume-alb"
  load_balancer_type = "application"
  internal           = false
  security_groups    = [aws_security_group.alb_sg.id]
  subnets            = [aws_subnet.public_1.id]

  enable_deletion_protection = false

  tags = { Name = "umamusume-alb" }
}

# --------------------------------
# Target Group for EC2 app instances
# --------------------------------
resource "aws_lb_target_group" "umamusume_tg" {
  name     = "umamusume-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    path                = "/"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
    matcher             = "200-399"
  }

  tags = { Name = "umamusume-tg" }
}

# --------------------------------
# Listener: ALB routes HTTP → Target Group
# --------------------------------
resource "aws_lb_listener" "umamusume_http" {
  load_balancer_arn = aws_lb.umamusume_alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.umamusume_tg.arn
  }
}

# --------------------------------
# Register EC2 with Target Group
# --------------------------------
resource "aws_lb_target_group_attachment" "umamusume_ec2_attach" {
  target_group_arn = aws_lb_target_group.umamusume_tg.arn
  target_id        = aws_instance.app.id
  port             = 80
}
