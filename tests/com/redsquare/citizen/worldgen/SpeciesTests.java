package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.entity.animal.Species;
import com.redsquare.citizen.util.IOForTesting;
import com.redsquare.citizen.util.Sets;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.Set;

public class SpeciesTests {
  @Test
  public void species() {
    GameDebug.activate();

    World world = new World(400, 255, 50);

    Set<Species> allSpecies = world.getFauna();

    for (int i = 0; i < 25; i++) {
      BufferedImage speciesMap = world.speciesRangeMap(5, Sets.randomEntry(allSpecies));
      IOForTesting.saveImage(speciesMap,
              "test_output/worldgen/species_maps/" + i + ".png");
    }
  }
}
