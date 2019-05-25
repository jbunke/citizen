package com.redsquare.citizen.game_states.menu_elements;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.game_states.MenuGameState;
import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.util.ColorMath;
import com.redsquare.citizen.util.Orientation;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextMenuElement extends MenuElement {

  // status
  private boolean isHighlighted;

  private final String text;
  private final Point renderPoint;
  private final Color baseColor;
  private final Color highlightColor;
  private final Font font;
  private final BufferedImage baseImage;
  private final BufferedImage highlightImage;
  private final Orientation orientation;

  private TextMenuElement(String text, Point renderPoint, Color baseColor,
                          Color highlightColor, Font font,
                          MenuStateCode linkCode, MenuStateCode fromCode,
                          Orientation orientation) {
    super(linkCode, fromCode);

    this.text = text;
    this.renderPoint = renderPoint;
    this.baseColor = baseColor;
    this.highlightColor = highlightColor;
    this.font = font;
    this.orientation = orientation;

    this.baseImage = baseColor.equals(Color.BLACK) ?
            font.getText(text.split("\n"), orientation) :
            ColorMath.recolor(font.getText(
                    text.split("\n"), orientation), baseColor);
    this.highlightImage = highlightColor.equals(Color.BLACK) ?
            font.getText(text.split("\n"), orientation) :
            ColorMath.recolor(font.getText(
                    text.split("\n"), orientation), highlightColor);
  }

  static TextMenuElement temp(String text, Point renderPoint) {
    return new TextMenuElement(text, renderPoint, new Color(0, 0, 0),
            new Color(255, 0, 0), Font.CLEAN, null, null,
            Orientation.CENTER_H_CENTER_V);
  }

  static TextMenuElement temp(String text, Point renderPoint,
                                     MenuStateCode linkCode) {
    return new TextMenuElement(text, renderPoint, new Color(0, 0, 0),
            new Color(255, 0, 0), Font.CLEAN, linkCode, null,
            Orientation.CENTER_H_CENTER_V);
  }

  static TextMenuElement temp(String text, Point renderPoint,
                              MenuStateCode linkCode, MenuStateCode fromCode) {
    return new TextMenuElement(text, renderPoint, new Color(0, 0, 0),
            new Color(255, 0, 0), Font.CLEAN, linkCode, fromCode,
            Orientation.CENTER_H_CENTER_V);
  }

  @Override
  public void update() {

  }

  @Override
  public void render(Graphics2D g) {
    Point trueRenderPoint = renderPoint;
    BufferedImage image = isHighlighted ? highlightImage : baseImage;

    switch (orientation) {
      case LEFT_BOTTOM:
        trueRenderPoint = new Point(renderPoint.x,
                renderPoint.y - image.getHeight());
        break;
      case LEFT_CENTER_V:
        trueRenderPoint = new Point(renderPoint.x,
                renderPoint.y - image.getHeight() / 2);
        break;
      case CENTER_H_TOP:
        trueRenderPoint = new Point(renderPoint.x - image.getWidth() / 2,
                renderPoint.y);
        break;
      case CENTER_H_BOTTOM:
        trueRenderPoint = new Point(renderPoint.x - image.getWidth() / 2,
                renderPoint.y - image.getHeight());
        break;
      case CENTER_H_CENTER_V:
        trueRenderPoint = new Point(renderPoint.x - image.getWidth() / 2,
                renderPoint.y - image.getHeight() / 2);
        break;
      case RIGHT_TOP:
        trueRenderPoint = new Point(renderPoint.x - image.getWidth(),
                renderPoint.y);
        break;
      case RIGHT_BOTTOM:
        trueRenderPoint = new Point(renderPoint.x - image.getWidth(),
                renderPoint.y - image.getHeight());
        break;
      case RIGHT_CENTER_V:
        trueRenderPoint = new Point(renderPoint.x - image.getWidth(),
                renderPoint.y - image.getHeight() / 2);
        break;
    }

    g.drawImage(image, trueRenderPoint.x, trueRenderPoint.y, null);
  }

  private void updateHighlightStatus(int mouseX, int mouseY) {
    int minX = renderPoint.x;
    int maxX = renderPoint.x;
    int minY = renderPoint.y;
    int maxY = renderPoint.y;

    switch (orientation) {
      case LEFT_TOP:
      case CENTER_H_TOP:
      case RIGHT_TOP:
        maxY += baseImage.getHeight();
        break;
      case LEFT_BOTTOM:
      case CENTER_H_BOTTOM:
      case RIGHT_BOTTOM:
        minY -= baseImage.getHeight();
        break;
      case LEFT_CENTER_V:
      case CENTER_H_CENTER_V:
      case RIGHT_CENTER_V:
        minY -= baseImage.getHeight() / 2;
        maxY += baseImage.getHeight() / 2;
        break;
    }

    switch (orientation) {
      case LEFT_TOP:
      case LEFT_BOTTOM:
      case LEFT_CENTER_V:
        maxX += baseImage.getWidth();
        break;
      case RIGHT_TOP:
      case RIGHT_BOTTOM:
      case RIGHT_CENTER_V:
        minX -= baseImage.getWidth();
        break;
      case CENTER_H_CENTER_V:
      case CENTER_H_TOP:
      case CENTER_H_BOTTOM:
        minX -= baseImage.getWidth() / 2;
        maxX += baseImage.getWidth() / 2;
        break;
    }

    isHighlighted = minX <= mouseX && mouseX <= maxX &&
            minY <= mouseY && mouseY <= maxY;
  }

  @Override
  public void input(InputHandler inputHandler, MenuGameState menuGameState) {
    updateHighlightStatus(inputHandler.getMouseX(), inputHandler.getMouseY());

    super.input(inputHandler, menuGameState);
  }
}
