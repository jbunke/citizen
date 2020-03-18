package com.redsquare.citizen.devkit.visualisation;

import javax.swing.*;

public class WorldVisualisationWindow extends JFrame {
  private static WorldVisualisationWindow instance = null;

  public static WorldVisualisationWindow getInstance() {
    if (instance == null) {
      instance = new WorldVisualisationWindow();
    }

    return instance;
  }

  private WorldVisualisationWindow() {
    setTitle("WORLD ALGORITHM VISUALISATION");
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setContentPane(VisualisationPanel.getInstance());

    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }
}
