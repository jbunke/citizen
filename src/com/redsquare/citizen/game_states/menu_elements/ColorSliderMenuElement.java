package com.redsquare.citizen.game_states.menu_elements;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.game_states.MenuGameState;
import com.redsquare.citizen.util.ColorMath;

import java.awt.*;
import java.util.function.Function;

public class ColorSliderMenuElement extends SliderMenuElement {

  private final Color darker;
  private final Color lighter;

  private Color current;

  private ColorSliderMenuElement(TextMenuElement affecting, int width, Point renderPoint,
                                 int minimum, int maximum, int increment,
                                 Function<Integer, String> sliderValToString,
                                 Color darker, Color lighter) {
    super(affecting, width, renderPoint, minimum,
            maximum, increment, sliderValToString);

    this.darker = darker;
    this.lighter = lighter;

    drawSlider();
  }

  static ColorSliderMenuElement generate(TextMenuElement affecting,
                                         int width, Point renderPoint,
                                         int minimum, int maximum,
                                         int increment,
                                         Function<Integer, String> sliderValToString,
                                         Color darker, Color lighter) {
    return new ColorSliderMenuElement(affecting, width, renderPoint, minimum,
            maximum, increment, sliderValToString, darker, lighter);
  }

  private void drawSlider() {
    Graphics2D g = (Graphics2D) slider.getGraphics();

    g.setColor(new Color(0, 0,0));
    g.fillOval(2, 2, 16, 16);

    double fraction = (position - minimum) / (double)(maximum - minimum);
    Color inside = ColorMath.colorBetween(darker, lighter, fraction);

    g.setColor(inside);
    g.fillOval(4, 4, 12, 12);

    current = inside;
  }

  @Override
  public void input(InputHandler inputHandler, MenuGameState menuGameState) {
    super.input(inputHandler, menuGameState);

    drawSlider();
  }
}
