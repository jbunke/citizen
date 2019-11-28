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

  private Glyph(WritingSystem ws, boolean partial) {
    hasP = false;
    hasS = false;

    components = new ArrayList<>();

    if (partial)
      buildComponentsPartial(ws);
    else
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
    return new Glyph(ws, false);
  }

  static Glyph generate(List<GlyphComponent> components, boolean hasP, boolean hasS) {
    return new Glyph(components, hasP, hasS);
  }

  static Glyph empty() {
    return new Glyph();
  }

  static Glyph period() {
    List<GlyphPoint> points = new ArrayList<>();

    points.add(new GlyphPoint(0.3, 0.7));
    points.add(new GlyphPoint(0.35, 0.75));
    points.add(new GlyphPoint(0.3, 0.8));
    points.add(new GlyphPoint(0.25, 0.75));
    points.add(new GlyphPoint(0.3, 0.7));

    List<GlyphComponent> components = List.of(GlyphComponent.specified(points));

    return new Glyph(components, false, false);
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

  static Glyph generatePartial(WritingSystem ws) {
    return new Glyph(ws, true);
  }

  boolean hasP() {
    return hasP;
  }

  boolean hasS() {
    return hasS;
  }

  List<GlyphComponent> getComponents() {
    return components;
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
      if (ws.type == WritingSystem.Type.SYLLABARY) {

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

  private void additionalComponents(int additional, boolean connecting, WritingSystem ws) {
    final int TOTAL = components.size() + additional;
    boolean started = false;

    GlyphComponent last = !components.isEmpty() ? components.get(0) : null;

    while (components.size() < TOTAL) {
      GlyphComponent current = !started ?
              (connecting && last != null ?
                      GlyphComponent.continuing(last, true, ws) :
                      GlyphComponent.orig(ws)) :
              (Randoms.deviation(ws.avgContinuationProb,
                      ws.continuationDeviationMax) <= Math.random() || last.endsOutOfBounds()
                      ? GlyphComponent.orig(ws) :
                      (Math.random() < 0.5 ?
                              GlyphComponent.continuing(last, true, ws) :
                              GlyphComponent.continuing(last, false, ws)));

      if (current.endsOutOfBounds()) continue;

      started = true;
      components.add(current);
      last = current;
    }
  }

  private void regularComponentAssembly(WritingSystem ws) {
    int max = ws.type == WritingSystem.Type.SYLLABARY
            ? 4 : 5;

    int compCount = max - (int) Math.round(Math.pow(Math.random(), 2) * (max - 3));

    // common element
    if (Math.random() < ws.commonElemProbability)
      components.add(Sets.randomEntry(ws.commonElements));

    GlyphComponent last = !components.isEmpty() ? components.get(0) : null;

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

  private Glyph choosePartialForReflection(WritingSystem ws, boolean justReflecting) {
    Glyph partial = Sets.randomEntry(ws.partialStructures);
    boolean satisfied = false;

    while (!satisfied) {
      partial = Sets.randomEntry(ws.partialStructures);
      if (partial != null && (!justReflecting || !ws.partialsJustReflected.contains(partial)) &&
              partial.widthToHeight() < 40) satisfied = true;
    }

    return partial;
  }

  private Glyph setPartial(Glyph partial) {
    double widthToHeight = partial.widthToHeight();

    List<GlyphComponent> newCs = new ArrayList<>();

    for (GlyphComponent c : partial.components) {
      Function<Double, Double> xFunc = widthToHeight > 1 ? Glyph::identity : (d -> d * 0.7);
      Function<Double, Double> yFunc = widthToHeight > 1 ? (d -> d * 0.6) : Glyph::identity;

      newCs.add(GlyphComponent.copyAndScale(c, xFunc, yFunc));
    }

    return new Glyph(newCs, partial.hasP, partial.hasS);
  }

  private void reflectPartial(WritingSystem ws, boolean justReflected) {
    Glyph partial = choosePartialForReflection(ws, justReflected);
    if (justReflected) ws.partialsJustReflected.add(partial);

    double widthToHeight = partial.widthToHeight();

    List<GlyphComponent> newCs = new ArrayList<>();

    for (GlyphComponent c : partial.components) {
      newCs.add(c.reflected(widthToHeight > 1 ? 0 : 1));
    }

    Glyph reflection = new Glyph(newCs, partial.hasP, partial.hasS);

    double value = widthToHeight > 1 ? partial.maxY() : partial.maxX();
    value *= widthToHeight > 1 ? 0.6 : 0.7;

    List<GlyphComponent> ipcs = new ArrayList<>();
    List<GlyphComponent> ircs = new ArrayList<>();

    for (int i = 0; i < partial.components.size(); i++) {
      Function<Double, Double> xFunc = widthToHeight > 1 ? Glyph::identity : (d -> d * 0.7);
      Function<Double, Double> yFunc = widthToHeight > 1 ? (d -> d * 0.6) : Glyph::identity;

      ipcs.add(GlyphComponent.copyAndScale(partial.components.get(i), xFunc, yFunc));
      ircs.add(GlyphComponent.copyAndScale(reflection.components.get(i), xFunc, yFunc));
    }

    for (GlyphComponent ipc : ipcs) {
      double translateX = widthToHeight > 1 ? 0.0 : 0.7 - value;
      double translateY = widthToHeight > 1 ? 0.6 - value : 0.0;

      ipc.translate(translateX, translateY);
      components.add(ipc);
    }

    for (GlyphComponent irc : ircs) {
      double translateX = widthToHeight > 1 ? 0.0 : value;
      double translateY = widthToHeight > 1 ? value : 0.0;

      irc.translate(translateX, translateY);
      components.add(irc);
    }
  }

  private void reflectWithAdditional(WritingSystem ws) {
    reflectPartial(ws, false);
    additionalComponents(Randoms.bounded(1, 4), true, ws);
  }

  private void partialAndAdditional(WritingSystem ws) {
    Glyph partial = setPartial(Sets.randomEntry(ws.partialStructures));

    components.addAll(partial.components);
    additionalComponents(Randoms.bounded(1, 4), Math.random() < 0.5, ws);
  }

  private void buildComponents(WritingSystem ws) {
    /*
     * Options:
     * x Generate as before (20%)
     * - Use a partial as a starting point and have system add one to three components onto it (25%)
     * - Use a partial, find out whether it is longer or wider, combine it with another partial with the same
     * dominant dimension (15%)
     * x Use a partial, reflect it and combine it according to its dominant dimension,
     * add third component in the middle (15%)
     * x Use a partial and just reflect it (25%)
     * */
    double p = Math.random();

    if (p < 0.2)
      regularComponentAssembly(ws);
//    else if (p < 0.45)
//      something();
    else if (p < 0.6)
      partialAndAdditional(ws);
    else if (p < 0.75 || ws.partialsJustReflected.size() == ws.partialStructures.size())
      reflectWithAdditional(ws);
    else
      reflectPartial(ws, true);
  }

  private void buildComponentsPartial(WritingSystem ws) {
    int compCount = Randoms.bounded(2, 4);

    GlyphComponent last = null;

    while (components.size() < compCount) {
      GlyphComponent current = last == null ?
              GlyphComponent.orig(ws) :
              GlyphComponent.continuing(last, true, ws);

      if (current.endsOutOfBounds()) continue;

      components.add(current);
      last = current;
    }
  }

  private double widthToHeight() {
    return xSpan() / (ySpan() > 0 ? ySpan() : 0.01);
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
