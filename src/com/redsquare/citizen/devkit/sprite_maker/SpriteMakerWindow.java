package com.redsquare.citizen.devkit.sprite_maker;

import javax.swing.*;

public class SpriteMakerWindow extends JFrame {

  public static void main(String[] args) {
    new SpriteMakerWindow();
  }

  private SpriteMakerWindow() {
    setTitle("CITIZEN Sprite Maker - for development purposes");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setContentPane(SpriteMakerPanel.instance());

    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }
}
