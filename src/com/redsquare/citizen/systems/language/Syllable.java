package com.redsquare.citizen.systems.language;

public class Syllable extends WordSubUnit {
  private final String prefix;
  private final String vowel;
  private final String suffix;

  Syllable(String prefix, String vowel, String suffix) {
    this.prefix = prefix;
    this.vowel = vowel;
    this.suffix = suffix;
  }

  String getPrefix() {
    return prefix;
  }

  String getVowel() {
    return vowel;
  }

  String getSuffix() {
    return suffix;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Syllable)) return false;

    Syllable comp = (Syllable) o;

    return prefix.equals(comp.prefix) &&
            vowel.equals(comp.vowel) && suffix.equals(comp.suffix);
  }

  @Override
  public int hashCode() {
    return prefix.length() + vowel.length() + suffix.length();
  }

  @Override
  public String toString() {
    return prefix + vowel + suffix;
  }
}
