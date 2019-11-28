package com.redsquare.citizen.entity;

import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.politics.State;
import com.redsquare.citizen.systems.time.GameDate;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersonTests {

  private static final int DAYS_IN_YEAR = 10;
  private static final String IMAGE_FORMAT = "png";
  private static final String PATH = "test_output/people/parents";

  @Test
  public void differentBackgroundParentsTest() {
    for (int run = 0; run < 10; run++) {
      State state1 = new State(null);
      Settlement settlement1 = new Settlement(new Point(0, 0), state1);

      State state2 = new State(null);
      Settlement settlement2 = new Settlement(new Point(0, 0), state2);

      GameDate date = new GameDate(1, 1);

      Person mother = Person.create(Sex.FEMALE, date, settlement1, null);
      Person father = Person.create(Sex.MALE, date, settlement2, null);

      List<Person> children = new ArrayList<>();

      for (int i = 0; i < 20; i++) {
        date = GameDate.increment(date, DAYS_IN_YEAR);
        children.add(Person.birth(mother, father, date, settlement1, null));
      }

      BufferedImage test = new BufferedImage(460, 30,
              BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D) test.getGraphics();

      g.drawImage(crudePersonDrawing(mother), 0, 0, null);
      g.drawImage(crudePersonDrawing(father), 20, 0, null);

      for (int i = 0; i < 20; i++) {
        g.drawImage(crudePersonDrawing(children.get(i)),
                60 + (20 * i), 0, null);
      }

      try {
        ImageIO.write(test, IMAGE_FORMAT,
                new File(PATH + run + "." + IMAGE_FORMAT));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private BufferedImage crudePersonDrawing(Person p) {
    final Color BODY = new Color(0, 140, 0);

    BufferedImage pImage = new BufferedImage(20, 30,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) pImage.getGraphics();

    Sex sex = p.getSex();
    Color hairColor = p.getHairColor();
    Color skinColor = p.getSkinColor();
    Person.BodyType bodyType = p.getBodyType();
    Person.Height height = p.getHeight();

    int y;

    switch (height) {
      case SHORT:
        y = 8;
        break;
      case MEDIUM:
        y = 5;
        break;
      default:
        y = 2;
    }

    int x;

    switch (bodyType) {
      case SLIM:
        x = 5;
        break;
      case AVERAGE:
      case MUSCULAR:
        x = 4;
        break;
      default:
        x = 2;
    }

    g.setColor(hairColor);

    switch (sex) {
      case MALE:
        g.fillRect(6, y, 8, 2);
        break;
      default:
        g.fillRect(6, y, 8, 15 - y);
    }

    g.setColor(skinColor);
    g.fillRect(7, y + 2, 6, 6);

    g.setColor(BODY);
    g.fillRect(x, y + 8, 20 - (2 * x), 22 - y);

    return pImage;
  }
}
