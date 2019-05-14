package com.redsquare.citizen.systems.language;

public class DemonymGenerator {
  public static String demonym(String placeName) {
    int remove = 0;
    String suffix = "";

    if (placeName.endsWith("es") || placeName.endsWith("is")
            || placeName.endsWith("ys")) {
      suffix = "hi";
    } else if (placeName.endsWith("s")) {
      suffix = "i";
    } else if (placeName.endsWith("i")) {
      suffix = "an";
    } else if (placeName.endsWith("any")) {
      remove = 1;
    } else if (placeName.endsWith("y")) {
      suffix = "an";
      remove = 1;
    } else if (placeName.endsWith("ain")) {
      suffix = "nish";
      remove = 2;
    } else if (placeName.endsWith("n")) {
      suffix = "ese";
    } else if (placeName.endsWith("ia") || placeName.endsWith("ca") ||
            placeName.endsWith("ka") || placeName.endsWith("la")) {
      suffix = "n";
    } else if (placeName.endsWith("a")) {
      suffix = "ian";
      remove = 1;
    } else if (Phonemes.endsWithAVowel(placeName)) {
      suffix = "nian";
    } else {
      suffix = "an";
    }

    return placeName.substring(0, placeName.length() - remove) + suffix;
  }
}
