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
 * Example: the code "W-AGGRO-RUN-4" conveys direction, posture, activity,
 * and frame number
 * */
public class SemanticMaps {
  public static final Map<String, Point> HOMINID_BODY = Map.ofEntries(
          Map.entry("SW-AGGRO-IDLE-BASE", new Point(0, 0)),
          Map.entry("SW-AGGRO-IDLE-1", new Point(0, 1))
          // Map.entry("S-IDLE-BASE", new Point(1, 0)),
          // ...
  );

  public static Map<String, Point> FONT() {
    Map<String, Point> fontMap = new HashMap<>();

    for (int i = 32; i <= 126; i++) {
      fontMap.put(Character.toString((char) i), new Point(i - 32, 0));
    }

    return fontMap;
  }
}
