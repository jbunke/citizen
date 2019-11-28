package com.redsquare.citizen.systems.language.sentences;

import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.Meaning;
import com.redsquare.citizen.systems.language.Word;

import java.util.List;

public class PronounNP extends NounPhrase {
  private final Meaning meaning;

  public PronounNP(Meaning meaning) {
    this.meaning = meaning;
  }

  @Override
  public List<Word> getWords(Language language) {
    return List.of(language.lookUpWord(meaning));
  }

  @Override
  public int number() {
    switch (meaning) {
      case I:
      case YOU:
      case HE:
      case SHE:
      case IT:
        return 1;
      default:
        return 2;
    }
  }

  @Override
  public int person(Language language) {
    switch (meaning) {
      case I:
        return I;
      case YOU:
        return YOU;
      case HE:
        return HE;
      case SHE:
        return SHE;
      case IT:
        return IT;
      case WE:
        return WE;
      case THEY:
        return THEY;
      case YOU_PLURAL:
      default:
        return YOU_PL;
    }
  }

}
