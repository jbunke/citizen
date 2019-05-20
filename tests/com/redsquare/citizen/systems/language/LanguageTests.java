package com.redsquare.citizen.systems.language;

import org.junit.Test;

public class LanguageTests {
  @Test
  public void vocabularyPopulation() {
    Language language = Language.generate();

    System.out.println(language.getName());
  }
}
