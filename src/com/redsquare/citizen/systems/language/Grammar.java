package com.redsquare.citizen.systems.language;

public class Grammar {

  final boolean isSVO; // FALSE = SOV, TRUE = SVO
  final Word pluralSuffix;

  private Grammar(Phonology phonology) {
    isSVO = Math.random() < 0.5;
    pluralSuffix = generatePluralSuffix(phonology);
  }

  static Grammar generate(Phonology phonology) {
    return new Grammar(phonology);
  }

  private Word generatePluralSuffix(Phonology phonology) {
    return Word.generate(new Syllable[] {
            new Syllable("", "",
                    Phonology.selectUnit(
                            phonology.SUFFIX_CONS_PHONEMES))
    });
  }

  public Word getPluralSuffix() {
    return pluralSuffix;
  }
}
