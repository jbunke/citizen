package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.entity.Entity;
import com.redsquare.citizen.util.FloatPoint;
import com.redsquare.citizen.util.MathExt;
import com.redsquare.citizen.util.Randoms;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class WorldPosition {
  public static final int CELLS_IN_WORLD_CELL_DIM = 50; // 384
  public static final double CELL_DIMENSION_LENGTH = 72.; // 72

  private final World world;
  private final Entity associated;

  private Point worldPos;
  private Point cellPos;
  private FloatPoint subCellPos;

  public WorldPosition(Point worldPos, Point cellPos, FloatPoint subCellPos,
                       World world, Entity associated) {
    this.worldPos = worldPos;
    this.cellPos = cellPos;
    this.subCellPos = subCellPos;

    this.world = world;
    this.associated = associated;

    if (isEntity())
      world.getCell(worldPos.x, worldPos.y).addEntity(associated);
  }

  public static FloatPoint randomizeWithinSubCell() {
    return new FloatPoint(
            Randoms.bounded(0, CELL_DIMENSION_LENGTH - 1.),
            Randoms.bounded(0, CELL_DIMENSION_LENGTH - 1.));
  }

  public static WorldPosition copy(WorldPosition ref) {
    return new WorldPosition(ref.worldPos, ref.cellPos,
            ref.subCellPos, ref.world, null);
  }

  public static FloatPoint diff(WorldPosition a, WorldPosition b) {
    final double WORLD_CELL_LENGTH =
            CELLS_IN_WORLD_CELL_DIM * CELL_DIMENSION_LENGTH;

    double x = ((b.worldPos.x - a.worldPos.x) * WORLD_CELL_LENGTH) +
            ((b.cellPos.x - a.cellPos.x) * CELL_DIMENSION_LENGTH) +
            (b.subCellPos.x - a.subCellPos.x);
    double y = ((b.worldPos.y - a.worldPos.y) * WORLD_CELL_LENGTH) +
            ((b.cellPos.y - a.cellPos.y) * CELL_DIMENSION_LENGTH) +
            (b.subCellPos.y - a.subCellPos.y);

    return new FloatPoint(x, y);
  }

  public World getWorld() {
    return world;
  }

  public Point world() {
    return worldPos;
  }

  public Point cell() {
    return cellPos;
  }

  public FloatPoint subCell() {
    return subCellPos;
  }

  public void move(double changeX, double changeY) {
    subCellPos = new FloatPoint(subCellPos.x + changeX, subCellPos.y + changeY);
    wrapFix();
  }

  public Set<Entity> getAllEntitiesWithinXCells(int cellsDistance) {
    Set<Entity> entities = new HashSet<>();
    Point thisPos = new Point((worldPos.x * CELLS_IN_WORLD_CELL_DIM) + cellPos.x,
            (worldPos.y * CELLS_IN_WORLD_CELL_DIM) + cellPos.y);

    int amount = (int)(Math.ceil(cellsDistance / (double)CELLS_IN_WORLD_CELL_DIM));

    for (int x = Math.max(0, worldPos.x - amount);
         x <= Math.min(world.getWidth() - 1, worldPos.x + amount); x++) {
      for (int y = Math.max(0, worldPos.y - amount);
           y <= Math.min(world.getHeight() - 1, worldPos.y + amount); y++) {
        Set<Entity> candidates = world.getCell(x, y).getEntities();

        for (Entity candidate : candidates) {
          Point w = candidate.position().worldPos;
          Point c = candidate.position().cellPos;
          Point position = new Point((w.x * CELLS_IN_WORLD_CELL_DIM) + c.x,
                  (w.y * CELLS_IN_WORLD_CELL_DIM) + c.y);

          if (MathExt.distance(thisPos, position) < cellsDistance)
            entities.add(candidate);
        }
      }
    }

    return entities;
  }

  private void wrapFix() {
    // X adjustment
    if (subCellPos.x < 0.) {
      subCellPos = new FloatPoint(CELL_DIMENSION_LENGTH + subCellPos.x, subCellPos.y);
      cellPos = new Point(cellPos.x - 1, cellPos.y);
      if (cellPos.x < 0) {
        cellPos = new Point(CELLS_IN_WORLD_CELL_DIM + cellPos.x, cellPos.y);

        // Remove entity from previously occupied world cell
        if (isEntity()) world.getCell(worldPos.x, worldPos.y).removeEntity(associated);

        worldPos = new Point(worldPos.x - 1, worldPos.y);

        if (isEntity()) world.getCell(worldPos.x, worldPos.y).addEntity(associated);
      }
    } else if (subCellPos.x >= CELL_DIMENSION_LENGTH) {
      subCellPos = new FloatPoint(subCellPos.x - CELL_DIMENSION_LENGTH, subCellPos.y);
      cellPos = new Point(cellPos.x + 1, cellPos.y);
      if (cellPos.x >= CELLS_IN_WORLD_CELL_DIM) {
        cellPos = new Point(cellPos.x - CELLS_IN_WORLD_CELL_DIM, cellPos.y);

        // Remove entity from previously occupied world cell
        if (isEntity()) world.getCell(worldPos.x, worldPos.y).removeEntity(associated);

        worldPos = new Point(worldPos.x + 1, worldPos.y);

        if (isEntity()) world.getCell(worldPos.x, worldPos.y).addEntity(associated);
      }
    }

    // Y adjustment
    if (subCellPos.y < 0.) {
      subCellPos = new FloatPoint(subCellPos.x, CELL_DIMENSION_LENGTH + subCellPos.y);
      cellPos = new Point(cellPos.x, cellPos.y - 1);
      if (cellPos.y < 0) {
        cellPos = new Point(cellPos.x, CELLS_IN_WORLD_CELL_DIM + cellPos.y);

        // Remove entity from previously occupied world cell
        if (isEntity()) world.getCell(worldPos.x, worldPos.y).removeEntity(associated);

        worldPos = new Point(worldPos.x, worldPos.y - 1);

        if (isEntity()) world.getCell(worldPos.x, worldPos.y).addEntity(associated);
      }
    } else if (subCellPos.y >= CELL_DIMENSION_LENGTH) {
      subCellPos = new FloatPoint(subCellPos.x, subCellPos.y - CELL_DIMENSION_LENGTH);
      cellPos = new Point(cellPos.x, cellPos.y + 1);
      if (cellPos.y >= CELLS_IN_WORLD_CELL_DIM) {
        cellPos = new Point(cellPos.x, cellPos.y - CELLS_IN_WORLD_CELL_DIM);

        // Remove entity from previously occupied world cell
        if (isEntity()) world.getCell(worldPos.x, worldPos.y).removeEntity(associated);

        worldPos = new Point(worldPos.x, worldPos.y + 1);

        if (isEntity()) world.getCell(worldPos.x, worldPos.y).addEntity(associated);
      }
    }
  }

  /** WORLD SCOPE: Used to see if people are within
   * walking distance of each other */
  public boolean isCloseTo(WorldPosition other) {
    return Math.abs(worldPos.x - other.worldPos.x) +
            Math.abs(worldPos.y - other.worldPos.y) <= 2;
  }

  private boolean isEntity() {
    return (world != null & associated != null);
  }

  @Override
  public String toString() {
    return "WORLD: (" + worldPos.x + ", " + worldPos.y + "); CELL: (" +
            cellPos.x + ", " + cellPos.y + "); SUB-CELL: (" +
            (int)subCellPos.x + ", " + (int)subCellPos.y + ")";
  }
}
