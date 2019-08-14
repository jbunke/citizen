package com.redsquare.citizen.devkit.sprite_maker;

import com.redsquare.citizen.InputHandler;

import java.awt.*;

class SpriteMakerManager {
  private static SpriteMakerManager instance;

  static  SpriteMakerManager init() {
    if (instance != null) return instance;

    instance = new SpriteMakerManager();
    return instance;
  }

  void update() {}

  void input(InputHandler inputHandler) {}

  void render(Graphics2D g) {}
}
