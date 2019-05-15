package com.redsquare.citizen.systems.language;

public class PlaceNameGenerator {

  public static String generateRandomName(int minSyllables, int maxSyllables) {
    Word placeName = Word.generateRandomWord(minSyllables, maxSyllables);
    return capitaliseFirstLetter(placeName.toString());
  }

  public static String generateRandomName(int minSyllables, int maxSyllables,
                                          PhoneticVocabulary vocabulary) {
    Word placeName = Word.generateRandomWord(minSyllables, maxSyllables, vocabulary);
    return capitaliseFirstLetter(placeName.toString());
  }

  private static String capitaliseFirstLetter(String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }
}
