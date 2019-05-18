package com.redsquare.citizen.systems.politics;

public class Culture {

  private Inheritance inheritance;

  private Culture() {
    inheritance = Math.random() < 0.7 ? Inheritance.PATRILINEAL :
            (Math.random() < 0.7 ? Inheritance.MATRILINEAL : Inheritance.OPEN);
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
}
