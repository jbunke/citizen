package com.redsquare.citizen.systems.language;

import org.junit.Test;

public class LanguageTests {
  @Test
  public void vocabularyPopulation() {
    PhoneticVocabulary v = PhoneticVocabulary.generate();
    WordVocabulary wv = WordVocabulary.generate(v);
  }
}
