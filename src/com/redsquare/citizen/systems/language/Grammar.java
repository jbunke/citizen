package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.util.Randoms;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class Grammar {
  private final Set<Word> suffixes = new HashSet<>();
  private final short I = 0, YOU = 1, HE = 2, SHE = 3, IT = 4, WE = 5, THEY = 6, YOU_PL = 7;
  private final short PAST = 0, PRESENT = 1, FUTURE = 2;

  final Word[][] conjugationEndings = new Word[8][3];
  final Word infinitiveVerbEnding;
  final WordOrder wordOrder;
  final Word pluralSuffix;
  final Articles articles;

  enum Articles {
    NO_GENDER, // [SINGLE DEF., SINGLE INDEF., PLURAL DEF., PLURAL INDEF.]
    MALE_FEMALE, // [SM DEF., SF DEF.,
    MALE_FEMALE_NEUTER;

    int numArticles() {
      switch (this) {
        case MALE_FEMALE:
          return 6;
        case MALE_FEMALE_NEUTER:
          return 8;
        case NO_GENDER:
        default:
          return 4;
      }
    }
  }

  enum WordOrder {
    SOV, SVO
  }

  private Grammar(Phonology phonology) {
    wordOrder = WordOrder.values()[Randoms.bounded(0, WordOrder.values().length)];
    articles = Articles.values()[Randoms.bounded(0, Articles.values().length)];
    pluralSuffix = generateSuffix(phonology);
    infinitiveVerbEnding = generateSuffix(phonology);

    for (int tense = 0; tense <= FUTURE; tense++) {
      for (int person = 0; person <= YOU_PL; person++) {
        conjugationEndings[person][tense] = generateSuffix(phonology);

        if (person == SHE && articles == Articles.NO_GENDER) {
          conjugationEndings[person][tense] = conjugationEndings[HE][tense];
        } else if (person == IT && articles != Articles.MALE_FEMALE_NEUTER) {
          conjugationEndings[person][tense] = conjugationEndings[HE][tense];
        }
      }
    }
  }

  static Grammar generate(Phonology phonology) {
    return new Grammar(phonology);
  }

  private Word generateSuffix(Phonology phonology) {
    Word suffix = Word.generate(new Syllable[] {
            Math.random() < 0.5 ? new Syllable("", "",
                    Phonology.selectUnit(
                            phonology.SUFFIX_CONS_PHONEMES)) :
                    new Syllable("",
                            Phonology.selectUnit(phonology.VOWEL_PHONEMES),
                            Phonology.selectUnit(phonology.SUFFIX_CONS_PHONEMES))
    });

    if (suffixes.contains(suffix)) {
      return generateSuffix(phonology);
    } else {
      suffixes.add(suffix);
      return suffix;
    }
  }

  public Word getPluralSuffix() {
    return pluralSuffix;
  }

  BufferedImage verbTable(Meaning meaning, Language language) {
    BufferedImage table = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) table.getGraphics();

    Word verb = language.lookUpWord(meaning);
    g.drawImage(Font.CLEAN.getText(Word.compound(verb,
            infinitiveVerbEnding).toString()), 5, 5, null);
    g.drawImage(language.getWritingSystem().drawWithFont(
            Word.compound(verb, infinitiveVerbEnding), 50, 2, 2,
            Fonts::fontIdentityX, Fonts::fontIdentityY), 5, 35, null);

    for (int person = 0; person <= YOU_PL; person++) {
      for (int tense = 0; tense <= FUTURE; tense++) {
        g.drawImage(Font.CLEAN.getText(Word.compound(verb,
                conjugationEndings[person][tense]).toString()),
                5 + (tense * 330), 105 + (person * 100), null);
        g.drawImage(language.getWritingSystem().drawWithFont(
                Word.compound(verb, conjugationEndings[person][tense]), 50, 2, 2,
                Fonts::fontIdentityX, Fonts::fontIdentityY),
                5 + (tense * 330), 135 + (person * 100), null);
      }
    }

    return table;
  }
}
