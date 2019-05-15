package com.redsquare.citizen.systems.language;

public class Language {
  private final PhoneticVocabulary vocabulary;
  private final WritingSystem writingSystem;
  private final WordVocabulary words;
  // private final Grammar grammar;

  private Language() {
    vocabulary = PhoneticVocabulary.generate();
    writingSystem = WritingSystem.generate(vocabulary);
    words = WordVocabulary.generate(vocabulary);
  }
}
