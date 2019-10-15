package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.language.*;
import com.redsquare.citizen.systems.vexillography.Flag;
import com.redsquare.citizen.util.Formatter;
import com.redsquare.citizen.worldgen.World;

import java.util.HashSet;
import java.util.Set;

public class State {
  private final World world;

  private Word name;

  // admin
  private Settlement capital;

  //culture
  private Language language;
  private Culture culture;
  private Flag flag;

  static State fromSecession(Settlement capital,
                                    Language language, Culture culture,
                                    State secededFrom) {
    return new State(capital, language, culture, secededFrom);
  }

  private State(Settlement capital, Language language, Culture culture,
                State secededFrom) {
    this.world = secededFrom.world;

    this.capital = capital;
    this.language = language;
    this.culture = culture;
    this.flag = Flag.generate(culture);

    // TODO: Example secessionist country name construction
    this.name = Word.compound(language.lookUpWord(Meaning.OPPOSITE), secededFrom.name);
  }

  public State(World world) {
    this.world = world;

    culture = Culture.generate();
    language = Language.generate();
    flag = Flag.generate(culture);
    this.name = language.lookUpWord(Meaning.THIS_STATE);
  }

  World getWorld() {
    return world;
  }

  public String getName() {
    return name.toString();
  }

  public Language getLanguage() {
    return language;
  }

  public Culture getCulture() {
    return culture;
  }

  public Flag getFlag() {
    return flag;
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

  @Override
  public String toString() {
    return Formatter.properNoun(name.toString());
  }
}
