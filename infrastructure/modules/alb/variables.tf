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

variable "security_group_id" {
  description = "Security group for the ALB"
  type        = string
}

variable "target_instance_ids" {
  description = "List of instance IDs to attach to the target group"
  type        = list(string)
}
