package com.redsquare.citizen.entity;

import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.ColorMath;
import com.redsquare.citizen.util.Randoms;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Person extends Animal {
  protected String[] name;
  protected String called;
  protected final GameDate birthday;

  protected final Person father;
  protected final Person mother;
  /* final because assignment to empty HashSet is then populated but is still
   * the same HashSet object */
  protected final Set<Person> children;

  protected final Color skinColor;
  protected final Color hairColor;
  protected final Height height;
  protected final BodyType bodyType;

  protected Person(Person father, Person mother, GameDate birthday) {
    this.father = father;
    this.mother = mother;
    this.birthday = birthday;
    this.children = new HashSet<>();

    // TODO: name generation

    skinColor = skinColorGeneration();
    hairColor = hairColorGeneration();
    height = heightGeneration();
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
    Color diff = new Color(father.skinColor.getRed() - mother.skinColor.getRed(),
            father.skinColor.getGreen() - mother.skinColor.getGreen(),
            father.skinColor.getBlue() - mother.skinColor.getBlue());
    return new Color(
            (int)(mother.skinColor.getRed() + (diff.getRed() * skinSkew)),
            (int)(mother.skinColor.getGreen() + (diff.getGreen() * skinSkew)),
            (int)(mother.skinColor.getBlue() + (diff.getBlue() * skinSkew)));
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
