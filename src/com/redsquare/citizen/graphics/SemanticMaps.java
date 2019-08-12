package com.redsquare.citizen.graphics;

import com.redsquare.citizen.entity.movement.RenderLogic;

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

  public static final Map<String, Point> HOMINID_BODY = generateHominidBody();
  public static final Map<String, Point> HOMINID_FACE = generateHominidFace();

  private static Map<String, Point> generateHominidBody() {
    // TODO: temp with calm assumption

    Map<String, Point> map = new HashMap<>();

    RenderActivity[] activities = RenderActivity.values();
    int y = 0;

    for (RenderActivity activity : activities) {
      for (int f = 0; f < activity.frameCount; f++) {
        String rest = "CALM-" + activity.name() + "-" + f;

        map.put("DL-" + rest, new Point(0, y));
        map.put("L-" + rest, new Point(1, y));
        map.put("UL-" + rest, new Point(2, y));
        map.put("U-" + rest, new Point(3, y));
        map.put("UR-" + rest, new Point(4, y));
        map.put("R-" + rest, new Point(5, y));
        map.put("DR-" + rest, new Point(6, y));
        map.put("D-" + rest, new Point(7, y));

        y++;
      }
    }

    return map;
  }

  private static Map<String, Point> generateHominidFace() {
    Map<String, Point> map = new HashMap<>();

    RenderMood[] moods = RenderMood.values();

    for (int m = 0; m < moods.length; m++) {
      int m_y_offset = m * 4;
      for (int t = 0; t < 2; t++) {
        int t_y_offset = t * 2;
        for (int b = 0; b < 2; b++) {
          String rest = moods[m].name() + "-" +
                  (t == 0 ? "NOT_TALK" : "TALK") + "-" +
                  (b == 0 ? "NOT_BLINK" : "BLINK");

          map.put("U-" + rest, new Point(0, m_y_offset + t_y_offset + b));
          map.put("UL-" + rest, new Point(1, m_y_offset + t_y_offset + b));
          map.put("L-" + rest, new Point(2, m_y_offset + t_y_offset + b));
          map.put("DL-" + rest, new Point(3, m_y_offset + t_y_offset + b));
          map.put("D-" + rest, new Point(4, m_y_offset + t_y_offset + b));
          map.put("DR-" + rest, new Point(5, m_y_offset + t_y_offset + b));
          map.put("R-" + rest, new Point(6, m_y_offset + t_y_offset + b));
          map.put("UR-" + rest, new Point(7, m_y_offset + t_y_offset + b));
        }
      }
    }

    // TODO: Further sections where face is not animated independently of the body

    return map;
  }

  public static Point faceOffset(RenderLogic rl) {
    // TODO: Expand as needed
    switch (rl.getActivity()) {
      case IDLE:
        if (rl.getPoseNum() == 1)
          return new Point(72, 4);
    }

    return new Point(72, 0);
  }

  static Map<String, Point> FONT() {
    Map<String, Point> fontMap = new HashMap<>();

    for (int i = 32; i <= 126; i++) {
      fontMap.put(Character.toString((char) i), new Point(i - 32, 0));
    }

    return fontMap;
  }
}
