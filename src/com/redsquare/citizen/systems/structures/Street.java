package com.redsquare.citizen.systems.structures;

import com.redsquare.citizen.util.Randoms;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

class Street {
  static final int BLOCK_WIDTH = 30;

  private final Direction direction;
  private final int length;

  private final StreetNode parent;
  private final StreetNode child;

  private Purpose purpose;
  private int breadth;

  private final Set<Building> buildings;

  private Street(Direction direction, StreetNode parent) {
    this.direction = direction;
    this.length = Randoms.bounded(2, 6) * BLOCK_WIDTH;

    // TODO - not just residential
    this.purpose = Purpose.RESIDENTIAL;

    this.parent = parent;

    boolean conflicts = false; // conflicts();

    this.child = conflicts ? null : StreetNode.fromStreet(this);

    if (!conflicts) fillStreetMap();

    this.breadth = generateBreadth();
    this.buildings = !conflicts ? generateBuildings() : new HashSet<>();
  }

  static Street generate(Direction direction, StreetNode parent) {
    return new Street(direction, parent);
  }

  public enum Direction {
    NORTH, WEST, SOUTH, EAST
  }

  public enum Purpose {
    RESIDENTIAL, MARKET
  }

  private boolean conflicts() {
    int x = 49 + ((999 - parent.location.x) / BLOCK_WIDTH);
    int y = 49 + ((999 - parent.location.y) / BLOCK_WIDTH);

    for (int i = 0; i <= length; i += BLOCK_WIDTH) {
      if (parent.layout.streetMap[x][y] && i > 0)
        return true;

      switch (direction) {
        case NORTH:
          y -= 1;
          break;
        case EAST:
          x += 1;
          break;
        case SOUTH:
          y += 1;
          break;
        case WEST:
          x -= 1;
          break;
      }
    }

    return false;
  }

  private void fillStreetMap() {
    int x = 49 + ((999 - parent.location.x) / BLOCK_WIDTH);
    int y = 49 + ((999 - parent.location.y) / BLOCK_WIDTH);

    for (int i = 0; i <= length; i += BLOCK_WIDTH) {
      parent.layout.streetMap[x][y] = true;

      switch (direction) {
        case NORTH:
          y -= 1;
          break;
        case EAST:
          x += 1;
          break;
        case WEST:
          x -= 1;
          break;
        case SOUTH:
          y += 1;
          break;
      }
    }
  }

  private int generateBreadth() {
    int depth = parent.depth();

    if (depth < 2) return Randoms.bounded(3, 5);
    else if (depth < 4) return 2;
    else return 1;
  }

  private Set<Building> generateBuildings() {
    Set<Building> buildings = new HashSet<>();

    int buildingsPerSide = length / BLOCK_WIDTH;

    switch (purpose) {
      case RESIDENTIAL:
        for (int i = 0; i < buildingsPerSide; i++) {
          if (Math.random() < 0.7) buildings.add(Residence.generate(this, true, i));
          if (Math.random() < 0.7) buildings.add(Residence.generate(this, false, i));
        }
        break;
      case MARKET:
        break;
    }

    return buildings;
  }

  StreetNode getParent() {
    return parent;
  }

  StreetNode getChild() {
    return child;
  }

  Direction getDirection() {
    return direction;
  }

  int getLength() {
    return length;
  }

  void draw(Graphics2D g) {
    g.setColor(new Color(0, 0, 0));
    g.setStroke(new BasicStroke(breadth));

    g.drawLine(parent.location.x, parent.location.y,
            child.location.x, child.location.y);

    for (Building building : buildings) {
      building.draw(g);
    }

    child.draw(g);
  }
}
