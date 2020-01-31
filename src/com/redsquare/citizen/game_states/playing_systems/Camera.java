package com.redsquare.citizen.game_states.playing_systems;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.config.Settings;
import com.redsquare.citizen.entity.Entity;
import com.redsquare.citizen.entity.Player;
import com.redsquare.citizen.input_events.Event;
import com.redsquare.citizen.input_events.KeyPressEvent;
import com.redsquare.citizen.util.FloatPoint;
import com.redsquare.citizen.util.MathExt;
import com.redsquare.citizen.worldgen.World;
import com.redsquare.citizen.worldgen.WorldPosition;
import com.redsquare.citizen.worldgen.WorldSubCell;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class Camera {
  private static final int IN = 1, OUT = 2, X = 0, Y = 1;

  private final Entity target;
  private int zoomLevel;

  private WorldPosition position;

  private FloatPoint ref;

  // DEBUG INFO
  private int entityCount = 0;

  private Camera(Entity target) {
    this.target = target;

    this.zoomLevel = IN;

    this.position = WorldPosition.copy(target.position());

    this.ref = new FloatPoint(0, 0);
  }

  public static Camera generate(Entity target) {
    return new Camera(target);
  }

  private boolean tooFar(Entity entity) {
    return MathExt.distance(new FloatPoint(0., 0.),
            referenceEntityLocation(entity)) >
            20 * WorldPosition.CELL_DIMENSION_LENGTH * zoomLevel;
  }

  private FloatPoint referenceEntityLocation(Entity entity) {
    return WorldPosition.diff(position, entity.position());
  }

  private void adjustRef() {
    ref = referenceEntityLocation(target);
  }

  private void track() {
    double distance = MathExt.distance(ref, new FloatPoint(0, 0));
    double screenAcross = MathExt.distance(new Point(0, 0),
            new Point(Settings.SCREEN_DIM[0], Settings.SCREEN_DIM[1]));

    if (distance < 3) {
      // MATCH LOCATION
      position = WorldPosition.copy(target.position());
    } else {
      double taperOff;
      if (distance / screenAcross < 0.2) taperOff = 0.05;
      else if (distance / screenAcross < 0.5) taperOff = 0.1;
      else taperOff = 0.2;

      FloatPoint adj = new FloatPoint(ref.x * taperOff,
              ref.y * taperOff);
      position.move(adj.x, adj.y);
    }
  }

  public void update() {
    adjustRef();
    track();
  }

  public void input(InputHandler inputHandler) {
    List<Event> events = inputHandler.getUnprocessedEvents();
    for (int i = 0; i < events.size(); i++) {
      boolean processed = false;

      if (events.get(i) instanceof KeyPressEvent) {
        KeyPressEvent kpe = (KeyPressEvent) events.get(i);

        if (kpe.eventType == KeyPressEvent.EventType.RELEASED &&
                ControlScheme.get().getAction(kpe.key) == ControlScheme.Action.ZOOM) {
          zoomLevel = zoomLevel == IN ? OUT : IN;
          processed = true;
        }
      } // TODO - else case for mouse move / click events

      if (processed) {
        events.remove(i);
        i--;
      }
    }
  }

  public void mouseToWorldLocation(InputHandler inputHandler, Player player) {
    int[] mouse = new int[] { inputHandler.getMouseX(), inputHandler.getMouseY() };

    int[] playerloc = entityCenter(player);

    FloatPoint lookingRef =
            new FloatPoint((mouse[X] - playerloc[X]) * zoomLevel,
                    (mouse[Y] - playerloc[Y]) * zoomLevel);

    player.setLookingRef(lookingRef);
  }

  public void render(Graphics2D g, World world) {
    // TODO - draw world behind and sort candidates by y pos
    WorldSubCell[][] subCells = getSubCells(world);
    renderSubCells(g, subCells);

    Set<Entity> candidates = getCandidateEntities(world);
    List<Entity> entities = new ArrayList<>();

    // Candidates are filtered out based on distance from the center of the camera
    candidates.forEach(x -> {
      if (!tooFar(x)) entities.add(x);
    });

    // Entities are sorted by y-position, which determines render order
    Collections.sort(entities);

    if (entities.size() != entityCount) {
      entityCount = entities.size();
      GameDebug.printMessage(
              "Rendering: " + entityCount + " entities",
              GameDebug::printDebug);
    }

    entities.forEach(x -> {
      x.renderUpdate();
      renderEntity(g, x);
    });

    if (GameDebug.isActive()) {
      g.setColor(new Color(0, 0, 0));
      g.setStroke(new BasicStroke(1));
      g.drawLine(Settings.SCREEN_DIM[X] / 2, 0,
              Settings.SCREEN_DIM[X] / 2, Settings.SCREEN_DIM[Y]);
      g.drawLine(0, Settings.SCREEN_DIM[Y] / 2,
              Settings.SCREEN_DIM[X], Settings.SCREEN_DIM[1] / 2);
    }
  }

  private Set<Entity> getCandidateEntities(World world) {
    final int MARGIN = 40;

    int startX = position.cell().x < MARGIN ? -1 : 0;
    int endX = position.cell().x >
            WorldPosition.CELLS_IN_WORLD_CELL_DIM - MARGIN ? 1 : 0;
    int startY = position.cell().y < MARGIN ? -1 : 0;
    int endY = position.cell().y >
            WorldPosition.CELLS_IN_WORLD_CELL_DIM - MARGIN ? 1 : 0;

    Set<Entity> candidates = new HashSet<>();

    for (int x = startX; x <= endX; x++) {
      for (int y = startY; y <= endY; y++) {
        candidates.addAll(
                world.getCell(position.world().x + x,
                        position.world().y + y).getEntities());
      }
    }

    return candidates;
  }

  private void renderSubCells(Graphics2D g, WorldSubCell[][] subCells) {

    int[] initial = new int[] {
            // (int)(-1 * position.subCell().x) / zoomLevel,
            // (int)(-1 * position.subCell().y) / zoomLevel
            // X:
            (Settings.SCREEN_DIM[X] / 2) -
                    (int)((position.subCell().x + (((subCells.length - 1) / 2) *
                            WorldPosition.CELL_DIMENSION_LENGTH)) / zoomLevel),
            // Y:
            (Settings.SCREEN_DIM[Y] / 2) -
                    (int)((position.subCell().y + ((subCells[0].length / 2) *
                            WorldPosition.CELL_DIMENSION_LENGTH)) / zoomLevel)
    };
    int[] increments = new int[] { (int)WorldPosition.CELL_DIMENSION_LENGTH / zoomLevel,
            (int)WorldPosition.CELL_DIMENSION_LENGTH / zoomLevel };
    int[] loc = new int[] { initial[X], initial[Y] };

    for (WorldSubCell[] row : subCells) {
      for (WorldSubCell subCell : row) {
        g.drawImage(subCell.draw(zoomLevel), loc[X], loc[Y], null);

        loc[Y] += increments[Y];
      }
      loc[X] += increments[X];
      loc[Y] = initial[Y];
    }
  }

  private WorldSubCell[][] getSubCells(World world) {
    final int WORLD = 0, CELL = 1;

    int width = (int)Math.ceil((Settings.SCREEN_DIM[X] * zoomLevel) /
            WorldPosition.CELL_DIMENSION_LENGTH) + 2;
    int height = (int)Math.ceil((Settings.SCREEN_DIM[Y] * zoomLevel) /
            WorldPosition.CELL_DIMENSION_LENGTH) + 1;

    WorldSubCell[][] subCells = new WorldSubCell[width][height];

    int[] xCoords = new int[] { position.world().x,
            position.cell().x - (int)Math.ceil(width / 2) };
    int[] yCoords = new int[] { position.world().y,
            position.cell().y - (int)Math.ceil(height / 2) };

    if (xCoords[CELL] < 0) {
      xCoords[WORLD]--;
      xCoords[CELL] += WorldPosition.CELLS_IN_WORLD_CELL_DIM;
    } else if (xCoords[CELL] >= WorldPosition.CELLS_IN_WORLD_CELL_DIM) {
      xCoords[WORLD]++;
      xCoords[CELL] -= WorldPosition.CELLS_IN_WORLD_CELL_DIM;
    }

    if (yCoords[CELL] < 0) {
      yCoords[WORLD]--;
      yCoords[CELL] += WorldPosition.CELLS_IN_WORLD_CELL_DIM;
    } else if (yCoords[CELL] >= WorldPosition.CELLS_IN_WORLD_CELL_DIM) {
      yCoords[WORLD]++;
      yCoords[CELL] -= WorldPosition.CELLS_IN_WORLD_CELL_DIM;
    }

    int[] origYCoords = Arrays.copyOf(yCoords, 2);

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        subCells[x][y] = world.getCell(xCoords[WORLD], yCoords[WORLD]).
                getSubCell(xCoords[CELL], yCoords[CELL]);

        yCoords[CELL]++;
        if (yCoords[CELL] >= WorldPosition.CELLS_IN_WORLD_CELL_DIM) {
          yCoords[CELL] = 0;
          yCoords[WORLD]++;
        }
      }
      yCoords = Arrays.copyOf(origYCoords, 2);

      xCoords[CELL]++;
      if (xCoords[CELL] >= WorldPosition.CELLS_IN_WORLD_CELL_DIM) {
        xCoords[CELL] = 0;
        xCoords[WORLD]++;
      }
    }

    return subCells;
  }

  private void renderEntity(Graphics2D g, Entity entity) {
    BufferedImage sprite = entity.getSprite();
    Point offset = entity.getSpriteOffset();

    int[] center = new int[] { Settings.SCREEN_DIM[X] / 2,
            Settings.SCREEN_DIM[Y] / 2 };

    int[] size = new int[] { sprite.getWidth() / zoomLevel,
            sprite.getHeight() / zoomLevel };

    if (zoomLevel == OUT) offset = new Point(offset.x / 2, offset.y / 2);

    FloatPoint distance = referenceEntityLocation(entity);
    int[] centerOffset = new int[] { (int)(distance.x / zoomLevel),
            (int)(distance.y / zoomLevel) };

    int[] loc = new int[] { center[X] + centerOffset[X] + offset.x,
            center[Y] + centerOffset[Y] + offset.y };

    int[] pixelLock = new int[] { (4 / zoomLevel) - ((int) (entity.position().subCell().x / zoomLevel) % (4 / zoomLevel)),
            (4 / zoomLevel) - ((int) (entity.position().subCell().y / zoomLevel) % (4 / zoomLevel)) };

    if (pixelLock[0] > (4 / zoomLevel) / 2) pixelLock[0] -= (4 / zoomLevel);
    if (pixelLock[1] > (4 / zoomLevel) / 2) pixelLock[1] -= (4 / zoomLevel);

    g.drawImage(sprite, loc[X] + pixelLock[X], loc[Y] + pixelLock[Y],
            size[X], size[Y], null);
  }

  private int[] entityCenter(Entity entity) {
    int[] center = new int[] { Settings.SCREEN_DIM[X] / 2,
            Settings.SCREEN_DIM[Y] / 2 };

    FloatPoint distance = referenceEntityLocation(entity);
    int[] centerOffset = new int[] { (int)(distance.x / zoomLevel),
            (int)(distance.y / zoomLevel) };

    return new int[] { center[X] + centerOffset[X],
            center[Y] + centerOffset[Y] };
  }
}
