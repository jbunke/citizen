package com.redsquare.citizen.game_states.menu_elements;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.game_states.MenuGameState;

import java.awt.*;

public abstract class MenuElement {

  final boolean hasLink;
  final MenuStateCode linkCode;
  final MenuStateCode fromCode;

  protected MenuElement(MenuStateCode linkCode, MenuStateCode fromCode) {
    this.hasLink = linkCode != null;
    this.linkCode = linkCode;
    this.fromCode = fromCode;
  }

  public abstract void update();

  public abstract void render(Graphics2D g);

  public abstract void input(InputHandler inputHandler,
                             MenuGameState menuGameState);
}
