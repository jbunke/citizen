package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.graphics.Font;
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
    BufferedImage maxMap = maxWorld.physicalGeography(5);
    Graphics2D g = (Graphics2D) maxMap.getGraphics();

    g.drawImage(Font.CLEAN.getText(dimensions), 10, 10, null);

    try {
      ImageIO.write(maxMap, IMAGE_FORMAT, new File(maxPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void chunkMapTest() {
    String filepath = "test_output/worldgen/chunk_map/chunks.png";

    World world = World.safeCreate(480, 270, 30, 10);

    BufferedImage i = world.chunkMap();

    try {
      ImageIO.write(i, IMAGE_FORMAT, new File(filepath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void generateWorldToMaps() {
    String tectonicPath = "test_output/worldgen/tectonic_map.png";
    String landSeaPath = "test_output/worldgen/land_sea_map.png";
    String politicalPath = "test_output/worldgen/political_map.png";
    String borderPath = "test_output/worldgen/political_border_map.png";
    String regionPath = "test_output/worldgen/region_map.png";

    World testWorld = World.safeCreate(640, 360, 35, 20);

    BufferedImage tectonicMap = testWorld.tectonicMap(10);
    BufferedImage landSeaMap = testWorld.physicalGeography(10);
    BufferedImage regionMap = testWorld.regionMap(10);
    BufferedImage politicalMap = testWorld.politicalMap(10, false, true, false);
    BufferedImage borderMap = testWorld.politicalMap(10, true, false, true);

    try {
      ImageIO.write(tectonicMap, IMAGE_FORMAT, new File(tectonicPath));
      ImageIO.write(landSeaMap, IMAGE_FORMAT, new File(landSeaPath));
      ImageIO.write(regionMap, IMAGE_FORMAT, new File(regionPath));
      ImageIO.write(politicalMap, IMAGE_FORMAT, new File(politicalPath));
      ImageIO.write(borderMap, IMAGE_FORMAT, new File(borderPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void stressTest() {
    String tectonicPath = "test_output/worldgen/stress_test/tectonic_map.png";
    String landSeaPath = "test_output/worldgen/stress_test/land_sea_map.png";
    String politicalPath = "test_output/worldgen/stress_test/political_map.png";
    String borderPath = "test_output/worldgen/stress_test/political_border_map.png";
    String regionPath = "test_output/worldgen/stress_test/region_map.png";

    World testWorld = null;
    int width = 1280; //960;
    int height = 720; //540;

    while (testWorld == null) {
      testWorld = World.safeCreate(width, height, 55, 10);
      width -= 16;
      height -= 9;
    }

    BufferedImage tectonicMap = testWorld.tectonicMap(5);
    BufferedImage landSeaMap = testWorld.physicalGeography(5);
    BufferedImage regionMap = testWorld.regionMap(5);
    BufferedImage politicalMap = testWorld.politicalMap(5, false, true, false);
    BufferedImage borderMap = testWorld.politicalMap(5, true, false, true);

    try {
      ImageIO.write(tectonicMap, IMAGE_FORMAT, new File(tectonicPath));
      ImageIO.write(landSeaMap, IMAGE_FORMAT, new File(landSeaPath));
      ImageIO.write(regionMap, IMAGE_FORMAT, new File(regionPath));
      ImageIO.write(politicalMap, IMAGE_FORMAT, new File(politicalPath));
      ImageIO.write(borderMap, IMAGE_FORMAT, new File(borderPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void variablePlateCount() {
    String templatePath = "test_output/worldgen/platecount/";

    for (int i = 20; i < 80; i += 5) {
      World testWorld = new World(480, 270, i);
      BufferedImage map = testWorld.physicalGeography(5);
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
    String templatePath = "test_output/worldgen/worlddump/number";

    for (int i = 0; i < 10; i++) {
      World testWorld = World.safeCreate(320, 180, 30, 10);
      BufferedImage map = testWorld.physicalGeography(5);

      try {
        ImageIO.write(map, IMAGE_FORMAT,
                new File(templatePath + i + ".png"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void bordersAfterNumberOfYears() {
    GameDebug.activate();

    String templatePath = "test_output/worldgen/changing_borders/after";

    World world = World.safeCreate(200, 200, 20, 25);

    for (int i = 0; i < 500; i += 1) {
      BufferedImage map = world.politicalMap(5, true, false, true);

      try {
        ImageIO.write(map, IMAGE_FORMAT, new File(templatePath + i + "years.png"));
      } catch (IOException e) {
        e.printStackTrace();
      }

      world.getWorldManager().simulateYears(1);
    }
  }
}
