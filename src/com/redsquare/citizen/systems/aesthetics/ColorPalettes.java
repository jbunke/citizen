package com.redsquare.citizen.systems.aesthetics;

import com.redsquare.citizen.util.Sets;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ColorPalettes {
  /* Primaries */
  private static final Color PRIMARY_FIRE_RED = new Color(200, 55, 0);
  private static final Color PRIMARY_FOREST_GREEN = new Color(12, 118, 2);
  private static final Color PRIMARY_OBSIDIAN_PURPLE = new Color(56, 0, 80, 199);
  private static final Color PRIMARY_BLACK_GOLD = new Color(160, 125, 39);
  private static final Color PRIMARY_ROYAL_BLUE = new Color(36, 4, 220);

  /* Secondaries */
  private static final Color[] SEC_VOLCANIC = new Color[] {
          new Color(0, 0, 0),
          new Color(255, 150, 0),
          new Color(255, 70, 0),
          new Color(120, 0, 0)
  };
  private static final Color[] SEC_LUSH = new Color[] {
          new Color(255, 255, 0),
          new Color(0, 150, 100),
          new Color(100, 255, 0),
          new Color(40, 0, 80)
  };
  private static final Color[] SEC_INSTITUTION = new Color[] {
          new Color(255, 255, 255),
          new Color(170, 170, 170),
          new Color(85, 85, 85),
          new Color(0, 0, 0)
  };
  private static final Color[] SEC_LEATHER = new Color[] {
          new Color(166, 101, 27, 229),
          new Color(86, 43, 0, 209),
          new Color(210, 129, 57, 196),
          new Color(63, 15, 9, 222)
  };
  private static final Color[] SEC_ROYAL = new Color[] {
          new Color(189, 0, 135),
          new Color(203, 203, 203),
          new Color(237, 178, 43),
          new Color(255, 24, 55)
  };
  private static final Color[] SEC_DECO_CONTRIVED = new Color[] {
          new Color(251, 255, 0),
          new Color(119, 255, 207),
          new Color(255, 105, 38),
          new Color(255, 81, 143)
  };
  private static final Color[] SEC_DEIFY = new Color[] {
          new Color(0, 0, 0),
          new Color(161, 178, 231),
          new Color(255, 77, 0),
          new Color(96, 39, 180)
  };


  /* Validity Mapping */
  private static final Map<Color, Set<Color[]>> validCombinations = Map.ofEntries(
    Map.entry(PRIMARY_FIRE_RED, Set.of(SEC_VOLCANIC, SEC_LEATHER, SEC_DEIFY, SEC_ROYAL)),
          Map.entry(PRIMARY_BLACK_GOLD, Set.of(SEC_LEATHER, SEC_DECO_CONTRIVED)),
          Map.entry(PRIMARY_FOREST_GREEN, Set.of(SEC_LUSH, SEC_LEATHER)),
          Map.entry(PRIMARY_OBSIDIAN_PURPLE, Set.of(SEC_VOLCANIC, SEC_LEATHER, SEC_INSTITUTION)),
          Map.entry(PRIMARY_ROYAL_BLUE, Set.of(SEC_ROYAL, SEC_DEIFY))
  );

  private static Color randomPrimary() {
    return Sets.randomEntry(Set.of(PRIMARY_FIRE_RED, PRIMARY_FOREST_GREEN,
            PRIMARY_OBSIDIAN_PURPLE, PRIMARY_ROYAL_BLUE, PRIMARY_BLACK_GOLD));
  }

  private static Color[] randomSecondary(final Color PRIMARY) {
    return Sets.randomEntry(validCombinations.get(PRIMARY));
  }

  public static Color[] randomColorScheme() {
    Color primary = randomPrimary();
    Color[] secondary = randomSecondary(primary);

    Color[] all = new Color[secondary.length + 1];
    all[0] = primary;

    System.arraycopy(secondary, 0, all, 1, secondary.length);

    return all;
  }
}
