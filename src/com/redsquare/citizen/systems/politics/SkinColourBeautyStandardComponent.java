package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.entity.Person;
import com.redsquare.citizen.util.MathExt;

import java.awt.*;

public class SkinColourBeautyStandardComponent extends BeautyStandardComponent {

  private final Color ideal;

  SkinColourBeautyStandardComponent(Race race) {
    ideal = race.generateSkinColor();
  }

  @Override
  TargetSex targetSex() {
    return TargetSex.BOTH;
  }

  @Override
  public double compliance(Person person) {
    Color actual = person.getSkinColor();
    int difference = Math.abs(ideal.getRed() - actual.getRed()) +
            Math.abs(ideal.getGreen() - actual.getGreen()) +
            Math.abs(ideal.getBlue() - actual.getBlue());

    return MathExt.bounded(1.0 - (difference / (double)400), 0.0, 1.0);
  }
}
