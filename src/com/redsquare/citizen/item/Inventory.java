package com.redsquare.citizen.item;

import com.redsquare.citizen.entity.ItemEntity;
import com.redsquare.citizen.entity.LivingMoving;
import com.redsquare.citizen.entity.Person;
import com.redsquare.citizen.graphics.RenderDirection;
import com.redsquare.citizen.util.FloatPoint;
import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.worldgen.WorldPosition;

import java.awt.*;

public class Inventory {
  private final LivingMoving associated;
  private final int slots;
  private final Item[] contents;

  private Inventory(LivingMoving associated) {
    this.associated = associated;

    if (associated instanceof Person)
      slots = 5;
    else
      slots = 3;

    contents = new Item[slots];
  }

  public static Inventory createInventory(LivingMoving associatedEntity) {
    return new Inventory(associatedEntity);
  }

  public Item[] getContents() {
    return contents;
  }

  public void dropItem(final int SELECTED_SLOT, final RenderDirection DIRECTION, final boolean DROP_STACK) {
    Item itemToDrop = contents[SELECTED_SLOT];

    if (itemToDrop == null)
      return;

    Point w = associated.position().world();
    Point c = DIRECTION.moveByNUnits(associated.position().cell(), 1);
    FloatPoint sc = new FloatPoint(
            Randoms.bounded(0., WorldPosition.CELL_DIMENSION_LENGTH),
            Randoms.bounded(0., WorldPosition.CELL_DIMENSION_LENGTH));

    if (itemToDrop instanceof UnstackableItem || DROP_STACK ||
            (itemToDrop instanceof StackableItem && ((StackableItem) itemToDrop).getQuantity() == 1)) {
      ItemEntity entity = ItemEntity.fromItem(itemToDrop);

      WorldPosition position = new WorldPosition(w, c, sc, associated.position().getWorld(), entity);
      entity.setPosition(position);

      contents[SELECTED_SLOT] = null;
    } else if (itemToDrop instanceof StackableItem) {
      ItemEntity entity = ItemEntity.fromItem(
              StackableItem.fromIDAndQuantity(itemToDrop.itemID, 1));

      WorldPosition position = new WorldPosition(w, c, sc, associated.position().getWorld(), entity);
      entity.setPosition(position);

      ((StackableItem) itemToDrop).decreaseBy(1);
    }
  }

  /**
   * Attempts to pick up item entities and insert them into inventory;
   * returns true if item entity should be deleted
   * */
  public boolean tryPickup(ItemEntity itemEntity) {
    int foundAt = -1;

    for (int i = 0; i < slots; i++) {
      if (contents[i] != null)
        if (contents[i].itemID.equals(itemEntity.getItem().itemID))
          foundAt = i;
    }

    if (foundAt != -1) {
      if (!(itemEntity.getItem() instanceof StackableItem))
        return false;
      else {
        if (((StackableItem) contents[foundAt]).getQuantity() < StackableItem.STACK_MAXIMUM) {

          StackableItem fromEntity = (StackableItem) itemEntity.getItem();
          StackableItem fromInventory = (StackableItem) contents[foundAt];

          int canAdd = StackableItem.STACK_MAXIMUM - fromInventory.getQuantity();
          int willAdd = Math.min(canAdd, fromEntity.getQuantity());

          fromInventory.increaseBy(willAdd);

          if (fromEntity.getQuantity() > willAdd) {
            fromEntity.decreaseBy(willAdd);
            return false;
          } else {
            return true;
          }
        }
      }
    }

    // Find first empty slot and populate item there
    for (int i = 0; i < slots; i++) {
      if (contents[i] == null) {
        contents[i] = itemEntity.getItem();
        return true;
      }
    }

    return false;
  }
}
