package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.util.Randoms;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GlyphComponent {
  private static final double MAX_DIST_BETWEEN_POINTS = 0.02;
  private static final int DEGREES_IN_A_CIRCLE = 360;
  private static final int LINE_WIDTH = 2;
  private static final double DANGER_ZONE = 0.4;

  private final int initialDirection; // deg 0 - 359 (unit circle ->, counter-clockwise)
  private final int directionChange; // -5 - 5
  private final int amountPoints;
  private final double distanceBetween;
  private final List<GlyphPoint> points;

  private GlyphComponent(WritingSystem ws) {
    this.initialDirection = (ws.directionalProclivity > Math.random()
            ? Randoms.degreeDeviation(
            ws.prefDirection, ws.maxDirectionSkew)
            : Randoms.bounded(0, DEGREES_IN_A_CIRCLE));
    int scalarDir = (int) Math.round(Math.pow(Randoms.deviation(
            ws.avgLineCurve, ws.curveDeviationMax), 1.5) * 5);
    this.directionChange = Math.random() < 0.5 ? scalarDir : scalarDir * -1;

    this.distanceBetween = ws.avgLineLength *
            MAX_DIST_BETWEEN_POINTS * Randoms.bounded(0.3, 1d);

    this.amountPoints = Randoms.bounded(10, 120);
    this.points = new ArrayList<>();

    GlyphPoint startPoint =new GlyphPoint(Randoms.bounded(0.2, 0.8),
            Randoms.bounded(0.2, 0.8));
    this.points.add(startPoint);

    generateRestFrom(startPoint);
  }

  private GlyphComponent(GlyphComponent last, boolean directionChange,
                         WritingSystem ws) {
    this.initialDirection = directionChange ?
            (ws.directionalProclivity > Math.random()
                    ? Randoms.degreeDeviation(
                            ws.prefDirection, ws.maxDirectionSkew)
                    : Randoms.bounded(0, DEGREES_IN_A_CIRCLE)) :
            last.endDirection();

    int scalarDir = (int) Math.round(Math.pow(Randoms.deviation(
            ws.avgLineCurve, ws.curveDeviationMax), 1.5) * 5);
    this.directionChange = Math.random() < 0.5 ? scalarDir : scalarDir * -1;

    this.distanceBetween = ws.avgLineLength *
            MAX_DIST_BETWEEN_POINTS * Randoms.bounded(0.3, 1d);

    this.amountPoints = Randoms.bounded(10, 73);
    this.points = new ArrayList<>();

    GlyphPoint lastInLast = last.points.get(last.points.size() - 1);
    GlyphPoint startPoint = new GlyphPoint(lastInLast.x, lastInLast.y);
    this.points.add(startPoint);

    generateRestFrom(startPoint);
  }

  double minX() {
    double minX = 1d;

    for (GlyphPoint point : points) {
      minX = Math.min(minX, point.x);
    }

    return minX;
  }

  double maxX() {
    double maxX = 0d;

    for (GlyphPoint point : points) {
      maxX = Math.max(maxX, point.x);
    }

    return maxX;
  }

  double minY() {
    double minY = 1d;

    for (GlyphPoint point : points) {
      minY = Math.min(minY, point.y);
    }

    return minY;
  }

  double maxY() {
    double maxY = 0d;

    for (GlyphPoint point : points) {
      maxY = Math.max(maxY, point.y);
    }

    return maxY;
  }

  void translate(double translateX, double translateY) {
    for (GlyphPoint point : points) {
      point.x += translateX; point.y += translateY;
    }
  }

  private void generateRestFrom(GlyphPoint startPoint) {
    double x = startPoint.x;
    double y = startPoint.y;

    while (points.size() < amountPoints && Math.abs(x - 0.5) < DANGER_ZONE &&
            Math.abs(y - 0.5) < DANGER_ZONE) {
      int d = initialDirection +
              ((points.size() - 1) * directionChange);

      while (d < 0) d += DEGREES_IN_A_CIRCLE;
      while (d >= DEGREES_IN_A_CIRCLE) d -= DEGREES_IN_A_CIRCLE;

      double r = Math.toRadians(d);

      double x2 = x + (distanceBetween * Math.cos(r));
      double y2 = y + (distanceBetween * Math.sin(r));

      points.add(new GlyphPoint(x2, y2));
      x = x2; y = y2;
    }
  }

  static GlyphComponent orig(WritingSystem ws) {
    return new GlyphComponent(ws);
  }

  static GlyphComponent continuing(GlyphComponent last, boolean directionChange,
                            WritingSystem ws) {
    return new GlyphComponent(last, directionChange, ws);
  }

  private int endDirection() {
    int d = initialDirection * ((amountPoints - 1) * directionChange);

    while (d < 0) d += DEGREES_IN_A_CIRCLE;

    while (d >= DEGREES_IN_A_CIRCLE) d -= DEGREES_IN_A_CIRCLE;

    return d;
  }

  BufferedImage draw(int size) {
    BufferedImage comp = new BufferedImage(size, size,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) comp.getGraphics();

    g.setColor(new Color(0, 0, 0));
    g.setStroke(new BasicStroke(LINE_WIDTH));

    for (int i = 1; i < points.size(); i++) {
      Point from = new Point((int) (points.get(i - 1).x * size),
              (int) (points.get(i - 1).y * size));
      Point to = new Point((int) (points.get(i).x * size),
              (int) (points.get(i).y * size));

      g.drawLine(from.x, from.y, to.x, to.y);
    }

    return comp;
  }

  private class GlyphPoint {
    double x;
    double y;

    GlyphPoint(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }
}
