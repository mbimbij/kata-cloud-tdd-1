package com.example;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.awaitility.Awaitility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
public class Ec2ModuleTestStepDefs {

  private Ec2Client ec2Client;
  private Region region;
  private String correlationId;
  private String terraformScriptPath = "..";

  @Before
  public void setUp() {
    correlationId = UUID.randomUUID().toString();
    Utils.executeLinuxShellCommand("terraform destroy -auto-approve", Paths.get(terraformScriptPath));
    Utils.executeLinuxShellCommand("terraform init", Paths.get(terraformScriptPath));
  }

  @After
  public void tearDown() {
    Utils.executeLinuxShellCommand("terraform destroy -auto-approve", Paths.get(terraformScriptPath));
  }

  @Given("the region {string}")
  public void theRegion(String region) {
    this.region = Region.of(region);
    ec2Client = Ec2Client.builder()
        .region(this.region)
        .build();
  }

  @Given("an account with only the default VPC")
  public void anAccountWithOnlyTheDefaultVPC() {
    DescribeVpcsResponse describeVpcsResponse = ec2Client.describeVpcs();
    assertThat(describeVpcsResponse.vpcs()).hasSize(1);
    Vpc vpc = describeVpcsResponse.vpcs().get(0);
    assertThat(vpc.isDefault()).isTrue();
  }

  @Given("no EC2 instance")
  public void no_ec2_instance() {
    DescribeInstancesResponse describeInstancesResponse = ec2Client.describeInstances();
    List<Instance> runningInstances = describeInstancesResponse.reservations().stream()
        .flatMap(reservation -> reservation.instances().stream())
        .filter(instance -> Objects.equals(instance.state().name(), InstanceStateName.RUNNING))
        .collect(Collectors.toList());
    assertThat(runningInstances).isEmpty();
  }

  @When("i create an EC2 instance with Apache and the following html content")
  public void iCreateAnECInstanceWithApacheAndTheFollowingHtmlContent(String htmlContent) {
    String userDataString = "#! /bin/bash\n" +
        "apt update\n" +
        "apt install apache2 -y\n" +
        "echo \"" + htmlContent + "\" > /var/www/html/index.html";
    String userDataBase64 = Base64.getEncoder().encodeToString(userDataString.getBytes(StandardCharsets.UTF_8));

    String terraformVars = String.format("--var 'user_data_base64=%s'", userDataBase64);
    String terraformCommand = String.format("terraform apply -auto-approve %s", terraformVars);
    int exitValue = Utils.executeLinuxShellCommand(terraformCommand, Paths.get(terraformScriptPath));
    log.info("executed with return value {}", exitValue);
    assertThat(exitValue).isZero();
  }

  @Then("we can send an http request to the public ip of the instance and the response matches {string}")
  public void weCanSendAnHttpRequestToThePublicIpOfTheInstanceAndTheResponseMatches(String expectedResponsePattern) {
    List<Instance> runningInstances = ec2Client.describeInstances().reservations().stream()
        .flatMap(reservation -> reservation.instances().stream())
        .filter(instance -> Objects.equals(instance.state().name(), InstanceStateName.RUNNING))
        .collect(Collectors.toList());
    assertThat(runningInstances).hasSize(1);
    Instance instance = runningInstances.get(0);
    RestTemplate restTemplate = new RestTemplate();

    Try<ResponseEntity<String>> responseEntityTry = await()
        .atMost(Duration.ofSeconds(120))
        .pollInterval(Duration.ofSeconds(2))
        .until(() -> Try.of(() -> restTemplate.getForEntity("http://" + instance.publicDnsName(), String.class)),
            Try::isSuccess);

    await()
        .atMost(Duration.ofSeconds(60))
        .pollInterval(Duration.ofSeconds(2))
        .until(() -> restTemplate.getForEntity("http://" + instance.publicDnsName(), String.class),
            responseEntity -> Objects.requireNonNull(responseEntity.getBody()).matches(expectedResponsePattern));
  }
}
