package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.systems.language.sentences.BasicNounNP;
import com.redsquare.citizen.util.Formatter;
import com.redsquare.citizen.util.Randoms;

import java.util.Map;

public class Name {
  private final BasicNounNP meaning;
  private final NameGender gender;
  private final Language languageOfOrigin;
  private final Word name;
  private final double popularity;

  private final Map<Language, Word> variants;

  public enum NameGender {
    M, F, U
  }

  private Name(final Word name, final BasicNounNP meaning,
               final NameGender gender, final Language languageOfOrigin) {
    this.name = name;
    this.gender = gender;
    this.languageOfOrigin = languageOfOrigin;
    this.meaning = meaning;

    this.variants = Map.ofEntries(
            Map.entry(languageOfOrigin, name)
    );

    this.popularity = Randoms.bounded(0., 1.);
  }

  public static Name generate(final Word name, final BasicNounNP meaning,
                              final NameGender gender, final Language languageOfOrigin) {
    return new Name(name, meaning, gender, languageOfOrigin);
  }

  public Word getName() {
    return name;
  }

  public double getPopularity() {
    return popularity;
  }

  @Override
  public String toString() {
    return Formatter.properNoun(name.toString());
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Name))
      return false;

    Name otherName = (Name) other;

    return name.equals(otherName.name) &&
            languageOfOrigin.equals(otherName.languageOfOrigin);
  }
}
