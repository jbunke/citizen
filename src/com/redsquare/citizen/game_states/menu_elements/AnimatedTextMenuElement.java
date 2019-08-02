package com.redsquare.citizen.game_states.menu_elements;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.game_states.MenuGameState;

import java.awt.*;

public class AnimatedTextMenuElement extends MenuElement {
  private final TextMenuElement[] tmes;
  private final int timing;

  private int index;
  private int count;

  private AnimatedTextMenuElement(TextMenuElement[] tmes, int timing) {
    super(null, null);
    this.tmes = tmes;
    this.timing = timing;

    this.index = 0;
    this.count = 0;
  }

  static AnimatedTextMenuElement temp(String[] texts,
                                      int timing, Point renderPoint) {
    TextMenuElement[] tmes = new TextMenuElement[texts.length];

    for (int i = 0; i < texts.length; i++) {
      tmes[i] = TextMenuElement.temp(texts[i], renderPoint);
    }

    return new AnimatedTextMenuElement(tmes, timing);
  }

  @Override
  public void update() {
    count++;

    if (count >= timing) {
      count = 0;
      index++;
      if (index >= tmes.length) index = 0;
    }
  }

  @Override
  public void render(Graphics2D g) {
    tmes[index].render(g);
  }

  @Override
  public void input(InputHandler inputHandler, MenuGameState menuGameState) {

  }
}
