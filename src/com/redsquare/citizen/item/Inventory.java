package com.redsquare.citizen.item;

import com.redsquare.citizen.entity.ItemEntity;
import com.redsquare.citizen.entity.LivingMoving;
import com.redsquare.citizen.entity.Person;

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
