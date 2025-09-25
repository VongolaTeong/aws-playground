# Network
output "vpc_id" {
  description = "VPC ID"
  value       = module.network.vpc_id
}

# Compute
output "ec2_public_ip" {
  description = "Public IP of EC2 instance"
  value       = module.compute.public_ip
}

# ALB
output "alb_dns" {
  description = "DNS name of the ALB"
  value       = module.alb.alb_dns_name
}

# Database
output "db_endpoint" {
  description = "Database endpoint"
  value       = module.database.db_endpoint
}
