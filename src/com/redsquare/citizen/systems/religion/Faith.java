package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.entity.Person;

public class Faith {
  private final Person associated;
  private Religion religion;
  private final double[] FAITH_VECTOR;
  private static final int F_STRENGTH_INDEX = 0, F_AWARENESS_INDEX = 1, NUM_INDICES = 2;

  private Faith(Person associated, Religion initialReligion) {
    this.associated = associated;
    this.religion = initialReligion;

    this.FAITH_VECTOR = new double[NUM_INDICES];
    this.FAITH_VECTOR[F_STRENGTH_INDEX] = 0.5;
    this.FAITH_VECTOR[F_AWARENESS_INDEX] = 0.;
  }
}
