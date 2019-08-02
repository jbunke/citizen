package com.redsquare.citizen.entity;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.game_states.playing_systems.ControlScheme;
import com.redsquare.citizen.graphics.RenderDirection;
import com.redsquare.citizen.input_events.Event;
import com.redsquare.citizen.input_events.KeyPressEvent;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.FloatPoint;
import com.redsquare.citizen.worldgen.World;

import java.util.ArrayList;
import java.util.List;

public final class Player extends Person {
  private static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3,
          X = 0, Y = 1;

  private final boolean[] dirKeys;
  private final double[] movementVector;

  /* Displacement from player to world location where the mouse is pointing;
   * used to determine direction */
  private FloatPoint lookingRef;

  private Player(Sex sex, GameDate birthday, Settlement birthplace, World world) {
    super(sex, birthday, birthplace, world);

    dirKeys = new boolean[4];
    movementVector = new double[2];
    lookingRef = new FloatPoint(0., 0.);
  }

  public static Player temp(World world) {
    Settlement settlement = world.randomSettlement();
    return new Player(Sex.MALE, new GameDate(1, 1),
            settlement, world);
  }

  public void setLookingRef(FloatPoint lookingRef) {
    this.lookingRef = lookingRef;
  }

  public void input(InputHandler inputHandler) {
    List<KeyPressEvent> keyEvents = new ArrayList<>();

    for (Event event : inputHandler.getUnprocessedEvents()) {
      if (event instanceof KeyPressEvent) keyEvents.add((KeyPressEvent) event);
    }

    for (int i = 0; i < keyEvents.size(); i++) {
      KeyPressEvent kpe = keyEvents.get(i);
      boolean processed = false;
      ControlScheme.Action action = ControlScheme.get().getAction(kpe.key);

      switch (kpe.eventType) {
        case PRESSED:

          switch (action) {
            case UP:
              dirKeys[UP] = true;
              processed = true;
              break;
            case DOWN:
              dirKeys[DOWN] = true;
              processed = true;
              break;
            case LEFT:
              dirKeys[LEFT] = true;
              processed = true;
              break;
            case RIGHT:
              dirKeys[RIGHT] = true;
              processed = true;
              break;
          }
          break;
        case RELEASED:
          switch (action) {
            case UP:
              dirKeys[UP] = false;
              processed = true;
              break;
            case DOWN:
              dirKeys[DOWN] = false;
              processed = true;
              break;
            case LEFT:
              dirKeys[LEFT] = false;
              processed = true;
              break;
            case RIGHT:
              dirKeys[RIGHT] = false;
              processed = true;
              break;
          }
          break;
      }

      if (processed) {
        keyEvents.remove(i);
        i--;
      }
    }
  }

  @Override
  public void update() {
    // TODO
    setDirection();
    setMovementVector();
    move();
  }

  private void setDirection() {
    double angle = Math.atan((-1. * lookingRef.y) / lookingRef.x);
    if (lookingRef.x < 0) angle += Math.PI;
    direction = RenderDirection.fromAngle(angle);
  }

  private void setMovementVector() {
    movementVector[X] = dirKeys[LEFT] == dirKeys[RIGHT] ? 0. :
            (dirKeys[LEFT] ? -1. : 1.);
    movementVector[Y] = dirKeys[UP] == dirKeys[DOWN] ? 0. :
            (dirKeys[UP] ? -1. : 1.);

    if (movementVector[X] != 0. && movementVector[Y] != 0) {
      movementVector[X] *= Math.sqrt(1.);
      movementVector[Y] *= Math.sqrt(1.);
    }
  }

  public void resetDirectionKeys() {
    for (int i = 0; i < dirKeys.length; i++)
      dirKeys[i] = false;
  }

  private void move() {
    position.move(movementVector[X] * speed,
            movementVector[Y] * speed);
  }
}
