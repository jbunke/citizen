package com.redsquare.citizen.debug;

import com.redsquare.citizen.config.Settings;
import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.util.Orientation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class GameDebug {
  private static final int MAX_AGE = 200;
  private static final int MAX_MESSAGE_AMOUNT = 10;

  private static boolean active = false;
  private static int importanceThreshold = 0;
  private static Message[] messages = new Message[MAX_MESSAGE_AMOUNT];

  public static class Message {
    final String content;
    int age = 0;

    Message(String content) {
      this.content = content;
    }

    void age() {
      age++;
    }
  }

  public static boolean isActive() {
    return active;
  }

  public static void activate() {
    active = true;
  }

  public static void deactivate() {
    active = false;
  }

  public static int getImportanceThreshold() {
    return importanceThreshold;
  }

  public static void resetImportanceThreshold() {
    importanceThreshold = 0;
  }

  public static void setImportanceThreshold(final int importanceThreshold) {
    GameDebug.importanceThreshold = importanceThreshold;
  }

  public static void printMessage(String message,
                                  Consumer<String> function) {
    if (active)
      function.accept(message);
  }

  public static void printError(String message) {
    String content = "ERROR: [" + message + "]";

    if (Settings.executionMode == Settings.ExecutionMode.GAME)
      addMessage(new Message(content));
    else System.out.println(content);
  }

  public static void printDebugWithImportance(String message, int importance) {
    if (importance >= importanceThreshold)
      printDebug(message);
  }

  public static void printDebug(String message) {
    String content = "DEBUG: [" + message + "]";

    if (Settings.executionMode == Settings.ExecutionMode.GAME)
      addMessage(new Message(content));
    else System.out.println(content);
  }

  private static void addMessage(Message toAdd) {
    for (int i = MAX_MESSAGE_AMOUNT - 2; i >= 0; i--) {
      messages[i + 1] = messages[i];
    }
    messages[0] = toAdd;
  }

  private static String messagesToString() {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < MAX_MESSAGE_AMOUNT && messages[i] != null; i++) {
      if (i > 0) sb.append("\n");
      sb.append(messages[i].content);
    }

    return sb.toString();
  }

  public static void update() {
    for (int i = 0; i < messages.length; i++) {
      if (messages[i] == null) break;

      messages[i].age();
      if (messages[i].age > MAX_AGE) messages[i] = null;
    }
  }

  public static void render(Graphics2D g) {
    if (messages[0] != null) {
      BufferedImage debug = Font.CLEAN.getText(
              messagesToString().split("\n"), Orientation.LEFT_TOP);
      BufferedImage background = new BufferedImage(
              debug.getWidth() + 4, debug.getHeight() + 8, BufferedImage.TYPE_INT_ARGB);
      Graphics2D bg = (Graphics2D) background.getGraphics();

      bg.setColor(new Color(255, 255, 255, 150));
      bg.fillRect(0, 0, background.getWidth(), background.getHeight());
      bg.drawImage(debug, 2, 4, null);

      g.drawImage(background, 10, 10, null);
    }
  }
}
