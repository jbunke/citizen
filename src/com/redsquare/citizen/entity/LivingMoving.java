package com.redsquare.citizen.entity;

import com.redsquare.citizen.entity.movement.MovementLogic;
import com.redsquare.citizen.entity.movement.RenderLogic;
import com.redsquare.citizen.graphics.RenderActivity;
import com.redsquare.citizen.graphics.RenderDirection;
import com.redsquare.citizen.graphics.RenderPosture;

public abstract class LivingMoving extends Lifeform {
  Sex sex;
  MovementLogic movementLogic;

  RenderDirection direction;
  RenderPosture posture;
  RenderActivity activity;
  int poseNum;

  public String getSpriteCode() {
    RenderLogic rl = movementLogic.renderLogic();

    return rl.getDirection().name() + "-" + rl.getPosture().name() + "-" +
            rl.getActivity().name() + "-" + rl.getPoseNum();
  }
}
