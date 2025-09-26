# Global settings
variable "project" {
  description = "Project name prefix"
  type        = string
  default     = "umamusume"
}

# Network
variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnets" {
  description = "Public subnets with CIDR and AZ"
  type = list(object({
    cidr = string
    az   = string
  }))
  default = [
    { cidr = "10.0.1.0/24", az = "ap-southeast-1" }
  ]
}

variable "private_subnets" {
  description = "Private subnets with CIDR and AZ"
  type = list(object({
    cidr = string
    az   = string
  }))
  default = [
    { cidr = "10.0.2.0/24", az = "ap-southeast-1" }
  ]
}

variable "enable_nat" {
  description = "Whether to enable a NAT gateway for private subnets"
  type        = bool
  default     = true
}

# Compute
variable "ami_id" {
  description = "AMI ID for EC2 instance"
  type        = string
  default     = "ami-0c02fb55956c7d316" # Amazon Linux 2 (example)
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t2.micro"
}

# Database
variable "db_username" {
  description = "Database admin username"
  type        = string
  default     = "admin"
}

variable "db_password" {
  description = "Database admin password"
  type        = string
  sensitive   = true
}
