package com.redsquare.citizen.systems.language;

public class Language {
  private final Phonology phonology;
  private final WritingSystem writingSystem;
  private final WordVocabulary words;
  private final Grammar grammar;

  private final Word name;

  private Language() {
    phonology = Phonology.generate();
    writingSystem = WritingSystem.generate(phonology,
            WritingSystem.Type.ALPHABETICAL); // TEMP explicitly alphabets
    grammar = Grammar.generate(phonology);
    words = WordVocabulary.generate(phonology);

    name = words.lookUp(Meaning.THIS_LANGUAGE);
  }

  public static Language generate() {
    return new Language();
  }

  public Phonology getPhonology() {
    return phonology;
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
