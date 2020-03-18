package com.redsquare.citizen.devkit.visualisation;

import com.redsquare.citizen.config.WorldConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VisualisationPanel extends JPanel implements Runnable {

  private static VisualisationPanel instance = null;

  private int width, height;
  private int counter, threshold;

  private BufferedImage image = null;
  private Graphics2D g;

  private VisualisationPanel() {
    setSize(false);
    setImage();

    counter = 0;
    threshold = 1;

    setFocusable(true);
    requestFocus();
  }

  private void setSize(final boolean referenceWindow) {
    this.width = WorldConfig.getXDim() * 2;
    this.height = (width * 9) / 16;

    setPreferredSize(new Dimension(width, height));

    if (referenceWindow) {
      WorldVisualisationWindow.getInstance().setSize(new Dimension(width, height));
    }
    // WorldVisualisationWindow.getInstance().setSize(new Dimension(width, height));
  }

  private void setImage() {
    this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    this.g = (Graphics2D) image.getGraphics();

    g.setColor(new Color(255, 255, 255));
    g.fillRect(0, 0, width, height);
  }

  void set() {
    setSize(true);
    setImage();
  }

  void updateCell(final int x, final int y, final Color c, int threshold) {
    if (threshold != this.threshold)
      this.threshold = threshold;

    g.setColor(c);
    g.fillRect(x * 2, y * 2, 2, 2);

    counter++;

    if (counter >= this.threshold) {
      draw();
      counter = 0;
    }
  }

  static VisualisationPanel getInstance() {
    if (instance == null)
      instance = new VisualisationPanel();

    return instance;
  }

  @Override
  public void run() {

  }

  private void draw() {
    Graphics panel = this.getGraphics();

    panel.drawImage(image, 0, 0, width, height, null);
    panel.dispose();
  }
}
