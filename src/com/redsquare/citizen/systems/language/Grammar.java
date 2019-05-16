package com.redsquare.citizen.systems.language;

public class Grammar {

  final boolean isSVO; // FALSE = SOV, TRUE = SVO
  final Word pluralSuffix;

  private Grammar(PhoneticVocabulary vocabulary) {
    isSVO = Math.random() < 0.5;
    pluralSuffix = generatePluralSuffix(vocabulary);
  }

  static Grammar generate(PhoneticVocabulary vocabulary) {
    return new Grammar(vocabulary);
  }

  private Word generatePluralSuffix(PhoneticVocabulary vocabulary) {
    return Word.generate(new Syllable[] {
            new Syllable("", "",
                    PhoneticVocabulary.selectUnit(
                            vocabulary.SUFFIX_CONS_PHONEMES))
    });
  }

  public Word getPluralSuffix() {
    return pluralSuffix;
  }
}
