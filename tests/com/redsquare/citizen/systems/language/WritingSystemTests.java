package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.GameDebug;
import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.util.Orientation;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WritingSystemTests {

  private static final String IMAGE_FORMAT = "png";

  @Test
  public void glyphGeneration() {
    String filepath = "res/test_output/language/writing_systems.png";

    Phonology p = Phonology.generate();

    WritingSystem[] wss = new WritingSystem[10];

    for (int i = 0; i < 10; i++) {
      wss[i] = WritingSystem.generate(p);
      GameDebug.printMessage(wss[i].type.toString(), GameDebug::printDebug);
    }

    StringBuilder testText = new StringBuilder();
    for (int i = 0; i < 25; i++) {
      testText.append(Word.generateRandomWord(1, 4, p));
      testText.append(" ");
      if (i % 5 == 0 && i > 0) testText.append("\n");
    }

    GameDebug.printMessage(testText.toString().toLowerCase(), GameDebug::printDebug);

    BufferedImage[] wsis = new BufferedImage[wss.length];

    for (int i = 0; i < 10; i++) {
      wsis[i] = wss[i].draw(testText.toString().split("\n"), 40);
    }

    BufferedImage fi =
            Font.CLEAN.getText(testText.toString().toLowerCase().
                    split("\n"), Orientation.LEFT_TOP);

    int maxWidth = fi.getWidth();
    int height = 0;

    for (int i = 0; i < 10; i++) {
      maxWidth = Math.max(maxWidth, wsis[i].getWidth());
      height += wsis[i].getHeight();
    }

    BufferedImage all = new BufferedImage(maxWidth, (int)(height * 2.2), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) all.getGraphics();

    g.setColor(new Color(255, 255, 255));
    g.fillRect(0, 0, all.getWidth(), all.getHeight());
    g.drawImage(fi, 0, 0, null);
    height = fi.getHeight() * 2;

    for (BufferedImage i : wsis) {
      g.drawImage(i, 0, height, null);
      height += 2 * i.getHeight();
    }

    try {
      ImageIO.write(all, IMAGE_FORMAT, new File(filepath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
