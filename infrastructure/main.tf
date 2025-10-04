# Network Module
module "network" {
  source   = "./modules/network"
  vpc_cidr = var.vpc_cidr
  vpc_name = var.project

  public_subnets  = var.public_subnets
  private_subnets = var.private_subnets
  enable_nat      = var.enable_nat
}

# Compute Module (App Server)
module "compute" {
  source              = "./modules/compute"
  project             = var.project
  vpc_id              = module.network.vpc_id
  subnet_id           = module.network.public_subnet_ids[0]
  ami_id              = var.ami_id
  instance_type       = var.instance_type
  associate_public_ip = true
  user_data           = <<-EOT
    #!/bin/bash
    set -euxo pipefail
    
    # Update system
    yum update -y
    
    # Install Python (should be available by default)
    yum install -y python3
    
    # Create a simple index.html
    mkdir -p /var/www/html
    echo "OK" > /var/www/html/index.html
    
    # Create a simple Python HTTP server script
    cat > /opt/simple-server.py << 'EOF'
#!/usr/bin/env python3
import http.server
import socketserver
import os

# Change to the web directory
os.chdir('/var/www/html')

# Create a simple HTTP server on port 80
PORT = 80
Handler = http.server.SimpleHTTPRequestHandler

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    print(f"Serving at port {PORT}")
    httpd.serve_forever()
EOF
    
    # Make the script executable
    chmod +x /opt/simple-server.py
    
    # Create a systemd service
    cat > /etc/systemd/system/simple-server.service << 'EOF'
[Unit]
Description=Simple HTTP Server
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/var/www/html
ExecStart=/usr/bin/python3 /opt/simple-server.py
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF
    
    # Enable and start the service
    systemctl daemon-reload
    systemctl enable simple-server
    systemctl start simple-server
    
    # Check if the service is running
    systemctl status simple-server
    
    # Test the server locally
    curl -f http://localhost || echo "Server test failed"
    
    # Log the status
    echo "Simple HTTP server installation and startup completed" >> /var/log/user-data.log
  EOT
}

# ALB Module
module "alb" {
  source              = "./modules/alb"
  project             = var.project
  vpc_id              = module.network.vpc_id
  public_subnet_ids   = module.network.public_subnet_ids
  target_instance_ids = [module.compute.instance_id]
}

# Database Module
module "database" {
  source                = "./modules/database"
  project               = var.project
  vpc_id                = module.network.vpc_id
  private_subnet_ids    = module.network.private_subnet_ids
  app_security_group_id = module.compute.security_group_id
  db_username           = var.db_username
  db_password           = var.db_password
}
