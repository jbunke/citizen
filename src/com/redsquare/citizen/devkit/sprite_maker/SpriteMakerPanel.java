package com.redsquare.citizen.devkit.sprite_maker;

import com.redsquare.citizen.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class SpriteMakerPanel extends JPanel implements Runnable {
  private static SpriteMakerPanel instance = null;
  private static final int WIDTH = 1280;
  private static final int HEIGHT = 720;

  private InputHandler inputHandler;
  private SpriteMakerManager manager;

  private boolean running = false;
  private Thread thread;
  private BufferedImage image;
  private Graphics2D g;

  static SpriteMakerPanel instance() {
    if (instance != null) return instance;

    instance = new SpriteMakerPanel();
    return instance;
  }

  private SpriteMakerPanel() {
    inputHandler = InputHandler.create(this);
    manager = SpriteMakerManager.init();

    setPreferredSize(new Dimension(WIDTH, HEIGHT));
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
    image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
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

  private void update() {
    manager.update();
  }

  private void input() {
    manager.input(inputHandler);
  }

  /**
   * Render the frame in "image" with "g"
   * */
  private void render() {
    if (g != null) {
      // CLEAR canvas?
      g.setColor(new Color(255, 255, 255));
      g.fillRect(0, 0, WIDTH, HEIGHT);

      // SpriteTester.render(g);
      manager.render(g);
    }
  }

  /**
   * Draw the GAME IMAGE onto the GamePanel
   * */
  private void draw() {
    Graphics panel = this.getGraphics();

    panel.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
    panel.dispose();
  }
}
