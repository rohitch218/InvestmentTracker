locals {
  service_configs = {
    "auth-service"        = { port = 8083, cpu = 256, memory = 512 }
    "tenant-service"      = { port = 8082, cpu = 256, memory = 512 }
    "portfolio-service"   = { port = 8084, cpu = 256, memory = 512 }
    "transaction-service" = { port = 8085, cpu = 256, memory = 512 }
    "audit-service"       = { port = 8086, cpu = 256, memory = 512 }
    "gateway"             = { port = 8081, cpu = 256, memory = 512 }
    "frontend"            = { port = 80,   cpu = 256, memory = 512 }
    "backend"             = { port = 8080, cpu = 256, memory = 512 }
  }
}
