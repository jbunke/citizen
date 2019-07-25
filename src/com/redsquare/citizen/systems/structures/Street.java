package com.redsquare.citizen.systems.structures;

import com.redsquare.citizen.util.Randoms;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

class Street {
  static final int BLOCK_WIDTH = 3;

  private final Direction direction;
  private final int length;

  private final StreetNode parent;
  private final StreetNode child;

  private Purpose purpose;
  private int breadth;

  private final Set<Building> buildings;

  private Street(Direction direction, StreetNode parent) {
    this.direction = direction;
    double r = Math.random();
    this.length = (2 + (int)(Math.pow(r, 2) * 9)) * BLOCK_WIDTH;

    this.parent = parent;

    this.purpose = generatePurpose();

    boolean conflicts = conflicts();

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

  private Purpose generatePurpose() {
    // TODO - Flesh out
    int depth = parent.depth();
    int maxDepth = parent.layout.maxDepth;
    double prob = Math.random();

    if (depth < 2) {
      if (prob < 0.5) return Purpose.MARKET;

      return Purpose.RESIDENTIAL;
    } else if (depth < maxDepth - 3) {
      if (prob < 0.2) return Purpose.MARKET;

      return Purpose.RESIDENTIAL;
    }

    if (depth == maxDepth) return Purpose.RESIDENTIAL;

    return Purpose.RESIDENTIAL;
  }

  private boolean conflicts() {
    int x = 64 + ((192 - parent.location.x) / BLOCK_WIDTH);
    int y = 64 + ((192 - parent.location.y) / BLOCK_WIDTH);

    for (int i = 0; i <= length; i += BLOCK_WIDTH) {
      if (parent.layout.streetMap[x][y] && i > 0)
        return true;

      switch (direction) {
        case NORTH:
          y += 1;
          break;
        case EAST:
          x -= 1;
          break;
        case SOUTH:
          y -= 1;
          break;
        case WEST:
          x += 1;
          break;
      }
    }

    return false;
  }

  private void fillStreetMap() {
    int x = 64 + ((192 - parent.location.x) / BLOCK_WIDTH);
    int y = 64 + ((192 - parent.location.y) / BLOCK_WIDTH);

    for (int i = 0; i <= length; i += BLOCK_WIDTH) {
      parent.layout.streetMap[x][y] = true;

      switch (direction) {
        case NORTH:
          y += 1;
          break;
        case EAST:
          x -= 1;
          break;
        case WEST:
          x += 1;
          break;
        case SOUTH:
          y -= 1;
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

    int buildingsPerSide = length;

    switch (purpose) {
      case RESIDENTIAL:
        for (int i = 1; i < buildingsPerSide - 1; i++) {
          double prob = 0.7;
          if (Math.random() < prob) buildings.add(Residence.generate(this, true, i));
          if (Math.random() < prob) buildings.add(Residence.generate(this, false, i));
        }
        break;
      case MARKET:
        for (int i = 1; i < buildingsPerSide; i++) {
          switch (direction) {
            case WEST:
              buildings.add(Residence.generate(this, false, i));
              break;
            case EAST:
              buildings.add(MarketStall.generate(this, true, i));
              break;
            default:
              buildings.add(MarketStall.generate(this, true, i));
              buildings.add(MarketStall.generate(this, false, i));
              break;
          }
        }
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
    g.setStroke(new BasicStroke(1));

    g.drawLine(parent.location.x * 2, parent.location.y * 2,
            child.location.x * 2, child.location.y * 2);

    for (Building building : buildings) {
      building.draw(g);
    }

    child.draw(g);
  }
}
