package com.redsquare.citizen.entity;

import com.redsquare.citizen.entity.movement.MovementLogic;
import com.redsquare.citizen.entity.movement.RenderLogic;

public abstract class LivingMoving extends Lifeform {
  Sex sex;
  MovementLogic movementLogic;

  public String getSpriteCode() {
    RenderLogic rl = movementLogic.renderLogic();

    return rl.getDirection().name() + "-" + rl.getPosture().name() + "-" +
            rl.getActivity().name() + "-" + rl.getPoseNum();
  }
}
