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
