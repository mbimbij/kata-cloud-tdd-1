package com.example;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class MyTest {
  @Test
  void name() {
    String response = "Hello world from ip-172-31-27-255 !\n";
    String regex = "Hello(.|[\\t\\r\\n])*";
    System.out.println(response.matches(regex));
    assertThat(response).matches(Pattern.compile(regex));
  }
}
