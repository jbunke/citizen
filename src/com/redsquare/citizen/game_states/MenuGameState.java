package com.redsquare.citizen.game_states;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.game_states.menu_elements.MenuElement;
import com.redsquare.citizen.game_states.menu_elements.MenuStateCode;

import java.awt.*;
import java.util.Set;

public final class MenuGameState extends GameState {

  private Set<MenuElement> elements;
  private MenuStateCode stateCode;

  private MenuGameState() {
    setStateCode(MenuStateCode.MAIN);
  }

  public void setStateCode(MenuStateCode stateCode) {
    this.stateCode = stateCode;
    elements = stateCode.generateElements();
  }

  public static MenuGameState init() {
    return new MenuGameState();
  }

  @Override
  public void update() {

  }

  @Override
  public void render(Graphics2D g) {
    elements.forEach(x -> x.render(g));
  }

  @Override
  public void input(InputHandler inputHandler) {
    elements.forEach(x -> x.input(inputHandler, this));
  }
}
