package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

class Glyph {

  private final List<GlyphComponent> components;

  private final boolean hasP;
  private final boolean hasS;

  private Glyph(WritingSystem ws) {
    hasP = false;
    hasS = false;

    components = new ArrayList<>();
    buildComponents(ws);
    centering();
  }

  private Glyph() {
    hasP = false;
    hasS = false;

    components = new ArrayList<>();
  }

  private Glyph(List<GlyphComponent> components, boolean hasP, boolean hasS) {
    this.hasP = hasP;
    this.hasS = hasS;

    this.components = components;
  }

  static Glyph generate(WritingSystem ws) {
    return new Glyph(ws);
  }

  static Glyph componentBased(WritingSystem ws, Glyph v, Glyph p, Glyph s) {
    List<GlyphComponent> components = new ArrayList<>();
    List<GlyphComponent> vowels = new ArrayList<>();
    List<GlyphComponent> prefixes = new ArrayList<>();
    List<GlyphComponent> suffixes = new ArrayList<>();

    Function<Double, Double> vXFunc = Glyph::identity;
    Function<Double, Double> vYFunc = Glyph::identity;
    Function<Double, Double> pXFunc = Glyph::identity;
    Function<Double, Double> pYFunc = Glyph::identity;
    Function<Double, Double> sXFunc = Glyph::identity;
    Function<Double, Double> sYFunc = Glyph::identity;

    switch (ws.compSyllabaryConfig) {
      case PS_ABOVE_V:
        if (s != null) pXFunc = Glyph::halve;
        pYFunc = Glyph::halve;

        if (p != null) sXFunc = Glyph::halveAndOffset;
        sYFunc = Glyph::halve;

        vYFunc = Glyph::halveAndOffset;

        break;
      case PVS_LTR:
        if (p == null && s == null) {
          pXFunc = Glyph::identity;

          vXFunc = Glyph::identity;

          sXFunc = Glyph::identity;
        } else if (p == null) {
          vXFunc = Glyph::halve;

          sXFunc = Glyph::halveAndOffset;
        } else if (s == null) {
          pXFunc = Glyph::halve;

          vXFunc = Glyph::halveAndOffset;
        } else {
          pXFunc = Glyph::halveAndNudgeBack;

          vXFunc = Glyph::halveAndCenter;

          sXFunc = Glyph::halveAndPushForth;
        }
        break;
      case PVS_TTB:
      default:
        if (p == null && s != null) {
          vYFunc = Glyph::halve;

          sYFunc = Glyph::halveAndOffset;
        } else if (s == null && p != null) {
          pYFunc = Glyph::halve;

          vYFunc = Glyph::halveAndOffset;
        } else {
          pYFunc = Glyph::halveAndNudgeBack;

          vYFunc = Glyph::halveAndCenter;

          sYFunc = Glyph::halveAndPushForth;
        }
        break;
    }

    // Vowels
    for (GlyphComponent vComp : v.components) {
      vowels.add(GlyphComponent.copyAndScale(vComp, vXFunc, vYFunc));
    }

    // Prefixes
    if (p != null) {
      for (GlyphComponent pComp : p.components) {
        prefixes.add(GlyphComponent.copyAndScale(pComp, pXFunc, pYFunc));
      }
    }

    // Suffixes
    if (s != null) {
      for (GlyphComponent sComp : s.components) {
        suffixes.add(GlyphComponent.copyAndScale(sComp, sXFunc, sYFunc));
      }
    }

    if (ws.compSyllabaryConnected) {
      if (!prefixes.isEmpty())
        components.add(GlyphComponent.connector(
                prefixes.get(prefixes.size() - 1), vowels.get(0)));
      if (!suffixes.isEmpty())
        components.add(GlyphComponent.connector(
                vowels.get(vowels.size() - 1), suffixes.get(0)));
    }

    components.addAll(vowels);
    components.addAll(prefixes);
    components.addAll(suffixes);

    return new Glyph(components, p != null, s != null);
  }

  static Glyph empty() {
    return new Glyph();
  }

  private static double halveAndPushForth(double d) {
    return (d * 0.4) + 0.65;
  }

  private static double halveAndNudgeBack(double d) {
    return (d * 0.4) - 0.05;
  }

  private static double halveAndCenter(double d) {
    return (d * 0.4) + 0.3;
  }

  private static double halveAndOffset(double d) {
    return (d * 0.5) + 0.45;
  }

  private static double halve(double d) {
    return (d * 0.5) + 0.05;
  }

  private static double identity(double d) {
    return d;
  }

  BufferedImage drawWithFont(int size, int startWidth, int endWidth,
                             BiFunction<Double, Double, Double> xFunc,
                             BiFunction<Double, Double, Double> yFunc) {
    BufferedImage glyph = new BufferedImage(size, size,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) glyph.getGraphics();

    for (GlyphComponent component : components) {
      g.drawImage(component.drawWithFont(size, startWidth, endWidth,
              xFunc, yFunc), 0, 0, null);
    }

    return glyph;
  }

  BufferedImage draw(int size, boolean debug, WritingSystem ws) {
    BufferedImage glyph = new BufferedImage(size, size,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) glyph.getGraphics();

    if (debug) {
      // debugging info
      if (ws.type == WritingSystem.Type.COMPONENT_SYLLABARY) {

        BufferedImage v =
                new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        BufferedImage p =
                new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        BufferedImage s =
                new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D vg = (Graphics2D) v.getGraphics();
        vg.setColor(new Color(255, 0, 0, 100));
        vg.fillRect(0, 0, size, size);

        Graphics2D pg = (Graphics2D) p.getGraphics();
        pg.setColor(new Color(0, 255, 0, 100));
        pg.fillRect(0, 0, size, size);

        Graphics2D sg = (Graphics2D) s.getGraphics();
        sg.setColor(new Color(0, 0, 255, 100));
        sg.fillRect(0, 0, size, size);


        switch (ws.compSyllabaryConfig) {
          case PVS_TTB:
            if (hasP && hasS) {
              g.drawImage(p, 0, 0, size, size / 3, null);
              g.drawImage(v, 0, size / 3, size, size / 3, null);
              g.drawImage(s, 0, (size * 2) / 3, size, size / 3, null);
            } else if (hasP) {
              g.drawImage(p, 0, 0, size, size / 2, null);
              g.drawImage(v, 0, size / 2, size, size / 2, null);
            } else if (hasS) {
              g.drawImage(v, 0, 0, size, size / 2, null);
              g.drawImage(s, 0, size / 2, size, size / 2, null);
            } else {
              g.drawImage(v, 0, 0, null);
            }
            break;
          case PVS_LTR:
            if (hasP && hasS) {
              g.drawImage(p, 0, 0, size / 3, size, null);
              g.drawImage(v, size / 3, 0, size / 3, size, null);
              g.drawImage(s, (size * 2) / 3, 0, size / 3, size, null);
            } else if (hasP) {
              g.drawImage(p, 0, 0, size / 2, size, null);
              g.drawImage(v, size / 2, 0, size / 2, size, null);
            } else if (hasS) {
              g.drawImage(v, 0, 0, size / 2, size, null);
              g.drawImage(s, size / 2, 0, size / 2, size, null);
            } else {
              g.drawImage(v, 0, 0, null);
            }
            break;
          case PS_ABOVE_V:
            if (hasP && hasS) {
              g.drawImage(p, 0, 0, size / 2, size / 2, null);
              g.drawImage(s, size / 2, 0, size / 2, size / 2, null);
            } else {
              if (hasP) {
                g.drawImage(p, 0, 0, size, size / 2, null);
              } else if (hasS) {
                g.drawImage(s, 0, 0, size, size / 2, null);
              }
            }

            g.drawImage(v, 0, size / 2, size, size / 2, null);
            break;
        }
      } else {
        g.setColor(new Color(255, 150, 0, 100));
        g.fillRect(size / 10, 0, (int)(size * (9 / 10d)), size);
      }

      g.setColor(new Color(255, 255, 0, 100));
      g.fillRect(0, 0, size / 10, size);
    }

    for (GlyphComponent component : components) {
      g.drawImage(component.draw(size), 0, 0, null);
    }

    return glyph;
  }

  private void buildComponents(WritingSystem ws) {
    int max = ws.type == WritingSystem.Type.COMPONENT_SYLLABARY
            ? 4 : 5;

    int compCount = max - (int) Math.round(Math.pow(Math.random(), 2) * (max - 3));

    // common element
    if (Math.random() < ws.commonElemProbability)
      components.add(Sets.randomEntry(ws.commonElements));

    GlyphComponent last = components.size() > 0 ? components.get(0) : null;

    while (components.size() < compCount ||
            (ySpan() < 0.4 && xSpan() < 0.4 &&
                    Math.random() > 0.25)) {
      GlyphComponent current = last == null ? GlyphComponent.orig(ws) :
              (Randoms.deviation(ws.avgContinuationProb,
                      ws.continuationDeviationMax) <= Math.random() || last.endsOutOfBounds()
                      ? GlyphComponent.orig(ws) :
                      (Math.random() < 0.5 ?
                              GlyphComponent.continuing(last, true, ws) :
                              GlyphComponent.continuing(last, false, ws)));

      if (current.endsOutOfBounds()) continue;

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
