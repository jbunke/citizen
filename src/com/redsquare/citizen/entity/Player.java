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
  // private final double[] movementVector;

  /* Displacement from player to world location where the mouse is pointing;
   * used to determine direction */
  private FloatPoint lookingRef;

  private int selectedInventorySlot;

  private Player(Sex sex, GameDate birthday, Settlement birthplace, World world) {
    super(sex, birthday, birthplace, world);

    dirKeys = new boolean[4];
    lookingRef = new FloatPoint(0., 0.);
    selectedInventorySlot = 0;
  }

  public static Player temp(World world, GameDate birthday) {
    Settlement settlement = world.randomSettlement();
    return new Player(Sex.MALE, birthday, settlement, world);
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
            case SELECT_SLOT_1:
              selectedInventorySlot = 0;
              processed = true;
              break;
            case SELECT_SLOT_2:
              selectedInventorySlot = 1;
              processed = true;
              break;
            case SELECT_SLOT_3:
              selectedInventorySlot = 2;
              processed = true;
              break;
            case SELECT_SLOT_4:
              selectedInventorySlot = 3;
              processed = true;
              break;
            case SELECT_SLOT_5:
              selectedInventorySlot = 4;
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
            case TOGGLE_SPRINT:
              movementLogic.toggleRunning();
              processed = true;
              break;
            case TOGGLE_AGGRO:
              movementLogic.renderLogic().switchPosture();
              processed = true;
              break;
            case DROP_SINGLE_ITEM:
              inventory.dropItem(selectedInventorySlot,
                      movementLogic.renderLogic().getDirection(), false);
              processed = true;
              break;
            case DROP_ITEM_STACK:
              inventory.dropItem(selectedInventorySlot,
                      movementLogic.renderLogic().getDirection(), true);
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

  public int getSelectedInventorySlot() {
    return selectedInventorySlot;
  }

  @Override
  public void update() {
    super.update();
    // TODO
    setDirection();
    setMovementVector();
  }

  private void setDirection() {
    double angle = Math.atan((-1. * lookingRef.y) / lookingRef.x);
    if (lookingRef.x < 0) angle += Math.PI;
    movementLogic.renderLogic().setDirection(RenderDirection.fromAngle(angle));
  }

  private void setMovementVector() {
    movementLogic.setMovementVector(X, dirKeys[LEFT] == dirKeys[RIGHT] ? 0. :
            (dirKeys[LEFT] ? -1. : 1.));
    movementLogic.setMovementVector(Y, dirKeys[UP] == dirKeys[DOWN] ? 0. :
            (dirKeys[UP] ? -1. : 1.));

    if (movementLogic.movementVector()[X] != 0. &&
            movementLogic.movementVector()[Y] != 0.) {
      movementLogic.movementVector()[X] *= Math.sqrt(1.);
      movementLogic.movementVector()[Y] *= Math.sqrt(1.);
    }
  }

  public void resetDirectionKeys() {
    dirKeys[UP] = false;
    dirKeys[DOWN] = false;
    dirKeys[LEFT] = false;
    dirKeys[RIGHT] = false;
  }
}
