package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.worldgen.World;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class CensusTests {

  private static final World world = World.safeCreate(480, 270, 30, 20);

  @Test
  public void saveWorld() {
    final String filepath = "res/test_output/politics/refWorld.png";
    final String imageFormat = "png";

    try {
      BufferedImage image = world.politicalMap(10, false, false, false);
      ImageIO.write(image, imageFormat, new File(filepath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void settlementCount() {
    final String filepath = "res/test_output/politics/settlementCount.txt";

    Set<State> states = world.getStates();
    Set<Settlement> settlements = world.allSettlements();
    int sum = 0;

    try {
      FileWriter fw = new FileWriter(new File(filepath));
      BufferedWriter bw = new BufferedWriter(fw);

      bw.write(settlements.size() + " settlements in world\n");
      bw.newLine();

      for (State state : states) {
        Set<Settlement> in = state.settlements();
        sum += in.size();

        bw.write(state.getName() + ": " + in.size() + " settlements");
        bw.newLine();
        bw.write("---------------------------");
        bw.newLine();
        for (Settlement s : in) {
          bw.write(s.getName() + " (" + s.getSetupPower() + ")");
          bw.newLine();
        }
        bw.newLine();
      }

      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertTrue(sum == settlements.size());
  }
}
