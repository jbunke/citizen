package com.redsquare.citizen.systems.language;

public class Language {
  private final PhoneticVocabulary vocabulary;
  private final WritingSystem writingSystem;
  private final WordVocabulary words;
  private final Grammar grammar;

  private final Word name;

  private Language() {
    vocabulary = PhoneticVocabulary.generate();
    writingSystem = WritingSystem.generate(vocabulary);
    grammar = Grammar.generate(vocabulary);
    words = WordVocabulary.generate(vocabulary);

    name = words.lookUp(Meaning.THIS_LANGUAGE);
  }

  public static Language generate() {
    return new Language();
  }

  public PhoneticVocabulary getVocabulary() {
    return vocabulary;
  }

  public WritingSystem getWritingSystem() {
    return writingSystem;
  }

  public Word getName() {
    return name;
  }

  public Word lookUpWord(Meaning meaning) {
    return words.lookUp(meaning);
  }
}
