package com.redsquare.citizen.systems.structures;

import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.politics.State;
import com.redsquare.citizen.util.Formatter;
import com.redsquare.citizen.worldgen.World;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LayoutTests {

  @Test
  public void settlementLayout() {

    final String filepath = "res/test_output/settlements/layout";
    final String IMAGE_FORMAT = "png";

    State state = new State();
    Settlement settlement = new Settlement(new Point(20, 20), state);

    for (int i = 0; i < 10; i++) {
      SettlementLayout layout = SettlementLayout.generate(settlement);

      BufferedImage img = layout.draw();

      try {
        ImageIO.write(img, IMAGE_FORMAT, new File(filepath + i + "." + IMAGE_FORMAT));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void worldAndCapitals() {
    final String filepath = "res/test_output/settlements/capitals/";
    final String IMAGE_FORMAT = "png";

    World world = World.safeCreate(800, 450, 45, 10);

    BufferedImage wIm = world.politicalMap(10, true, false, true);

    Set<State> states = world.getStates();
    List<BufferedImage> capitals = new ArrayList<>();

    for (State state : states) {
      SettlementLayout capital = state.getCapital().getLayout();
      BufferedImage cIm = capital.draw();
      Graphics2D g = (Graphics2D) cIm.getGraphics();

      g.drawImage(Font.CLEAN.getText(Formatter.capitaliseFirstLetter(
              state.getCapital().getName())), 10, 1950, null);
      g.drawImage(state.getFlag().draw(2), 10, 1850, null);

      capitals.add(cIm);
    }

    try {
      ImageIO.write(wIm, IMAGE_FORMAT, new File(filepath + "world." + IMAGE_FORMAT));

      for (int i = 0; i < capitals.size(); i++) {
        ImageIO.write(capitals.get(i), IMAGE_FORMAT, new File(filepath + "capital" + i + "." + IMAGE_FORMAT));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
