package com.redsquare.citizen.entity;

import com.redsquare.citizen.entity.collision.Collider;
import com.redsquare.citizen.item.Item;
import com.redsquare.citizen.item.StackableItem;
import com.redsquare.citizen.worldgen.World;
import com.redsquare.citizen.worldgen.WorldPosition;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class ItemEntity extends Entity {
  private final static int SPRITE_DIMENSION = 32;

  private final Item item;

  private boolean increasing;
  private double renderFloatHeight;

  private ItemEntity(Item item, World world) {
    Player player = world.getWorldManager().getPlayer();

    this.item = item;
    this.position = new WorldPosition(player.position().world(),
            new Point(10, 10), WorldPosition.randomizeWithinSubCell(),
            world, this);
    this.collider = Collider.getColliderFromType(Collider.EntityType.NO_COLLISION);

    this.increasing = Math.random() < 0.5;
    this.renderFloatHeight = Math.random();
  }

  // TODO - TEMPORARY

  public static ItemEntity itemEntityCreateTest(World world) {
    return new ItemEntity(StackableItem.testCreateItem(), world);
  }

  /**
   * Function makes item entities float up and down to indicate
   * that they can be interacted with; picked up
   * */
  private void floatingUpdate() {
    double increment = 0.03 + (0.03 * Math.abs(1. - renderFloatHeight));

    renderFloatHeight = increasing ?
            renderFloatHeight + increment :
            renderFloatHeight - increment;

    if (renderFloatHeight >= 1.)
      increasing = false;
    else if (renderFloatHeight <= 0.)
      increasing = true;
  }

  @Override
  public BufferedImage getSprite() {
    BufferedImage sprite = new BufferedImage(SPRITE_DIMENSION, SPRITE_DIMENSION,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) sprite.getGraphics();

    g.drawImage(item.getItemIcon(), 0, 0, SPRITE_DIMENSION, SPRITE_DIMENSION, null);

    return sprite;
  }

  @Override
  public Point getSpriteOffset() {
    return new Point((-1 * (Item.ICON_DIMENSION / 2)),
            -1 * (Item.ICON_DIMENSION / 2) - (int)(12 * renderFloatHeight));
  }

  @Override
  public void renderUpdate() {
    floatingUpdate();
  }
}
