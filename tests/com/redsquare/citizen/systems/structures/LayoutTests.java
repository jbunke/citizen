package com.redsquare.citizen.systems.structures;

import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.systems.language.Fonts;
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

    final String filepath = "test_output/settlements/layout";
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
    final String filepath = "test_output/settlements/capitals/";
    final String IMAGE_FORMAT = "png";

    World world = World.safeCreate(800, 450, 45, 10);

    BufferedImage wIm = world.politicalMap(10, true, false, true);

    Set<State> states = world.getStates();
    List<BufferedImage> capitals = new ArrayList<>();

    for (State state : states) {
      SettlementLayout capital = state.getCapital().getLayout();
      BufferedImage cIm = capital.draw();
      Graphics2D g = (Graphics2D) cIm.getGraphics();

      g.drawImage(Font.CLEAN.getText(Formatter.properNoun(
              state.getCapital().getName())), 10, 710, null);
      g.drawImage(state.getFlag().draw(1), 10, 732, null);

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

  @Test
  public void allSettlements() {
    final String filepath = "test_output/settlements/all_in_world/";
    final String IMAGE_FORMAT = "png";

    World world = World.safeCreate(480, 270, 45, 10);

    BufferedImage wIm = world.politicalMap(10, true, false, true);

    Set<State> states = world.getStates();

    for (State state : states) {
      Set<Settlement> settlements = state.settlements();
      for (Settlement settlement : settlements) {
        SettlementLayout s = settlement.getLayout();
        BufferedImage sIm = s.draw();
        Graphics2D g = (Graphics2D) sIm.getGraphics();

        g.drawImage(state.getLanguage().getWritingSystem().draw(settlement.getName(), 36, false), 10, 648, null);
        g.drawImage(Font.CLEAN.getText(Formatter.properNoun(
                settlement.getName()) + ", " +
                Formatter.properNoun(state.getName())), 10, 710, null);
        if (settlement.equals(state.getCapital()))
          g.drawImage(Font.CLEAN.getText("[CAPITAL]"), 10, 688, null);
        g.drawImage(state.getFlag().draw(1), 10, 732, null);

        try {
          String filename = filepath + "[" +
                  Formatter.properNoun(state.getName()) + "] " +
                  Formatter.properNoun(settlement.getName());

          ImageIO.write(sIm, IMAGE_FORMAT, new File(filename +
                  "." + IMAGE_FORMAT));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    try {
      ImageIO.write(wIm, IMAGE_FORMAT, new File(
              filepath + "world." + IMAGE_FORMAT));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
