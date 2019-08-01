package com.redsquare.citizen.systems.vexillology;

import com.redsquare.citizen.systems.politics.Culture;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlagTests {

  @Test
  public void hundredFlagsFromCulture() {

    final String filepath = "test_output/vexillology/flags.png";
    final String IMAGE_FORMAT = "png";

    BufferedImage canvas = new BufferedImage(800, 1300, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) canvas.getGraphics();

    g.setColor(new Color(255, 255, 255));
    g.fillRect(0, 0, 800, 1300);

    for (int j = 0; j < 3; j++) {
      Culture culture = Culture.generate();
      List<Flag> flags = new ArrayList<>();

      while (flags.size() < 100) flags.add(Flag.generate(culture));

      for (int i = 0; i < flags.size(); i++) {
        int x = ((i % 10) * 80) + 10;
        int y = ((i / 10) * 40) + 5 + (450 * j);

        g.drawImage(flags.get(i).draw(1), x, y, null);
      }
    }

    try {
      ImageIO.write(canvas, IMAGE_FORMAT, new File(filepath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void thousandFlags() {

    final String filepath = "test_output/vexillology/thousand_flags.png";
    final String IMAGE_FORMAT = "png";

    BufferedImage canvas = new BufferedImage(800, 4000, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) canvas.getGraphics();

    g.setColor(new Color(255, 255, 255));
    g.fillRect(0, 0, 800, 1300);

    List<Flag> flags = new ArrayList<>();

    while (flags.size() < 1000) {
      Culture culture = Culture.generate();
      flags.add(Flag.generate(culture));
    }

    for (int i = 0; i < flags.size(); i++) {
      int x = ((i % 10) * 80) + 10;
      int y = ((i / 10) * 40) + 5;

      g.drawImage(flags.get(i).draw(1), x, y, null);
    }

    try {
      ImageIO.write(canvas, IMAGE_FORMAT, new File(filepath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
