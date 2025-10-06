variable "project" {
  description = "Project name"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "public_subnet_ids" {
  description = "List of public subnet IDs"
  type        = list(string)
}

# Removed security_group_id variable - ALB now has its own security group

variable "target_instance_ids" {
  description = "List of instance IDs to attach to the target group"
  type        = list(string)
}
