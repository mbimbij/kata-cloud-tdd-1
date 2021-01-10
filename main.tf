provider "aws" {
  region = var.region
}

resource "aws_security_group" "ssh_in_sg" {
  ingress {
    from_port = 22
    protocol = "tcp"
    to_port = 22
    cidr_blocks = [
      "0.0.0.0/0"]
    ipv6_cidr_blocks = [
      "::/0"]
  }
}

resource "aws_security_group" "web_in_sg" {
  ingress {
    from_port = 80
    protocol = "tcp"
    to_port = 80
    cidr_blocks = [
      "0.0.0.0/0"]
    ipv6_cidr_blocks = [
      "::/0"]
  }
}

resource "aws_security_group" "all_out_sg" {
  egress {
    from_port = 0
    to_port = 0
    protocol = -1
    cidr_blocks = [
      "0.0.0.0/0"]
    ipv6_cidr_blocks = [
      "::/0"]
  }
}

resource "aws_instance" "web" {
  count = var.instance_count
  ami = var.ami
  instance_type = var.instance_type
  subnet_id = var.subnet_id
  user_data_base64 = var.user_data_base64
  security_groups = [
    aws_security_group.web_in_sg.name,
    aws_security_group.ssh_in_sg.name,
    aws_security_group.all_out_sg.name
  ]

  tags = {
    Name = var.name
  }
}
