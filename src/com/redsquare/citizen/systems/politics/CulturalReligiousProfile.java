package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.religion.Religion;

public class CulturalReligiousProfile {
  private double tolerance; // 0. - 1. for religious tolerance
  private Religion[] religions;
  private double[] prevalence; // corresponds to religions and must sum to one

  private CulturalReligiousProfile() {

  }

  static CulturalReligiousProfile generate() {
    return new CulturalReligiousProfile();
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
