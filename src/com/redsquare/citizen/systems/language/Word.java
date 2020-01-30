package com.redsquare.citizen.systems.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Word {
  private static final int FIRST_SYLLABLE = 1, MIDDLE_SYLLABLE = 2, LAST_SYLLABLE = 3;

  private final Syllable[] syllables;

  private static final double PREFIX_PROB = 0.7;
  private static final double SUFFIX_PROB = 0.4;

  private Word(Syllable[] syllables) {
    this.syllables = syllables;
  }

  private Word(Word w1, Word w2) {
    syllables = new Syllable[w1.syllables.length + w2.syllables.length];

    System.arraycopy(w1.syllables, 0, syllables, 0, w1.syllables.length);
    System.arraycopy(w2.syllables, 0, syllables, w1.syllables.length, w2.syllables.length);
  }

  public static Word EMPTY() {
    return new Word(new Syllable[] {});
  }

  public static Word compound(Word w1, Word w2) {
    return new Word(w1, w2);
  }

  static Word generate(Syllable[] syllables) {
    return new Word(syllables);
  }

  Syllable[] getSyllables() {
    return syllables;
  }

  private static Syllable generateSyllable(Syllable lastSyllable,
                                           Phonology phonology, final int PLACE) {
    String[] vowels = (phonology != null) ?
            phonology.VOWEL_PHONEMES : Phonemes.VOWEL_PHONEMES;
    String[] prefixes = (phonology != null) ?
            phonology.PREFIX_CONS_PHONEMES : Phonemes.PREFIX_CONS_PHONEMES;
    String[] suffixes = (phonology != null) ?
            phonology.SUFFIX_CONS_PHONEMES : Phonemes.SUFFIX_CONS_PHONEMES;

    boolean hasPrefix = PLACE != FIRST_SYLLABLE || Math.random() < PREFIX_PROB;
    boolean hasSuffix = Math.random() < SUFFIX_PROB;

    String vowel = Phonology.selectUnit(vowels);
    String prefix = hasPrefix ? Phonology.selectUnit(prefixes) : "";
    String suffix = hasSuffix ? Phonology.selectUnit(suffixes) : "";

    while (hasPrefix && (Phonemes.ILLEGAL_PREFIX_TO_VOWEL.
            getOrDefault(prefix, List.of()).contains(vowel) ||
            (lastSyllable != null && lastSyllable.getSuffix().equals(prefix)))) {
      prefix = Phonology.selectUnit(prefixes);
    }

    while (hasSuffix && Phonemes.ILLEGAL_VOWEL_TO_SUFFIX.
            getOrDefault(vowel, List.of()).contains(suffix)) {
      suffix = Phonology.selectUnit(suffixes);
    }

    return new Syllable(prefix, vowel, suffix);
  }

  static Word generateRandomWord(int minSyllables, int maxSyllables) {
    return generateRandomWord(minSyllables, maxSyllables, null);
  }

  public static Word generateRandomWord(int minSyllables, int maxSyllables,
                                          Phonology phonology) {
    int syllableCount = minSyllables +
            (int)(Math.random() * (maxSyllables - minSyllables));
    Syllable[] syllables = new Syllable[syllableCount];

    for (int i = 0; i < syllables.length; i++) {
      Syllable lastSyllable = null;
      if (i > 0) lastSyllable = syllables[i - 1];
      syllables[i] = generateSyllable(lastSyllable,
              phonology, i == 0 ? FIRST_SYLLABLE :
                      (i + 1 == syllables.length ? LAST_SYLLABLE : MIDDLE_SYLLABLE));
    }

    return new Word(syllables);
  }

  private List<Phoneme> toPhonemes() {
    List<Phoneme> phonemes = new ArrayList<>();

    for (Syllable syllable : this.syllables) {
      if (syllable.getPrefix().length() > 0) phonemes.add(new Phoneme(syllable.getPrefix()));
      phonemes.add(new Phoneme(syllable.getVowel()));
      if (syllable.getSuffix().length() > 0) phonemes.add(new Phoneme(syllable.getSuffix()));
    }

    return phonemes;
  }

  boolean endsWith(final Phoneme[] phonemes) {
    boolean endsWith = true;

    List<Phoneme> word = toPhonemes();

    if (word.size() < phonemes.length) return false;

    for (Phoneme phoneme : phonemes) {
      endsWith &= phoneme.equals(word.get(word.size() - 1));
      word.remove(word.size() - 1);
    }

    return endsWith;
  }

  Word offspring(Set<SoundShift> soundShifts) {
    Syllable[] syllables = new Syllable[this.syllables.length];

    for (int i = 0; i < this.syllables.length; i++) {
      Syllable syllable = this.syllables[i];
      Syllable replacement = new Syllable(syllable.getPrefix(),
              syllable.getVowel(), syllable.getSuffix());
      for (SoundShift soundShift : soundShifts) {
        if (syllable.getPrefix().equals(soundShift.from) &&
                Math.random() < soundShift.consistency) {
          replacement = new Syllable(soundShift.to, replacement.getVowel(), replacement.getSuffix());
        } else if (syllable.getVowel().equals(soundShift.from) &&
                Math.random() < soundShift.consistency) {
          replacement = new Syllable(replacement.getPrefix(), soundShift.to, replacement.getSuffix());
        } else if (syllable.getSuffix().equals(soundShift.from) &&
                Math.random() < soundShift.consistency) {
          replacement = new Syllable(replacement.getPrefix(), replacement.getVowel(), soundShift.to);
        }
      }

      syllables[i] = replacement;
    }

    return new Word(syllables);
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
