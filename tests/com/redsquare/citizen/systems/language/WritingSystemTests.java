package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.politics.State;
import com.redsquare.citizen.util.Formatter;
import com.redsquare.citizen.util.Orientation;
import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;
import com.redsquare.citizen.worldgen.World;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class WritingSystemTests {

  private static final String IMAGE_FORMAT = "png";
  private static boolean archiveMode = false;
  private static int archiveCount = 0;
  private static final String ARCHIVE_PATH = "test_output/language/archive/";

  public static void main(String[] args) {
    WritingSystemTests testInstance = new WritingSystemTests();

    testLoop(testInstance::customiseEverything, 10);
    testLoop(testInstance::fontCompare, 100);
    testLoop(testInstance::allSettlementsInNativeTongue, 10);
    testLoop(testInstance::glyphGeneration, 10);
  }

  private static void testLoop(Runnable test, int iterations) {
    archiveMode = true;

    for (int i = 0; i < iterations; i++) {
      archiveCount = i;

      GameDebug.deactivate();

      test.run();

      GameDebug.activate();
      GameDebug.printMessage("Test: [" + (i + 1) + "/" +
              iterations + "]", GameDebug::printDebug);
    }
  }

  @Test
  public void allSettlementsInNativeTongue() {
    final String filepath = "test_output/language/settlementsInNative.png";

    World world = World.safeCreate(480, 270, 30, 10);

    State state = Sets.randomEntry(world.getStates());

    if (state == null) return;

    Language l = state.getLanguage();
    Set<Settlement> settlements = state.settlements();

    List<BufferedImage> ims = new ArrayList<>();
    int width = 0;
    int height = 0;

    BiFunction<Double, Double, Double> xFunc = Fonts.randomXFunc();
    BiFunction<Double, Double, Double> yFunc = Fonts.randomYFunc();

    for (Settlement settlement : settlements) {
      BufferedImage newIm = l.getWritingSystem().drawWithFont(
              settlement.getName(), 80, 4, 2,
              xFunc, yFunc);

      width = Math.max(width, newIm.getWidth());
      height += newIm.getHeight() * 2;

      ims.add(newIm);
    }

    BufferedImage all = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) all.getGraphics();

    for (int i = 0; i < ims.size(); i++) {
      g.drawImage(ims.get(i), 0, 160 * i, null);
    }

    try {
      ImageIO.write(all, IMAGE_FORMAT, new File(filepath));

      if (archiveMode) ImageIO.write(all, IMAGE_FORMAT,
              new File(ARCHIVE_PATH + "snt" + archiveCount + "." + IMAGE_FORMAT));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void customiseEverything() {
    String filepath = "test_output/language/custom.png";

    WritingSystem.Type type = WritingSystem.Type.ALPHABET;

    double avgLineCurve = 0.0; // 0 - 1 skewed ^2
    double curveDeviationMax = 0.5; // 0.1 - 0.9
    double deviationProb = 0.06;
    double avgLineLength = 0.95; // 0 - 1
    double avgContinuationProb = 0.95; // 0.5 - 1
    double continuationDeviationMax = 0.0; // 0 - 0.5
    double commonElemProbability = 0.4; // 0 - 1

    double directionalProclivity = 0.9; // 0 - 1
    Set<Integer> directionSet = Set.of(0, 60, 90, 120, 180, 240, 270, 300);
    int maxDirectionSkew = 0; // 0 - 30

    double startPointProclivity = 0.75; // 0.5 - 1
    Set<double[]> startPoints = Set.of(new double[] { 0.22, 0.15 },
            new double[] { 0.78, 0.15 }, new double[] { 0.22, 0.85 },
            new double[] { 0.78, 0.85 }, new double[] { 0.5, 0.15 },
            new double[] { 0.5, 0.5 }  );

    boolean compSyllabaryConnected = false;
    WritingSystem.CompSyllabaryConfig compSyllabaryConfig =
            WritingSystem.CompSyllabaryConfig.PS_ABOVE_V;

    Phonology p = Phonology.generate();
    WritingSystem ws = WritingSystem.generate(p, type, avgLineCurve,
            curveDeviationMax, deviationProb, avgLineLength, avgContinuationProb,
            continuationDeviationMax, commonElemProbability,
            directionalProclivity, directionSet, maxDirectionSkew,
            startPointProclivity, startPoints, compSyllabaryConnected,
            compSyllabaryConfig);
    // Language l = Language.generate(ws);

    int size = 100;
    int startWidth = 3;
    int endWidth = 2;
    BiFunction<Double, Double, Double> xFunc = Fonts::fontIdentityX;
    BiFunction<Double, Double, Double> yFunc = Fonts::fontCompressY;

    StringBuilder word = new StringBuilder();
    for (int i = 0; i < 20; i++) {
      word.append(Word.generateRandomWord(1, 6, p));
      word.append(" ");

      if ((i + 1) % 5 == 0) word.append("\n");
    }

    String noNewLines = word.toString().replaceAll("\n", "");

    BufferedImage printed = Font.CLEAN.getText(noNewLines.toLowerCase());
    BufferedImage wsText =
            ws.drawWithFont(word.toString().split("\n"), size,
                    startWidth, endWidth, xFunc, yFunc);

    BufferedImage all = new BufferedImage(wsText.getWidth(),
            printed.getHeight() + 60 + wsText.getHeight(),
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) all.getGraphics();

    g.drawImage(printed, 0, 0, null);
    g.drawImage(ws.draw(noNewLines, 40, true), 0,
            printed.getHeight() + 10, null);
    g.drawImage(wsText, 0, printed.getHeight() + 60, null);

    try {
      ImageIO.write(all, IMAGE_FORMAT, new File(filepath));

      if (archiveMode) ImageIO.write(all, IMAGE_FORMAT,
              new File(ARCHIVE_PATH + "cust" + archiveCount + "." + IMAGE_FORMAT));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void fontCompare() {
    String filepath = "test_output/language/font_compare.png";
    int size = 100;
    int iterations = 10;

    Language l = Language.generate();
    Word word = l.lookUpWord(Meaning.CAPITAL);
            // Word.generateRandomWord(3, 5, l.getPhonology());

    GameDebug.printMessage(Formatter.properNoun(word.toString()),
            GameDebug::printDebug);

    int maxX = 0;
    int y = 0;
    BufferedImage[] fonts = new BufferedImage[iterations];

    for (int i = 0; i < iterations; i++) {
      int startWidth = Randoms.bounded(2, 6);
      int endWidth = Randoms.bounded(1, 4);

      BiFunction<Double, Double, Double> xFunc = Fonts.randomXFunc();
      BiFunction<Double, Double, Double> yFunc = Fonts.randomYFunc();

      fonts[i] = l.getWritingSystem().drawWithFont(word.toString(),
              size, startWidth, endWidth, xFunc, yFunc);
      maxX = Math.max(maxX, fonts[i].getWidth());
      y += fonts[i].getHeight();
    }

    BufferedImage fi =
            Font.CLEAN.getText(word.toString().toLowerCase());

    y += fi.getHeight();

    BufferedImage all = new BufferedImage(maxX, y + (fonts.length * 20),
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) all.getGraphics();

    g.drawImage(fi, 0, 0, null);

    y = fi.getHeight();

    for (int i = 0; i < iterations; i++) {
      g.drawImage(fonts[i], 0, y, null);
      y += fonts[i].getHeight() + 20;
    }

    try {
      ImageIO.write(all, IMAGE_FORMAT, new File(filepath));

      if (archiveMode) ImageIO.write(all, IMAGE_FORMAT,
              new File(ARCHIVE_PATH + "fntcmp" + archiveCount + "." + IMAGE_FORMAT));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void glyphGeneration() {
    String filepath = "test_output/language/writing_systems.png";

    Phonology p = Phonology.generate();

    WritingSystem[] wss = new WritingSystem[10];

    for (int i = 0; i < 10; i++) {
      wss[i] = WritingSystem.generate(p);
      GameDebug.printMessage(wss[i].type.toString(), GameDebug::printDebug);
    }

    StringBuilder testText = new StringBuilder();
    for (int i = 0; i < 25; i++) {
      testText.append(Word.generateRandomWord(1, 4, p));
      testText.append(" ");
      if (i % 5 == 0 && i > 0) testText.append("\n");
    }

    GameDebug.printMessage(testText.toString().toLowerCase(), GameDebug::printDebug);

    BufferedImage[] wsis = new BufferedImage[wss.length];

    for (int i = 0; i < 10; i++) {
      wsis[i] = wss[i].draw(testText.toString().split("\n"), 40, true);
    }

    BufferedImage fi =
            Font.CLEAN.getText(testText.toString().toLowerCase().
                    split("\n"), Orientation.LEFT_TOP);

    int maxWidth = fi.getWidth();
    int height = 0;

    for (int i = 0; i < 10; i++) {
      maxWidth = Math.max(maxWidth, wsis[i].getWidth());
      height += wsis[i].getHeight();
    }

    BufferedImage all = new BufferedImage(maxWidth, (int)(height * 2.2), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) all.getGraphics();

    g.setColor(new Color(255, 255, 255));
    g.fillRect(0, 0, all.getWidth(), all.getHeight());
    g.drawImage(fi, 0, 0, null);
    height = fi.getHeight() * 2;

    for (BufferedImage i : wsis) {
      g.drawImage(i, 0, height, null);
      height += 2 * i.getHeight();
    }

    try {
      ImageIO.write(all, IMAGE_FORMAT, new File(filepath));

      if (archiveMode) ImageIO.write(all, IMAGE_FORMAT,
              new File(ARCHIVE_PATH + "glyph" + archiveCount + "." + IMAGE_FORMAT));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
