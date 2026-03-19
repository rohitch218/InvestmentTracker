variable "aws_region" {
  description = "AWS region"
  default     = "ap-south-1"
}

variable "project_name" {
  description = "Project name"
  default     = "investment-tracker"
}

variable "services" {
  description = "List of microservices"
  type        = list(string)
  default     = ["auth-service", "tenant-service", "portfolio-service", "transaction-service", "audit-service", "gateway", "frontend", "backend"]
}

variable "certificate_arn" {
  description = "ARN of ACM certificate for HTTPS"
  default     = ""
}

variable "vpc_id" {
  description = "Existing VPC ID"
  default     = "vpc-074049289c87e4855"
}

variable "subnet_ids" {
  description = "Existing Subnet IDs"
  type        = list(string)
  default     = ["subnet-0cead9e8f231f6a6b", "subnet-02a86ecbff90a692e"]
}
