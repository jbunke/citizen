package com.redsquare.citizen.entity.animal;

import java.awt.*;

public class Species {
  public final Range range;
  public final Habitat habitat;
  public final boolean domesticated;
  public final boolean mountable;
  public final Classification classification;
  public final HumanRelation humanRelation;

//  public final int avgLifespan;
//  public final int amountOfColours;
//  public final Color[][] coatOptions;

  private Species(Range range, Habitat habitat, HumanRelation humanRelation) {
    this.range = range;
    this.habitat = habitat;
    this.humanRelation = humanRelation;

    this.domesticated = humanRelation.isDomesticated();
    this.classification = humanRelation.getClassification();
    this.mountable = classification.isMountable();
  }

  public enum Range {
    GLOBAL, CONTINENTAL
  }

  public enum Habitat {
    AMPHIBIOUS, FRESHWATER, DESERT, FOREST, MOUNTAINS, PLAINS
  }

  public enum Classification {
    FELINE, EQUINE, PRIMATE, AVIAN, CANINE, URSINE,
    HIRCINE, BOVINE, OVINE, // GOAT, COW
    ANGUINE, CERVINE;

    boolean isMountable() {
      switch (this) {
        case FELINE:
        case EQUINE:
        case URSINE:
        case CERVINE:
          return true;
        default:
          return false;
      }
    }
  }

  public enum HumanRelation {
    // Domesticated
    BEAST_OF_BURDEN, FAST_TRANSPORT, ASSISTANCE,
    LARGE_LIVESTOCK, SMALL_LIVESTOCK, MESSENGER,
    // Wild
    APEX_PREDATOR, VENOMOUS, LARGE_HERBIVORE;

    Classification getClassification() {
      final double[] probs;
      final Classification[] possibilities;
      double prob = Math.random();

      switch (this) {
        // TODO
        case LARGE_HERBIVORE:
          possibilities = new Classification[] {
                  Classification.FELINE, Classification.EQUINE,
                  Classification.PRIMATE, Classification.CANINE,
                  Classification.URSINE, Classification.HIRCINE,
                  Classification.BOVINE, Classification.OVINE,
                  Classification.CERVINE
          };
          probs = new double[] {
                  0.03, 0.1, 0.15, 0.18, 0.3, 0.4, 0.5, 0.7, 1.0
          };
          break;
        default:
          possibilities = new Classification[] { Classification.FELINE };
          probs = new double[] { 1. };
          break;
      }

      for (int i = 0; i < probs.length; i++) {
        if (prob < probs[i])
          return possibilities[i];
      }

      return possibilities[possibilities.length - 1];
    }

    boolean isDomesticated() {
      switch (this) {
        case APEX_PREDATOR:
        case VENOMOUS:
        case LARGE_HERBIVORE:
          return false;
        default:
          return true;
      }
    }
  }
}
