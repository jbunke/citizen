package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.entity.animal.AnimalSpecies;
import com.redsquare.citizen.util.IOForTesting;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.Set;

public class AnimalSpeciesTests {
  @Test
  public void species() {
    GameDebug.activate();

    World world = new World(400, 255, 50);

    Set<AnimalSpecies> allSpecies = world.getFauna();

    for (AnimalSpecies animalSpecies : allSpecies) {
      BufferedImage speciesMap = world.speciesRangeMap(5, animalSpecies);
      IOForTesting.saveImage(speciesMap,
              "test_output/worldgen/species_maps/" + animalSpecies.toString() + ".png");
    }

    System.out.println("DONE and commencing FEB 2020");
  }
}
