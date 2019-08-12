package com.redsquare.citizen.entity.movement;

import com.redsquare.citizen.entity.Animal;

public class MovementLogic {
  private final Animal associated;
  private final double[] movementVector;

  private MovementLogic(Animal associated) {
    this.associated = associated;
    this.movementVector = new double[2];
  }

  public static MovementLogic assign(Animal associated) {
    return new MovementLogic(associated);
  }
}
