package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.util.Randoms;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Grammar {
  private final Set<Word> suffixes = new HashSet<>();
  private final short I = 0, YOU = 1, HE = 2, SHE = 3, IT = 4, WE = 5, THEY = 6, YOU_PL = 7;
  private final short PAST = 0, PRESENT = 1, FUTURE = 2;
  private final short SMD = 0, SFD = 1, SND = 2, SMI = 3, SFI = 4, SNI = 5, PD = 6, PI = 7;

  private final Word[][] conjugationEndings = new Word[8][3];
  private final Word[] articleWords = new Word[8];
  private final Word infinitiveVerbEnding;
  private final WordOrder wordOrder;
  private final Word[] pluralSuffixes;
  private final Articles articles;

  private final Language language;

  enum Articles {
    NO_GENDER, // [SINGLE DEF., SINGLE INDEF., PLURAL DEF., PLURAL INDEF.]
    MALE_FEMALE, // [SM DEF., SF DEF.,
    MALE_FEMALE_NEUTER
  }

  enum WordOrder {
    SOV, SVO
  }

  private Grammar(Language language) {
    this.language = language;

    Phonology phonology = language.getPhonology();

    wordOrder = WordOrder.values()[Randoms.bounded(0, WordOrder.values().length)];
    articles = Articles.values()[Randoms.bounded(0, Articles.values().length)];

    pluralSuffixes = new Word[2];
    pluralSuffixes[0] = generateSuffix(phonology);
    pluralSuffixes[1] = generateSuffix(phonology);

    infinitiveVerbEnding = generateSuffix(phonology);

    Word snd = generateSuffix(phonology);
    articleWords[SND] = snd;

    Word sni = generateSuffix(phonology);
    articleWords[SNI] = sni;

    switch (articles) {
      case NO_GENDER:
        articleWords[SMD] = snd;
        articleWords[SFD] = snd;
        articleWords[SMI] = sni;
        articleWords[SFI] = sni;
        break;
      case MALE_FEMALE:
        articleWords[SMD] = snd;
        articleWords[SFD] = generateSuffix(phonology);
        articleWords[SMI] = sni;
        articleWords[SFI] = generateSuffix(phonology);
        break;
      case MALE_FEMALE_NEUTER:
      default:
        articleWords[SMD] = generateSuffix(phonology);
        articleWords[SFD] = generateSuffix(phonology);
        articleWords[SMI] = generateSuffix(phonology);
        articleWords[SFI] = generateSuffix(phonology);
        break;
    }

    articleWords[PD] = Math.random() < 0.7 ? Word.compound(snd, pluralSuffixes[0]) : generateSuffix(phonology);
    articleWords[PI] = Math.random() < 0.7 ? Word.compound(sni, pluralSuffixes[0]) : generateSuffix(phonology);

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

  static Grammar generate(Language language) {
    return new Grammar(language);
  }

  private Word generateSuffix(Phonology phonology) {
    double prob = Randoms.bounded(0., 1.);
    Word suffix;

    if (prob < 0.2) {
      suffix = Word.generate(new Syllable[] {
                      new Syllable(
                              Phonology.selectUnit(phonology.PREFIX_CONS_PHONEMES),
                              Phonology.selectUnit(phonology.VOWEL_PHONEMES),
                              Phonology.selectUnit(phonology.SUFFIX_CONS_PHONEMES))
      });
    } else if (prob < 0.6) {
      suffix = Word.generate(new Syllable[] {
              new Syllable(
                      "",
                      Phonology.selectUnit(phonology.VOWEL_PHONEMES),
                      Phonology.selectUnit(phonology.SUFFIX_CONS_PHONEMES))
      });
    } else {
        suffix = Word.generate(new Syllable[] {
                new Syllable(
                        "",
                        Phonology.selectUnit(phonology.VOWEL_PHONEMES),
                        "")
        });
    }

    if (suffixes.contains(suffix)) {
      return generateSuffix(phonology);
    } else {
      suffixes.add(suffix);
      return suffix;
    }
  }

  public Word pluralForm(Meaning meaning) {
    List<Phoneme> pluralSuffixPhonemes = pluralSuffixes[0].toPhonemes();
    Phoneme lastLetter =
            pluralSuffixPhonemes.get(pluralSuffixPhonemes.size() - 1);

    Word word = language.lookUpWord(meaning);

    if (word.endsWith(List.of(lastLetter)))
      return Word.compound(word, pluralSuffixes[1]);
    else
      return Word.compound(word, pluralSuffixes[0]);
  }

  public Word conjugate(Word verb, int tense, int person) {
    return Word.compound(verb, conjugationEndings[person][tense]);
  }

  public int getPerson(int number, Meaning meaning) {
    if (number > 1) {
      return THEY;
    } else {
      // TODO: Gender
      return HE;
    }
  }

  public Word getArticle(int number, boolean definite, Meaning meaning) {
    if (definite) {
      if (number > 1) {
        return articleWords[PD];
      } else {
        // TODO: Gender
        return articleWords[SND];
      }
    } else {
      if (number > 1) {
        return articleWords[PI];
      } else {
        // TODO: Gender
        return articleWords[SNI];
      }
    }
  }

  BufferedImage verbTable(Meaning meaning, Language language) {
    BufferedImage table = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) table.getGraphics();
    g.setColor(new Color(255, 255, 255));
    g.fillRect(0, 0, table.getWidth(), table.getHeight());

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
