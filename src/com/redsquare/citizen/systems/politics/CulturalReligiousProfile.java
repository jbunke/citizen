package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.religion.Religion;

public class CulturalReligiousProfile {
  private double tolerance; // 0. - 1. for religious tolerance
  private Religion[] religions;
  private double[] prevalence; // corresponds to religions and must sum to one

  private CulturalReligiousProfile(final Culture culture) {

  }

  static CulturalReligiousProfile generate(final Culture culture) {
    return new CulturalReligiousProfile(culture);
  }

  public double getTolerance() {
    return tolerance;
  }

  public Religion[] getReligions() {
    return religions;
  }

  public double[] getPrevalence() {
    return prevalence;
  }
}
