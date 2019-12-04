package com.redsquare.citizen.graphics;

import com.redsquare.citizen.util.IOForTesting;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextTests {

  @Test
  public void textRecoloringAndResizing() {
    BufferedImage image = Font.CLEAN.getText("TEST", 1.5, new Color(255, 0, 0));

    IOForTesting.saveImage(image, "test_output/graphics/text.png");
  }
}
