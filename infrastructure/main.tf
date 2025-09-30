# Network Module
module "network" {
  source   = "./modules/network"
  vpc_cidr = var.vpc_cidr
  vpc_name = var.project

  public_subnets  = var.public_subnets
  private_subnets = var.private_subnets
  enable_nat      = var.enable_nat
}

# Compute Module (App Server)
module "compute" {
  source              = "./modules/compute"
  project             = var.project
  vpc_id              = module.network.vpc_id
  subnet_id           = module.network.public_subnet_ids[0]
  ami_id              = var.ami_id
  instance_type       = var.instance_type
  associate_public_ip = true
  user_data           = <<-EOT
    #!/bin/bash
    set -euxo pipefail
    yum install -y nginx
    echo "OK" > /usr/share/nginx/html/index.html
    systemctl enable nginx
    systemctl start nginx
  EOT
}

# ALB Module
module "alb" {
  source              = "./modules/alb"
  project             = var.project
  vpc_id              = module.network.vpc_id
  public_subnet_ids   = module.network.public_subnet_ids
  security_group_id   = module.compute.security_group_id
  target_instance_ids = [module.compute.instance_id]
}

# Database Module
module "database" {
  source                = "./modules/database"
  project               = var.project
  vpc_id                = module.network.vpc_id
  private_subnet_ids    = module.network.private_subnet_ids
  app_security_group_id = module.compute.security_group_id
  db_username           = var.db_username
  db_password           = var.db_password
}
