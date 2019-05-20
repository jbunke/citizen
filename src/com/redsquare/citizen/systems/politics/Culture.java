package com.redsquare.citizen.systems.politics;

public class Culture {

  private Inheritance inheritance;
  private final Race NATIVE_RACE;

  private Culture() {
    inheritance = Math.random() < 0.7 ? Inheritance.PATRILINEAL :
            (Math.random() < 0.7 ? Inheritance.MATRILINEAL : Inheritance.OPEN);
    NATIVE_RACE = Race.generate();
  }

  public static Culture generate() {
    return new Culture();
  }

  public enum Inheritance {
    PATRILINEAL, MATRILINEAL, OPEN
  }

  public Inheritance getInheritance() {
    return inheritance;
  }

  public Race getNativeRace() {
    return NATIVE_RACE;
  }
}
