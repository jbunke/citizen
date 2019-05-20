package com.redsquare.citizen.systems.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Phonology {
  final String[] VOWEL_PHONEMES;

  final String[] PREFIX_CONS_PHONEMES;

  final String[] SUFFIX_CONS_PHONEMES;

  private Phonology() {
    VOWEL_PHONEMES =
            generateSet(new String[] { "a", "e", "i", "o", "u" },
                    3, 4, Phonemes.VOMEL_PHONEMES);
    PREFIX_CONS_PHONEMES =
            generateSet(new String[] { "b", "d", "f", "g", "k", "l",
                    "m", "n", "p", "r", "s", "t" },
                    4, 5, Phonemes.PREFIX_CONS_PHONEMES);
    SUFFIX_CONS_PHONEMES =
            generateSet(new String[] { "b", "d", "f", "g", "k", "l",
                    "m", "n", "p", "r", "s", "t" },
                    4, 5, Phonemes.SUFFIX_CONS_PHONEMES);
  }

  public static Phonology generate() {
    return new Phonology();
  }

  private String[] generateSet(String[] determined, int minimum, int range,
                               String[] source) {
    List<String> all = new ArrayList<>(Arrays.asList(source));

    String[] set = new String[determined.length +
            (int)(minimum + Math.random() * range)];

    for (int i = 0; i < determined.length; i++) {
      set[i] = determined[i];
      all.remove(determined[i]);
    }

    for (int i = determined.length; i < set.length; i++) {
      String pick = all.get((int)(all.size() * Math.random()));
      all.remove(pick);
      set[i] = pick;
    }

    return set;
  }

  static String selectUnit(String[] pool) {
    int size = pool.length;
    int index = (int)(size * Math.random());

    return pool[index];
  }

}
