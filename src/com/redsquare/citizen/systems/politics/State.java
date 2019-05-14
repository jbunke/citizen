package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.language.PhoneticVocabulary;
import com.redsquare.citizen.systems.language.PlaceNameGenerator;

import java.util.HashSet;
import java.util.Set;

public class State {
  private String name;

  // admin
  private Settlement capital;

  //culture
  private PhoneticVocabulary vocabulary;

  public State() {
    this.vocabulary = PhoneticVocabulary.generate();
    this.name = PlaceNameGenerator.generateRandomName(2, 3, vocabulary);
  }

  public PhoneticVocabulary getVocabulary() {
    return vocabulary;
  }

  public Settlement getCapital() {
    return capital;
  }

  public void setCapital(Settlement capital) {
    this.capital = capital;
    capital.removeLiege();
  }

  public Set<Settlement> settlements() {
    Set<Settlement> settlements = new HashSet<>();

    if (capital == null) return settlements;

    settlements.add(capital);

    Set<Settlement> regionalCapitals = capital.getVassals();

    settlements.addAll(regionalCapitals);

    for (Settlement region : regionalCapitals) {
      settlements.addAll(region.getVassals());
    }

    return settlements;
  }
}
