package com.redsquare.citizen.systems.language;

public class Language {
  private final Phonology phonology;
  private final WritingSystem writingSystem;
  private final WordVocabulary words;
  private final Grammar grammar;

  private final Word name;

  private Language() {
    phonology = Phonology.generate();
    writingSystem = WritingSystem.generate(phonology);
    grammar = Grammar.generate(phonology);
    words = WordVocabulary.generate(phonology);

    name = words.lookUp(Meaning.THIS_LANGUAGE);
  }

  private Language(WritingSystem writingSystem) {
    this.phonology = writingSystem.phonology;
    this.writingSystem = writingSystem;
    grammar = Grammar.generate(phonology);
    words = WordVocabulary.generate(phonology);

    name = words.lookUp(Meaning.THIS_LANGUAGE);
  }

  public static Language generate() {
    return new Language();
  }

  public static Language generate(WritingSystem ws) {
    return new Language(ws);
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
