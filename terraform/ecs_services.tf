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

resource "aws_ecs_task_definition" "service" {
  for_each                 = local.service_configs
  family                   = "${var.project_name}-${each.key}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = each.value.cpu
  memory                   = each.value.memory
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = each.key
      image     = "${aws_ecr_repository.services[each.key].repository_url}:latest"
      essential = true
      portMappings = [
        {
          containerPort = each.value.port
          hostPort      = each.value.port
        }
      ]
      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = "prod" }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/${var.project_name}-${each.key}"
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])
}

resource "aws_cloudwatch_log_group" "ecs" {
  for_each = local.service_configs
  name     = "/ecs/${var.project_name}-${each.key}"
  retention_in_days = 7
}

resource "aws_lb_target_group" "service" {
  for_each    = local.service_configs
  name        = "${var.project_name}-${each.key}-tg"
  port        = each.value.port
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path                = "/actuator/health"
    healthy_threshold   = 2
    unhealthy_threshold = 10
  }
}

resource "aws_ecs_service" "service" {
  for_each        = local.service_configs
  name            = "${var.project_name}-${each.key}"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.service[each.key].arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    security_groups  = [aws_security_group.ecs_tasks.id]
    subnets          = aws_subnet.private[*].id
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.service[each.key].arn
    container_name   = each.key
    container_port   = each.value.port
  }
}
