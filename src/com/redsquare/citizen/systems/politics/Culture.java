package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.vexillography.FlagPattern;
import com.redsquare.citizen.util.Randoms;

import java.util.HashSet;
import java.util.Set;

public class Culture {

  private Inheritance inheritance;
  private final Race NATIVE_RACE;
  private final BeautyStandard beautyStandard;
  private final Set<FlagPattern> patterns;
  private final Set<FlagPattern> symbols;

  private Culture() {
    inheritance = Math.random() < 0.7 ? Inheritance.PATRILINEAL :
            (Math.random() < 0.7 ? Inheritance.MATRILINEAL : Inheritance.OPEN);
    NATIVE_RACE = Race.generate();
    beautyStandard = BeautyStandard.generate(this);
    patterns = generatePatterns();
    symbols = generateSymbols();
  }

  public static Culture generate() {
    return new Culture();
  }

  public enum Inheritance {
    PATRILINEAL, MATRILINEAL, OPEN
  }

  private Set<FlagPattern> generatePatterns() {
    Set<FlagPattern> patterns = new HashSet<>();

    int nonSymbols = Randoms.bounded(2, 5);

    for (int i = 0; i < nonSymbols; i++) {
      patterns.add(FlagPattern.generatePattern());
    }

    return patterns;
  }

  private Set<FlagPattern> generateSymbols() {
    Set<FlagPattern> symbols = new HashSet<>();

    int symbolCount = Randoms.bounded(3, 6);

    while (symbols.size() < symbolCount)
      symbols.add(FlagPattern.generateSymbol());

    return symbols;
  }

  public BeautyStandard getBeautyStandard() {
    return beautyStandard;
  }

  public Inheritance getInheritance() {
    return inheritance;
  }

  public Race getNativeRace() {
    return NATIVE_RACE;
  }

  public Set<FlagPattern> getPatterns() {
    return patterns;
  }

  public Set<FlagPattern> getSymbols() {
    return symbols;
  }
}
