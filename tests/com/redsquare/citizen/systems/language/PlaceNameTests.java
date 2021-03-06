package com.redsquare.citizen.systems.language;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PlaceNameTests {
  @Test
  public void hundredPlaceNames() {
    String placeNameFile = "test_output/language/place_names.txt";

    try {
      FileWriter fw = new FileWriter(placeNameFile);
      BufferedWriter bw = new BufferedWriter(fw);

      for (int i = 0; i < 100; i++) {
        bw.write(PlaceNameGenerator.generateRandomName(2, 4).toString());
        bw.newLine();
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void demonymGeneratorTest() {
    String demonymFile = "test_output/language/demonyms.txt";
    Phonology p = Phonology.generate();

    try {
      FileWriter fw = new FileWriter(demonymFile);
      BufferedWriter bw = new BufferedWriter(fw);

      for (int i = 0; i < 100; i++) {
        String placeName =
                PlaceNameGenerator.generateRandomName(2, 3, p).toString();
        String demonym = DemonymGenerator.demonym(placeName);
        bw.write(placeName + " -> " + demonym);
        bw.newLine();
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void vocabularies() {
    String vocabulariesFile = "test_output/language/vocabularies.txt";

    try {
      FileWriter fw = new FileWriter(vocabulariesFile);
      BufferedWriter bw = new BufferedWriter(fw);

      for (int i = 0; i < 10; i++) {
        Phonology p = Phonology.generate();

        bw.write("Vocabulary " + (i + 1) + ":");
        bw.newLine();

        for (int j = 0; j < 25; j++) {
          bw.write(PlaceNameGenerator.
                  generateRandomName(2, 3, p).toString());
          bw.newLine();
        }
        bw.newLine();
      }

      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
