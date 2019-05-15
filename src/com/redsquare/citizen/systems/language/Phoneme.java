package com.redsquare.citizen.systems.language;

/**
 * Class only exists to support polymorphism of WritingSystems for both
 * alphabets & syllabaries
 * */
public class Phoneme extends WordSubUnit {
  private final String phoneme;

  public Phoneme(String phoneme) {
    this.phoneme = phoneme;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Phoneme) && ((Phoneme) o).phoneme.equals(phoneme);
  }

  @Override
  public int hashCode() {
    return phoneme.length();
  }

  @Override
  public String toString() {
    return phoneme;
  }
}
