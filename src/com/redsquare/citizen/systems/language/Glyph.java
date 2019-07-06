package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Glyph {

  private final List<GlyphComponent> components;

  private Glyph(WritingSystem ws) {
    components = new ArrayList<>();
    buildComponents(ws);
    centering();
  }

  private Glyph() {
    components = new ArrayList<>();
  }

  static Glyph generate(WritingSystem ws) {
    return new Glyph(ws);
  }

  static Glyph empty() {
    return new Glyph();
  }

  BufferedImage draw(int size) {
    BufferedImage glyph = new BufferedImage(size, size,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) glyph.getGraphics();

    for (GlyphComponent component : components) {
      g.drawImage(component.draw(size), 0, 0, null);
    }

    return glyph;
  }

  private void buildComponents(WritingSystem ws) {
    int compCount = Randoms.bounded(2, 8);

    // common element
    if (Math.random() < ws.commonElemProbability)
      components.add(Sets.randomEntry(ws.commonElements));

    GlyphComponent last = components.size() > 0 ? components.get(0) : null;

    while (components.size() < compCount ||
            (ySpan() < 0.4 && xSpan() < 0.4 &&
                    Math.random() > 0.25)) {
      GlyphComponent current = last == null ? GlyphComponent.orig(ws) :
              (Randoms.deviation(ws.avgContinuationProb,
                      ws.continuationDeviationMax) <= Math.random()
                      ? GlyphComponent.orig(ws) :
                      (Math.random() < 0.5 ?
                              GlyphComponent.continuing(last, true, ws) :
                              GlyphComponent.continuing(last, false, ws)));

      components.add(current);
      last = current;
    }
  }

  private double xSpan() {
    return maxX() - minX();
  }

  private double ySpan() {
    return maxY() - minY();
  }

  private double minX() {
    double minX = 1d;

    for (GlyphComponent component : components) {
      minX = Math.min(minX, component.minX());
    }

    return minX;
  }

  private double maxX() {
    double maxX = 0d;

    for (GlyphComponent component : components) {
      maxX = Math.max(maxX, component.maxX());
    }

    return maxX;
  }

  private double minY() {
    double minY = 1d;

    for (GlyphComponent component : components) {
      minY = Math.min(minY, component.minY());
    }

    return minY;
  }

  private double maxY() {
    double maxY = 0d;

    for (GlyphComponent component : components) {
      maxY = Math.max(maxY, component.maxY());
    }

    return maxY;
  }

  private void centering() {
    double idealMinX = 0.5 - ((maxX() - minX()) * 0.5);
    double idealMinY = 0.5 - ((maxY() - minY()) * 0.5);

    double translateX = idealMinX - minX();
    double translateY = idealMinY - minY();

    for (GlyphComponent component : components) {
      component.translate(translateX, translateY);
    }
  }
}
