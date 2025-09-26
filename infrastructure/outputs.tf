# -------------------------
# Network outputs
# -------------------------
output "vpc_id" {
  description = "VPC ID"
  value       = module.network.vpc_id
}

output "public_subnet_ids" {
  description = "List of public subnet IDs"
  value       = module.network.public_subnet_ids
}

output "private_subnet_ids" {
  description = "List of private subnet IDs"
  value       = module.network.private_subnet_ids
}

# -------------------------
# Compute outputs
# -------------------------
output "compute_instance_id" {
  description = "EC2 instance ID"
  value       = module.compute.instance_id
}

output "compute_public_ip" {
  description = "Public IP of the compute instance"
  value       = module.compute.public_ip
}

output "compute_security_group_id" {
  description = "Security group ID for the compute instance"
  value       = module.compute.security_group_id
}

# -------------------------
# ALB outputs
# -------------------------
output "alb_dns_name" {
  description = "DNS name of the Application Load Balancer"
  value       = module.alb.dns_name
}

# -------------------------
# Database outputs
# -------------------------
output "db_endpoint" {
  description = "Database endpoint"
  value       = module.database.db_endpoint
}

output "db_security_group_id" {
  description = "Security group ID for the database"
  value       = module.database.db_security_group_id
}
