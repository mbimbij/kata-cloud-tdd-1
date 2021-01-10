package com.example;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
public class Utils {


  /**
   * Executes a Linux shell command
   * @param command the command to execute
   * @return the exit value
   */
  @SneakyThrows
  public static int executeLinuxShellCommand(String command, Path executionPath) {
    log.info("executing command: \"{}\"", command);
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("sh", "-c", command);
    builder.directory(executionPath.toFile());
    Process process = builder.start();
    StreamGobbler streamGobbler =
        new StreamGobbler(process.getInputStream(), System.out::println);
    StreamGobbler errorGobbler =
        new StreamGobbler(process.getErrorStream(), System.out::println);
    Executors.newSingleThreadExecutor().submit(streamGobbler);
    Executors.newSingleThreadExecutor().submit(errorGobbler);
    return process.waitFor();
  }

  private static class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
      this.inputStream = inputStream;
      this.consumer = consumer;
    }

    @Override
    public void run() {
      new BufferedReader(new InputStreamReader(inputStream)).lines()
          .forEach(consumer);
    }
  }
}
