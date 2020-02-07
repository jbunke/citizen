package com.redsquare.citizen.entity;

import com.redsquare.citizen.entity.building.BuildingComponent;
import com.redsquare.citizen.entity.building.BuildingLayouts;
import com.redsquare.citizen.entity.building.Entryway;
import com.redsquare.citizen.entity.building.Wall;
import com.redsquare.citizen.entity.collision.Collider;
import com.redsquare.citizen.worldgen.TileID;
import com.redsquare.citizen.worldgen.World;
import com.redsquare.citizen.worldgen.WorldPosition;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class Building extends Entity {

  private final Set<BuildingComponent> buildingComponents;
  /* f : Floor, w : Wall,
   * d : Downward-facing entryway, u : Upward ..., l : Left..., r : Right...
   * v : Void, s : SideDoor */
  private final char[][] layout;

  private Building(final WorldPosition worldPosition, final char[][] layout) {
    this.position = new WorldPosition(worldPosition.world(), worldPosition.cell(),
            WorldPosition.centralWithinSubCell(), worldPosition.getWorld(), this);
    this.collider = Collider.getColliderFromType(Collider.EntityType.NO_COLLISION);

    this.layout = layout;
    this.buildingComponents = new HashSet<>();

    World world = this.position.getWorld();

    for (int y = 0; y < layout.length; y++) {
      for (int x = 0; x < layout[y].length; x++) {
        BuildingComponent component;

        switch (layout[y][x]) {
          case 'w':
            component = new Wall(this);
            break;
          case 'd':
            component = new Entryway(this, Entryway.FacingDirection.DOWN);
            break;
          case 'u':
            component = new Entryway(this, Entryway.FacingDirection.UP);
            break;
          case 'l':
            component = new Entryway(this, Entryway.FacingDirection.LEFT);
            break;
          case 'r':
            component = new Entryway(this, Entryway.FacingDirection.RIGHT);
            break;
          case 'f':
          case 'v':
          default:
            component = null;
            break;
        }

        if (layout[y][x] != 'v') {
          world.getCell(worldPosition.world().x, worldPosition.world().y).
                  getSubCell(worldPosition.cell().x + x,
                          worldPosition.cell().y + y).
                  setTileID(TileID.COBBLESTONE_FLOOR);
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
    char[][] layout = BuildingLayouts.randomResidentialLayout();
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
