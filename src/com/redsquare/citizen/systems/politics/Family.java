package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.language.Phonology;
import com.redsquare.citizen.systems.language.Word;

public class Family {
  private Word name;

  // TODO

  protected Family() {
    name = Word.generateRandomWord(2, 4,
            Phonology.generate());
  }

  public static Family generate() {
    return new Family();
  }

  public Word getName() {
    return name;
  }
}
