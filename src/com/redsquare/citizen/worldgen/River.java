package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.systems.language.Word;
import com.redsquare.citizen.util.MathExt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

class River {

  private Word name;
  private List<RiverPoint> points = new ArrayList<>();
  private final Direction generalDirection;
  private Point central;

  River(Point origin, Point firstPoint) {
    Direction direction;

    if (firstPoint.x < origin.x) {
        if (firstPoint.y < origin.y) {
          direction = Direction.NW;
        } else if (firstPoint.y > origin.y) {
          direction = Direction.SW;
        } else
          direction = Direction.W;
    } else if (firstPoint.x > origin.x) {
      if (firstPoint.y < origin.y) {
        direction = Direction.NE;
      } else if (firstPoint.y > origin.y) {
        direction = Direction.SE;
      } else
        direction = Direction.E;
    } else
      if (firstPoint.y < origin.y) {
        direction = Direction.N;
      } else
        direction = Direction.S;

    generalDirection = direction;

    points.add(new RiverPoint(origin, direction, this));
    points.add(new RiverPoint(firstPoint, direction, this));
  }

  Word getName() {
    return name;
  }

  void setName(Word name) {
    this.name = name;
  }

  void setCentral() {
    central = MathExt.averagePoint(getPurePoints());
  }

  void addRiverPoint(RiverPoint rp) {
    points.add(rp);
  }

  private List<Point> getPurePoints() {
    List<Point> points = new ArrayList<>();

    for (RiverPoint rp : this.points)
      points.add(new Point(rp.point));

    return points;
  }

  Point getCentral() {
    return central;
  }

  List<RiverPoint> getRiverPoints() {
    return points;
  }

  RiverPoint generateNext() {
    RiverPoint last = points.get(points.size() - 1);

    int nextCode = last.flow.getCode() + directionModifier();

    if (nextCode < 0) nextCode += 8;
    if (nextCode > 7) nextCode -= 8;

    // Reset flow if river doubles back on initial flow
    if (Math.abs(generalDirection.code - nextCode) == 4)
      nextCode = generalDirection.code;

    Direction direction = Direction.fromCode(nextCode);
    Point ref = last.point;
    Point off = offset(direction);

    return new RiverPoint(
            new Point(ref.x + off.x, ref.y + off.y), direction, this);
  }

  private Point offset(Direction direction) {
    switch (direction) {
      case NW:
        return new Point(-1, -1);
      case N:
        return new Point(0, -1);
      case NE:
        return new Point(1, -1);
      case E:
        return new Point(1, 0);
      case SE:
        return new Point(1, 1);
      case S:
        return new Point(0, 1);
      case SW:
        return new Point(-1, 1);
      case W:
      default:
        return new Point(-1, 0);
    }
  }

  private int directionModifier() {
    final double THRESHOLD = 0.2;
    double modifier = Math.random();

    if (modifier < THRESHOLD) return -1;
    if (modifier > 1 - THRESHOLD) return 1;
    return 0;
  }

  enum Direction {
    NW(0), N(1), NE(2), E(3), SE(4), S(5), SW(6), W(7);

    private int code;

    Direction(int code) {
      this.code = code;
    }

    static Direction fromCode(int code) {
      switch (code) {
        case 0:
          return NW;
        case 1:
          return N;
        case 2:
          return NE;
        case 3:
          return E;
        case 4:
          return SE;
        case 5:
          return S;
        case 6:
          return SW;
        case 7:
        default:
          return W;
      }
    }

    int getCode() {
      return code;
    }
  }

  static class RiverPoint {
    final Point point;
    final Direction flow;
    final River river;

    RiverPoint(Point point, Direction flow, River river) {
      this.point = point;
      this.flow = flow;
      this.river = river;
    }
  }
}
