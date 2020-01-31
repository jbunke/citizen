package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.systems.politics.State;
import com.redsquare.citizen.util.IOForTesting;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WorldGenTests {

  private static final String IMAGE_FORMAT = "png";

  @Test
  public void maximumWorldSize() {
    int width = 1600;
    int height = 900;
    boolean possible = false;

    World maxWorld = null;

    while (!possible) {
      possible = true;

      try {
        maxWorld = new World(width, height, (int)(Math.sqrt(width)));
      } catch (StackOverflowError error) {
        possible = false;
        width -= 16;
        height -= 9;
      }
    }

    String dimensions = "(" + width + ", " + height + ")";

    System.out.println(dimensions);

    String maxPath = "test_output/worldgen/boundaries/maxPossible.png";
    BufferedImage maxMap = maxWorld.physicalGeography(5, false);
    Graphics2D g = (Graphics2D) maxMap.getGraphics();

    g.drawImage(Font.CLEAN.getText(dimensions), 10, 10, null);

    try {
      ImageIO.write(maxMap, IMAGE_FORMAT, new File(maxPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void stateMaps() {
    World testWorld = new World(300, 300, 42);

    for (State state : testWorld.getStates()) {
      IOForTesting.saveImage(
              testWorld.stateMap(15, state),
              "test_output/worldgen/states/" + state.getName() + "_map.png");
    }

    IOForTesting.saveImage(testWorld.politicalMap(10, true, false, false),
            "test_output/worldgen/states/_world_map.png");
  }

  @Test
  public void generateWorldToMaps() {
    String tectonicPath = "test_output/worldgen/tectonic_map.png";
    String landSeaPath = "test_output/worldgen/land_sea_map.png";
    String landSeaPathMarked = "test_output/worldgen/geography_map.png";
    String politicalPath = "test_output/worldgen/political_map.png";
    String borderPath = "test_output/worldgen/political_border_map.png";

    World testWorld = new World(640, 360, 75); // 300, 300, 42);

    BufferedImage tectonicMap = testWorld.tectonicMap(10);
    BufferedImage landSeaMap = testWorld.physicalGeography(10, false);
    BufferedImage landSeaMapMarked = testWorld.physicalGeography(15, true);
    BufferedImage politicalMap = testWorld.politicalMap(10, false, true, false);
    BufferedImage borderMap = testWorld.politicalMap(10, true, false, true);

    IOForTesting.saveImage(tectonicMap, tectonicPath);
    IOForTesting.saveImage(landSeaMap, landSeaPath);
    IOForTesting.saveImage(landSeaMapMarked, landSeaPathMarked);
    IOForTesting.saveImage(politicalMap, politicalPath);
    IOForTesting.saveImage(borderMap, borderPath);
  }

  @Test
  public void stressTest() {
    String tectonicPath = "test_output/worldgen/stress_test/tectonic_map.png";
    String landSeaPath = "test_output/worldgen/stress_test/land_sea_map.png";
    String politicalPath = "test_output/worldgen/stress_test/political_map.png";
    String borderPath = "test_output/worldgen/stress_test/political_border_map.png";
    String regionPath = "test_output/worldgen/stress_test/region_map.png";

    World testWorld = new World(960, 540, 75);

    BufferedImage tectonicMap = testWorld.tectonicMap(5);
    BufferedImage landSeaMap = testWorld.physicalGeography(5, true);
    BufferedImage regionMap = testWorld.regionMap(5);
    BufferedImage politicalMap = testWorld.politicalMap(5, false, true, false);
    BufferedImage borderMap = testWorld.politicalMap(5, true, false, true);

    IOForTesting.saveImage(tectonicMap, tectonicPath);
    IOForTesting.saveImage(landSeaMap, landSeaPath);
    IOForTesting.saveImage(regionMap, regionPath);
    IOForTesting.saveImage(politicalMap, politicalPath);
    IOForTesting.saveImage(borderMap, borderPath);
  }

  @Test
  public void variablePlateCount() {
    String templatePath = "test_output/worldgen/platecount/";

    for (int i = 20; i < 80; i += 5) {
      World testWorld = new World(480, 270, i);
      BufferedImage map = testWorld.physicalGeography(5, true);
      BufferedImage tectonic = testWorld.tectonicMap(5);

      try {
        ImageIO.write(map, IMAGE_FORMAT,
                new File(templatePath + i + ".png"));
        ImageIO.write(tectonic, IMAGE_FORMAT,
                new File(templatePath + i + "tectonic.png"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void tenWorldsPhysicalGeography() {
    final int N = 20;
    String templatePath = "test_output/worldgen/n_worlds/number";

    for (int i = 0; i < N; i++) {
      World testWorld = new World(480, 270, 75);
      BufferedImage map = testWorld.physicalGeography(5, true);

      IOForTesting.saveImage(map, templatePath + i + ".png");
    }
  }

  @Test
  public void bordersAfterNumberOfYears() {
    GameDebug.activate();

    String templatePath = "test_output/worldgen/changing_borders/after";

    World world = new World(480, 270, 75);
    // new World(500, 500, 60);

    for (int i = 0; i < 500; i += 1) {
      BufferedImage map = world.politicalMap(10, true, false, true);

      IOForTesting.saveImage(map, templatePath + i + "years.png");

      world.getWorldManager().simulateYears(1);
      world.establishBorders();
    }
  }
}
