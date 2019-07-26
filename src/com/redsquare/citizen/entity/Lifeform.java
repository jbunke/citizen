package com.redsquare.citizen.entity;

import com.redsquare.citizen.graphics.RenderActivity;
import com.redsquare.citizen.graphics.RenderDirection;
import com.redsquare.citizen.graphics.RenderPosture;
import com.redsquare.citizen.systems.time.GameDate;

public abstract class Lifeform extends Entity {
  RenderDirection direction;
  RenderPosture posture;
  RenderActivity activity;
  int poseNum;

  String getSpriteCode() {
    return direction.name() + "-" + posture.name() + "-" +
            activity.name() + "-" +
            (poseNum == 0 ? "BASE" : String.valueOf(poseNum));
  }

  /**
   * @return The age of the life form in game-years
   * */
  abstract int age(GameDate now);
}
