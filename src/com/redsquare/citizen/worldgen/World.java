package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.GameDebug;
import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.systems.language.WritingSystem;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.politics.State;
import com.redsquare.citizen.systems.vexillology.Flag;
import com.redsquare.citizen.util.Formatter;
import com.redsquare.citizen.util.Sets;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class World {

  public final static int DEFAULT_WIDTH = 640;
  public final static int DEFAULT_HEIGHT = 360;

  private final static int LAND_FILL_REPS = 1;
  private final static int LAND_PEER_THRESHOLD = 5;

  private final static double MOUNTAIN_PROB = 0.85;
  private final static double HILL_PROB = 0.9;
  private final static double BEACH_PROB = 0.95;
  private final static double FOREST_PROB = 0.2;
  private final static int MOUNTAIN_RANGE = 1;
  private final static int HILL_RANGE = 10;
  private static final int BEACH_RANGE = 1;
  private static final int SHALLOW_RANGE = 2;
  private static final int FOREST_RANGE = 10;
  private static final double FOREST_SPAWN_PROB = 0.05;
  private static final int FOREST_MAX_DIAM = 20;
  private static final int MIN_DESERTS = 3;
  private static final double DESERT_PROB = 0.95;
  private static final double DESERT_MULT = 1.5;
  private static final int MAX_DESERTS = 10;
  private static final int DESERT_MAX_DIST = 60;
  private static final int DESERT_PEER_THRESHOLD = 5;
  private static final int DESERT_FILL_REPS = 3;

  private boolean generated[][];

  private Set<Settlement> settlements = null;

  private final TectonicPlate[] plates;
  private final Set<State> states;
  private final WorldCell[][] cells;
  private State[][] borders;
  private final List<River> rivers;

  private final int width;
  private final int height;

  private int poleAt;

  public static World safeCreate(int width, int height,
                                 int plateCount, int trials) {
    boolean success = false;
    World world = null;

    while (!success && trials > 0) {
      success = true;
      trials--;

      try {
        world = new World(width, height, plateCount);
      } catch (StackOverflowError e) {
        GameDebug.printMessage("World creation attempt failed for (" +
                width + ", " + height + ")", GameDebug::printDebug);
        success = false;
      }
    }

    if (world != null)
      GameDebug.printMessage("World creation success", GameDebug::printDebug);

    return world;
  }

  public World(int width, int height, int plateCount) {
    this.width = width;
    this.height = height;

    // GENERATE PLATES
    plates = new TectonicPlate[plateCount];
    generatePlates(plateCount);

    // OCEANIC PLATES
    sortPlatesByArea();
    int oceanicCount = (int) (plateCount * 0.3);

    for (int i = 0; i < oceanicCount; i++) {
      plates[i * 2].makeOceanic();
    }

    // BASIC LAND/SEA
    cells = new WorldCell[width][height];
    for (TectonicPlate plate : plates) {
      plate.createTerrain(cells);
    }

    for (int i = 0; i < LAND_FILL_REPS; i++)
      fillInterstitialLand();

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (plateIndex(x, y) == -1)
          cells[x][y] = new WorldCell(WorldCell.Type.SEA, this, new Point(x, y));
      }
    }

    // ELEVATION
    elevationDetermination();

    // RIVERS
    rivers = new ArrayList<>();
    for (TectonicPlate plate : plates) {
      rivers.addAll(plate.generateRivers(cells));
    }

    // DESERTS
    generateDeserts();

    for (int i = 0; i < DESERT_FILL_REPS; i++) {
      for (int x = 1; x < width - 1; x++) {
        for (int y = 1; y < height - 1; y++) {
          if (cells[x][y].getType() != WorldCell.Type.PLAIN) continue;

          int peers = 0;
          for (int x1 = x - 1; x1 <= x + 1; x1++) {
            for (int y1 = y - 1; y1 <= y + 1; y1++) {
              if (cells[x1][y1].getType() == WorldCell.Type.DESERT)
                peers++;
            }
          }

          if (peers >= DESERT_PEER_THRESHOLD)
            cells[x][y].setElevationAndType(0, WorldCell.Type.DESERT);
        }
      }
    }

    // FOREST
    generateForests();

    // POLES
    generatePoles();

    /*
     * END OF PHYSICAL GEOGRAPHY
     *
     * START OF POLITICAL GEOGRAPHY
     * */

    // STATES
    states = new HashSet<>();

    for (TectonicPlate plate : plates) {
      states.add(plate.generateState(cells));
    }

    // REMOVE STATES WITH NO SETTLEMENTS
    removeTrivialStates();

    // BORDERS
    establishBorders();
  }

  private void removeTrivialStates() {
    Set<State> toRemove = new HashSet<>();
    for (State state : states) {
      if (state.settlements().isEmpty()) toRemove.add(state);
    }
    states.removeAll(toRemove);
  }

  private void establishBorders() {
    borders = new State[width][height];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (cells[x][y].isLand()) {
          // check for settlement on tile
          if (cells[x][y].hasSettlement()) {
            borders[x][y] = cells[x][y].getSettlement().getState();
            cells[x][y].populateProvince(cells[x][y].getSettlement().regionCapital());
            continue;
          }

          // find closest settlement
          Settlement closest = closestTo(x, y);

          if (closest != null) {
            borders[x][y] = closest.getState();
            cells[x][y].populateProvince(closest.regionCapital());
          }
        }
      }
    }
  }

  private Settlement closestTo(int x, int y) {
    if (cells[x][y].hasSettlement()) return cells[x][y].getSettlement();

    Set<Settlement> all = allSettlements();

    double closestDistance = Double.MAX_VALUE;
    Settlement closest = null;

    for (Settlement s : all) {
      double distance = Math.hypot(Math.abs(x - s.getLocation().x),
              Math.abs(y - s.getLocation().y));
      if (distance < closestDistance) {
        closest = s;
        closestDistance = distance;
      }
    }

    return closest;
  }

  int getWidth() {
    return width;
  }

  int getHeight() {
    return height;
  }

  WorldCell getCell(int x, int y) {
    return (x >= 0 && x < width && y >= 0 && y < height) ? cells[x][y] : null;
  }

  public Set<State> getStates() {
    return states;
  }

  public Set<Settlement> allSettlements() {
    if (settlements != null) return settlements;

    Set<Set<Settlement>> settlementsPerState = new HashSet<>();
    states.forEach(x -> settlementsPerState.add(x.settlements()));

    settlements = Sets.union(settlementsPerState);

    return settlements;
  }

  public Settlement randomSettlement() {
    return Sets.randomEntry(allSettlements());
  }

  private void generatePoles() {
    poleAt = width / 2;
    Point northPole = new Point(poleAt, 0);
    Point southPole = new Point((width - 1) - poleAt, height - 1);

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        double distNP = Math.hypot(Math.abs(northPole.x - x),
                Math.abs(northPole.y - y));
        double distSP = Math.hypot(Math.abs(southPole.x - x),
                Math.abs(southPole.y - y));

        if (distNP == 0 || distSP == 0) {
          cells[x][y].setRegion(WorldCell.Region.POLAR);
          continue;
        }

        double ratio = distNP / distSP;
        double northVariation = 0.8 + (0.4 * Math.random());
        double southVariation = 0.8 + (0.4 * Math.random());

        if (ratio < (1/4.0) * northVariation || ratio > 4.0 / southVariation) {
          cells[x][y].setRegion(WorldCell.Region.POLAR);
        } else if (ratio < (1/2.0) * northVariation || ratio > (2/1.0) / southVariation) {
          cells[x][y].setRegion(WorldCell.Region.TEMPERATE);
        } else if (ratio < (4/5.0) * northVariation || ratio > (5/4.0) / southVariation) {
          cells[x][y].setRegion(WorldCell.Region.SUBTROPICAL);
        } else {
          cells[x][y].setRegion(WorldCell.Region.TROPICAL);
        }
      }
    }
  }

  private void generateDeserts() {
    int desertCount = MIN_DESERTS +
            (int)((MAX_DESERTS - MIN_DESERTS) * Math.random());

    for (int i = 0; i < desertCount; i++) {
      boolean found = false;
      Point point = randomPoint();

      while (!found) {
        point = randomPoint();

        if (cells[point.x][point.y].getType() == WorldCell.Type.PLAIN)
          found = true;
      }

      spawnDesert(point.x, point.y, DESERT_MAX_DIST);
    }
  }

  private void spawnDesert(int x, int y, int maxDist) {
    generated = new boolean[width][height];

    generated[x][y] = true;
    cells[x][y].setElevationAndType(0, WorldCell.Type.DESERT);

    extendDesert(new Point(x, y), x - 1, y, maxDist);
    extendDesert(new Point(x, y), x + 1, y, maxDist);
    extendDesert(new Point(x, y), x, y - 1, maxDist);
    extendDesert(new Point(x, y), x, y + 1, maxDist);
  }

  private void extendDesert(Point origin, int x, int y, int maxDist) {
    // Out of bounds check
    if (x < 0 || x >= width || y < 0 || y >+ height) return;

    // Generated check
    if (generated[x][y]) return;

    double dist = Math.hypot(Math.abs(x - origin.x), Math.abs(y - origin.y));

    generated[x][y] = true;

    if (dist > maxDist) return;

    if (Math.random() < DESERT_PROB &&
            Math.random() / DESERT_MULT < 1 - (dist / maxDist)) {
      if (cells[x][y].getType() == WorldCell.Type.PLAIN) {
        cells[x][y].setElevationAndType(0, WorldCell.Type.DESERT);

        extendDesert(origin, x - 1, y, maxDist);
        extendDesert(origin, x + 1, y, maxDist);
        extendDesert(origin, x, y - 1, maxDist);
        extendDesert(origin, x, y + 1, maxDist);
      }
    }
  }

  private void generateForests() {
    for (River river : rivers) {
      for (River.RiverPoint riverPoint : river.getRiverPoints()) {
        Point loc = riverPoint.point;

        for (int x = Math.max(loc.x - FOREST_RANGE, 0);
             x < Math.min(loc.x + FOREST_RANGE, width); x++) {
          for (int y = Math.max(loc.y - FOREST_RANGE, 0);
               y < Math.min(loc.y + FOREST_RANGE, height); y++) {
            if (!cells[x][y].isLand()) continue;

            if (Math.random() < 1 -
                    (Math.hypot(Math.abs(x - loc.x),
                            Math.abs(y - loc.y)) / FOREST_RANGE)) {
              if (Math.random() < FOREST_PROB && cells[x][y].getElevation() == 0) {
                if (cells[x][y].getType() == WorldCell.Type.PLAIN) {
                  cells[x][y].setElevationAndType(0, WorldCell.Type.FOREST);
                }

                if (Math.random() < FOREST_SPAWN_PROB)
                  spawnForest(x, y, FOREST_MAX_DIAM);
              }
            }
          }
        }
      }
    }
  }

  private void spawnForest(int x, int y, int maxDist) {
    for (int x1 = Math.max(x - maxDist, 0);
         x1 < Math.min(x + maxDist, width); x1++) {
      for (int y1 = Math.max(y - maxDist, 0);
           y1 < Math.min(y + maxDist, height); y1++) {
        double dist = Math.hypot(Math.abs(x - x1), Math.abs(y - y1));

        if (Math.random() < FOREST_PROB &&
                Math.random() < 1 - (dist / maxDist)) {
          if (cells[x1][y1].getType() == WorldCell.Type.PLAIN) {
            cells[x1][y1].setElevationAndType(0, WorldCell.Type.FOREST);
          }
        }
      }
    }
  }

  private void elevationDetermination() {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int plateIndex = plateIndex(x, y);
        if (cells[x][y].isLand()) {
          // can be mountain, hill, beach
          boolean isMountain = false;

          for (int x1 = Math.max(0, x - MOUNTAIN_RANGE); x1 <= Math.min(width - 1, x + MOUNTAIN_RANGE); x1++) {
            for (int y1 = Math.max(0, y - MOUNTAIN_RANGE); y1 <= Math.min(height - 1, y + MOUNTAIN_RANGE); y1++) {
              if (cells[x1][y1].isLand() && plateIndex(x1, y1) != plateIndex)
                isMountain = true;
            }
          }

          if (isMountain) {
            if (Math.random() < MOUNTAIN_PROB)
              cells[x][y].setElevationAndType(2,
                      WorldCell.Type.MOUNTAIN);
            continue;
          }

          boolean isHill = false;
          double closestDistance = Double.MAX_VALUE;

          for (int x1 = Math.max(0, x - HILL_RANGE); x1 <= Math.min(width - 1, x + HILL_RANGE); x1++) {
            for (int y1 = Math.max(0, y - HILL_RANGE); y1 <= Math.min(height - 1, y + HILL_RANGE); y1++) {
              if (cells[x1][y1].isLand() && plateIndex(x1, y1) != plateIndex) {
                isHill = true;
                closestDistance = Math.min(closestDistance,
                        Math.hypot(Math.abs(x - x1), Math.abs(y - y1)));
              }
            }
          }

          if (isHill & Math.random() <
                  HILL_PROB * 2 / Math.max(1, closestDistance - MOUNTAIN_RANGE))
            cells[x][y].setElevationAndType(1, WorldCell.Type.HILL);
        }
      }
    }

    for (int x = SHALLOW_RANGE; x < width - SHALLOW_RANGE; x++) {
      for (int y = SHALLOW_RANGE; y < height - SHALLOW_RANGE; y++) {
        if (cells[x][y].isLand()) {
          boolean isBeach = false;

          for (int x1 = x - BEACH_RANGE; x1 <= x + BEACH_RANGE; x1++) {
            for (int y1 = y - BEACH_RANGE; y1 <= y + BEACH_RANGE; y1++) {
              if (!cells[x1][y1].isLand())
                isBeach = true;
            }
          }

          if (isBeach && Math.random() < BEACH_PROB) {
            cells[x][y].setElevationAndType(0, WorldCell.Type.BEACH);
          }
        } else {
          boolean isShallow = false;

          for (int x1 = x - SHALLOW_RANGE; x1 <= x + SHALLOW_RANGE; x1++) {
            for (int y1 = y - SHALLOW_RANGE; y1 <= y + SHALLOW_RANGE; y1++) {
              if (cells[x1][y1].isLand())
                isShallow = true;
            }
          }

          if (isShallow)
            cells[x][y].setElevationAndType(-1, WorldCell.Type.SHALLOW);
        }
      }
    }
  }

  private void fillInterstitialLand() {
    for (int x = 1; x < width - 1; x++) {
      for (int y = 1; y < height - 1; y++) {

        if (cells[x][y] != null &&
                cells[x][y].getType() == WorldCell.Type.PLAIN) continue;

        int peers = 0;
        for (int x1 = x - 1; x1 <= x + 1; x1++) {
          for (int y1 = y - 1; y1 <= y + 1; y1++) {
            if (cells[x1][y1] != null &&
                    cells[x1][y1].getType() == WorldCell.Type.PLAIN)
              peers++;
          }
        }

        if (peers >= LAND_PEER_THRESHOLD)
          cells[x][y] = new WorldCell(WorldCell.Type.PLAIN, this, new Point(x, y));
      }
    }
  }

  private void sortPlatesByArea() {
    for (int i = 0; i < plates.length; i++) {
      for (int j = i + 1; j < plates.length; j++) {
        // if (i == j) continue;

        if (plates[j].getArea() > plates[i].getArea()) {
          TectonicPlate temp = plates[i];
          plates[i] = plates[j];
          plates[j] = temp;
        }
      }
    }
  }

  /** TEST FUNCTION */
  BufferedImage chunkMap() {
    BufferedImage map = new BufferedImage(width * 16, height * 16,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) map.getGraphics();

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        cells[x][y].generate();

        g.drawImage(cells[x][y].getChunkImage(), x * 16, y * 16, null);
      }
    }

    return map;
  }

  BufferedImage regionMap(final int SCALE_UP) {
    BufferedImage map = new BufferedImage(width * SCALE_UP, height * SCALE_UP,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) map.getGraphics();

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        switch (cells[x][y].getRegion()) {
          case POLAR:
            g.setColor(new Color(0, 255, 255));
            break;
          case TEMPERATE:
            g.setColor(new Color(85, 221, 170));
            break;
          case SUBTROPICAL:
            g.setColor(new Color(170, 187, 85));
            break;
          case TROPICAL:
          default:
            g.setColor(new Color(255, 153, 0));
        }

        g.fillRect(x * SCALE_UP, y * SCALE_UP, SCALE_UP, SCALE_UP);
      }
    }

    return map;
  }

  private BufferedImage borderMap(final int SCALE_UP, boolean regionBorders) {
    BufferedImage map =
            new BufferedImage(width * SCALE_UP, height * SCALE_UP,
                    BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) map.getGraphics();

    // base geography underneath
    g.drawImage(physicalGeography(SCALE_UP), 0, 0, null);

    Map<State, Color> stateColorMap = new HashMap<>();

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (borders[x][y] != null) {
          State state = borders[x][y];

          Color color;

          if (stateColorMap.containsKey(state)) {
            color = stateColorMap.get(state);
          } else {
            color = new Color((int)(51 + 153 * Math.random()),
                    (int)(51 + 153 * Math.random()),
                    (int)(51 + 153 * Math.random()), 200);
            stateColorMap.put(state, color);
          }
          g.setColor(color);

          g.fillRect(x * SCALE_UP, y * SCALE_UP, SCALE_UP, SCALE_UP);
        }

        if (x > 0 && x < width - 1 && y > 0 && y < height - 1) {
          if (borders[x][y] == null) continue;

          // Check if it's an international border
          boolean stateBorder = false;

          for (int x1 = x - 1; x1 <= x + 1; x1++) {
            for (int y1 = y - 1; y1 <= y + 1; y1++) {
              stateBorder |= (x != x1 || y != y1) &&
                      (borders[x1][y1] != null &&
                              borders[x][y] != borders[x1][y1]);
            }
          }

          if (stateBorder) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(x * SCALE_UP, y * SCALE_UP, SCALE_UP, SCALE_UP);
            continue;
          }

          if (!regionBorders) continue;

          // Check if it's a regional border
          boolean regionalBorder = false;
          Settlement closest = cells[x][y].getProvince();

          for (int x1 = x - 1; !regionalBorder && x1 <= x + 1; x1++) {
            for (int y1 = y - 1; !regionalBorder && y1 <= y + 1; y1++) {
              if (x == x1 && y == y1) continue;
              Settlement other = cells[x1][y1].getProvince();
              if (borders[x1][y1] != null && !other.equals(closest)) {
                regionalBorder = true;
              }
            }
          }

          if (regionalBorder) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(x * SCALE_UP, y * SCALE_UP, SCALE_UP, SCALE_UP);
          }
        }
      }
    }

    return map;
  }

  public BufferedImage politicalMap(final int SCALE_UP, boolean withBorders,
                             boolean withLines, boolean regionBorders) {
    BufferedImage map =
            new BufferedImage(width * SCALE_UP, height * SCALE_UP,
                    BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) map.getGraphics();

    // base geography underneath
    if (withBorders)
      g.drawImage(borderMap(SCALE_UP, regionBorders), 0, 0, null);
    else g.drawImage(physicalGeography(SCALE_UP), 0, 0, null);

    for (State state : states) {
      Set<Settlement> settlements = state.settlements();

      for (Settlement settlement : settlements) {
        int powerLevel = settlement.powerLevel();
        Point location = settlement.getLocation();
        int dotSize = 4 - powerLevel;

        g.setColor(new Color(0, 0, 0));

        switch (powerLevel) {
          case 1:
            g.setColor(new Color(255, 0, 0));
            break;
          case 2:
            Point liegeLoc = settlement.getLiege().getLocation();
            if (withLines) {
              g.drawLine(location.x * SCALE_UP, location.y * SCALE_UP,
                      liegeLoc.x * SCALE_UP, liegeLoc.y * SCALE_UP);
            }

            g.setColor(new Color(100, 0, 0));
            break;
          default:
            if (withLines) {
              liegeLoc = settlement.getLiege().getLocation();

              g.drawLine(location.x * SCALE_UP + (SCALE_UP / 2),
                      location.y * SCALE_UP + (SCALE_UP / 2),
                      liegeLoc.x * SCALE_UP + (SCALE_UP / 2),
                      liegeLoc.y * SCALE_UP + (SCALE_UP / 2));
            }

            g.setColor(new Color(0, 0, 0));
        }

        g.fillOval((int)((location.x - (dotSize / 2.)) * SCALE_UP + (SCALE_UP / 2)),
                (int)((location.y - (dotSize / 2.)) * SCALE_UP + (SCALE_UP / 2)),
                dotSize * SCALE_UP, dotSize * SCALE_UP);

        // Don't print names of lowest-tier settlements
        if (powerLevel < 3) {
          BufferedImage name = Font.CLEAN.getText(
                  Formatter.properNoun(settlement.getName()) +
                          " (" + settlement.getSetupPower() + ")");
          WritingSystem ws =
                  settlement.getState().getLanguage().getWritingSystem();
          BufferedImage wsName = ws.draw(settlement.getName(), 40, false);
          g.drawImage(name, location.x * SCALE_UP + dotSize * SCALE_UP,
                  location.y * SCALE_UP - (name.getHeight() / 2),null);
          g.drawImage(wsName, location.x * SCALE_UP + dotSize * SCALE_UP,
                  location.y * SCALE_UP + name.getHeight(),null);
        }
      }

//      Point capitalLoc = state.getCapital().getLocation();
//      WritingSystem ws = state.getLanguage().getWritingSystem();
//
//      BufferedImage text = Font.CLEAN.getText(
//              Formatter.properNoun(state.getName()));
//      BufferedImage symbols = ws.draw(state.getName(), 2);
//
//      g.setColor(new Color(255, 255, 255, 150));
//      g.fillRect(capitalLoc.x * SCALE_UP - (symbols.getWidth() / 2 + 5),
//              capitalLoc.y * SCALE_UP - symbols.getHeight(),
//              symbols.getWidth() + 10, symbols.getHeight() * 2);
//
//      g.drawImage(text, capitalLoc.x * SCALE_UP - text.getWidth() / 2,
//              capitalLoc.y * SCALE_UP - (int)(1.5 * text.getHeight()),
//              null);
//      g.drawImage(symbols, capitalLoc.x * SCALE_UP - symbols.getWidth() / 2,
//              capitalLoc.y * SCALE_UP, null);
    }

    for (State state : states) {
      Settlement capital = state.getCapital();
      Point location = capital.getLocation();
      Flag flag = state.getFlag();

      g.drawImage(flag.draw(2), location.x * SCALE_UP, location.y * SCALE_UP + 30,
              null);
    }

    return map;
  }

  BufferedImage physicalGeography(final int SCALE_UP) {
    BufferedImage map =
            new BufferedImage(width * SCALE_UP, height * SCALE_UP,
                    BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) map.getGraphics();

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        g.setColor(WorldCell.getMapColor(
                cells[x][y].getType(), cells[x][y].getRegion()));
        g.fillRect(x * SCALE_UP, y * SCALE_UP, SCALE_UP, SCALE_UP);
      }
    }

    return map;
  }

  BufferedImage tectonicMap(final int SCALE_UP) {
    BufferedImage map =
            new BufferedImage(width * SCALE_UP, height * SCALE_UP,
                    BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) map.getGraphics();

    for (TectonicPlate plate : plates) {
      Color color;
      switch (plate.getPlateType()) {
        case OCEANIC:
          color = new Color(0, 150, 205);
          break;
        default:
          color = new Color(15 * (int) (17 * Math.random()),
                  15 * (int) (17 * Math.random()),
                  15 * (int) (9 * Math.random()));
      }

      g.setColor(color);

      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          if (plate.onPlate(new Point(x, y)))
            g.fillRect(x * SCALE_UP, y * SCALE_UP, SCALE_UP, SCALE_UP);
        }
      }
    }

    // BORDERS
    g.setColor(new Color(0, 0, 0));

    for (int x = 1; x < width - 1; x++) {
      for (int y = 1; y < height - 1; y++) {
        int thisIndex = plateIndex(x, y);
        int leftIndex = plateIndex(x - 1, y);
        int rightIndex = plateIndex(x + 1, y);
        int aboveIndex = plateIndex(x, y - 1);
        int belowIndex = plateIndex(x, y + 1);

        if (leftIndex != thisIndex || rightIndex != thisIndex ||
                aboveIndex != thisIndex || belowIndex != thisIndex)
          g.fillRect(x * SCALE_UP, y * SCALE_UP, SCALE_UP, SCALE_UP);
      }
    }

    return map;
  }

  private int plateIndex(int x, int y) {
    for (int i = 0; i < plates.length; i++) {
      if (plates[i].onPlate(new Point(x, y))) return i;
    }
    return -1;
  }

  private void generatePlates(int plateCount) {
    for (int i = 0; i < plateCount; i++) {
      boolean available = false;
      Point potentialOrigin = randomPoint();

      while (!available) {
        potentialOrigin = randomPoint();
        available = true;
        for (int j = 0; j < i; j++) {
          if (plates[j].onPlate(potentialOrigin)) available = false;
        }
      }

      plates[i] = new TectonicPlate(this,
              potentialOrigin, width, height, plates, i);
    }
  }

  private Point randomPoint() {
    return new Point((int) (width * Math.random()),
            (int) (height * Math.random()));
  }

  private Point randomPoint(int xmin, int xmax, int ymin, int ymax) {
    return new Point(xmin + (int) ((xmax - xmin) * Math.random()),
            ymin + (int) ((ymax - ymin) * Math.random()));
  }

  enum PlateType {
    OCEANIC, LAND_CAPABLE
  }

  class TectonicPlate {

    private final static double DIVISOR = 3.0;
    private final static int PEER_THRESHOLD = 5;
    private final static int FILL_REPS = 15;
    private final static int MAX_ENCLOSED = 50;
    private final static int MASSES_THRESHOLD = 500;
    private final static int MAX_ADDITIONAL_MASSES = 1;
    private final static int MAX_RIVER_PER_PLATE = 40;
    private final static int SUPPORTS_RIVER_THRESHOLD = 500;
    private final static int TRIALS_PER_RIVER = 200;
    private final static int GROWTH_TURNS = 20;

    private final World world;

    private final Point origin;
    private int area;
    private final boolean[][] grid;
    private boolean[][] generated;

    private PlateType plateType = PlateType.LAND_CAPABLE;

    private final int width;
    private final int height;

    private int leftmost;
    private int rightmost;
    private int topmost;
    private int bottommost;

    TectonicPlate(World world, Point origin, int width, int height,
                  TectonicPlate[] plates, int index) {
      this.world = world;

      this.origin = origin;
      area = 0;
      grid = new boolean[width][height];
      generated = new boolean[width][height];

      this.width = width;
      this.height = height;

      leftmost = origin.x;
      rightmost = origin.x;
      topmost = origin.y;
      bottommost = origin.y;

      generateGrid(plates, index);
    }

    List<River> generateRivers(WorldCell[][] cells) {
      if (area < SUPPORTS_RIVER_THRESHOLD) return new ArrayList<>();

      List<River> rivers = new ArrayList<>();

      int RIVER_RANGE = 5;
      int riverCount = (int)(Math.random() * MAX_RIVER_PER_PLATE);

      for (int i = 0; i < riverCount; i++) {
        // GENERATE ORIGIN
        boolean found = false;
        int trials = 0;
        Point riverStart = randomPoint();
        while (!found && trials < TRIALS_PER_RIVER) {
          trials++;
          found = true;
          riverStart = randomPoint(Math.max(leftmost, RIVER_RANGE),
                  Math.min(rightmost, width - RIVER_RANGE),
                  Math.max(topmost, RIVER_RANGE),
                  Math.min(bottommost, height - RIVER_RANGE));
          WorldCell.Type type = cells[riverStart.x][riverStart.y].getType();
          if (!onPlate(riverStart) ||
                  (type != WorldCell.Type.SHALLOW))
            found = false;
        }

        if (!found) continue;

        // GENERATE FIRST POINT AND FLOW DIRECTION
        found = false;
        trials = 0;

        Point point = randomPoint();

        while (!found && trials < TRIALS_PER_RIVER) {
          trials++;
          found = true;
          point = randomPoint(riverStart.x - 3, riverStart.x + 3,
                  riverStart.y - 3, riverStart.y + 3);
          if (!cells[point.x][point.y].isLand() ||
                  cells[point.x][point.y].getElevation() != 0)
            found = false;
        }

        if (!found) continue;

        // GENERATE RIVER ARTICULATION POINTS
        River river = new River(riverStart, point);
        rivers.add(river);
        boolean done = false;

        while (!done) {
          River.RiverPoint next = river.generateNext();
          Point loc = next.point;

          done = Math.random() < 0.02;

          if (loc.x < 0 || loc.x > width || loc.y < 0 || loc.y > height) {
            break;
          }

          done |= (cells[loc.x][loc.y].getElevation() != 0 ||
                  !cells[loc.x][loc.y].isLand());

          if (!done)
            river.addRiverPoint(next);
        }

        for (River.RiverPoint riverPoint : river.getRiverPoints()) {
          Point loc = riverPoint.point;

          cells[loc.x][loc.y].setElevationAndType(0, WorldCell.Type.SHALLOW);
        }
      }

      return rivers;
    }

    void createTerrain(WorldCell[][] cells) {
      generated = new boolean[width][height];

      int masses = 1;
      if (area > MASSES_THRESHOLD)
        masses += (int)(MAX_ADDITIONAL_MASSES * Math.random());

      double maxDist = Math.sqrt(area) / (double) masses;

      for (int mass = 0; mass < masses; mass++) {

        if (plateType == PlateType.OCEANIC) break;

        boolean found = false;
        Point potential = randomPoint();

        while (!found) {
          potential = randomPoint(leftmost, rightmost, topmost, bottommost);
          found = onPlate(potential) && !generated[potential.x][potential.y];
        }

        generateTerrain(potential, cells, maxDist, potential);
      }

      for (int x = leftmost; x <= rightmost; x++) {
        for (int y = topmost; y <= bottommost; y++) {
          if (cells[x][y] == null)
            cells[x][y] = new WorldCell(WorldCell.Type.SEA, world, new Point(x, y));
        }
      }
    }

    void generateTerrain(Point at, WorldCell[][] cells,
                      double maxDist, Point origin) {
      // Out of bounds check
      if (at.x < leftmost || at.x >= rightmost ||
              at.y < topmost || at.y >= bottommost) return;

      // Generation check
      if (generated[at.x][at.y]) return;

      generated[at.x][at.y] = true;

      double dist = Math.hypot(Math.abs(at.x - origin.x),
              Math.abs(at.y - origin.y));

      if (landLikelihood(dist, maxDist) && onPlate(at)) {
        cells[at.x][at.y] = new WorldCell(WorldCell.Type.PLAIN, world, new Point(at));

        generateTerrain(new Point(at.x - 1, at.y), cells, maxDist, origin);
        generateTerrain(new Point(at.x + 1, at.y), cells, maxDist, origin);
        generateTerrain(new Point(at.x, at.y - 1), cells, maxDist, origin);
        generateTerrain(new Point(at.x, at.y + 1), cells, maxDist, origin);
      }
    }

    private boolean landLikelihood(double dist, double maxDist) {
      if (dist > maxDist) return false;

      double multiplier = 1.5 + (1.5 * Math.random());
      double likelihood = 1.0 - (dist / (maxDist * multiplier));

      return Math.random() < likelihood;
    }

    void makeOceanic() {
      plateType = PlateType.OCEANIC;
    }

    PlateType getPlateType() {
      return plateType;
    }

    int getArea() {
      return area;
    }

    private void generateGrid(TectonicPlate[] plates, int index) {
      double worldDiag = Math.hypot((double) width, (double) height);

      int reduction = plates.length;

      double maxDist = worldDiag / DIVISOR;

      while (reduction > 20) {
        maxDist -= (worldDiag / DIVISOR) / 10;
        reduction -= 10;
      }

      generateAt(origin.x, origin.y, plates, index, maxDist);

      for (int i = 0; i < FILL_REPS; i++) {
        fillInterstitial(plates, index);
      }

      fillEnclosed(plates, index);
    }

    private void addToGrid(int x, int y) {
      grid[x][y] = true;
      area++;

      leftmost = Math.min(leftmost, x);
      rightmost = Math.max(rightmost, x);
      topmost = Math.min(topmost, y);
      bottommost = Math.max(bottommost, y);
    }

    private void fillEnclosed(TectonicPlate[] plates, int index) {
      generated = new boolean[width][height];

      for (int x = leftmost; x < rightmost; x++) {
        generated[x][topmost] = true;
        generated[x][bottommost] = true;
      }
      for (int y = topmost; y < bottommost; y++) {
        generated[leftmost][y] = true;
        generated[rightmost][y] = true;
      }

      for (int x = Math.max(1, leftmost);
           x < Math.min(rightmost, width - 1); x++) {
        for (int y = Math.max(1, topmost);
             y < Math.min(bottommost, height - 1); y++) {

          if (claimed(plates, index, x, y) || generated[x][y] ||
                  grid[x][y]) continue;

          List<Point> pointList = new ArrayList<>();
          addEnclosed(new Point(x, y), pointList, plates, index);

          if (pointList.size() > MAX_ENCLOSED) return;

          for (Point point : pointList) {
            addToGrid(point.x, point.y);
          }
        }
      }
    }

    private void addEnclosed(Point at, List<Point> pointList,
                             TectonicPlate[] plates, int index) {
      // Out of bounds check
      if (at.x < leftmost || at.x >= rightmost ||
              at.y < topmost || at.y >= bottommost) return;

      generated[at.x][at.y] = true;

      if (claimed(plates, index, at.x, at.y) || pointList.contains(at) ||
              grid[at.x][at.y] || pointList.size() > MAX_ENCLOSED) return;

      pointList.add(at);
      generated[at.x][at.y] = true;

      addEnclosed(new Point(at.x - 1, at.y), pointList, plates, index);
      addEnclosed(new Point(at.x + 1, at.y), pointList, plates, index);
      addEnclosed(new Point(at.x, at.y - 1), pointList, plates, index);
      addEnclosed(new Point(at.x, at.y + 1), pointList, plates, index);
    }

    private void fillInterstitial(TectonicPlate[] plates, int index) {
      for (int x = Math.max(1, leftmost);
           x < Math.min(rightmost, width - 1); x++) {
        for (int y = Math.max(1, topmost);
             y < Math.min(bottommost, height - 1); y++) {

          if (claimed(plates, index, x, y) || grid[x][y]) continue;

          int peers = 0;
          for (int x1 = x - 1; x1 <= x + 1; x1++) {
            for (int y1 = y - 1; y1 <= y + 1; y1++) {
              if (onPlate(new Point(x1, y1))) peers++;
            }
          }

          if (peers >= PEER_THRESHOLD)
            addToGrid(x, y);
        }
      }
    }

    private boolean claimed(TectonicPlate[] plates,
                            int index, int x, int y) {
      for (int i = 0; i < index; i++) {
        if (plates[i].onPlate(new Point(x, y))) return true;
      }
      return false;
    }

    private void generateAt(int x, int y,
                            TectonicPlate[] plates, int index,
                            double maxDist) {
      // Out of bounds check
      if (x < 0 || x >= width || y < 0 || y >= height) return;

      // Already processed check
      if (generated[x][y]) return;

      // Already on another tectonic plate check
      if (claimed(plates, index, x, y)) return;

      boolean outcome = assignToPlate(maxDist, x, y);
      generated[x][y] = true;

      // Recurse over adjacent cells
      if (outcome) {

        addToGrid(x, y);

        generateAt(x - 1, y, plates, index, maxDist);
        generateAt(x + 1, y, plates, index, maxDist);
        generateAt(x, y - 1, plates, index, maxDist);
        generateAt(x, y + 1, plates, index, maxDist);
      }
    }

    private boolean assignToPlate(double maxDist, int x, int y) {
      double dist = Math.hypot((double) Math.abs(origin.x - x),
              (double) Math.abs(origin.y - y));

      if (dist > maxDist) return false;

      double multiplier = 0.65 + (0.4 * Math.random());
      double likelihood = 1.0 - (dist / (maxDist * multiplier));

      return Math.random() < likelihood;
    }

    boolean onPlate(Point check) {
      return grid[check.x][check.y];
    }

    State generateState(WorldCell[][] cells) {
      State state = new State();

      int landArea = getLandArea(cells);

      int settlementCount = (int)(Math.sqrt(landArea)) +
              (int)(Math.random() * Math.sqrt(landArea));

      List<Settlement> settlements = new ArrayList<>();
      int placed = 0;
      int trials = 0;

      // Place and name settlements as part of the state
      while (placed < settlementCount &&
              trials < settlementCount * 100) {
        trials++;

        Point point = randomPoint(leftmost, rightmost, topmost, bottommost);

        if (onPlate(point) && cells[point.x][point.y].isLand()) {
          Settlement settlement = new Settlement(point, state);
          cells[point.x][point.y].populateSettlement(settlement);
          settlements.add(settlement);

          placed++;
        }
      }

      if (settlements.size() == 0) return state;

      // Simulate growth and accrued power based on resources
      for (Settlement settlement : settlements) {
        for (int i = 0; i < GROWTH_TURNS; i++) {
          simulateGrowth(settlement, cells);
        }
      }

      // Sort settlements based on strength (INSERTION)
      for (int i = 0; i < settlements.size(); i++) {
        for (int j = i + 1; j < settlements.size(); j++) {
          if (settlements.get(j).getSetupPower() >
                  settlements.get(i).getSetupPower()) {
            Settlement temp = settlements.get(i);
            settlements.set(i, settlements.get(j));
            settlements.set(j, temp);
          }
        }
      }

      // Strongest becomes capital
      state.setCapital(settlements.get(0));

      // Regional capitals
      int regionCount = (int)((0.05 * settlementCount) +
              (Math.random() * 0.05 * settlementCount));

      placed = 0;
      trials = 0;

      int sortedIndex = 1; // 0 was the capital

      Set<Settlement> regions = new HashSet<>();
      regions.add(state.getCapital());
      int minimumDistance = (int)(Math.sqrt(landArea) / 3);

      while (placed < regionCount && trials < regionCount * 100
              && sortedIndex < settlements.size()) {
        trials++;

        Point location = settlements.get(sortedIndex).getLocation();
        boolean violated = false;
        for (Settlement region : regions) {
          Point regLoc = region.getLocation();
          if (Math.hypot(Math.abs(location.x - regLoc.x),
                  Math.abs(location.y - regLoc.y)) < minimumDistance) {
            violated = true;
          }
        }

        if (!violated) {
          placed++;
          regions.add(settlements.get(sortedIndex));
        }

        sortedIndex++;
      }

      // Make regions vassals of capital
      for (Settlement region : regions) {
        if (!region.equals(state.getCapital()))
          state.getCapital().addVassal(region);
      }

      // Remaining settlements
      Set<Settlement> settlementSet = new HashSet<>(settlements);
      Set<Settlement> remaining = Sets.difference(settlementSet, regions);

      for (Settlement leftover : remaining) {
        Point location = leftover.getLocation();
        double closestDistance = Double.MAX_VALUE;
        Settlement closestRegion = null;
        for (Settlement region : regions) {
          Point regLoc = region.getLocation();
          double distance = Math.hypot(Math.abs(location.x - regLoc.x),
                  Math.abs(location.y - regLoc.y));
          if (distance < closestDistance) {
            closestDistance = distance;
            closestRegion = region;
          }
        }

        if (closestRegion != null) {
          closestRegion.addVassal(leftover);
        }
      }

      return state;
    }

    private void simulateGrowth(Settlement settlement, WorldCell[][] cells) {
      Point location = settlement.getLocation();
      int rawPower = 0;

      int minX = Math.max(0, location.x - 2);
      int maxX = Math.min(width - 1, location.x + 1);
      int minY = Math.max(0, location.y - 2);
      int maxY = Math.min(height - 1, location.y + 1);

      for (int x = minX; x <= maxX; x++) {
        for (int y = minY; y <= maxY; y++) {
          switch (cells[x][y].getType()) {
            case FOREST:
            case SEA:
            case SHALLOW:
              rawPower += 2;
              break;
            case PLAIN:
              rawPower++;
              break;
            case MOUNTAIN:
              rawPower += 3;
              break;
          }
        }
      }

      settlement.accruePower(rawPower);
    }

    private int getLandArea(WorldCell[][] cells) {
      int landArea = 0;

      for (int x = leftmost; x <= rightmost; x++) {
        for (int y = topmost; y <= bottommost; y++) {
          if (grid[x][y] && cells[x][y].isLand()) landArea++;
        }
      }

      return landArea;
    }
  }
}
