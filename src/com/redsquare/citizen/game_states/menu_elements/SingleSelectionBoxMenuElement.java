package com.redsquare.citizen.game_states.menu_elements;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.game_states.MenuGameState;
import com.redsquare.citizen.graphics.Font;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SingleSelectionBoxMenuElement extends MenuElement {

  private final String[] options;
  private final Point renderPoint;
  private final int width;

  private int selection;

  private int minX;
  private int maxX;
  private int minY;
  private int maxY;

  private final BufferedImage choices;

  protected SingleSelectionBoxMenuElement(String[] options, Point renderPoint,
                                          int width) {
    super(null, null);

    this.options = options;
    this.renderPoint = renderPoint;
    this.width = width;

    choices = new BufferedImage(width, 20, BufferedImage.TYPE_INT_ARGB);

    selection = 0;
  }

  private void drawChoices() {
    Graphics2D g = (Graphics2D) choices.getGraphics();

    for (int i = 0; i < options.length; i++) {
      BufferedImage choiceText = Font.CLEAN.getText(options[i]);
      int xBuffer = (int)(i * (double)(width / options.length));
      // TODO NEXT
    }
  }

  @Override
  public void update() {

  }

  @Override
  public void render(Graphics2D g) {

  }

  @Override
  public void input(InputHandler inputHandler, MenuGameState menuGameState) {

  }
}
