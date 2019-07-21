package com.redsquare.citizen.systems.structures;

import java.awt.*;

abstract class Building {
  static final int HOUSE_LENGTH = 14;
  static final int HOUSE_DEPTH = 8;

  private final Street street;
  final Point location;
  final Direction direction;

  Building(Street street, boolean lhs, int location) {
    this.street = street;

    Point streetStart = street.getParent().location;
    int x, y;

    switch (street.getDirection()) {
      case NORTH:
        if (lhs) {
          direction = Direction.EAST;
          x = streetStart.x - (HOUSE_DEPTH + 1);
        } else {
          direction = Direction.WEST;
          x = streetStart.x + 2;
        }
        y = streetStart.y - (Street.BLOCK_WIDTH * (location + 1)) + (Street.BLOCK_WIDTH - HOUSE_LENGTH) / 2;
        break;
      case SOUTH:
        if (lhs) {
          direction = Direction.WEST;
          x = streetStart.x + 2;
        } else {
          direction = Direction.EAST;
          x = streetStart.x - (HOUSE_DEPTH + 1);
        }
        y = streetStart.y + (Street.BLOCK_WIDTH * location) + (Street.BLOCK_WIDTH - HOUSE_LENGTH) / 2;
        break;
      case EAST:
        if (lhs) {
          direction = Direction.SOUTH;
          y = streetStart.y - (HOUSE_DEPTH + 1);
        } else {
          direction = Direction.NORTH;
          y = streetStart.y + 2;
        }
        x = streetStart.x + (Street.BLOCK_WIDTH * location) + (Street.BLOCK_WIDTH - HOUSE_LENGTH) / 2;
        break;
      case WEST:
      default:
        if (lhs) {
          direction = Direction.NORTH;
          y = streetStart.y + 2;
        } else {
          direction = Direction.SOUTH;
          y = streetStart.y - (HOUSE_DEPTH + 1);
        }
        x = streetStart.x - (Street.BLOCK_WIDTH * (location + 1)) + (Street.BLOCK_WIDTH - HOUSE_LENGTH) / 2;
        break;
    }

    this.location = new Point(x, y);
  }

  public enum Direction {
    NORTH, WEST, SOUTH, EAST
  }

  void draw(Graphics2D g) {
    draw(g, new Color(0, 0, 0));
  }

  void draw(Graphics2D g, Color c) {
    g.setColor(c);

    switch (direction) {
      case WEST:
      case EAST:
        g.fillRect(location.x, location.y, HOUSE_DEPTH, HOUSE_LENGTH);
        break;
      case NORTH:
      case SOUTH:
        g.fillRect(location.x, location.y, HOUSE_LENGTH, HOUSE_DEPTH);
        break;
    }
  }
}
