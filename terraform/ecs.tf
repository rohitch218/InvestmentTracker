resource "aws_ecs_cluster" "service" {
  for_each = local.service_configs
  name     = "${var.project_name}-${each.key}-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }
}

resource "aws_ecs_cluster_capacity_providers" "service" {
  for_each     = local.service_configs
  cluster_name = aws_ecs_cluster.service[each.key].name

  capacity_providers = ["FARGATE"]

  default_capacity_provider_strategy {
    base              = 1
    weight            = 100
    capacity_provider = "FARGATE"
  }
}
