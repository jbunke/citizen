package com.redsquare.citizen.systems.language;

public class PlaceNameGenerator {

  public static Word generateRandomName(int minSyllables, int maxSyllables) {
    return Word.generateRandomWord(minSyllables, maxSyllables);
  }

  public static Word generateRandomName(int minSyllables, int maxSyllables,
                                          PhoneticVocabulary vocabulary) {
    return Word.generateRandomWord(minSyllables, maxSyllables, vocabulary);
  }
}
