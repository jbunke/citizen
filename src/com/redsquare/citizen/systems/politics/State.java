package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.language.*;
import com.redsquare.citizen.systems.vexillography.Flag;
import com.redsquare.citizen.util.Formatter;
import com.redsquare.citizen.util.MathExt;
import com.redsquare.citizen.util.Sets;
import com.redsquare.citizen.worldgen.World;

import java.awt.*;
import java.util.*;
import java.util.List;

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
    language = Language.generate(WritingSystem.generate(Phonology.generate(), WritingSystem.Type.ALPHABET));
    // TODO: language = Language.generate(); must be made more memory efficient in the SYLLABARY case
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

  /**
   * Restructures the administrative subdivisions according to
   * the new geography or power balance */
  void administrativeRestructuring() {
    List<Settlement> settlements = new ArrayList<>(settlements());

    for (Settlement s : settlements) {
      s.resetVassals();
      s.removeLiege();
    }

    int regionCount = (int)((0.05 * settlements.size()) +
            (Math.random() * 0.05 * settlements.size()));
    Set<Settlement> regions = new HashSet<>();
    regions.add(capital);

    settlements.sort(Comparator.comparingInt(
            x -> x.getSetupPower() * -1
    ));

    int trials = 0;
    int placed = 0;
    int sortedIndex = 1;

    int minimumDistance = (int)(Math.sqrt(settlements.size()) * 2.5);

    // Add region capitals by order of strength as long as they
    // satisfy a minimum distance from each other
    while (placed < regionCount && trials < regionCount * 100
            && sortedIndex < settlements.size()) {
      boolean violated = false;
      Point location = settlements.get(sortedIndex).getLocation();
      for (Settlement region : regions) {
        Point regLoc = region.getLocation();
        if (Math.hypot(Math.abs(location.x - regLoc.x),
                Math.abs(location.y - regLoc.y)) < minimumDistance) {
          violated = true;
        }
      }

      trials++;

      if (!violated) {
        placed++;
        regions.add(settlements.get(sortedIndex));
      }

      sortedIndex++;
    }

    for (Settlement region : regions) {
      if (!region.equals(capital)) capital.addVassal(region);
    }

    Set<Settlement> remaining = new HashSet<>(
            Sets.difference(new HashSet<>(settlements), regions));

    for (Settlement r : remaining) {
      double d = Double.MAX_VALUE;
      Settlement closestRegion = null;

      for (Settlement region : regions) {
        double candidate = MathExt.distance(
                r.getLocation(), region.getLocation());
        if (candidate < d) {
          closestRegion = region;
          d = candidate;
        }
      }

      if (closestRegion != null) closestRegion.addVassal(r);
    }
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
