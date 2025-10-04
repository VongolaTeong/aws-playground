output "alb_dns_name" {
  value = aws_lb.this.dns_name
}

output "alb_security_group_id" {
  description = "Security group ID for the ALB"
  value       = aws_security_group.alb_sg.id
}