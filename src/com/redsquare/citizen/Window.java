package com.redsquare.citizen;

import javax.swing.*;

public class Window extends JFrame {

  Window() {
    setTitle("CITIZEN");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setContentPane(GamePanel.instance());

    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

}
