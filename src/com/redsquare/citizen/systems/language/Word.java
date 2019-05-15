package com.redsquare.citizen.systems.language;

import java.util.List;

public class Word {
  private final Syllable[] syllables;

  private static final double PREFIX_PROB = 0.7;
  private static final double SUFFIX_PROB = 0.4;

  private Word(Syllable[] syllables) {
    this.syllables = syllables;
  }

  private Word(Word w1, Word w2) {
    syllables = new Syllable[w1.syllables.length + w2.syllables.length];

    for (int i = 0; i < w1.syllables.length; i++) {
      syllables[i] = w1.syllables[i];
    }
    for (int i = 0; i < w2.syllables.length; i++) {
      syllables[w1.syllables.length + i] = w2.syllables[i];
    }
  }

  static Word compound(Word w1, Word w2) {
    return new Word(w1, w2);
  }

  private static String selectUnit(String[] pool) {
    int size = pool.length;
    int index = (int)(size * Math.random());

    return pool[index];
  }

  private static Syllable generateSyllable(String lastSyllable,
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

    return new Syllable(prefix, vowel, suffix);
  }

  static Word generateRandomWord(int minSyllables, int maxSyllables) {
    int syllableCount = minSyllables +
            (int)(Math.random() * (maxSyllables - minSyllables));
    Syllable[] syllables = new Syllable[syllableCount];

    for (int i = 0; i < syllables.length; i++) {
      String lastSyllable = "";
      if (i > 0) lastSyllable = syllables[i - 1].toString();
      syllables[i] = generateSyllable(lastSyllable, null);
    }

    return new Word(syllables);
  }

  public static Word generateRandomWord(int minSyllables, int maxSyllables,
                                          PhoneticVocabulary vocabulary) {
    int syllableCount = minSyllables +
            (int)(Math.random() * (maxSyllables - minSyllables));
    Syllable[] syllables = new Syllable[syllableCount];

    for (int i = 0; i < syllables.length; i++) {
      String lastSyllable = "";
      if (i > 0) lastSyllable = syllables[i - 1].toString();
      syllables[i] = generateSyllable(lastSyllable, vocabulary);
    }

    return new Word(syllables);
  }

  // Refactor to some utility class later
  private static boolean endsWithVowel(String syllable, String[] vowels) {
    boolean endsWithVowel = false;

    for (String vowel : vowels)
      endsWithVowel |= syllable.endsWith(vowel);

    return endsWithVowel;
  }

  @Override
  public String toString() {
    StringBuilder word = new StringBuilder();

    for (Syllable syllable : syllables) word.append(syllable.toString());

    return word.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Word)) return false;

    boolean matches = true;
    Word comp = (Word) obj;

    if (comp.syllables.length != syllables.length) return false;

    for (int i = 0; i < syllables.length; i++) {
      matches &= syllables[i].equals(comp.syllables[i]);
    }

    return matches;
  }

  @Override
  public int hashCode() {
    return syllables.length;
  }
}
