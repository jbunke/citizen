package com.redsquare.citizen.entity.movement;

import com.redsquare.citizen.entity.LivingMoving;
import com.redsquare.citizen.graphics.RenderActivity;

public class MovementLogic {
  private static final int X = 0, Y = 1;

  private final LivingMoving associated;
  private final RenderLogic renderLogic;
  private final double[] movementVector;
  private double speed;
  private boolean running;

  private MovementLogic(LivingMoving associated) {
    this.associated = associated;
    this.movementVector = new double[2];
    this.renderLogic = new RenderLogic();
    this.running = false;

    // TODO temp
    this.speed = 6.;
  }

  public static MovementLogic assign(LivingMoving associated) {
    return new MovementLogic(associated);
  }

  public void toggleRunning() {
    running = !running;
  }

  public double[] movementVector() {
    return movementVector;
  }

  public RenderLogic renderLogic() {
    return renderLogic;
  }

  public void setMovementVector(int dimension, double value) {
    movementVector[dimension] = value;
  }

  public void update() {
    renderActivityUpdate();
    renderLogic.update();
    move();
  }

  private void renderActivityUpdate() {
    // TODO: Primitive temp
    if (moving()) {
      renderLogic.setActivity(running ?
              RenderActivity.RUNNING : RenderActivity.WALKING);
    } else {
      renderLogic.setActivity(RenderActivity.IDLE);
    }
  }

  private boolean moving() {
    return movementVector[X] != 0. || movementVector[Y] != 0.;
  }

  private void move() {
    double actualSpeed = speed / (running ? 1. : 1.5);

    associated.position().move(movementVector[X] * actualSpeed,
            movementVector[Y] * actualSpeed);
  }
}
