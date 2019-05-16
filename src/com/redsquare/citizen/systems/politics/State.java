package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.language.*;

import java.util.HashSet;
import java.util.Set;

public class State {
  private Word name;

  // admin
  private Settlement capital;

  //culture
  private Language language;

  public State() {
    language = Language.generate();
    this.name = language.lookUpWord(Meaning.THIS_STATE);
  }

  public String getName() {
    return name.toString();
  }

  public Language getLanguage() {
    return language;
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
