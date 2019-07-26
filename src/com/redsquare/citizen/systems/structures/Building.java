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
          x = streetStart.x - 1;
        } else {
          direction = Direction.WEST;
          x = streetStart.x + 1;
        }
        y = streetStart.y - location;
        break;
      case SOUTH:
        if (lhs) {
          direction = Direction.WEST;
          x = streetStart.x + 1;
        } else {
          direction = Direction.EAST;
          x = streetStart.x - 1;
        }
        y = streetStart.y + location;
        break;
      case EAST:
        if (lhs) {
          direction = Direction.SOUTH;
          y = streetStart.y - 1;
        } else {
          direction = Direction.NORTH;
          y = streetStart.y + 1;
        }
        x = streetStart.x + location;
        break;
      case WEST:
      default:
        if (lhs) {
          direction = Direction.NORTH;
          y = streetStart.y + 1;
        } else {
          direction = Direction.SOUTH;
          y = streetStart.y - 1;
        }
        x = streetStart.x - location;
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
    g.fillRect(location.x * 2, location.y * 2, 1, 1);
  }
}
