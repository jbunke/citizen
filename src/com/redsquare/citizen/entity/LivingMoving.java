package com.redsquare.citizen.entity;

import com.redsquare.citizen.entity.movement.MovementLogic;
import com.redsquare.citizen.entity.movement.RenderLogic;
import com.redsquare.citizen.item.Inventory;

public abstract class LivingMoving extends LivingEntity {

  Sex sex;
  MovementLogic movementLogic;
  Inventory inventory;

  public String getSpriteCode() {
    RenderLogic rl = movementLogic.renderLogic();

    return rl.getDirection().name() + "-" + rl.getPosture().name() + "-" +
            rl.getActivity().name() + "-" + rl.getPoseNum();
  }

  public Sex getSex() {
    return sex;
  }

  public Inventory getInventory() {
    return inventory;
  }

  protected void pickupItem(ItemEntity itemEntity) {
    boolean delete = inventory.tryPickup(itemEntity);

    if (itemEntity != null && delete)
      itemEntity.position.getWorld().getCell(
              itemEntity.position.world().x, itemEntity.position.world().y).removeEntity(itemEntity);
  }

  @Override
  public void update() {
    super.update();
    movementLogic.update();
  }
}
