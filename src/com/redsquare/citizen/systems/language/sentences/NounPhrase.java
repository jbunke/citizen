package com.redsquare.citizen.systems.language.sentences;

import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.Word;

import java.util.List;

public abstract class NounPhrase {
  public final short I = 0, YOU = 1, HE = 2, SHE = 3, IT = 4, WE = 5, THEY = 6, YOU_PL = 7;
  public abstract List<Word> getWords(Language language);
  public abstract int number();
  public abstract int person(Language language);
}
