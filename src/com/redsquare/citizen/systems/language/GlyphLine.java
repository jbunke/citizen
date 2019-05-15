package com.redsquare.citizen.systems.language;

import java.awt.*;

public class GlyphLine {
  static final int ARTICULATIONS = 17;

  final Point from;
  final Point to;

  static GlyphLine followsFrom(GlyphLine last, int maxDistance) {
    Point crit = last.to;
    Point opCrit = last.from;
    if (Math.random() < 0.2) {
      crit = last.from;
      opCrit = last.to;
    }

    Point next = new Point((int)(Math.random() * ARTICULATIONS),
            (int)(Math.random() * ARTICULATIONS));
    boolean violates = true;

    while (violates) {
      violates = false;

      int x = Math.random() < 0.5 ?
              crit.x :
              Math.random() < 0.5 ? (ARTICULATIONS - 1) - crit.x :
                      (int)(Math.random() * ARTICULATIONS);
      int y = Math.random() < 0.5 ?
              crit.y :
              Math.random() < 0.5 ? (ARTICULATIONS - 1) - crit.y :
                      (int)(Math.random() * ARTICULATIONS);

      next = new Point(x, y);

      if ((next.x == crit.x && next.y == crit.y) ||
              (next.x == opCrit.x && next.y == opCrit.x) ||
              Math.hypot(Math.abs(next.x - crit.x),
                      Math.abs(next.y - crit.y)) >
                      maxDistance)
        violates = true;
    }

    return new GlyphLine(crit, next);
  }

  static GlyphLine random(int maxDistance) {
    Point from = new Point((int)(Math.random() * ARTICULATIONS),
            (int)(Math.random() * ARTICULATIONS));

    Point to = new Point(0, 0);
    boolean violates = true;

    while (violates) {
      violates = false;
      to = new Point((int)(Math.random() * ARTICULATIONS),
              (int)(Math.random() * ARTICULATIONS));

      if (Math.hypot(Math.abs(from.x - to.x), Math.abs(from.y - to.y)) >
              (1.2 * ARTICULATIONS) ||
              from.equals(to) ||
              Math.hypot(Math.abs(from.x - to.x), Math.abs(from.y - to.y)) >
                      maxDistance) violates = true;
    }

    return new GlyphLine(from, to);
  }

  private GlyphLine(Point from, Point to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GlyphLine)) return false;

    GlyphLine comp = (GlyphLine) obj;

    return (from.equals(comp.from) && to.equals(comp.to)) ||
            (to.equals(comp.from) && from.equals(comp.to));
  }

  @Override
  public int hashCode() {
    return from.x + to.x + from.y + to.y;
  }
}
