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
