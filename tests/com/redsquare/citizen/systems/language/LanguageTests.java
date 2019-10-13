package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.graphics.Font;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LanguageTests {

  private static final String IMAGE_FORMAT = "png";
  private static final String FOLDER_PATH = "test_output/language/genealogy/";

  @Test
  public void vocabularyPopulation() {
    Language language = Language.generate();

    System.out.println(language.getName());
  }

  @Test
  public void linguisticGenealogy() {
    Meaning[] meanings = new Meaning[] { Meaning.THIS_LANGUAGE, Meaning.BOY,
            Meaning.DISTANT, Meaning.GREAT_COMP, Meaning.PARENT,
            Meaning.FATHER, Meaning.MOTHER, Meaning.LOVER, Meaning.SPOUSE,
            Meaning.RED, Meaning.ENEMY, Meaning.MARRIAGE, Meaning.HUSBAND };

    Language origin = Language.generate();

    Language child1 = origin.daughterLanguage();
    Language child2 = origin.daughterLanguage();
    Language child3 = origin.daughterLanguage();

    Language grandchild = Math.random() < 1/3. ? child1.daughterLanguage() :
            (Math.random() < 0.5 ? child2.daughterLanguage() : child3.daughterLanguage());

    BufferedImage image = new BufferedImage(1800, 2000, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) image.getGraphics();

    for (int i = 0; i < meanings.length; i++) {
      g.drawImage(Font.CLEAN.getText(meanings[i].toString()), 5, 5 + (i * 100), null);

      g.drawImage(Font.CLEAN.getText(origin.lookUpWord(meanings[i]).toString()),
              305, 5 + (i * 100), null);
      g.drawImage(origin.getWritingSystem().draw(origin.lookUpWord(meanings[i]), 30, false),
              305, 40 + (i * 100), null);
      g.drawImage(Font.CLEAN.getText(child1.lookUpWord(meanings[i]).toString()),
              605, 5 + (i * 100), null);
      g.drawImage(child1.getWritingSystem().draw(child1.lookUpWord(meanings[i]), 30, false),
              605, 40 + (i * 100), null);
      g.drawImage(Font.CLEAN.getText(child2.lookUpWord(meanings[i]).toString()),
              905, 5 + (i * 100), null);
      g.drawImage(child2.getWritingSystem().draw(child2.lookUpWord(meanings[i]), 30, false),
              905, 40 + (i * 100), null);
      g.drawImage(Font.CLEAN.getText(child3.lookUpWord(meanings[i]).toString()),
              1205, 5 + (i * 100), null);
      g.drawImage(child3.getWritingSystem().draw(child3.lookUpWord(meanings[i]), 30, false),
              1205, 40 + (i * 100), null);
      g.drawImage(Font.CLEAN.getText(grandchild.lookUpWord(meanings[i]).toString()),
              1505, 5 + (i * 100), null);
      g.drawImage(grandchild.getWritingSystem().draw(grandchild.lookUpWord(meanings[i]), 30, false),
              1505, 40 + (i * 100), null);
    }

    try {
      ImageIO.write(image, IMAGE_FORMAT,
              new File(FOLDER_PATH + "comp_origin_with_descendants." + IMAGE_FORMAT));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void wordInTenDescendedLanguages() {
    GameDebug.activate();

    Language[] languages = new Language[10];
    languages[0] = Language.generate();

    for (int i = 1; i < languages.length; i++) {
      languages[i] = languages[i - 1].daughterLanguage();
    }

    Meaning meaning = Meaning.GRANDMOTHER;

    BufferedImage scroll = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) scroll.getGraphics();

    for (int i = 0; i < languages.length; i++) {
      Language l = languages[i];
      BufferedImage text =
              l.getWritingSystem().drawWithFont(l.lookUpWord(meaning), 50, 2, 2, Fonts::fontItalicX, Fonts::fontIdentityY);
      g.drawImage(text, (1000 - text.getWidth()) / 2, i * 100, null);
    }

    try {
      ImageIO.write(scroll, IMAGE_FORMAT,
              new File(FOLDER_PATH + meaning.toString() + "_in_ten_descended_languages." + IMAGE_FORMAT));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void verbTable() {
    GameDebug.activate();

    Meaning verb = Meaning.RUN;

    Language language = Language.generate();
    Grammar grammar = language.getGrammar();
    BufferedImage verbTable = grammar.verbTable(verb, language);

    try {
      ImageIO.write(verbTable, IMAGE_FORMAT,
              new File(FOLDER_PATH + verb.toString() + "_conjugation_table." + IMAGE_FORMAT));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
