package com.redsquare.citizen.systems.language;

public class PlaceNameGenerator {

  public static Word generateRandomName(int minSyllables, int maxSyllables) {
    return Word.generateRandomWord(minSyllables, maxSyllables);
  }

  public static Word generateRandomName(int minSyllables, int maxSyllables,
                                          Phonology phonology) {
    return Word.generateRandomWord(minSyllables, maxSyllables, phonology);
  }
}
