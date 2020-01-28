package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.systems.language.Phonology;
import com.redsquare.citizen.systems.language.Word;
import com.redsquare.citizen.systems.politics.Culture;
import com.redsquare.citizen.util.Sets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class God {
  private final boolean isOmniscient;
  private final boolean isOmnipotent;
  private final boolean isCorporeal;
  private final GodGender gender;
  private final Attribute attribute;
  private final boolean isAnthropomorphic;
  private final boolean isNamed;
  private final Word name;

  public enum GodGender {
    NONE, M, F
  }

  public enum Attribute {
    NONE,

    // CORE
    HARVEST, RAIN, WAR, LOVE, YOUTH, DEATH, LIFE, MIND, SPIRIT,
    KNOWLEDGE, STRENGTH, CUNNING, SKY, EARTH,

    // NEGATIVES
    GLUTTONY, SLOTH, PRIDE, WRATH, ENVY;

    static Attribute nonNoneAndAvailable(Set<God> pantheon) {
      Set<Attribute> available = Sets.difference(
              new HashSet<>(Arrays.asList(Attribute.values())),
              Set.of(NONE));

      for (God god : pantheon) {
        available.remove(god.attribute);
      }

      return Sets.randomEntry(available);
    }
  }

  God(final boolean MONOTHEISTIC_RELIGION, Set<God> pantheon, final boolean IS_ATTRIBUTIVE, Culture culture) {
    this.isOmniscient = MONOTHEISTIC_RELIGION ? Math.random() < 0.7 : Math.random() < 0.2;
    this.isOmnipotent = MONOTHEISTIC_RELIGION && Math.random() < 0.8;
    this.isNamed = !MONOTHEISTIC_RELIGION || Math.random() < 0.5;

    // TODO - temporary
    if (isNamed)
      name = Word.generateRandomWord(1, 6,
              culture.getDaughterLanguages().get(0).getPhonology());
    else {
      name = Word.generateRandomWord(0, 0, Phonology.generate());
    }

    // TODO
    this.gender = GodGender.NONE;
    this.isCorporeal = false;
    this.isAnthropomorphic = false;

    if (MONOTHEISTIC_RELIGION || !IS_ATTRIBUTIVE)
      this.attribute = Attribute.NONE;
    else {
      this.attribute = Attribute.nonNoneAndAvailable(pantheon);
    }
  }
}
