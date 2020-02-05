package com.redsquare.citizen.entity;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.entity.building.Entryway;
import com.redsquare.citizen.game_states.playing_systems.ControlScheme;
import com.redsquare.citizen.graphics.RenderDirection;
import com.redsquare.citizen.graphics.RenderPosture;
import com.redsquare.citizen.input_events.Event;
import com.redsquare.citizen.input_events.KeyPressEvent;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.FloatPoint;
import com.redsquare.citizen.worldgen.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Player extends Person {
  private static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3,
          X = 0, Y = 1;

  private final boolean[] dirKeys;
  // private final double[] movementVector;

  /* Displacement from player to world location where the mouse is pointing;
   * used to determine direction */
  private FloatPoint lookingRef;

  private String interactionMessage;
  private int interactionCounter;

  private int selectedInventorySlot;

  private Player(Sex sex, GameDate birthday, Settlement birthplace, World world) {
    super(sex, birthday, birthplace, world);

    dirKeys = new boolean[4];
    lookingRef = new FloatPoint(0., 0.);
    selectedInventorySlot = 0;

    interactionMessage = "";
    interactionCounter = 0;
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
            case INTERACT_FIGHT:
              if (movementLogic.renderLogic().getPosture() == RenderPosture.CALM)
                interact();

              // TODO: else case (fight)
              break;
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

  public String getInteractionMessage() {
    return interactionMessage;
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
    interactionChecking();
  }

  private void interactionChecking() {
    interactionCounter++;
    interactionCounter %= 10;

    if (interactionCounter == 0)
      interactionMessage = interactCheck();
  }

  /**
   * Interaction hierarchy:
   * Item pickup
   * Entryway
   * Person
   * */
  private void interact() {
    Set<Entity> nearby = position.getAllEntitiesWithinXCells(2);

    for (Entity e : nearby) {
      if (e instanceof ItemEntity) {
        pickupItem((ItemEntity) e);
        return;
      }
    }

    for (Entity e : nearby) {
      if (e instanceof Entryway) {
        ((Entryway) e).tryOpenOrClose();
        return;
      }
    }

    // TODO: people
  }

  private String interactCheck() {
    Set<Entity> nearby = position.getAllEntitiesWithinXCells(2);

    for (Entity e : nearby) {
      if (e instanceof ItemEntity)
        return "PICK UP";
    }

    for (Entity e : nearby) {
      if (e instanceof Entryway) {
        Entryway de = ((Entryway) e);

        if (de.isOpen())
          return "CLOSE";
        else if (!de.isLocked())
          return "OPEN";
      }
    }

    // TODO: people
    return "";
  }

  /**
   * If AGGRO or stationary (not moving),
   * player should face where they are LOOKING
   *
   * Otherwise, player should face where they are MOVING
   * */
  private void setDirection() {
    RenderDirection facingDirection;

    if (movementLogic.renderLogic().getPosture() == RenderPosture.AGGRO ||
            !isMoving()) {
      facingDirection = lookingDirection();
    } else {
      facingDirection = movingDirection();
    }

    movementLogic.renderLogic().setDirection(facingDirection);
  }

  private RenderDirection lookingDirection() {
    double angle = Math.atan((-1. * lookingRef.y) / lookingRef.x);
    if (lookingRef.x < 0) angle += Math.PI;
    return RenderDirection.fromAngle(angle);
  }

  private RenderDirection movingDirection() {
    if (dirKeys[UP] && !dirKeys[DOWN]) {
      if (dirKeys[LEFT] == dirKeys[RIGHT])
        return RenderDirection.U;
      else if (dirKeys[LEFT])
        return RenderDirection.UL;
      else
        return RenderDirection.UR;
    } else if (dirKeys[DOWN] && !dirKeys[UP]) {
      if (dirKeys[LEFT] == dirKeys[RIGHT])
        return RenderDirection.D;
      else if (dirKeys[LEFT])
        return RenderDirection.DL;
      else
        return RenderDirection.DR;
    } else {
      if (dirKeys[LEFT])
        return RenderDirection.L;
      else if (dirKeys[RIGHT])
        return RenderDirection.R;
      else
        return lookingDirection();
    }
  }

  private boolean isMoving() {
    return dirKeys[UP] != dirKeys[DOWN] || dirKeys[LEFT] != dirKeys[RIGHT];
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
