package com.redsquare.citizen.graphics;

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
}
