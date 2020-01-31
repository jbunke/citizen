package com.redsquare.citizen.graphics;

import java.awt.*;

/** Used for animating people, animals, and animate objects (i.e. wheelbarrows) */
public enum RenderDirection {
  U, UL, L, DL, D, DR, R, UR;

  public static RenderDirection fromAngle(double rad) {
    while (rad > 2 * Math.PI) rad -= (2 * Math.PI);
    while (rad < 0) rad += (2 * Math.PI);

    if (rad < Math.PI / 8. || rad >= (15 * Math.PI) / 8.) return R;
    else if (rad < (3 * Math.PI) / 8.) return UR;
    else if (rad < (5 * Math.PI) / 8.) return U;
    else if (rad < (7 * Math.PI) / 8.) return UL;
    else if (rad < (9 * Math.PI) / 8.) return L;
    else if (rad < (11 * Math.PI) / 8.) return DL;
    else if (rad < (13 * Math.PI) / 8.) return D;
    else return DR;
  }

  public Point moveByNUnits(final Point INPUT, final int N) {
    switch (this) {
      case D:
        return new Point(INPUT.x, INPUT.y + N);
      case DL:
        return new Point(INPUT.x - N, INPUT.y + N);
      case DR:
        return new Point(INPUT.x + N, INPUT.y + N);
      case L:
        return new Point(INPUT.x - N, INPUT.y);
      case R:
        return new Point(INPUT.x + N, INPUT.y);
      case U:
        return new Point(INPUT.x, INPUT.y - N);
      case UL:
        return new Point(INPUT.x - N, INPUT.y - N);
      case UR:
        return new Point(INPUT.x + N, INPUT.y - N);
    }

    return INPUT;
  }

  public static RenderDirection opposite(RenderDirection d) {
    switch (d) {
      case D:
        return U;
      case DL:
        return UR;
      case L:
        return R;
      case UL:
        return DR;
      case U:
        return D;
      case UR:
        return DL;
      case R:
        return L;
      case DR:
      default:
        return UL;
    }
  }
}
