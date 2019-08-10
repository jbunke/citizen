package com.redsquare.citizen.entity.collision;

import com.redsquare.citizen.entity.Entity;
import com.redsquare.citizen.worldgen.WorldPosition;

public class CollisionManager {
  private static final int X = 0, Y = 1;

  public static void check(Entity a, Entity b) {
    if (potentialCollision(a, b)) {
      collisionManagement(a, b);
    }
  }

  private static long[] getCoords(Entity e, Collider.CollisionBox b) {
    int comb = WorldPosition.CELLS_IN_WORLD_CELL_DIM * (int)WorldPosition.CELL_DIMENSION_LENGTH;
    long[] ref = new long[] { (e.position().world().x * comb) +
            (e.position().cell().x * (int)WorldPosition.CELL_DIMENSION_LENGTH) + (int)e.position().subCell().x,
            (e.position().world().y * comb) +
                    (e.position().cell().y * (int)WorldPosition.CELL_DIMENSION_LENGTH) +
                    (int)e.position().subCell().y };
    return new long[] {
            ref[X] + b.getStart()[X], ref[Y] + b.getStart()[Y],
            ref[X] + b.getStart()[X] + b.getSize()[X],
            ref[Y] + b.getStart()[Y] + b.getSize()[Y] };
  }

  private static long[] getCoords(Entity e) {
    long[] ref = new long[] { (e.position().world().x * WorldPosition.CELLS_IN_WORLD_CELL_DIM * (int)WorldPosition.CELL_DIMENSION_LENGTH) +
            (e.position().cell().x * (int)WorldPosition.CELL_DIMENSION_LENGTH) + (int)e.position().subCell().x,
            (e.position().world().y * WorldPosition.CELLS_IN_WORLD_CELL_DIM * (int)WorldPosition.CELL_DIMENSION_LENGTH) +
                    (e.position().cell().y * (int)WorldPosition.CELL_DIMENSION_LENGTH) +
                    (int)e.position().subCell().y };
    return new long[] {
            ref[X] + e.collider().minX(), ref[Y] + e.collider().minY(),
            ref[X] + e.collider().maxX(), ref[Y] + e.collider().maxY() };
  }

  private static boolean colliding(long[] a, long[] b) {
    final int MIN_X = 0, MIN_Y = 1, MAX_X = 2, MAX_Y = 3;

    return ((a[MIN_X] >= b[MIN_X] && a[MIN_X] <= b[MAX_X]) ||
            (b[MIN_X] >= a[MIN_X] && b[MIN_X] <= a[MAX_X])) &&
            ((a[MIN_Y] >= b[MIN_Y] && a[MIN_Y] <= b[MAX_Y]) ||
            (b[MIN_Y] >= a[MIN_Y] && b[MIN_Y] <= a[MAX_Y]));
  }

  private static boolean potentialCollision(Entity a, Entity b) {
    long[] aCoords = getCoords(a);
    long[] bCoords = getCoords(b);

    return colliding(aCoords, bCoords);
  }

  private static void collisionManagement(Entity a, Entity b) {
    Collider.CollisionBox[] aBoxes = a.collider().getBoxes();
    Collider.CollisionBox[] bBoxes = b.collider().getBoxes();

    for (Collider.CollisionBox aBox : aBoxes) {
      for (Collider.CollisionBox bBox : bBoxes) {
        // minX, minY, maxX, maxY
        final int MIN_X = 0, MIN_Y = 1, MAX_X = 2, MAX_Y = 3;

        long[] aCoords = getCoords(a, aBox);
        long[] bCoords = getCoords(b, bBox);

        while (colliding(aCoords, bCoords)) {
          aCoords = getCoords(a, aBox);
          bCoords = getCoords(b, bBox);

          // X dim
          if (aCoords[MIN_X] <= bCoords[MIN_X] &&
                  aCoords[MAX_X] >= bCoords[MIN_X]) {
            if (!a.collider().immovable)
              a.position().move(-1., 0.);
            if (!b.collider().immovable)
              b.position().move(1., 0.);
          } else if (bCoords[MIN_X] <= aCoords[MIN_X] &&
                  bCoords[MAX_X] >= aCoords[MIN_X]) {
            if (!a.collider().immovable)
              a.position().move(1., 0.);
            if (!b.collider().immovable)
              b.position().move(-1., 0.);
          }

          // Y dim
          if (aCoords[MIN_Y] <= bCoords[MIN_Y] &&
                  aCoords[MAX_Y] >= bCoords[MIN_Y]) {
            if (!a.collider().immovable)
              a.position().move(0., -1.);
            if (!b.collider().immovable)
              b.position().move(0., 1.);
          } else if (bCoords[MIN_Y] <= aCoords[MIN_Y] &&
                  bCoords[MAX_Y] >= aCoords[MIN_Y]) {
            if (!a.collider().immovable)
              a.position().move(0., 1.);
            if (!b.collider().immovable)
              b.position().move(0., -1.);
          }
        }
      }
    }
  }
}
