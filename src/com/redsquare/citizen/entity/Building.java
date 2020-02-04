package com.redsquare.citizen.entity;

import com.redsquare.citizen.entity.building.BuildingComponent;
import com.redsquare.citizen.entity.building.Wall;
import com.redsquare.citizen.entity.collision.Collider;
import com.redsquare.citizen.worldgen.WorldPosition;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class Building extends Entity {
  private static char[][][] RESIDENTIAL_LAYOUTS = new char[][][] {
          new char[][] {
                  new char[] { 'w', 'w', 'w', 'w', 'w' },
                  new char[] { 'w', 'f', 'f', 'f', 'w' },
                  new char[] { 'w', 'f', 'f', 'f', 'w' },
                  new char[] { 'w', 'f', 'f', 'f', 'w' },
                  new char[] { 'w', 'f', 'f', 'f', 'w' },
                  new char[] { 'w', 'w', 'd', 'w', 'w' }
          }
  };

  private final Set<BuildingComponent> buildingComponents;
  // f : Floor, w : Wall, d : Door, v : Void, s : SideDoor
  private final char[][] layout;

  private Building(final WorldPosition worldPosition, final char[][] layout) {
    this.position = new WorldPosition(worldPosition.world(), worldPosition.cell(),
            WorldPosition.centralWithinSubCell(), worldPosition.getWorld(), this);
    this.collider = Collider.getColliderFromType(Collider.EntityType.NO_COLLISION);

    this.layout = layout;
    this.buildingComponents = new HashSet<>();

    for (int y = 0; y < layout.length; y++) {
      for (int x = 0; x < layout[y].length; x++) {
        BuildingComponent component;

        switch (layout[y][x]) {
          case 'w':
            component = new Wall(this);
            break;
          case 'f':
          case 'v':
          default:
            component = null;
            break;
        }

        if (component == null)
          continue;

        WorldPosition componentPosition = new WorldPosition(worldPosition.world(),
                new Point(worldPosition.cell().x + x, worldPosition.cell().y + y),
                WorldPosition.centralWithinSubCell(), worldPosition.getWorld(), component);
        component.setPosition(componentPosition);

        buildingComponents.add(component);
      }
    }
  }

  public static Building generate(WorldPosition position) {
    // TODO
    char[][] layout = RESIDENTIAL_LAYOUTS[0];
    return new Building(position, layout);
  }

  @Override
  public BufferedImage getSprite() {
    return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
  }

  @Override
  public Point getSpriteOffset() {
    return new Point(0, 0);
  }
}
