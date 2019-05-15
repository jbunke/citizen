package com.redsquare.citizen.systems.language;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Glyph {

  static final int SIZE = GlyphLine.ARTICULATIONS + 2;
  private static final int LINE_WIDTH = 2;

  private final List<GlyphLine> lines;

  private Glyph(List<GlyphLine> commonElements, int maxDistance) {
    lines = buildLines(commonElements, maxDistance);
  }

  private Glyph() {
    lines = new ArrayList<>();
  }

  static Glyph generate(List<GlyphLine> commonElements, int maxDistance) {
    return new Glyph(commonElements, maxDistance);
  }

  static Glyph empty() {
    return new Glyph();
  }

  BufferedImage draw() {
    BufferedImage glyph = new BufferedImage(SIZE, SIZE,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) glyph.getGraphics();
    g.setColor(new Color(0, 0, 0));
    g.setStroke(new BasicStroke(LINE_WIDTH));

    for (GlyphLine line : lines) {
      g.drawLine(1 + line.from.x, 1 + line.from.y,
              1 + line.to.x, 1 + line.to.y);
    }

    return glyph;
  }

  private List<GlyphLine> buildLines(
          List<GlyphLine> commonElements, int maxDistance) {
    List<GlyphLine> lines = new ArrayList<>();

    // between 1 - 8 lines in glyph
    int lineCount = 3 + (int)(Math.random() * 4 *
            (GlyphLine.ARTICULATIONS / maxDistance));

    // start with common element?
    if (Math.random() < 0.9)
      lines.add(commonElements.get(
              (int)(Math.random() * commonElements.size())));

    // rest of lines
    for (int i = lines.size(); i < lineCount; i++) {
      boolean violates = true;
      GlyphLine candidate = null;

      while (violates) {
        violates = false;

        // following on or free
        if (Math.random() < 0.7 && !lines.isEmpty()) candidate =
                GlyphLine.followsFrom(lines.get(lines.size() - 1), maxDistance);
        else if (Math.random() < 0.95)
          candidate = commonElements.get(
                  (int)(Math.random() * commonElements.size()));
        else candidate = GlyphLine.random(maxDistance);

        if (lines.contains(candidate))
          violates = true;
      }

      lines.add(candidate);
    }

    return lines;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Glyph)) return false;

    Glyph comp = (Glyph) obj;

    for (GlyphLine cl : comp.lines) {
      if (!lines.contains(cl)) return false;
    }
    for (GlyphLine l : lines) {
      if (!comp.lines.contains(l)) return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return lines.size();
  }
}
