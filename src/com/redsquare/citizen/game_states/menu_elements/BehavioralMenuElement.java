package com.redsquare.citizen.game_states.menu_elements;

import com.redsquare.citizen.GameManager;
import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.game_states.MenuGameState;

import java.awt.*;

public class BehavioralMenuElement extends MenuElement {

  private final Behaviour behaviour;
  private final Object[] arguments;

  private BehavioralMenuElement(Behaviour behaviour,
                                  Object[] arguments) {
    super(null, null);

    this.behaviour = behaviour;
    this.arguments = arguments;
  }

  static BehavioralMenuElement generate(Behaviour behaviour,
                                        Object[] arguments) {
    return new BehavioralMenuElement(behaviour, arguments);
  }

  public enum Behaviour {
    QUIT, START_GAME
  }

  @Override
  public void update() {
    switch (behaviour) {
      case QUIT:
        // TODO: proper close
        System.exit(0);
        break;
      case START_GAME:
        GameManager gm = GameManager.get();

        // Visual feedback during start up
        MenuGameState state = (MenuGameState)(gm.getGameState());
        state.setStateCode(MenuStateCode.GENERATING_WORLD, MenuStateCode.START_GAME);

        Thread initializer = new Thread(gm::initPlaying);
        initializer.start();
        break;
    }
  }

  @Override
  public void render(Graphics2D g) {

  }

  @Override
  public void input(InputHandler inputHandler, MenuGameState menuGameState) {

  }
}
