package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.systems.language.sentences.Sentence;
import com.redsquare.citizen.util.Formatter;

import java.util.Map;

public class Name {
  private final Sentence meaning;
  private final NameGender gender;
  private final Language languageOfOrigin;
  private final Word name;

  private final Map<Language, Word> variants;

  public enum NameGender {
    M, F, U
  }

  private Name(final Word name, final Sentence meaning,
               final NameGender gender, final Language languageOfOrigin) {
    this.name = name;
    this.gender = gender;
    this.languageOfOrigin = languageOfOrigin;
    this.meaning = meaning;

    this.variants = Map.ofEntries(
            Map.entry(languageOfOrigin, name)
    );
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
