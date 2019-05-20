package com.redsquare.citizen.entity;

import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.politics.Culture;
import com.redsquare.citizen.systems.politics.Family;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.ColorMath;
import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Person extends Animal {
  protected String[] name;
  protected String called;

  /* Not final as cultures may have Family transitions during institutions
   * like marriage */
  private Family family;
  private final GameDate birthday;
  private final Settlement birthplace;

  private final Person father;
  private final Person mother;
  /* final because assignment to empty HashSet is then populated but is still
   * the same HashSet object */
  private final Set<Person> children;

  private final Color skinColor;
  private final Color hairColor;
  private final Height height;
  private final BodyType bodyType;

  private final Language motherTongue;
  private final Set<Language> languages;
  private Culture culture;

  protected Person(Person father, Person mother, GameDate birthday,
                   Settlement birthplace) {
    sex = Math.random() < 0.5 ? Sex.MALE : Sex.FEMALE;

    this.father = father;
    this.mother = mother;
    this.birthday = birthday;
    this.birthplace = birthplace;

    this.children = new HashSet<>();

    // TODO: name generation

    motherTongue = motherTongueGeneration();
    languages = Set.of(motherTongue);
    culture = cultureGeneration();
    family = familyGeneration();

    skinColor = skinColorGeneration();
    hairColor = hairColorGeneration();
    height = heightGeneration();
    bodyType = randomBodyType();

    this.mother.children.add(this);
    this.father.children.add(this);
  }

  protected Person(Sex sex, GameDate birthday, Settlement birthplace) {
    this.sex = sex;
    this.birthday = birthday;
    this.birthplace = birthplace;

    this.father = null;
    this.mother = null;

    this.children = new HashSet<>();

    motherTongue = birthplace.getState().getLanguage();
    languages = Set.of(motherTongue);
    culture = birthplace.getState().getCulture();
    family = Family.generate();

    skinColor = birthplace.getState().getCulture().
            getNativeRace().generateSkinColor();
    hairColor = birthplace.getState().getCulture().
            getNativeRace().generateHairColor();
    height = randomHeight();
    bodyType = randomBodyType();
  }

  public static Person create(Sex sex, GameDate birthday,
                              Settlement birthplace) {
    return new Person(sex, birthday, birthplace);
  }

  public static Person birth(Person mother, Person father, GameDate birthday,
                             Settlement birthplace) {
    return new Person(father, mother, birthday, birthplace);
  }

  Sex getSex() {
    return sex;
  }

  Height getHeight() {
    return height;
  }

  BodyType getBodyType() {
    return bodyType;
  }

  Color getSkinColor() {
    return skinColor;
  }

  Color getHairColor() {
    return hairColor;
  }

  public Set<Person> getChildren() {
    return children;
  }

  private Family familyGeneration() {
    if (culture.getInheritance() != Culture.Inheritance.MATRILINEAL)
      return father.family;

    return mother.family;
  }

  private Culture cultureGeneration() {
    if (father.culture.getInheritance() == Culture.Inheritance.PATRILINEAL ||
            mother.culture.getInheritance() == Culture.Inheritance.PATRILINEAL)
      return father.culture;
    else if (mother.culture.getInheritance() == Culture.Inheritance.MATRILINEAL)
      return mother.culture;

    /* sharedChildren is the set of children that this person's parents share
     * excluding them; their full siblings */
    Set<Person> sharedChildren =
            Sets.difference(Sets.intersection(father.children, mother.children), Set.of(this));

    /* Consistency among siblings: if parents share children already,
     * apply same culture as older siblings final case is non-deterministic */
    if (sharedChildren.size() > 0)
      return new ArrayList<>(sharedChildren).get(0).culture;

    return Math.random() < 0.5 ? mother.culture : father.culture;
  }

  private Language motherTongueGeneration() {
    Language regional = birthplace.getState().getLanguage();

    if (father.speaks(regional) || mother.speaks(regional)) return regional;

    return mother.motherTongue;
  }

  private BodyType randomBodyType() {
    double prob = Math.random();

    if (prob < 0.4) return BodyType.AVERAGE;
    else if (prob < 0.7) return BodyType.SLIM;
    else if (prob < 0.9) return BodyType.MUSCULAR;
    return BodyType.FAT;
  }

  private Height randomHeight() {
    double prob = Math.random();

    if (prob < 0.5) return Height.MEDIUM;
    else if (prob < 0.8) return Height.TALL;
    else return Height.SHORT;
  }

  private Height heightGeneration() {
    Height[] possibilities = new Height[0];
    double[] probabilities = new double[0];

    switch (father.height) {
      case TALL:
        switch (mother.height) {
          case TALL:
          case MEDIUM:
            return Height.TALL;
          case SHORT:
            possibilities = new Height[] { Height.MEDIUM, Height.TALL };
            probabilities = new double[] { 2/3.0, 1.0 };
            break;
        }
        break;
      case MEDIUM:
        switch (mother.height) {
          case TALL:
            return Height.TALL;
          case MEDIUM:
            possibilities = new Height[]
                    { Height.MEDIUM, Height.TALL, Height.SHORT };
            probabilities = new double[] { 1/2.0, 4/5.0, 1.0 };
            break;
          case SHORT:
            possibilities = new Height[]
                    { Height.MEDIUM, Height.SHORT, Height.TALL };
            probabilities = new double[] { 1/2.0, 4/5.0, 1.0 };
            break;
        }
        break;
      case SHORT:
        switch (mother.height) {
          case TALL:
            possibilities = new Height[] { Height.MEDIUM, Height.TALL };
            probabilities = new double[] { 2/3.0, 1.0 };
            break;
          case MEDIUM:
            possibilities = new Height[]
                    { Height.MEDIUM, Height.SHORT, Height.TALL };
            probabilities = new double[] { 1/2.0, 4/5.0, 1.0 };
            break;
          case SHORT:
            possibilities = new Height[] { Height.SHORT, Height.MEDIUM };
            probabilities = new double[] { 2/3.0, 1.0 };
            break;
        }
        break;
    }

    double prob = Math.random();

    for (int i = 0; i < possibilities.length; i++) {
      if (prob < probabilities[i]) return possibilities[i];
    }

    return Height.MEDIUM;
  }

  private Color skinColorGeneration() {
    double skinSkew = Randoms.bounded(0.3, 0.7);
    Color darker = mother.skinColor;
    Color lighter = father.skinColor;

    if (ColorMath.lightness(mother.skinColor) >
            ColorMath.lightness(father.skinColor)) {
      darker = father.skinColor;
      lighter = mother.skinColor;
    }

    Color diff = new Color(lighter.getRed() - darker.getRed(),
            lighter.getGreen() - darker.getGreen(),
            lighter.getBlue() - darker.getBlue());
    return new Color(
            (int)(darker.getRed() + (diff.getRed() * skinSkew)),
            (int)(darker.getGreen() + (diff.getGreen() * skinSkew)),
            (int)(darker.getBlue() + (diff.getBlue() * skinSkew)));
  }

  private Color hairColorGeneration() {
    Color darker = father.hairColor;
    Color lighter = mother.hairColor;

    if (ColorMath.lightness(father.hairColor) >
            ColorMath.lightness(mother.hairColor)) {
      lighter = father.hairColor;
      darker = mother.hairColor;
    }

    double hairSkew = Math.random() * Math.random(); // average value is .25; so skew favours darker hair

    Color diff = new Color(lighter.getRed() - darker.getRed(),
            lighter.getGreen() - darker.getGreen(),
            lighter.getBlue() - darker.getBlue());
    return new Color(
            (int)(darker.getRed() + (diff.getRed() * hairSkew)),
            (int)(darker.getGreen() + (diff.getGreen() * hairSkew)),
            (int)(darker.getBlue() + (diff.getBlue() * hairSkew)));
  }

  protected enum Height {
    TALL, MEDIUM, SHORT
  }

  protected enum BodyType {
    AVERAGE, SLIM, MUSCULAR, FAT
  }

  private boolean speaks(Language language) {
    return languages.contains(language);
  }

  private boolean ancestorOf(Person p) {
    return p.descendantOf(this);
  }

  private boolean descendantOf(Person p) {
    // Can't be a descendant of someone you were born before
    if (birthday.equals(GameDate.priorEvent(birthday, p.birthday))) return false;

    if (father == null) return false;
    else if (father.equals(p) || mother.equals(p)) return true;

    return father.descendantOf(p) || mother.descendantOf(p);
  }

  @Override
  int age(GameDate now) {
    return GameDate.yearsBetween(birthday, now);
  }

  @Override
  Point worldLocation() {
    return null;
  }

  @Override
  Point cellLocation() {
    return null;
  }
}
