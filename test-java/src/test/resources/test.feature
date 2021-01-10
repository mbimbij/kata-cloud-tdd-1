Feature: "hello-world" instances in ASG, behind ALB

  Background: a "clean" region
    Given the region "eu-west-3"
    And an account with only the default VPC
#    And no EC2 instance
#    And no auto-scaling group

  Scenario: create an EC2 instance with Apache and a "hello-world" page, publicly accessible
    When i create an EC2 instance with Apache and the following html content
      """
      Hello world from $(hostname) !
      """
    Then we can send an http request to the public ip of the instance and the response matches "Hello world from(.|[\t\r\n])*"