package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;

public class WritingSystem {

  private static final Set<Set<Integer>> directionSets = Set.of(
          Set.of(0, 90, 180, 270), Set.of(45, 135, 225, 315),
          Set.of(60, 120, 240, 300), Set.of(0), Set.of(90, 210, 330)
  );

  private static final Set<Set<double[]>> startPointSets = Set.of(
          Set.of(new double[] { 0.5, 0.2 }, new double[] { 0.5, 0.5 },
                  new double[] { 0.5, 0.8 }, new double[] { 0.2, 0.5 },
                  new double[] { 0.8, 0.5 } ),
          Set.of(new double[] { 0.25, 0.25 }, new double[] { 0.25, 0.75 },
                  new double[] { 0.75, 0.25 }, new double[] { 0.75, 0.75 } ),
          Set.of(new double[] { 0.5, 0.2 }, new double[] { 0.5, 0.8 },
                  new double[] { 0.2, 0.5 }, new double[] { 0.8, 0.5 } )
  );

  final Phonology phonology;
  private final Map<WordSubUnit, Glyph> glyphs;
  private final List<WordSubUnit> keys;
  final Type type;

  // VISUAL CRITERIA
  final double avgLineCurve; // 0 - 0.6 skewed ^2
  final double curveDeviationMax; // 0.1 - 0.6
  final double deviationProb; // 0 - 1
  final double avgLineLength; // 0 - 1
  final double avgContinuationProb; // 0.5 - 1
  final double continuationDeviationMax; // 0 - 0.5
  final double commonElemProbability; // 0 - 1

  final double directionalProclivity; // 0 - 1
  final Set<Integer> directionSet;
  final int maxDirectionSkew; // 0 - 30

  final double startPointProclivity; // 0.5 - 1
  final Set<double[]> startPoints;

  final boolean compSyllabaryConnected;
  final CompSyllabaryConfig compSyllabaryConfig;

  final Set<GlyphComponent> commonElements;
  final Set<Glyph> partialStructures;

  Set<Glyph> partialsJustReflected = new HashSet<>();
  Map<Glyph, List<Glyph>> combinedPartials = new HashMap<>();

  private WritingSystem(WritingSystem parent) {
    this.phonology = parent.phonology;
    this.type = parent.type;

    avgLineCurve = parent.avgLineCurve;
    avgLineLength = parent.avgLineLength;
    curveDeviationMax = parent.curveDeviationMax;
    deviationProb = parent.deviationProb;
    avgContinuationProb = parent.avgContinuationProb;
    continuationDeviationMax = parent.continuationDeviationMax;
    commonElemProbability = parent.commonElemProbability;

    directionalProclivity = parent.directionalProclivity;
    directionSet = parent.directionSet;
    maxDirectionSkew = parent.maxDirectionSkew;

    startPointProclivity = parent.startPointProclivity;
    startPoints = parent.startPoints;

    compSyllabaryConnected =
            Math.random() > 0.7 == parent.compSyllabaryConnected;

    compSyllabaryConfig = parent.compSyllabaryConfig;

    Set<GlyphComponent> parentalCE = parent.commonElements;
    commonElements = new HashSet<>();

    while (commonElements.size() < parentalCE.size()) {
      commonElements.add(GlyphComponent.orig(this));
    }

    partialStructures = parent.partialStructures;

    keys = enumerateKeys();
    sortKeys();

    glyphs = generateGlyphsFromParent(parent, parentalCE);

    partialsJustReflected = null;
    combinedPartials = null;
  }

  private WritingSystem(Phonology phonology, Type type) {
    // Random likelihood of syllabic vs alphabetical
    this.phonology = phonology;
    this.type = type;

    avgLineCurve = Math.random() < 0.5 ? Randoms.bounded(0.0, 0.2) : Randoms.bounded(0.35, 0.5);
    avgLineLength = Randoms.bounded(0.4, 1d);
    curveDeviationMax = Randoms.bounded(0.2, 0.4);
    deviationProb = Math.random();
    avgContinuationProb = 1 - Math.pow(Randoms.bounded(0d, Math.sqrt(0.5)), 2);
    continuationDeviationMax = Math.pow(Randoms.bounded(0d, 0.5), 2);
    commonElemProbability = Math.random();

    directionalProclivity = Math.random();
    directionSet = Sets.randomEntry(directionSets);
    maxDirectionSkew = Randoms.bounded(0, 30);

    startPointProclivity = Randoms.bounded(0.5, 1.0);
    startPoints = Sets.randomEntry(startPointSets);

    compSyllabaryConnected = Math.random() < 0.5;

    double prob = Math.random();

    if (prob < 1/3f) compSyllabaryConfig = CompSyllabaryConfig.PS_ABOVE_V;
    else if (prob < 2/3f) compSyllabaryConfig = CompSyllabaryConfig.PVS_LTR;
    else compSyllabaryConfig = CompSyllabaryConfig.PVS_TTB;

    int amountCommonElements = Randoms.bounded(4, 7);
    commonElements = new HashSet<>();

    while (commonElements.size() < amountCommonElements) {
      commonElements.add(GlyphComponent.orig(this));
    }

    int amountPartialStructures = Randoms.bounded(5, 9);
    partialStructures = new HashSet<>();

    while (partialStructures.size() < amountPartialStructures) {
      Glyph partial = Glyph.generatePartial(this);
      partialStructures.add(partial);
      combinedPartials.put(partial, new ArrayList<>());
    }

    // populate and sort keys
    keys = enumerateKeys();
    sortKeys();

    glyphs = generateGlyphs();

    partialsJustReflected = null;
    combinedPartials = null;
  }

  private WritingSystem(Phonology phonology, Type type,
                        double avgLineCurve, double curveDeviationMax,
                        double deviationProb,
                        double avgLineLength, double avgContinuationProb,
                        double continuationDeviationMax,
                        double commonElemProbability,
                        double directionalProclivity,
                        Set<Integer> directionSet, int maxDirectionSkew,
                        double startPointProclivity,
                        Set<double[]> startPoints,
                        boolean compSyllabaryConnected,
                        CompSyllabaryConfig compSyllabaryConfig) {
    this.phonology = phonology;
    this.type = type;

    this.avgLineCurve = avgLineCurve;
    this.avgLineLength = avgLineLength;
    this.curveDeviationMax = curveDeviationMax;
    this.deviationProb = deviationProb;
    this.avgContinuationProb = avgContinuationProb;
    this.continuationDeviationMax = continuationDeviationMax;
    this.commonElemProbability = commonElemProbability;

    this.directionalProclivity = directionalProclivity;
    this.directionSet = directionSet;
    this.maxDirectionSkew = maxDirectionSkew;

    this.startPointProclivity = startPointProclivity;
    this.startPoints = startPoints;

    this.compSyllabaryConnected = compSyllabaryConnected;

    this.compSyllabaryConfig = compSyllabaryConfig;

    int amountCommonElements = Randoms.bounded(4, 7);
    commonElements = new HashSet<>();

    while (commonElements.size() < amountCommonElements) {
      commonElements.add(GlyphComponent.orig(this));
    }

    int amountPartialStructures = Randoms.bounded(5, 9);
    partialStructures = new HashSet<>();

    while (partialStructures.size() < amountPartialStructures) {
      Glyph partial = Glyph.generatePartial(this);
      partialStructures.add(partial);
      combinedPartials.put(partial, new ArrayList<>());
    }
    // populate and sort keys
    keys = enumerateKeys();
    sortKeys();

    glyphs = generateGlyphs();

    partialsJustReflected = null;
    combinedPartials = null;
  }

  WritingSystem modify() {
    return new WritingSystem(this);
  }

  public static WritingSystem generate(Phonology phonology, Type type,
        double avgLineCurve, double curveDeviationMax, double deviationProb,
        double avgLineLength, double avgContinuationProb,
        double continuationDeviationMax, double commonElemProbability,
        double directionalProclivity, Set<Integer> directionSet, int maxDirectionSkew,
        double startPointProclivity, Set<double[]> startPoints,
        boolean compSyllabaryConnected, CompSyllabaryConfig compSyllabaryConfig) {

    return new WritingSystem(phonology, type, avgLineCurve, curveDeviationMax,
            deviationProb, avgLineLength, avgContinuationProb, continuationDeviationMax,
            commonElemProbability, directionalProclivity, directionSet,
            maxDirectionSkew, startPointProclivity, startPoints,
            compSyllabaryConnected, compSyllabaryConfig);
  }

  public static WritingSystem generate(Phonology phonology) {
    Type type;

    double p = Math.random();

    if (p < 2/3f) type = Type.ALPHABET;
    else type = Type.SYLLABARY;

    return new WritingSystem(phonology, type);
  }

  public static WritingSystem generate(Phonology phonology,
                                       Type type) {
    return new WritingSystem(phonology, type);
  }

  public enum Type {
    ALPHABET, SYLLABARY
  }

  public enum CompSyllabaryConfig {
    PS_ABOVE_V, PVS_LTR, PVS_TTB
  }

  List<WordSubUnit> getKeys() {
    return keys;
  }

  Glyph getGlyph(WordSubUnit key) {
    return glyphs.getOrDefault(key, Glyph.empty());
  }

  private void sortKeys() {
    keys.sort(Comparator.comparingInt((WordSubUnit w) -> w.toString().length()));
  }

  private Map<WordSubUnit, Glyph> generateGlyphsFromParent(WritingSystem parent,
                                                           Set<GlyphComponent> parentalCE) {
    Map<WordSubUnit, Glyph> glyphs = new HashMap<>();

    for (WordSubUnit key : keys) {
      Glyph parentGlyph = parent.glyphs.get(key);
      List<GlyphComponent> components = parentGlyph.getComponents();
      List<GlyphComponent> newComps = new ArrayList<>();

      for (GlyphComponent component : components) {
        if (parentalCE.contains(component)) {
          newComps.add(Sets.randomEntry(commonElements));
        } else {
          newComps.add(component);
        }
      }

      Glyph newG = Glyph.generate(newComps, parentGlyph.hasP(), parentGlyph.hasS());
      glyphs.put(key, newG);
    }

    return glyphs;
  }

  private Map<WordSubUnit, Glyph> generateGlyphs() {
    Map<WordSubUnit, Glyph> glyphs = new HashMap<>();

    Map<Phoneme, Glyph> phonemes = new HashMap<>();

    if (this.type == Type.SYLLABARY) {
      for (String v : phonology.VOWEL_PHONEMES) {
        Phoneme vowel = new Phoneme(v);
        phonemes.put(vowel, Glyph.generate(this));
      }

      for (String p : phonology.PREFIX_CONS_PHONEMES) {
        Phoneme prefix = new Phoneme(p);
        phonemes.put(prefix, Glyph.generate(this));
      }

      for (String s : phonology.SUFFIX_CONS_PHONEMES) {
        Phoneme suffix = new Phoneme(s);
        phonemes.put(suffix, Glyph.generate(this));
      }
    }

    for (WordSubUnit key : keys) {
      Glyph candidate;

      if (key.equals(new Phoneme(" "))) continue;

      if (type == Type.SYLLABARY) {
        Glyph vGlyph = phonemes.get(new Phoneme(((Syllable) key).getVowel()));
        Glyph pGlyph = phonemes.get(new Phoneme(((Syllable) key).getPrefix()));
        Glyph sGlyph = phonemes.get(new Phoneme(((Syllable) key).getSuffix()));

        candidate = Glyph.componentBased(this, vGlyph, pGlyph, sGlyph);
      } else {
        candidate = Glyph.generate(this);
      }

      // generate glyph
      glyphs.put(key, candidate);
    }

    glyphs.put(new Phoneme(" "), Glyph.empty());

    return glyphs;
  }

  private List<WordSubUnit> enumerateKeys() {
    List<WordSubUnit> keys = new ArrayList<>();

    switch (type) {
      case SYLLABARY:
        Set<Syllable> vowelsOnly = new HashSet<>();
        Set<Syllable> prefixVowel = new HashSet<>();
        Set<Syllable> vowelSuffix = new HashSet<>();
        Set<Syllable> prefixVowelSuffix = new HashSet<>();

        for (String vowel : phonology.VOWEL_PHONEMES) {
          vowelsOnly.add(new Syllable("", vowel, ""));

          for (String prefix : phonology.PREFIX_CONS_PHONEMES) {
            prefixVowel.add(new Syllable(prefix, vowel, ""));

            for (String suffix : phonology.SUFFIX_CONS_PHONEMES) {
              prefixVowelSuffix.add(new Syllable(prefix, vowel, suffix));
            }
          }

          for (String suffix : phonology.SUFFIX_CONS_PHONEMES) {
            vowelSuffix.add(new Syllable("", vowel, suffix));
          }
        }

        keys.addAll(vowelsOnly);
        keys.addAll(prefixVowel);
        keys.addAll(prefixVowelSuffix);
        keys.addAll(vowelSuffix);
        break;
      case ALPHABET:
        for (String vowel : phonology.VOWEL_PHONEMES)
          if (!keys.contains(new Phoneme(vowel))) keys.add(new Phoneme(vowel));
        for (String prefix : phonology.PREFIX_CONS_PHONEMES)
          if (!keys.contains(new Phoneme(prefix))) keys.add(new Phoneme(prefix));
        for (String suffix : phonology.SUFFIX_CONS_PHONEMES)
          if (!keys.contains(new Phoneme(suffix))) keys.add(new Phoneme(suffix));
        break;
    }

    keys.add(new Phoneme(" "));

    return keys;
  }

  private List<Glyph> translate(Word word) {
    List<Glyph> glyphs = new ArrayList<>();

    for (Syllable syllable : word.getSyllables()) {
      if (type == Type.SYLLABARY) {
        glyphs.add(this.glyphs.get(syllable));
      } else {
        Phoneme prefix = new Phoneme(syllable.getPrefix());
        Phoneme vowel = new Phoneme(syllable.getVowel());
        Phoneme suffix = new Phoneme(syllable.getSuffix());

        if (prefix.toString().length() > 0)
          glyphs.add(this.glyphs.get(prefix));
        if (vowel.toString().length() > 0)
          glyphs.add(this.glyphs.get(vowel));
        if (suffix.toString().length() > 0)
          glyphs.add(this.glyphs.get(suffix));
      }
    }

    return glyphs;
  }

  private List<Glyph> translate(String text) {
    List<Glyph> glyphs = new ArrayList<>();

    boolean noMatchFound = false;
    while (text.length() > 0 && !noMatchFound) {
      noMatchFound = true;
      for (WordSubUnit key : keys) {
        String toMatch = key.toString();

        if (text.startsWith(toMatch) && this.glyphs.containsKey(key)) {
          glyphs.add(this.glyphs.get(key));
          text = text.substring(toMatch.length());
          noMatchFound = false;
          break;
        }
      }

      if (noMatchFound) text = text.substring(1);
    }

    return glyphs;
  }

  public BufferedImage draw(String[] lines, final int SIZE, boolean debug) {
    List<BufferedImage> images = new ArrayList<>();
    int widest = Integer.MIN_VALUE;
    int height = 0;

    for (String line : lines) {
      BufferedImage image = draw(line, SIZE, debug);
      images.add(image);
      widest = Math.max(widest, image.getWidth());
      height += image.getHeight() + (SIZE / 2);
    }

    return combineLines(images, widest, height, SIZE);
  }

  public BufferedImage draw(String text, final int SIZE, boolean debug) {
    List<Glyph> glyphs = translate(text.toLowerCase());

    return draw(glyphs, SIZE, debug);
  }

  BufferedImage draw(List<Glyph> glyphs, final int SIZE, boolean debug) {
    // TODO: Remove Math.max hotfix
    BufferedImage writing =
            new BufferedImage(Math.max(1, glyphs.size() * SIZE),
                    SIZE, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) writing.getGraphics();

    int x = 0;

    for (Glyph glyph : glyphs) {
      BufferedImage img = glyph.draw(SIZE, debug, this);

      g.drawImage(img, x, 0, null);
      x += SIZE;
    }

    return writing;
  }

  public BufferedImage draw(Word word, final int SIZE, boolean debug) {
    List<Glyph> glyphs = translate(word);

    return draw(glyphs, SIZE, debug);
  }

  public BufferedImage drawWithFont(String[] lines, final int SIZE, int startWidth,
         int endWidth, BiFunction<Double, Double, Double> xFunc,
         BiFunction<Double, Double, Double> yFunc) {
    List<BufferedImage> images = new ArrayList<>();
    int widest = Integer.MIN_VALUE;
    int height = 0;

    for (String line : lines) {
      BufferedImage image =
              drawWithFont(line, SIZE, startWidth, endWidth, xFunc, yFunc);
      images.add(image);
      widest = Math.max(widest, image.getWidth());
      height += image.getHeight() + (SIZE / 2);
    }

    return combineLines(images, widest, height, SIZE);
  }

  private BufferedImage drawWithFont(List<Glyph> glyphs, final int SIZE, int startWidth,
                                     int endWidth, BiFunction<Double, Double, Double> xFunc,
                                     BiFunction<Double, Double, Double> yFunc) {
    BufferedImage writing =
            new BufferedImage(glyphs.size() * SIZE,
                    SIZE, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) writing.getGraphics();

    int x = 0;

    for (int i = 0; i < glyphs.size(); i++) {
      BufferedImage img = glyphs.get(i).
              drawWithFont(SIZE, startWidth, endWidth, xFunc, yFunc);

      double tightness = glyphs.get(i).getComponents().size() == 0 ? 1.4 : 0.7;
      g.drawImage(img, x, 0, null);
      x += (int)(SIZE * tightness);
    }

    return writing;
  }

  public BufferedImage drawSentenceWithFont(
          List<Word> sentence, final int SIZE, int startWidth, int endWidth,
          BiFunction<Double, Double, Double> xFunc,
          BiFunction<Double, Double, Double> yFunc) {
    List<Glyph> glyphs = new ArrayList<>();

    for (int i = 0; i < sentence.size(); i++) {
      glyphs.addAll(translate(sentence.get(i)));
      if (i < sentence.size() - 1) glyphs.add(Glyph.empty());
    }

    glyphs.add(Glyph.period());

    return drawWithFont(glyphs, SIZE, startWidth, endWidth, xFunc, yFunc);
  }

  public BufferedImage drawWithFont(Word word, final int SIZE, int startWidth,
                                    int endWidth, BiFunction<Double, Double, Double> xFunc,
                                    BiFunction<Double, Double, Double> yFunc) {
    GameDebug.printMessage(this.type + " " + word.toString(), GameDebug::printDebug);

    List<Glyph> glyphs = translate(word);

    return drawWithFont(glyphs, SIZE, startWidth, endWidth, xFunc, yFunc);
  }

  public BufferedImage drawWithFont(String text, final int SIZE, int startWidth,
      int endWidth, BiFunction<Double, Double, Double> xFunc,
                    BiFunction<Double, Double, Double> yFunc) {
    List<Glyph> glyphs = translate(text.toLowerCase());

    return drawWithFont(glyphs, SIZE, startWidth, endWidth, xFunc, yFunc);
  }

  private BufferedImage combineLines(List<BufferedImage> lines, int widest, int height, final int SIZE) {
    BufferedImage allLines =
            new BufferedImage(widest, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) allLines.getGraphics();

    height = 0;
    for (BufferedImage image : lines) {
      g.drawImage(image, 0, height, null);
      height += image.getHeight() + (SIZE / 2);
    }

    return allLines;
  }
}
