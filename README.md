# Umamusume Clone - AWS Free Tier Project

A simplified horse racing game inspired by Umamusume (Pretty Derby) built with AWS, the purpose is to experiment with AWS.

## Architecture Overview

This project implements a cloud-native architecture using AWS Free Tier services, designed for learning and experimentation with modern cloud infrastructure patterns.

### Infrastructure Components

#### Networking Layer
- **VPC**: `10.0.0.0/16` CIDR block with DNS support
- **Public Subnets**: 
  - `10.0.1.0/24` (ap-southeast-1a)
  - `10.0.3.0/24` (ap-southeast-1b)
- **Private Subnets**:
  - `10.0.2.0/24` (ap-southeast-1a) 
  - `10.0.4.0/24` (ap-southeast-1b)
- **Internet Gateway**: For public internet access
- **NAT Gateway**: For private subnet outbound access
- **Route Tables**: Separate routing for public/private subnets

#### Compute Layer
- **EC2 Instance**: t2.micro (Free Tier eligible)
  - Amazon Linux 2 AMI
  - Python HTTP server on port 80
  - Auto-configured with systemd service
  - Public IP for external access
- **Security Groups**:
  - SSH access (port 22)
  - HTTP access (port 80)
  - Outbound internet access

#### Database Layer
- **RDS PostgreSQL**: 
  - Multi-AZ deployment in private subnets
  - Security group restricted to application tier
  - Automated backups enabled
  - Free Tier eligible instance class

#### Load Balancing
- **Application Load Balancer (ALB)**:
  - Internet-facing load balancer
  - Health checks on port 80
  - Target group with EC2 instances
  - HTTP listener on port 80

#### Security Architecture
- **Network Security**:
  - VPC isolation with private/public subnet separation
  - Security groups with least privilege access
  - Database in private subnets only
- **Access Control**:
  - IAM roles and policies
  - Database access restricted to application tier
  - No direct database internet access

### Infrastructure as Code

The entire infrastructure is defined using Terraform with a modular approach:

```
infrastructure/
├── main.tf                 # Main configuration
├── variables.tf            # Input variables
├── outputs.tf              # Output values
├── terraform.tfvars        # Variable values
└── modules/
    ├── network/            # VPC, subnets, gateways
    ├── compute/            # EC2 instances, security groups
    ├── database/           # RDS configuration
    └── alb/                # Load balancer setup
```

### 🚀 Development Environment

#### Local Development
- **LocalStack**: AWS service emulation for local development
- **Docker Compose**: Containerized LocalStack environment
- **Terraform**: Infrastructure provisioning
- **Python**: Application runtime

### Roadmap

#### Done
- AWS Free Tier account setup
- IAM users and access keys configured
- Terraform installation and configuration
- LocalStack development environment
- VPC with public/private subnets
- Internet Gateway and route tables
- NAT Gateway for private subnet access
- Security groups for network isolation
- EC2 instance with Python HTTP server
- RDS PostgreSQL database
- Application Load Balancer
- Complete infrastructure deployment

#### Next Steps
- Frontend application development
- Backend API implementation
- Database schema design
- User authentication system
- Game logic implementation
- Real-time features
- Monitoring and logging
- CI/CD pipeline setup

### Technology Stack

#### Infrastructure
- **Terraform**: Infrastructure as Code
- **AWS Services**: EC2, RDS, ALB, VPC, IAM
- **LocalStack**: Local AWS service emulation
- **Docker**: Containerized development environment

## 📄 License
MIT License - see LICENSE file for details.