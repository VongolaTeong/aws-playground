variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
}

variable "vpc_name" {
  description = "Name prefix for resources"
  type        = string
}

variable "public_subnets" {
  description = "List of public subnets (cidr, az)"
  type = list(object({
    cidr = string
    az   = string
  }))
}

variable "private_subnets" {
  description = "List of private subnets (cidr, az)"
  type = list(object({
    cidr = string
    az   = string
  }))
}

variable "enable_nat" {
  description = "Whether to create a NAT gateway for private subnets"
  type        = bool
  default     = true
}
