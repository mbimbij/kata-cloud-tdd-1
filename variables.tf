variable "instance_count" {
  default = 1
}
variable "ami" {
  default = "ami-0d3f551818b21ed81" //ubuntu 20.04 in "eu-west-3" (paris) region
}
variable "instance_type" {
  default = "t2.micro"
}
variable "subnet_id" {
  default = ""
}
variable "name" {
  default = "Main"
}

variable "user_data_base64" {
  default = ""
}

variable "region" {
  default = "eu-west-3"
}