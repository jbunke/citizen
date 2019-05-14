package com.redsquare.citizen;

import javax.swing.*;

public class Window extends JFrame {

  private static final int DEFAULT_WIDTH = 1280;
  private static final int DEFAULT_HEIGHT = 720;

  Window() {
    setTitle("CITIZEN");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setContentPane(new GamePanel(DEFAULT_WIDTH, DEFAULT_HEIGHT));

    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

}
