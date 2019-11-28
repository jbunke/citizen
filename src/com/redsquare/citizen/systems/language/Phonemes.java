package com.redsquare.citizen.systems.language;

import java.util.List;
import java.util.Map;

public class Phonemes {
  static final String[] VOWEL_PHONEMES =
          new String[] { "a", "e", "i", "o", "u", "y",
                  "au", "ae", "ai",
                  "ee", "ea", "ei", "eu", "ey",
                  "ie",
                  "oo", "ou", "oa", "oi", "oe", "oy",
                  "ue"
  };
  static final String[] PREFIX_CONS_PHONEMES = new String[] {
          "y",
          "b", "c", "d", "f", "g", "h", "j", "k", "l", "m",
          "n", "p", "q", "r", "s", "t", "v", "w", "x", "z",
          "st", "sp", "sw", "sc", "sh", "sl", "sm", "sn",
          "kh", "ph", "pl", "pr",
          "ch", "cl", "cr",
          "fl", "fr",
          "th", "tr"
  };

  static final String[] SUFFIX_CONS_PHONEMES = new String[] {
          "y",
          "b", "c", "f", "g", "h", "j", "k", "l", "m",
          "n", "p", "q", "r", "s", "t", "v", "w", "x", "z",
          "st", "sp", "sh", "kh", "ch",
          "th", "ck"
  };

  static final Map<String, List<String>> ILLEGAL_PREFIX_TO_VOWEL = Map.ofEntries(
          Map.entry("y", List.of("oy", "y", "ey", "oi", "oa")),
          Map.entry("q", List.of("ei", "ee", "eu", "oe")),
          Map.entry("x", List.of("y")),
          Map.entry("st", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("sp", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("sw", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("sc", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("sh", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("sl", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("sm", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("sn", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("pl", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("pr", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("cl", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("cr", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("fl", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("fr", List.of("ei", "ai", "ey", "eu", "oy")),
          Map.entry("tr", List.of("ei", "ai", "ey", "eu", "oy"))
  );

  static final Map<String, List<String>> ILLEGAL_VOWEL_TO_SUFFIX = Map.ofEntries(
          Map.entry("y", List.of("y", "j", "q", "r")),
          Map.entry("ei", List.of("sp", "ck", "y", "j")),
          Map.entry("ey", List.of("sp", "ck", "y", "j")),
          Map.entry("oi", List.of("sp", "ck", "y", "j")),
          Map.entry("oy", List.of("sp", "ck", "y", "j")),
          Map.entry("ai", List.of("sp", "ck", "y", "j")),
          Map.entry("eu", List.of("sp", "ck", "y", "j"))
  );

  static boolean endsWithAVowel(String s) {
    boolean match = false;

    for (String vowel : VOWEL_PHONEMES) {
      match |= s.endsWith(vowel);
    }

    return match;
  }
}
