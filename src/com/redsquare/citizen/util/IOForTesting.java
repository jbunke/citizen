package com.redsquare.citizen.util;

import com.redsquare.citizen.debug.GameDebug;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class IOForTesting {
  public static void saveImage(BufferedImage im, String location) {
    try {
      ImageIO.write(im, "PNG", new File(location));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void createDirectory(String location) {
    File directory = new File(location);

    if (!directory.mkdir())
      GameDebug.printMessage(
              "Could not create directory at " + location,
              GameDebug::printError);
  }
}
