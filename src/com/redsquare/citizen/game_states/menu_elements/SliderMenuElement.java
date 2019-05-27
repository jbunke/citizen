package com.redsquare.citizen.game_states.menu_elements;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.game_states.MenuGameState;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class SliderMenuElement extends MenuElement {

  private final TextMenuElement affecting;
  private final Point renderPoint;
  private final int width;
  private final int minimum;
  private int position;
  private final int maximum;
  private final int increment;
  private final Function<Integer, String> sliderValToString;

  private int minX;
  private int maxX;
  private int minY;
  private int maxY;

  private final BufferedImage bar;
  private final BufferedImage slider;

  private SliderMenuElement(TextMenuElement affecting, int width,
                              Point renderPoint,
                              int minimum, int maximum, int increment,
                              Function<Integer, String> sliderValToString) {
    super(null, null);

    this.affecting = affecting;
    this.sliderValToString = sliderValToString;

    this.renderPoint = renderPoint;
    this.width = width;
    this.minimum = minimum;
    this.maximum = maximum;
    this.increment = increment;

    /* Set the position of the slider to the middle by default.
     * If middle is not a valid slider position based on the decided increment,
     * decrement until it is. */
    position = minimum + ((maximum - minimum) / 2);
    position -= (position - minimum) % increment;

    bar = new BufferedImage(width, 20, BufferedImage.TYPE_INT_ARGB);
    drawBar();
    slider = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
    drawSlider();

    setBounds();
  }

  static SliderMenuElement generate(TextMenuElement affecting, int width,
                                    Point renderPoint, int minimum,
                                    int maximum, int increment,
                                    Function<Integer, String> sliderValToString) {
    return new SliderMenuElement(affecting, width, renderPoint, minimum,
            maximum, increment, sliderValToString);
  }

  private void setBounds() {
    minX = renderPoint.x;
    maxX = renderPoint.x;
    minY = renderPoint.y;
    maxY = renderPoint.y;

    minY -= 10;
    maxY += 10;
    minX -= width / 2;
    maxX += width / 2;
  }

  private void drawBar() {
    Graphics2D g = (Graphics2D) bar.getGraphics();

    g.setColor(new Color(0, 0,0));
    g.setStroke(new BasicStroke(2));
    g.drawLine(0, 10, width, 10);
  }

  private void drawSlider() {
    Graphics2D g = (Graphics2D) slider.getGraphics();

    g.setColor(new Color(0, 0,0));
    g.fillOval(2, 2, 16, 16);
  }

  private int sliderX() {
    return (int)(((position - minimum) / (double)(maximum - minimum)) * width);
  }

  private boolean isIn(int mouseX, int mouseY) {
    return minX <= mouseX && mouseX <= maxX &&
            minY <= mouseY && mouseY <= maxY;
  }

  @Override
  public void update() {

  }

  @Override
  public void render(Graphics2D g) {
    Point trueRenderPoint = new Point(renderPoint.x - width / 2,
            renderPoint.y - 10);

    g.drawImage(bar, trueRenderPoint.x, trueRenderPoint.y, null);
    g.drawImage(slider, (trueRenderPoint.x + sliderX()) - 10,
            trueRenderPoint.y, null);
  }

  @Override
  public void input(InputHandler inputHandler, MenuGameState menuGameState) {
    if (inputHandler.isMouseDown()) {
      if (isIn(inputHandler.getMouseX(), inputHandler.getMouseY())) {
        // Do slider update
        double fraction =
                (inputHandler.getMouseX() - minX) / (double)(maxX - minX);
        position = minimum + (int)((maximum - minimum) * fraction);

        if ((position - minimum) % increment != 0) {
          if ((position - minimum) % increment > increment / 2)
            position += increment - ((position - minimum) % increment);
          else
            position -= (position - minimum) % increment;
        }

        affecting.updateText(sliderValToString.apply(position));
      }
    }
  }

  // usage functions
  static String worldSizeFunction(Integer x) {
    return x + "x" + ((x * 9) / 16);
  }
}
