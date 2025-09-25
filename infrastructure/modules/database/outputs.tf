output "db_endpoint" {
  description = "Database endpoint"
  value       = aws_db_instance.this.endpoint
}

output "db_security_group_id" {
  description = "Database security group"
  value       = aws_security_group.db_sg.id
}
