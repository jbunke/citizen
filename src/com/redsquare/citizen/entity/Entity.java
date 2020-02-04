package com.redsquare.citizen.entity;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.entity.collision.Collider;
import com.redsquare.citizen.graphics.Sprite;
import com.redsquare.citizen.worldgen.WorldPosition;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public abstract class Entity implements Comparable<Entity> {

  protected String ID;
  protected Sprite[] layers;
  protected WorldPosition position;
  protected Collider collider;

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

  public void drawCollision(Graphics2D g) {
    if (GameDebug.isActive()) {
      g.setColor(new Color(100, 255, 0));
      g.setStroke(new BasicStroke(1));

      Collider.CollisionBox[] boxes = collider.getBoxes();
      Point offset = getSpriteOffset();

      for (Collider.CollisionBox box : boxes) {
        int x = (box.getStart()[0] - offset.x),
                y = (box.getStart()[1] - offset.y);

        g.drawRect(x, y, box.getSize()[0], box.getSize()[1]);
      }
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
