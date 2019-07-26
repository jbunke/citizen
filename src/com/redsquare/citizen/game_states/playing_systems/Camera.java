package com.redsquare.citizen.game_states.playing_systems;

import com.redsquare.citizen.GameDebug;
import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.config.Settings;
import com.redsquare.citizen.entity.Entity;
import com.redsquare.citizen.entity.Player;
import com.redsquare.citizen.input_events.Event;
import com.redsquare.citizen.input_events.KeyPressEvent;
import com.redsquare.citizen.util.FloatPoint;
import com.redsquare.citizen.util.MathExt;
import com.redsquare.citizen.worldgen.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.redsquare.citizen.GameManager.WorldMaths;

public class Camera {
  private static final int IN = 1, OUT = 2, X = 0, Y = 1;

  private final Entity target;
  private int zoomLevel;

  private Point worldLocation;
  private Point cellLocation;
  private FloatPoint subCellLocation;

  private FloatPoint ref;

  private Camera(Entity target) {
    this.target = target;

    this.zoomLevel = IN;

    this.worldLocation = target.worldLocation();
    this.cellLocation = target.cellLocation();
    this.subCellLocation = target.subCellLocation();

    this.ref = new FloatPoint(0, 0);
  }

  public static Camera generate(Entity target) {
    return new Camera(target);
  }

  private boolean tooFar(Entity entity) {
    return MathExt.distance(new FloatPoint(0., 0.),
            referenceEntityLocation(entity)) >
            20 * WorldMaths.CELL_DIMENSION_LENGTH * zoomLevel;
  }

  private FloatPoint referenceEntityLocation(Entity entity) {
    double worldCellLength = WorldMaths.CELLS_IN_WORLD_CELL_DIM *
            WorldMaths.CELL_DIMENSION_LENGTH;
    double x = ((entity.worldLocation().x - worldLocation.x) * worldCellLength) +
            ((entity.cellLocation().x - cellLocation.x) * WorldMaths.CELL_DIMENSION_LENGTH) +
            (entity.subCellLocation().x - subCellLocation.x);
    double y = ((entity.worldLocation().y - worldLocation.y) * worldCellLength) +
            ((entity.cellLocation().y - cellLocation.y) * WorldMaths.CELL_DIMENSION_LENGTH) +
            (entity.subCellLocation().y - subCellLocation.y);

     return new FloatPoint(x, y);
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
      worldLocation = target.worldLocation();
      cellLocation = target.cellLocation();
      subCellLocation = target.subCellLocation();
    } else {
      double taperOff;
      if (distance / screenAcross < 0.2) taperOff = 0.05;
      else if (distance / screenAcross < 0.5) taperOff = 0.1;
      else taperOff = 0.2;

      FloatPoint adj = new FloatPoint(ref.x * taperOff,
              ref.y * taperOff);
      subCellLocation = new FloatPoint(subCellLocation.x + adj.x,
              subCellLocation.y + adj.y);
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

  public void render(Graphics2D g, Set<Entity> candidates, World world) {
    // TODO - draw world behind and sort candidates by y pos

    List<Entity> entities = new ArrayList<>();

    candidates.forEach(x -> {
      if (!tooFar(x)) entities.add(x);
    });

    Collections.sort(entities);

    entities.forEach(x -> renderEntity(g, x));

    if (GameDebug.isActive()) {
      g.setColor(new Color(0, 0, 0));
      g.setStroke(new BasicStroke(1));
      g.drawLine(Settings.SCREEN_DIM[0] / 2, 0,
              Settings.SCREEN_DIM[0] / 2, Settings.SCREEN_DIM[1]);
      g.drawLine(0, Settings.SCREEN_DIM[1] / 2,
              Settings.SCREEN_DIM[0], Settings.SCREEN_DIM[1] / 2);
    }
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

    int[] pixelLock = new int[] { (4 / zoomLevel) - (loc[X] % (4 / zoomLevel)),
            (4 / zoomLevel) - (loc[Y] % (4 / zoomLevel)) };

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
