package com.redsquare.citizen.entity.movement;

import com.redsquare.citizen.entity.Animal;

public class MovementLogic {
  private static final int X = 0, Y = 1;

  private final Animal associated;
  private final double[] movementVector;
  private double speed;

  private MovementLogic(Animal associated) {
    this.associated = associated;
    this.movementVector = new double[2];

    // TODO temp
    this.speed = 6.;
  }

  public static MovementLogic assign(Animal associated) {
    return new MovementLogic(associated);
  }

  public double[] movementVector() {
    return movementVector;
  }

  public void setMovementVector(int dimension, double value) {
    movementVector[dimension] = value;
  }

  public void update() {
    move();
  }

  private void move() {
    associated.position().move(movementVector[X] * speed,
            movementVector[Y] * speed);
  }
}
