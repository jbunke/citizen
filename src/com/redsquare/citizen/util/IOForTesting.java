package com.redsquare.citizen.util;

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
}
