package com.example;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"html:target/cucumber-report.html",
    "json:target/cucumber.json", "junit:target/cucumber-results.xml",
    "pretty:target/cucumber-pretty.txt", "usage:target/cucumber-usage.json"},
    glue = {"com.example"},
    features = "src/test/resources")
public class RunCucumberTest {
}
