package com.redsquare.citizen.entity;

import com.redsquare.citizen.entity.collision.Collider;
import com.redsquare.citizen.graphics.Sprite;
import com.redsquare.citizen.worldgen.WorldPosition;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public abstract class Entity implements Comparable<Entity> {

  String ID;
  Sprite[] layers;
  WorldPosition position;
  Collider collider;

  static Set<Entity> filterEntitySet(Set<Entity> entities, final boolean OTHER_PEOPLE, final boolean PLAYER,
                                     final boolean ANIMALS, final boolean ITEMS) {
    Set<Entity> filtered = new HashSet<>();

    entities.forEach(x -> {
      if (PLAYER && x instanceof Player)
        filtered.add(x);
      else if (OTHER_PEOPLE && x instanceof Person)
        filtered.add(x);
      else if (ANIMALS && x instanceof Animal)
        filtered.add(x);
      else if (ITEMS && x instanceof ItemEntity)
        filtered.add(x);
    });

    return filtered;
  }

  public WorldPosition position() {
    return position;
  }

  public Collider collider() {
    return collider;
  }

  @Override
  public int compareTo(Entity other) {
    if (position.world().y < other.position.world().y) return -1;
    else if (position.world().y > other.position.world().y) return 1;
    else {
      if (position.cell().y < other.position.cell().y) return -1;
      else if (position.cell().y > other.position.cell().y) return 1;
      else return Double.compare(position.subCell().y,
                other.position.subCell().y);
    }
  }

  public abstract BufferedImage getSprite();

  public abstract Point getSpriteOffset();

  public void update() {  }

  public void renderUpdate() {  }

  public boolean spritesSetUp() {
    return layers != null;
  }
}
