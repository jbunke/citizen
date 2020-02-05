package com.redsquare.citizen.entity.building;

import com.redsquare.citizen.entity.Building;
import com.redsquare.citizen.entity.collision.Collider;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entryway extends BuildingComponent {
  private final boolean canLock;
  private boolean isLocked;
  private boolean isOpen;

  private final FacingDirection facingDirection;

  public Entryway(final Building building, final FacingDirection facingDirection) {
    super(building);

    this.facingDirection = facingDirection;

    // TODO: temp
    this.canLock = false;
    this.isLocked = false;
    this.isOpen = false;

    setCollider();
  }

  public boolean isOpen() {
    return isOpen;
  }

  public boolean isLocked() {
    return isLocked;
  }

  public void tryOpenOrClose() {
    if (isOpen)
      this.isOpen = false;
    else {
      if (!isLocked)
        this.isOpen = true;
    }

    setCollider();
  }

  private void setCollider() {
    this.collider = !isOpen ?
            Collider.getColliderFromDoor(facingDirection) :
            Collider.getColliderFromType(Collider.EntityType.NO_COLLISION);
  }

  public enum FacingDirection {
    DOWN, LEFT, UP, RIGHT
  }

  @Override
  public BufferedImage getSprite() {
    BufferedImage sprite = new BufferedImage(72, 144, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) sprite.getGraphics();

    // TODO: Finish after date ;P

    switch (facingDirection) {
      case DOWN:
        if (isOpen) {
          g.setColor(new Color(100, 100, 100));
          g.fillRect(0, 0, 12, 72);

          g.setColor(new Color(0, 0, 0));
          g.fillRect(0, 72, 12, 72);
        } else {
          g.setColor(new Color(100, 100, 100));
          g.fillRect(0, 60, 72, 12);

          g.setColor(new Color(0, 0, 0));
          g.fillRect(0, 72, 72, 72);
        }
        break;
      case UP:
        if (isOpen) {
          g.setColor(new Color(100, 100, 100));
          g.fillRect(60, 0, 12, 72);

          g.setColor(new Color(0, 0, 0));
          g.fillRect(60, 72, 12, 72);
        } else {
          g.setColor(new Color(100, 100, 100));
          g.fillRect(0, 0, 72, 12);

          g.setColor(new Color(0, 0, 0));
          g.fillRect(0, 12, 72, 72);
        }
        break;
      case LEFT:

        break;
      case RIGHT:
      default:

        break;
    }

    drawCollision(g);
    drawCoordinate(g);

    return sprite;
  }
}
