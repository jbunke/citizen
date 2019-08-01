package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.util.FloatPoint;

import java.awt.*;

public class WorldPosition {
  public static final int CELLS_IN_WORLD_CELL_DIM = 384;
  public static final double CELL_DIMENSION_LENGTH = 200.;

  private Point world;
  private Point cell;
  private FloatPoint subCell;

  public WorldPosition(Point world, Point cell, FloatPoint subCell) {
    this.world = world;
    this.cell = cell;
    this.subCell = subCell;
  }

  public static WorldPosition copy(WorldPosition ref) {
    return new WorldPosition(ref.world, ref.cell, ref.subCell);
  }

  public static FloatPoint diff(WorldPosition a, WorldPosition b) {
    final double WORLD_CELL_LENGTH =
            CELLS_IN_WORLD_CELL_DIM * CELL_DIMENSION_LENGTH;

    double x = ((b.world.x - a.world.x) * WORLD_CELL_LENGTH) +
            ((b.cell.x - a.cell.x) * CELL_DIMENSION_LENGTH) +
            (b.subCell.x - a.subCell.x);
    double y = ((b.world.y - a.world.y) * WORLD_CELL_LENGTH) +
            ((b.cell.y - a.cell.y) * CELL_DIMENSION_LENGTH) +
            (b.subCell.y - a.subCell.y);

    return new FloatPoint(x, y);
  }

  public Point world() {
    return world;
  }

  public Point cell() {
    return cell;
  }

  public FloatPoint subCell() {
    return subCell;
  }

  public void move(double changeX, double changeY) {
    subCell = new FloatPoint(subCell.x + changeX, subCell.y + changeY);
    wrapFix();
  }

  private void wrapFix() {
    // X adjustment
    if (subCell.x < 0.) {
      subCell = new FloatPoint(CELL_DIMENSION_LENGTH + subCell.x, subCell.y);
      cell = new Point(cell.x - 1, cell.y);
      if (cell.x < 0) {
        cell = new Point(CELLS_IN_WORLD_CELL_DIM + cell.x, cell.y);
        world = world.x > 0 ? new Point(world.x - 1, world.y) : world;
      }
    } else if (subCell.x >= CELL_DIMENSION_LENGTH) {
      subCell = new FloatPoint(subCell.x - CELL_DIMENSION_LENGTH, subCell.y);
      cell = new Point(cell.x + 1, cell.y);
      if (cell.x >= CELLS_IN_WORLD_CELL_DIM) {
        cell = new Point(cell.x - CELLS_IN_WORLD_CELL_DIM, cell.y);
        world = new Point(world.x + 1, world.y);
      }
    }

    // Y adjustment
    if (subCell.y < 0.) {
      subCell = new FloatPoint(subCell.x, CELL_DIMENSION_LENGTH + subCell.y);
      cell = new Point(cell.x, cell.y - 1);
      if (cell.y < 0) {
        cell = new Point(cell.x, CELLS_IN_WORLD_CELL_DIM + cell.y);
        world = world.y > 0 ? new Point(world.x, world.y - 1) : world;
      }
    } else if (subCell.y >= CELL_DIMENSION_LENGTH) {
      subCell = new FloatPoint(subCell.x, subCell.y - CELL_DIMENSION_LENGTH);
      cell = new Point(cell.x, cell.y + 1);
      if (cell.y >= CELLS_IN_WORLD_CELL_DIM) {
        cell = new Point(cell.x, cell.y - CELLS_IN_WORLD_CELL_DIM);
        world = new Point(world.x, world.y + 1);
      }
    }
  }
}
