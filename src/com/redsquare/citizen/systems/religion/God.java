package com.redsquare.citizen.systems.religion;

public final class God {
  private final boolean isOmniscient;
  private final boolean isOmnipotent;
  private final boolean isCorporeal;
  private final GodGender gender;
  private final boolean isAnthropomorphic;
  private final boolean isNamed;

  public enum GodGender {
    NONE, M, F
  }

  God(final boolean MONOTHEISTIC_RELIGION) {
    this.isOmniscient = MONOTHEISTIC_RELIGION ? Math.random() < 0.7 : Math.random() < 0.2;
    this.isOmnipotent = MONOTHEISTIC_RELIGION && Math.random() < 0.8;
    this.isNamed = !MONOTHEISTIC_RELIGION || Math.random() < 0.5;

    // TODO
    this.gender = GodGender.NONE;
    this.isCorporeal = false;
    this.isAnthropomorphic = false;
  }
}
