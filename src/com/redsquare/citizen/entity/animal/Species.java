package com.redsquare.citizen.entity.animal;

import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.Word;
import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;
import com.redsquare.citizen.worldgen.WorldCell;

import java.util.*;

public class Species {
  private final double SEXUAL_DIMORPHISM_COEFFICIENT;

  private final Habitat habitat;
  private final boolean domesticated;
  private final boolean mountable;
  private final Classification classification;
  private final HumanRelation humanRelation;
  private final Set<String> characteristics;

  private final Map<Language, Word> nameInLanguageMap;

//  public final int avgLifespan;
//  public final int amountOfColours;
//  public final Color[][] coatOptions;

  private Species(HumanRelation humanRelation,
                  WorldCell.CellLandType primary, Habitat.Range range) {
    this.SEXUAL_DIMORPHISM_COEFFICIENT = Randoms.bounded(0., 1.);

    this.habitat = Habitat.generate(primary, range);
    this.humanRelation = humanRelation;

    this.domesticated = humanRelation.isDomesticated();
    this.classification = humanRelation.getClassification();
    this.mountable = humanRelation.isMountable();

    this.characteristics = this.classification.generateCharacteristics();
    this.nameInLanguageMap = new HashMap<>();
  }

  public static Species generate(HumanRelation humanRelation,
                                 WorldCell.CellLandType primary, Habitat.Range range) {
    return new Species(humanRelation, primary, range);
  }

  public enum Classification {
    FELINE, EQUINE, PRIMATE, // CAT, HORSE, MONKEY
    AVIAN, CANINE, URSINE, // BIRD, DOG, BEAR
    GOAT_LIKE, BOVINE, SHEEP_LIKE, // GOAT, COW, SHEEP
    SERPENTINE, DEER_LIKE; // SNAKE, DEER

    public static Set<Classification> all() {
      return new HashSet<>(Arrays.asList(Classification.values()));
    }

    Set<String> generateCharacteristics() {
      Set<Set<String>> selectionSet;

      switch (this) {
        case FELINE:
          selectionSet = Set.of(
                  Set.of("STRIPED", "SPOTTED", "PLAIN")
          );
          break;
        case CANINE:
          selectionSet = Set.of(
                  Set.of("POINTY_EARS", "FLOPPY_EARS", "BUNNY_EARS")
          );
          break;
        case EQUINE:
          selectionSet = Set.of(
                  Set.of("STRIPED", "SPOTTED", "PLAIN"),
                  Set.of("FLAT_BACKED", "HUMPED")
          );
          break;
        case URSINE:
          selectionSet = Set.of(
                  Set.of("STRIPED", "SPOTTED", "PLAIN"),
                  Set.of("HIBERNATING", "NOT_HIBERNATING")
          );
          break;
        case DEER_LIKE:
          selectionSet = Set.of(
                  Set.of("STRIPED", "SPOTTED", "PLAIN"),
                  Set.of("STAMPEDING", "ROAMING")
          );
          break;
        case SHEEP_LIKE:
          selectionSet = Set.of(
                  Set.of("WOOLLY", "PARTIAL_COAT", "NOT_WOOLLY")
          );
          break;
        case BOVINE:
          selectionSet = Set.of(
                  Set.of("SPOTTED", "PLAIN")
          );
          break;
        case GOAT_LIKE:
          selectionSet = Set.of(
                  Set.of("GOATEE", "NO_GOATEE"),
                  Set.of("IBEX_HORNS", "SCREW_HORNS", "LITTLE_HORNS")
          );
          break;
        case AVIAN:
          selectionSet = Set.of(
                  Set.of("FLIGHTLESS", "CAN_FLY"),
                  Set.of("DIFFERENT_HEAD_COAT", "HOMOGENEOUS_COAT"),
                  Set.of("ARCHED_BEAK", "POINTY_BEAK"),
                  Set.of("CRANED_NECK", "SHORT_NECK")
          );
          break;
        case PRIMATE:
          selectionSet = Set.of(
                  Set.of("HAS_TAIL", "NO_TAIL"),
                  Set.of("BIPED", "QUADRUPED")
          );
          break;
        case SERPENTINE:
        default:
          selectionSet = Set.of(
                  Set.of("COBRA_NECK", "REGULAR_NECK"),
                  Set.of("STRIPED", "PLAIN")
          );
          break;
      }

      Set<String> solutionSet = new HashSet<>();

      for (Set<String> set : selectionSet) {
        solutionSet.add(Sets.randomEntry(set));
      }

      return solutionSet;
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
        case LARGE_HERBIVORE:
          possibilities = new Classification[] {
                  Classification.FELINE, Classification.EQUINE,
                  Classification.PRIMATE, Classification.CANINE,
                  Classification.URSINE, Classification.GOAT_LIKE,
                  Classification.BOVINE, Classification.SHEEP_LIKE,
                  Classification.DEER_LIKE
          };
          probs = new double[] {
                  0.03, 0.1, 0.15, 0.18, 0.3, 0.4, 0.5, 0.7, 1.0
          };
          break;
        case VENOMOUS:
          possibilities = new Classification[] { Classification.SERPENTINE };
          probs = new double[] {
                  1.0
          };
          break;
        case APEX_PREDATOR:
          possibilities = new Classification[] {
                  Classification.FELINE, Classification.PRIMATE,
                  Classification.CANINE, Classification.URSINE,
                  Classification.SERPENTINE
          };
          probs = new double[] {
                  0.3, 0.4, 0.6, 0.9, 1.
          };
          break;
        case MESSENGER:
          possibilities = new Classification[] { Classification.AVIAN };
          probs = new double[] { 1. };
          break;
        case ASSISTANCE:
          possibilities = new Classification[] {
                  Classification.FELINE, Classification.PRIMATE,
                  Classification.CANINE
          };
          probs = new double[] { 0.25, 0.5, 1. };
          break;
        case FAST_TRANSPORT:
          possibilities = new Classification[] {
                  Classification.FELINE, Classification.CANINE,
                  Classification.DEER_LIKE, Classification.EQUINE
          };
          probs = new double[] { 0.2, 0.4, 0.7, 1. };
          break;
        case BEAST_OF_BURDEN:
          possibilities = new Classification[] {
                  Classification.EQUINE, Classification.DEER_LIKE,
                  Classification.BOVINE, Classification.PRIMATE,
                  Classification.URSINE
          };
          probs = new double[] { 0.2, 0.4, 0.6, 0.7, 1. };
          break;
        default:
          possibilities = new Classification[] {
                  Classification.URSINE, Classification.PRIMATE,
                  Classification.CANINE, Classification.FELINE,
                  Classification.SERPENTINE, Classification.SHEEP_LIKE,
                  Classification.GOAT_LIKE, Classification.DEER_LIKE,
                  Classification.AVIAN, Classification.BOVINE,
                  Classification.EQUINE
          };
          probs = new double[] {
                  1 / 11., 2 / 11., 3 / 11., 4 / 11., 5 / 11.,
                  6 / 11., 7 / 11., 8 / 11., 9 / 11., 10 / 11., 1.
          };
          break;
      }

      for (int i = 0; i < probs.length; i++) {
        if (prob < probs[i])
          return possibilities[i];
      }

      return possibilities[possibilities.length - 1];
    }

    public boolean isDomesticated() {
      switch (this) {
        case APEX_PREDATOR:
        case VENOMOUS:
        case LARGE_HERBIVORE:
          return false;
        default:
          return true;
      }
    }

    boolean isMountable() {
      switch (this) {
        case BEAST_OF_BURDEN:
        case FAST_TRANSPORT:
          return true;
        default:
          return false;
      }
    }
  }

  public Habitat getHabitat() {
    return habitat;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(classification);
    sb.append(", ");
    sb.append(humanRelation);
    sb.append("; [");

    for (String characteristic : characteristics) {
      sb.append(" ");
      sb.append(characteristic);
    }

    sb.append(" ] ");

    sb.append(habitat.toString());

    return sb.toString();
  }
}
