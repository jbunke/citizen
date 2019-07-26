package com.redsquare.citizen.graphics;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * SEMANTIC MAPS are mappings from sprite animation codes that convey
 * semantic meaning to the coordinates of the corresponding sprite in the
 * sprite array. This way, I can make changes to sprite arrangements during
 * development and only have to change the code in one place.
 *
 * Example: the code "L-AGGRO-RUN-4" conveys direction, posture, activity,
 * and frame number
 * */
public class SemanticMaps {
  public static final Map<String, Point> HOMINID_BODY = Map.ofEntries(
          Map.entry("DL-CALM-IDLE-BASE", new Point(0, 0)),
          Map.entry("L-CALM-IDLE-BASE", new Point(1, 0)),
          Map.entry("UL-CALM-IDLE-BASE", new Point(2, 0)),
          Map.entry("U-CALM-IDLE-BASE", new Point(3, 0)),
          Map.entry("UR-CALM-IDLE-BASE", new Point(4, 0)),
          Map.entry("R-CALM-IDLE-BASE", new Point(5, 0)),
          Map.entry("DR-CALM-IDLE-BASE", new Point(6, 0)),
          Map.entry("D-CALM-IDLE-BASE", new Point(7, 0))
          // rest
  );

  public static Map<String, Point> FONT() {
    Map<String, Point> fontMap = new HashMap<>();

    for (int i = 32; i <= 126; i++) {
      fontMap.put(Character.toString((char) i), new Point(i - 32, 0));
    }

    return fontMap;
  }
}
