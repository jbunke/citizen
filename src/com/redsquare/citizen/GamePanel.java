package com.redsquare.citizen;

import com.redsquare.citizen.testing.SpriteTester;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements Runnable {

  private int width, height;

  private boolean running = false;
  private Thread thread;
  private BufferedImage image;
  private Graphics2D g;

  GamePanel(int width, int height) {
    this.width = width;
    this.height = height;

    setPreferredSize(new Dimension(width, height));
    setFocusable(true);
    requestFocus();
  }

  @Override
  public void addNotify() {
    super.addNotify();

    if (thread == null) {
      thread = new Thread(this, "Citizen");
      thread.start();
    }
  }

  private void init() {
    running = true;
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    g = (Graphics2D) image.getGraphics();
  }

  @Override
  public void run() {
    init();

    final double UPDATE_HZ = 60.0;
    final double NANOSECONDS_PER_UPDATE = 1e9 / UPDATE_HZ;

    final int MUST_UPDATE_BEFORE_RENDER = 5;

    double lastUpdateTime = System.nanoTime();
    double lastRenderTime;

    final double TARGET_FPS = 60.0;
    final double NANOSECONDS_PER_RENDER = 1e9 / TARGET_FPS;

    int frameCount = 0;
    int lastSecondSlice = (int) (lastUpdateTime / 1e9);
    int oldFrameCount = 0;

    while (running) {

      double now = System.nanoTime();

      // UPDATE BLOCK

      int updateCount = 0;
      while (((now - lastUpdateTime) > NANOSECONDS_PER_UPDATE) &&
              (updateCount < MUST_UPDATE_BEFORE_RENDER)) {
        update();
        input();
        lastUpdateTime += NANOSECONDS_PER_UPDATE;
        updateCount++;
      }

      if (now - lastUpdateTime > NANOSECONDS_PER_UPDATE)
        lastUpdateTime = now - NANOSECONDS_PER_UPDATE;

      // RENDER BLOCK

      input(); // not sure about this
      render();
      draw();

      lastRenderTime = now;
      frameCount++;

      // FRAME RATE CHECK BLOCK

      int thisSecondSlice = (int) (lastUpdateTime / 1e9);

      if (thisSecondSlice != lastSecondSlice) {

        if (frameCount != oldFrameCount) {
          GameDebug.printMessage(
                  thisSecondSlice + ": " + frameCount + " FPS",
                  GameDebug::printDebug);
          oldFrameCount = frameCount;
        }

        frameCount = 0;
        lastSecondSlice = thisSecondSlice;
      }

      // BREATHE BLOCK

      while ((now - lastRenderTime < NANOSECONDS_PER_RENDER) &&
              (now - lastUpdateTime) < NANOSECONDS_PER_UPDATE) {
        Thread.yield();

        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        now = System.nanoTime();
      }
    }
  }

  private void update() {}

  private void input() {}

  /**
   * Render the frame in "image" with "g"
   * */
  private void render() {
    if (g != null) {
      SpriteTester.render(g);
    }
  }

  /**
   * Draw the GAME IMAGE onto the GamePanel
   * */
  private void draw() {
    Graphics panel = this.getGraphics();

    panel.drawImage(image, 0, 0, width, height, null);
    panel.dispose();
  }
}
