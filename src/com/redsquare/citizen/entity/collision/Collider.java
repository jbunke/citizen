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
    boolean immovable = false;

    // TODO
    boxes = new CollisionBox[] {
            // new CollisionBox(-25, -40, 50, 40)
            new CollisionBox(-28, -24, 56, 34),
            new CollisionBox(-22, -30, 44, 46)
    };

    return new Collider(boxes, immovable, type);
  }

  public enum EntityType {
    PERSON, WALL
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
