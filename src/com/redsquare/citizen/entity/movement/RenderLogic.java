package com.redsquare.citizen.entity.movement;

import com.redsquare.citizen.graphics.RenderActivity;
import com.redsquare.citizen.graphics.RenderDirection;
import com.redsquare.citizen.graphics.RenderPosture;
import com.redsquare.citizen.util.Randoms;

public class RenderLogic {
  private RenderDirection direction;
  private RenderPosture posture;
  private RenderActivity activity;
  private int poseNum;

  private RenderActivity waiting;

  private int frameDuration;
  private int renderCounter;

  RenderLogic() {
    this.direction = RenderDirection.D;
    this.posture = RenderPosture.CALM;
    this.activity = RenderActivity.IDLE;
    this.poseNum = 0;

    this.waiting = null;

    setFrameDuration();
    this.renderCounter = 0;
  }

  private void setFrameDuration() {
    frameDuration = Randoms.bounded(activity.frameDurationRange[0],
            activity.frameDurationRange[1]);
  }

  void update() {
    renderCounter++;

    if (renderCounter >= frameDuration) {
      renderCounter = 0;

      if (poseNum + 1 < activity.frameCount) {
        poseNum++;
      } else {
        if (waiting != null) {
          this.activity = waiting;
          this.waiting = null;
          setFrameDuration();
        }
        poseNum = 0;
      }
    }
  }

  public void setDirection(RenderDirection direction) {
    this.direction = direction;
  }

  void setActivity(RenderActivity activity) {
    if (this.activity == activity) return;

    if (activity.priority >= this.activity.priority) {
      this.activity = activity;
      this.poseNum = 0;
      this.renderCounter = 0;

      this.waiting = null;

      setFrameDuration();
    } else {
      this.waiting = activity;
    }
  }

  public RenderDirection getDirection() {
    return direction;
  }

  public RenderPosture getPosture() {
    return posture;
  }

  public RenderActivity getActivity() {
    return activity;
  }

  public int getPoseNum() {
    return poseNum;
  }
}
