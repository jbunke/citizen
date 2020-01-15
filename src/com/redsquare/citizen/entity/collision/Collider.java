package com.redsquare.citizen.entity.collision;

public class Collider {
  private static final int X = 0, Y = 1;

  final boolean immovable;
  private final EntityType type;
  private final CollisionBox[] boxes;

  private Collider(CollisionBox[] boxes, boolean immovable, EntityType type) {
    this.immovable = immovable;
    this.type = type;
    this.boxes = boxes;
  }

  public static Collider getColliderFromType(EntityType type) {
    CollisionBox[] boxes;
    boolean immovable;

    switch (type) {
      case PERSON:
        immovable = false;
        boxes = new CollisionBox[] {
                new CollisionBox(-22, -24, 44, 34)
        };
        break;
      case WALL:
        immovable = true;
        boxes = new CollisionBox[] {
                new CollisionBox(-36, -36, 72, 72)
        };
        break;
      case NO_COLLISION:
      default:
        immovable = true;
        boxes = new CollisionBox[] {};
        break;
    }

    return new Collider(boxes, immovable, type);
  }

  public enum EntityType {
    PERSON, WALL, NO_COLLISION
  }

  public static class CollisionBox {
    private int[] start;
    private int[] size;

    CollisionBox(int x, int y, int width, int height) {
      // start position is in reference to the entity position
      start = new int[] { x, y };

      size = new int[] { width, height };
    }

    public int[] getSize() {
      return size;
    }

    public int[] getStart() {
      return start;
    }
  }

  public CollisionBox[] getBoxes() {
    return boxes;
  }

  int minX() {
    int minX = Integer.MAX_VALUE;

    for (CollisionBox box : boxes) {
      minX = Math.min(minX, box.start[X]);
    }

    return minX;
  }

  int minY() {
    int minY = Integer.MAX_VALUE;

    for (CollisionBox box : boxes) {
      minY = Math.min(minY, box.start[Y]);
    }

    return minY;
  }

  int maxX() {
    int maxX = Integer.MIN_VALUE;

    for (CollisionBox box : boxes) {
      maxX = Math.max(maxX, box.start[X] + box.size[X]);
    }

    return maxX;
  }

  int maxY() {
    int maxY = Integer.MIN_VALUE;

    for (CollisionBox box : boxes) {
      maxY = Math.max(maxY, box.start[X] + box.size[X]);
    }

    return maxY;
  }
}
