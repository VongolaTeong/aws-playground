output "instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.compute.id
}

output "public_ip" {
  description = "Public IP of the EC2 instance"
  value       = aws_instance.compute.public_ip
}

output "security_group_id" {
  description = "Security group ID for the EC2 instance"
  value       = aws_security_group.compute_sg.id
}
