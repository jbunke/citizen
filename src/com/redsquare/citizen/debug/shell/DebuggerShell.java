package com.redsquare.citizen.debug.shell;

import com.redsquare.citizen.debug.GameDebug;

import java.util.Scanner;

public class DebuggerShell {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_BOLD = "\u001B[1m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_BLUE = "\u001B[34m";

  private static final Scanner in = new Scanner(System.in);

  public static void launch() {
    startUp();
    loop();
  }

  private static void loop() {
    String command = "";

    while (!command.equals("quit")) {
      prompt();
      command = in.nextLine();
      process(command);
    }

    System.exit(0);
  }

  private static void startUp() {
    System.out.println("Citizen COMMAND DEBUGGER\n");
  }

  private static void process(String command) {
    switch (command) {
      case "activate":
        GameDebug.activate();
        break;
      case "deactivate":
        GameDebug.deactivate();
        break;
    }
  }

  private static void prompt() {
    System.out.print(ANSI_GREEN + ANSI_BOLD + "[Jordan] > " + ANSI_RESET);
  }
}
