package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SoundShift {
  private final static Map<String, Set<String>> LEGAL_SOUND_SHIFTS = Map.ofEntries(
          Map.entry("a", Set.of("e")),
          Map.entry("p", Set.of("b", "f")),
          Map.entry("b", Set.of("v", "p")),
          Map.entry("f", Set.of("v", "p")),
          Map.entry("v", Set.of("b", "f", "w")),
          Map.entry("d", Set.of("t")),
          Map.entry("t", Set.of("d")),
          Map.entry("q", Set.of("k", "kh")),
          Map.entry("k", Set.of("c", "q", "kh")),
          Map.entry("c", Set.of("k")),
          Map.entry("j", Set.of("y")),
          Map.entry("y", Set.of("i")),
          Map.entry("i", Set.of("y", "ee", "e")),
          Map.entry("z", Set.of("s")),
          Map.entry("s", Set.of("z", "x")),
          Map.entry("w", Set.of("v")),
          Map.entry("ae", Set.of("a", "e")),
          Map.entry("e", Set.of("ae", "a")),
          Map.entry("ee", Set.of("i")),
          Map.entry("sh", Set.of("ch")),
          Map.entry("ch", Set.of("c", "sh"))
          // TODO
  );

  final double consistency;
  final String from;
  final String to;

  private SoundShift(String[] subset) {
    consistency = Randoms.bounded(0.3, 1.0);

    String from = "NULL";
    int attempts = 0;

    while (!LEGAL_SOUND_SHIFTS.containsKey(from) && attempts < 100) {
      attempts++;
      from = subset[Randoms.bounded(0, subset.length)];
    }

    this.from = from;

    Set<String> tos = LEGAL_SOUND_SHIFTS.get(from);

    String to = Sets.randomEntry(tos);
    List<String> subsetList = Arrays.asList(subset);

    while (!subsetList.contains(to) && attempts < 100) {
      attempts++;
      to = Sets.randomEntry(tos);
    }

    this.to = subsetList.contains(to) ? to : "NULL";
  }

  static SoundShift vowelSoundShift(Phonology p) {
    return new SoundShift(p.VOWEL_PHONEMES);
  }

  static SoundShift prefixSoundShift(Phonology p) {
    return new SoundShift(p.PREFIX_CONS_PHONEMES);
  }

  static SoundShift suffixSoundShift(Phonology p) {
    return new SoundShift(p.SUFFIX_CONS_PHONEMES);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof SoundShift)) return false;

    SoundShift s = (SoundShift)other;

    return s.from.equals(from);
  }

  @Override
  public int hashCode() {
    int sum = 0;

    for (char c : from.toCharArray()) {
      sum *= 4;
      sum += c;
    }

    return sum;
  }
}
