package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.systems.language.Phonology;
import com.redsquare.citizen.systems.language.Word;
import com.redsquare.citizen.systems.politics.Culture;
import com.redsquare.citizen.util.Formatter;
import com.redsquare.citizen.util.Randoms;
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
  private final GodForm form;

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
    this.isOmniscient = MONOTHEISTIC_RELIGION ? Randoms.prob(0.7) : Randoms.prob(0.2);
    this.isOmnipotent = MONOTHEISTIC_RELIGION && Randoms.prob(0.8);
    this.isNamed = !MONOTHEISTIC_RELIGION || Randoms.prob(0.5);

    // TODO - temporary
    if (isNamed)
      name = Word.generateRandomWord(1, 6,
              culture.getDaughterLanguages().get(0).getPhonology());
    else {
      name = Word.generateRandomWord(0, 0, Phonology.generate());
    }

    // TODO
    this.gender = GodGender.NONE;

    if (MONOTHEISTIC_RELIGION || !IS_ATTRIBUTIVE)
      this.attribute = Attribute.NONE;
    else {
      this.attribute = Attribute.nonNoneAndAvailable(pantheon);
    }

    // Corporeal, anthropomorphism, and form
    if (MONOTHEISTIC_RELIGION) {
      this.isCorporeal = Randoms.prob(0.7);
      this.isAnthropomorphic = isCorporeal && Randoms.prob(0.5);
    } else {
      if (!pantheon.isEmpty()) {
        God reference = Sets.randomEntry(pantheon);

        this.isCorporeal = reference.isCorporeal;
        this.isAnthropomorphic = reference.isAnthropomorphic;
      } else {
        this.isCorporeal = Randoms.prob(0.7);
        this.isAnthropomorphic = isCorporeal && Randoms.prob(0.5);
      }
    }

    this.form = isAnthropomorphic ?
            GodForm.generateAnthropomorphic(attribute) :
            GodForm.generateBeastly(attribute);
  }

  Attribute getAttribute() {
    return attribute;
  }

  @Override
  public String toString() {
    if (attribute.equals(Attribute.NONE))
      return Formatter.properNoun(name.toString());
    else
      return Formatter.properNoun(name.toString()) + ", god of " + attribute.toString();
  }
}
