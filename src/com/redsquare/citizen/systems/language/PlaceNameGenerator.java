package com.redsquare.citizen.systems.language;

import java.util.List;

public class PlaceNameGenerator {

  private static final double PREFIX_PROB = 0.7;
  private static final double SUFFIX_PROB = 0.4;

  private static String selectUnit(String[] pool) {
    int size = pool.length;
    int index = (int)(size * Math.random());

    return pool[index];
  }

//  private static String selectUnit(String[] pool, int threshold, double prob) {
//    int size = pool.length;
//
//    if (Math.random() < prob) size = threshold;
//
//    int index = (int)(size * Math.random());
//
//    return pool[index];
//  }

  private static String generateSyllable(String lastSyllable,
                                         PhoneticVocabulary vocabulary) {
    String[] vowels = Phonemes.VOMEL_PHONEMES;
    String[] prefixes = Phonemes.PREFIX_CONS_PHONEMES;
    String[] suffixes = Phonemes.SUFFIX_CONS_PHONEMES;

    if (vocabulary != null) {
      vowels = vocabulary.VOWEL_PHONEMES;
      prefixes = vocabulary.PREFIX_CONS_PHONEMES;
      suffixes = vocabulary.SUFFIX_CONS_PHONEMES;
    }

    boolean hasPrefix = Math.random() < PREFIX_PROB ||
            (!lastSyllable.equals("") && endsWithVowel(lastSyllable, vowels));
    if (!lastSyllable.equals("")) hasPrefix &= !lastSyllable.endsWith("h");
    boolean hasSuffix = Math.random() < SUFFIX_PROB;
    String vowel = selectUnit(vowels);
    String prefix = "";
    String suffix = "";

    if (hasPrefix) {
      boolean violates = true;
      while (violates) {
        violates = false;

        prefix = selectUnit(prefixes);
        if (Phonemes.ILLEGAL_PREFIX_TO_VOWEL.containsKey(prefix)) {
          List<String> violatingVowels = Phonemes.ILLEGAL_PREFIX_TO_VOWEL.get(prefix);
          violates = violatingVowels.contains(vowel);
        }

        if (!lastSyllable.equals(""))
          violates |= (lastSyllable.endsWith(prefix) ||
                  lastSyllable.endsWith(prefix + vowel));
      }
    }

    if (hasSuffix) {
      boolean violates = true;
      while (violates) {
        suffix = selectUnit(suffixes);
        if (Phonemes.ILLEGAL_VOWEL_TO_SUFFIX.containsKey(vowel)) {
          List<String> violatingSuffixes = Phonemes.ILLEGAL_VOWEL_TO_SUFFIX.get(vowel);
          violates = violatingSuffixes.contains(suffix);
        } else violates = false;
      }
    }

    return prefix + vowel + suffix;
  }

  public static String generateRandomName(int minSyllables, int maxSyllables) {
    int syllableCount = minSyllables +
            (int)(Math.random() * (maxSyllables - minSyllables));
    String[] syllables = new String[syllableCount];

    for (int i = 0; i < syllables.length; i++) {
      String lastSyllable = "";
      if (i > 0) lastSyllable = syllables[i - 1];
      syllables[i] = generateSyllable(lastSyllable, null);
    }

    StringBuilder name = new StringBuilder();

    for (String syllable : syllables) name.append(syllable);

    return capitaliseFirstLetter(name.toString());
  }

  public static String generateRandomName(int minSyllables, int maxSyllables,
                                          PhoneticVocabulary vocabulary) {
    int syllableCount = minSyllables +
            (int)(Math.random() * (maxSyllables - minSyllables));
    String[] syllables = new String[syllableCount];

    for (int i = 0; i < syllables.length; i++) {
      String lastSyllable = "";
      if (i > 0) lastSyllable = syllables[i - 1];
      syllables[i] = generateSyllable(lastSyllable, vocabulary);
    }

    StringBuilder name = new StringBuilder();

    for (String syllable : syllables) name.append(syllable);

    return capitaliseFirstLetter(name.toString());
  }

  private static String capitaliseFirstLetter(String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  // Refactor to some utility class later
  private static boolean endsWithVowel(String syllable, String[] vowels) {
    boolean endsWithVowel = false;

    for (String vowel : vowels)
      endsWithVowel |= syllable.endsWith(vowel);

    return endsWithVowel;
  }
}
