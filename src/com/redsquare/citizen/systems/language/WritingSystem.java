package com.redsquare.citizen.systems.language;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class WritingSystem {

  private final PhoneticVocabulary vocabulary;
  private final Map<WordSubUnit, Glyph> glyphs;
  private final List<WordSubUnit> keys;
  private final Type type;

  private WritingSystem(PhoneticVocabulary vocabulary, Type type) {
    // Random likelihood of syllabic vs alphabetical
    this.vocabulary = vocabulary;
    this.type = type;

    // max distance from last line
    int maxDistance = (int)((GlyphLine.ARTICULATIONS / 4) +
            (Math.random() * GlyphLine.ARTICULATIONS));

    // between 3 and 5 common elements
    List<GlyphLine> common = generateCommonElements(maxDistance);

    // populate and sort keys
    keys = enumerateKeys();
    sortKeys();

    glyphs = generateGlyphs(common, maxDistance);
  }

  public static WritingSystem generate(PhoneticVocabulary vocabulary) {
    Type type;

    if (Math.random() < 0.5) type = Type.ALPHABETICAL;
    else type = Type.SYLLABIC;

    return new WritingSystem(vocabulary, type);
  }

  public static WritingSystem generate(PhoneticVocabulary vocabulary,
                                       Type type) {
    return new WritingSystem(vocabulary, type);
  }

  public enum Type {
    ALPHABETICAL, SYLLABIC
  }

  private void sortKeys() {
    for (int i = 0; i < keys.size(); i++) {
      for (int j = i + 1; j < keys.size(); j++) {
        if (keys.get(j).toString().length() > keys.get(i).toString().length()) {
          WordSubUnit temp = keys.get(i);
          keys.set(i, keys.get(j));
          keys.set(j, temp);
        }
      }
    }
  }

  private Map<WordSubUnit, Glyph> generateGlyphs(List<GlyphLine> common,
                                                 int maxDistance) {
    Map<WordSubUnit, Glyph> glyphs = new HashMap<>();

    for (WordSubUnit key : keys) {
      // generate glyph
      boolean violates = true;
      Glyph candidate = null;

      while (violates) {
        violates = false;

        candidate = Glyph.generate(common, maxDistance);
        if (glyphs.containsValue(candidate)) violates = true;
      }

      // populate
      glyphs.put(key, candidate);
    }

    glyphs.put(new Phoneme(" "), Glyph.empty());

    return glyphs;
  }

  private List<GlyphLine> generateCommonElements(int maxDistance) {
    List<GlyphLine> common = new ArrayList<>();
    int commonCount = 1 + (int)(Math.random() * 2);

    for (int i = 0; i < commonCount; i++) {
      GlyphLine candidate = GlyphLine.random(maxDistance);

      if (common.contains(candidate)) {
        i--;
        continue;
      }
      common.add(candidate);
    }

    return common;
  }

  private List<WordSubUnit> enumerateKeys() {
    List<WordSubUnit> keys = new ArrayList<>();

    switch (type) {
      case SYLLABIC:
        Set<Syllable> vowelsOnly = new HashSet<>();
        Set<Syllable> prefixVowel = new HashSet<>();
        Set<Syllable> vowelSuffix = new HashSet<>();
        Set<Syllable> prefixVowelSuffix = new HashSet<>();

        for (String vowel : vocabulary.VOWEL_PHONEMES) {
          vowelsOnly.add(new Syllable("", vowel, ""));

          for (String prefix : vocabulary.PREFIX_CONS_PHONEMES) {
            if (!Phonemes.ILLEGAL_PREFIX_TO_VOWEL.containsKey(prefix) ||
                    !Phonemes.ILLEGAL_PREFIX_TO_VOWEL.
                    get(prefix).contains(vowel)) {
              prefixVowel.add(new Syllable(prefix, vowel, ""));

              for (String suffix : vocabulary.SUFFIX_CONS_PHONEMES) {
                if (!Phonemes.ILLEGAL_VOWEL_TO_SUFFIX.containsKey(vowel) ||
                        !Phonemes.ILLEGAL_VOWEL_TO_SUFFIX.
                        get(vowel).contains(suffix)) {
                  prefixVowelSuffix.add(new Syllable(prefix, vowel, suffix));
                }
              }
            }
          }

          for (String suffix : vocabulary.SUFFIX_CONS_PHONEMES) {
            if (!Phonemes.ILLEGAL_VOWEL_TO_SUFFIX.containsKey(vowel) ||
                    !Phonemes.ILLEGAL_VOWEL_TO_SUFFIX.
                    get(vowel).contains(suffix)) {
              vowelSuffix.add(new Syllable("", vowel, suffix));
            }
          }
        }

        keys.addAll(vowelsOnly);
        keys.addAll(prefixVowel);
        keys.addAll(prefixVowelSuffix);
        keys.addAll(vowelSuffix);
        break;
      case ALPHABETICAL:
        for (String vowel : vocabulary.VOWEL_PHONEMES)
          keys.add(new Phoneme(vowel));
        for (String prefix : vocabulary.PREFIX_CONS_PHONEMES)
          keys.add(new Phoneme(prefix));
        for (String suffix : vocabulary.SUFFIX_CONS_PHONEMES)
          keys.add(new Phoneme(suffix));
        break;
    }

    keys.add(new Phoneme(" "));

    return keys;
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

  public BufferedImage draw(String[] lines, final int SCALE_UP) {
    List<BufferedImage> images = new ArrayList<>();
    int widest = Integer.MIN_VALUE;
    int height = 0;

    for (String line : lines) {
      BufferedImage image = draw(line, SCALE_UP);
      images.add(image);
      widest = Math.max(widest, image.getWidth());
      height += image.getHeight();
    }

    BufferedImage allLines =
            new BufferedImage(widest, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) allLines.getGraphics();

    height = 0;
    for (int i = 0; i < images.size(); i++) {
      g.drawImage(images.get(i), 0, height, null);
      height += images.get(i).getHeight();
    }

    return allLines;
  }

  public BufferedImage draw(String text, final int SCALE_UP) {
    List<Glyph> glyphs = translate(text.toLowerCase());

    BufferedImage writing =
            new BufferedImage(Glyph.SIZE * glyphs.size() * SCALE_UP,
            Glyph.SIZE * SCALE_UP, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) writing.getGraphics();

    for (int i = 0; i < glyphs.size(); i++) {
      BufferedImage img = glyphs.get(i).draw();
      g.drawImage(img, Glyph.SIZE * i * SCALE_UP, 0,
              img.getWidth() * SCALE_UP, img.getHeight() * SCALE_UP, null);
    }

    return writing;
  }
}
