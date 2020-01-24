package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.GameManager;
import com.redsquare.citizen.config.Settings;
import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.game_states.MenuGameState;
import com.redsquare.citizen.game_states.menu_elements.MenuStateCode;
import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.systems.language.*;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.politics.State;
import com.redsquare.citizen.systems.vexillography.Flag;
import com.redsquare.citizen.util.*;
import com.redsquare.citizen.util.Formatter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class World {

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

  private final WorldManager worldManager;

  private boolean[][] generated;

  private Set<Settlement> settlements = null;

  private final TectonicPlate[] plates;
  private final Set<State> states;
  private final WorldCell[][] cells;
  private State[][] borders;
  private final List<River> rivers;
  private final List<Desert> deserts;
  private final List<BodyOfWater> bodiesOfWater;

  private final int width;
  private final int height;

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
    int oceanic = 0;

    for (int i = 0; i < plateCount && oceanic < oceanicCount; i++) {
      if (i % 3 == 2) continue;
      plates[i].makeOceanic();
      oceanic++;
    }

    updateMenuScreen(MenuStateCode.PHYSICAL_GEOGRAPHY);

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

    // BODIES OF WATER
      bodiesOfWater = new ArrayList<>();
    for (TectonicPlate plate : plates) {
        if (plate.getPlateType() == PlateType.OCEANIC && plate.getArea() > 500)
            bodiesOfWater.add(new BodyOfWater(plate.central(), plate.bodyOfWaterClassification()));
    }

    // RIVERS
    rivers = new ArrayList<>();
    riverLogic();

    // DESERTS
    deserts = new ArrayList<>();
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
            cells[x][y].setType(WorldCell.Type.DESERT);
        }
      }
    }

    // FOREST
    generateForests();

    // POLES
    generatePoles();

    // ELEVATION
    generateElevation();

    /*
     * END OF PHYSICAL GEOGRAPHY
     *
     * START OF POLITICAL GEOGRAPHY
     * */

    updateMenuScreen(MenuStateCode.STATES_SETTLEMENTS);

    // STATES
    states = new HashSet<>();

    for (TectonicPlate plate : plates) {
      states.add(plate.generateState(this, cells));
    }

    // REMOVE STATES WITH NO SETTLEMENTS
    removeTrivialStates();

    // BORDERS
    establishBorders();

    // Name physical geography
    namePhysicalGeography();

    updateMenuScreen(MenuStateCode.SIMULATING_HISTORY);

    this.worldManager = WorldManager.init(this);
  }

  private void generateElevation() {
    // TODO: potentially a naive implementation

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        WorldCell wc = getCell(x, y);
        int elevation = 0;

        switch (wc.getType()) {
          case SEA:
            elevation = Randoms.bounded(-2000, -30);
            break;
          case SHALLOW:
            elevation = Randoms.bounded(-30, 0);
            break;
          case PLAIN:
          case DESERT:
          case FOREST:
            elevation = Randoms.bounded(0, 300);
            break;
          case HILL:
            elevation = Randoms.bounded(300, 1500);
            break;
          case MOUNTAIN:
            elevation = Randoms.bounded(1500, 4500);
            break;
        }

        wc.setElevation(elevation);
      }
    }
  }

  public WorldManager getWorldManager() {
    return worldManager;
  }

  private void riverLogic() {
      for (TectonicPlate plate : plates) {
          if (plate.area > 100)
              rivers.addAll(plate.generateRivers(cells));
      }

      // Remove really small rivers
      for (int i = 0; i < rivers.size(); i++) {
          if (rivers.get(i).getRiverPoints().size() < 6) {
              rivers.remove(i);
              i--;
          }
      }
  }

  private void namePhysicalGeography() {
    // Rivers
    for (River r : rivers) {
      Point rp = r.getCentral();
      Settlement closest = closestTo(rp.x, rp.y);
      r.setName(PlaceNameGenerator.generateRandomName(2, 4,
              closest.getState().getLanguage().getPhonology()));
    }

    // Desert
    for (Desert d : deserts) {
      Point dp = d.getOrigin();
      Settlement closest = closestTo(dp.x, dp.y);
      d.nameDesert(PlaceNameGenerator.generateRandomName(2, 4,
              closest.getState().getLanguage().getPhonology()));
    }

    // Bodies of water
    for (BodyOfWater body : bodiesOfWater) {
        Point bp = body.getOrigin();
        Settlement closest = closestTo(bp.x, bp.y);
        body.setName(PlaceNameGenerator.generateRandomName(2, 4,
                closest.getState().getLanguage().getPhonology()));
    }
  }

  public void processConsolidation(State from, State to, boolean wasCapital) {
    if (wasCapital) states.remove(from);
    // else updateBorders(from);
    // updateBorders(to);
  }

  public void addState(State state) {
    states.add(state);
    updateBorders(state);
  }

  private void updateMenuScreen(MenuStateCode code) {
    if (Settings.executionMode == Settings.ExecutionMode.GAME) {
      MenuGameState menuState =
              (MenuGameState)(GameManager.get().getGameState());

      menuState.setStateCode(code, null);
    }
  }

  private void removeTrivialStates() {
    Set<State> toRemove = new HashSet<>();
    for (State state : states) {
      if (state.settlements().isEmpty()) toRemove.add(state);
    }
    states.removeAll(toRemove);
  }

  void establishBorders() {
    borders = new State[width][height];

    borderUpdateLoop(0, 0, width - 1, height - 1);
  }

  private void borderUpdateLoop(int xMin, int yMin, int xMax, int yMax) {
    for (int x = xMin; x <= xMax; x++) {
      for (int y = yMin; y <= yMax; y++) {
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

  private void updateBorders(State newState) {
    int xMin = Integer.MAX_VALUE;
    int yMin = Integer.MAX_VALUE;
    int xMax = Integer.MIN_VALUE;
    int yMax = Integer.MIN_VALUE;

    for (Settlement settlement : newState.settlements()) {
      xMin = settlement.getLocation().x < xMin ? settlement.getLocation().x : xMin;
      yMin = settlement.getLocation().y < yMin ? settlement.getLocation().y : yMin;
      xMax = settlement.getLocation().x > xMax ? settlement.getLocation().x : xMax;
      yMax = settlement.getLocation().y > yMax ? settlement.getLocation().y : yMax;
    }

    xMin = xMin - 40 >= 0 ? xMin - 40 : 0;
    yMin = yMin - 40 >= 0 ? yMin - 40 : 0;
    xMax = xMax + 40 < width ? xMax + 40 : width - 1;
    yMax = yMax + 40 < height ? yMax + 40 : height - 1;

    borderUpdateLoop(xMin, yMin, xMax, yMax);
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

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public WorldCell getCell(int x, int y) {
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
    int poleAt = width / 2;
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
      Point point = randomPoint(0, width - 1, height / 3, 2 * (height / 3));

      while (!found) {
        point = randomPoint(0, width - 1, height / 3, 2 * (height / 3));

        if (cells[point.x][point.y].getType() == WorldCell.Type.PLAIN)
          found = true;
      }

      spawnDesert(point);
    }
  }

  private void spawnDesert(Point origin) {
    deserts.add(new Desert(origin));
    generated = new boolean[width][height];

    generated[origin.x][origin.y] = true;
    cells[origin.x][origin.y].setType(WorldCell.Type.DESERT);

    List<Point> potentials = new ArrayList<>();

    int leftmost = Math.max(0, origin.x - DESERT_MAX_DIST);
    int rightmost = Math.min(width - 1, origin.x + DESERT_MAX_DIST);
    int topmost = Math.max(0, origin.y - DESERT_MAX_DIST);
    int bottommost =  Math.min(height - 1, origin.y + DESERT_MAX_DIST);

    for (int x = leftmost; x <= rightmost; x++) {
      for (int y = topmost; y <= bottommost; y++) {
        if (cells[x][y].getType() == WorldCell.Type.PLAIN &&
          MathExt.distance(new Point(x, y), origin) != 0.)
          potentials.add(new Point(x, y));
      }
    }

    potentials.sort(Comparator.comparingDouble((Point p) -> MathExt.distance(p, origin)));

    for (Point p : potentials) {
      if (Math.random() < DESERT_PROB &&
              Math.random() / DESERT_MULT < 1 -
                      (MathExt.distance(origin, p) / DESERT_MAX_DIST)) {
        boolean allowed = MathExt.pointAllowance(p, origin,
                leftmost, rightmost, topmost, bottommost,
                3, (Point c) -> cells[c.x][c.y].getType() == WorldCell.Type.DESERT);

        if (allowed)
          cells[p.x][p.y].setType(WorldCell.Type.DESERT);
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
                  cells[x][y].setType(WorldCell.Type.FOREST);
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
            cells[x1][y1].setType(WorldCell.Type.FOREST);
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
              cells[x][y].setType(WorldCell.Type.MOUNTAIN);
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
            cells[x][y].setType(WorldCell.Type.HILL);
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
            cells[x][y].setType(WorldCell.Type.BEACH);
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
            cells[x][y].setType(WorldCell.Type.SHALLOW);
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

  BufferedImage stateMap(final int SCALE_UP, State state) {
    if (!states.contains(state))
      return null;

    final int LEFT = 0, RIGHT = 1, TOP = 2, BOTTOM = 3;
    final int[] indices = new int[4];
    final boolean[] found = new boolean[4];
    final int BORDER = 5;

    for (int i = LEFT; i <= RIGHT; i++) {
      for (int x = i < RIGHT ? 0 : width - 1; x < width && x >= 0; x += (i < RIGHT ? 1 : -1)) {
        for (int y = 0; y < height; y++) {
          if (found[i]) break;
          if (borders[x][y]!= null && borders[x][y].equals(state)) {
            found[i] = true;
            switch (i) {
              case 0:
                indices[i] = Math.max(0, x - BORDER);
                break;
              case 1:
                indices[i] = Math.min(width - 1, x + BORDER);
                break;
            }
          }
        }
      }
    }

    for (int i = TOP; i <= BOTTOM; i++) {
      for (int y = i < BOTTOM ? 0 : height - 1; y < height && y >= 0; y += (i < BOTTOM ? 1 : -1)) {
        for (int x = 0; x < width; x++) {
          if (found[i]) break;
          if (borders[x][y]!= null && borders[x][y].equals(state)) {
            found[i] = true;
            switch (i) {
              case 2:
                indices[i] = Math.max(0, y - BORDER);
                break;
              case 3:
                indices[i] = Math.min(height - 1, y + BORDER);
                break;
            }
          }
        }
      }
    }

    BufferedImage image = new BufferedImage(((indices[RIGHT] - indices[LEFT]) + 1) * SCALE_UP,
            ((indices[BOTTOM] - indices[TOP]) + 1) * SCALE_UP, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) image.getGraphics();

    for (int x = indices[LEFT]; x <= indices[RIGHT]; x ++) {
      for (int y = indices[TOP]; y <= indices[BOTTOM]; y++) {
        WorldCell wc = cells[x][y];
        Color c = borders[x][y] != null && borders[x][y].equals(state) ?
                WorldCell.getMapColor(wc.getType(), wc.getRegion()) :
                ColorMath.sepia(WorldCell.getMapColor(wc.getType(), wc.getRegion()));
        g.setColor(c);
        g.fillRect((x - indices[LEFT]) * SCALE_UP, (y - indices[TOP]) * SCALE_UP, SCALE_UP, SCALE_UP);

        int xm = Math.max(0, x - 1), xp = Math.min(width - 1, x + 1),
                ym = Math.max(0, y - 1), yp = Math.min(height - 1, y + 1);

        boolean isBorder =
                ((borders[x][y] != null && borders[x][y].equals(state) &&
                        ((borders[xm][y] == null || !borders[xm][y].equals(state)) ||
                                (borders[xp][y] == null || !borders[xp][y].equals(state)) ||
                                (borders[x][ym] == null || !borders[x][ym].equals(state)) ||
                                (borders[x][yp] == null || !borders[x][yp].equals(state)))));

        if (isBorder) {
          g.setColor(new Color(0, 0, 0, 200));
          g.fillRect((x - indices[LEFT]) * SCALE_UP, (y - indices[TOP]) * SCALE_UP, SCALE_UP, SCALE_UP);
        }
      }
    }

    Settlement capital = state.getCapital();
    int[] coords = new int[] { (capital.getLocation().x - indices[LEFT]) * SCALE_UP,
            (capital.getLocation().y - indices[TOP]) * SCALE_UP };
    BufferedImage setLabel = state.getLanguage().
            getWritingSystem().drawWithFont(capital.getName(), 40, 2, 2,
            Fonts::fontIdentityX, Fonts::fontIdentityY);

    g.setColor(new Color(255, 0, 0));
    g.fillOval(coords[0] - 4, coords[1] - 4, 9, 9);
    g.drawImage(setLabel, coords[0] + 10, coords[1] - 20, null);

    BufferedImage countryName = state.getLanguage().getWritingSystem().drawWithFont(
            state.getLanguage().lookUpWord(Meaning.THIS_STATE), SCALE_UP * 10,
            SCALE_UP / 2, SCALE_UP / 3, Fonts::fontIdentityX, Fonts::fontIdentityY);

    while (countryName.getWidth() / (double)(image.getWidth()) > 0.9) {
      countryName = Formatter.scale(countryName, 0.8);
    }

    g.drawImage(countryName, (image.getWidth() / 2) - (countryName.getWidth() / 2),
            image.getHeight() - (countryName.getHeight() + 2 * SCALE_UP), null);
    g.drawImage(state.getFlag().draw(SCALE_UP / 7), 10, 10, null);

    return image;
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
    g.drawImage(physicalGeography(SCALE_UP, false), 0, 0, null);

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
    else g.drawImage(physicalGeography(SCALE_UP, false), 0, 0, null);

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
                  Formatter.properNoun(settlement.getName().toString()) +
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


  public BufferedImage worldMiniMap(Point worldLocation) {
    BufferedImage miniMap = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
    BufferedImage wholeWorld = physicalGeography(4, false);

    Graphics2D g = (Graphics2D) miniMap.getGraphics();

    g.drawImage(wholeWorld, -1 * ((worldLocation.x - 12) * 4),
            -1 * ((worldLocation.y - 12) * 4), null);

    g.setColor(new Color(0, 0, 0));
    g.fillRect(48, 48, 4, 4);

    return miniMap;
  }


  public BufferedImage physicalGeography(final int SCALE_UP, boolean marked) {
    BufferedImage map =
            new BufferedImage(width * SCALE_UP, height * SCALE_UP,
                    BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) map.getGraphics();

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (cells[x][y] == null) continue;
        g.setColor(WorldCell.getMapColor(
                cells[x][y].getType(), cells[x][y].getRegion()));
        if (cells[x][y].getRiverPoint() != null)
          g.setColor(WorldCell.getMapColor(
                  WorldCell.Type.SHALLOW, cells[x][y].getRegion()));
        g.fillRect(x * SCALE_UP, y * SCALE_UP, SCALE_UP, SCALE_UP);
      }
    }

    if (marked) {
      // NAMES
      // Rivers
      for (River r : rivers) {
        Word name = r.getName();
        Point p = r.getCentral();

        BufferedImage label = Font.CLEAN.getText(Formatter.properNoun(name.toString() + " River"));
        g.drawImage(label, p.x * SCALE_UP - label.getWidth() / 2, p.y * SCALE_UP, null);
      }
      // Deserts
      for (Desert d : deserts) {
        Word name = d.getName();
        Point p = d.getOrigin();

        BufferedImage label = Font.CLEAN.getText(Formatter.properNoun(name.toString() + " Desert"));
        g.drawImage(label, p.x * SCALE_UP - label.getWidth() / 2, p.y * SCALE_UP, null);
      }
        // Deserts
        for (BodyOfWater body : bodiesOfWater) {
            Point p = body.getOrigin();

            BufferedImage label = Font.CLEAN.getText(Formatter.properNoun(body.getName()));
            g.drawImage(label, p.x * SCALE_UP - label.getWidth() / 2, p.y * SCALE_UP, null);
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
        final int DENOMINATOR = 6;
        potentialOrigin = randomPoint(width / DENOMINATOR,
                (DENOMINATOR - 1) * (width / DENOMINATOR),
                height / DENOMINATOR,
                (DENOMINATOR - 1) * (height / DENOMINATOR));
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

  static class BodyOfWater {
      private final double R;
      private Word name;
      private final Point origin;
      private final Classification classification;

      enum Classification {
          OCEAN, SEA, GULF
      }

      BodyOfWater(Point origin, Classification classification) {
          this.R = Math.random();
          this.classification = classification;
          this.origin = origin;
      }

      void setName(Word name) {
          this.name = name;
      }

      Point getOrigin() {
          return origin;
      }

      String getName() {
          if (R < 0.7)
              return Formatter.properNoun(name.toString()) + " " +
                      Formatter.properNoun(classification.name().toLowerCase());
          return Formatter.properNoun(classification.name().toLowerCase()) + " of " +
                  Formatter.properNoun(name.toString());
      }
  }

  static class Desert {
    private Word name;
    private final Point origin;

    Desert(Point origin) {
      this.origin = origin;
    }

    Word getName() {
      return name;
    }

    Point getOrigin() {
      return origin;
    }

    void nameDesert(Word name) {
      this.name = name;
    }
  }

  class TectonicPlate {

    private final static double DIVISOR = 3.0;
    private final static int PEER_THRESHOLD = 5;
    private final static int FILL_REPS = 15;
    private final static int MAX_ENCLOSED = 50;
    private final static int MASSES_THRESHOLD = 500;
    private final static int MAX_ADDITIONAL_MASSES = 1;
    private final static int MAX_RIVER_PER_PLATE = 12;
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

    Point central() {
      List<Point> points = new ArrayList<>();

      for (int x = leftmost; x <= rightmost; x++) {
        for (int y = topmost; y <= bottommost; y++) {
          if (grid[x][y])
            points.add(new Point(x, y));
        }
      }

      return MathExt.averagePoint(points);
    }

    BodyOfWater.Classification bodyOfWaterClassification() {
        if (area > 6000)
            return BodyOfWater.Classification.OCEAN;
        else if (area > 2000)
            return BodyOfWater.Classification.SEA;
        else
            return BodyOfWater.Classification.GULF;
    }

    List<River> generateRivers(WorldCell[][] cells) {
      if (area < SUPPORTS_RIVER_THRESHOLD) return new ArrayList<>();

      List<River> rivers = new ArrayList<>();

      int RIVER_RANGE = 5;
      int riverCount = (int)(Randoms.bounded(0.5, 1.0) *
              Math.min(Math.sqrt(area) / 60., 1.) * MAX_RIVER_PER_PLATE);

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
          if (!onPlate(riverStart))
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
          point = randomPoint(riverStart.x - 1, riverStart.x + 2,
                  riverStart.y - 1, riverStart.y + 2);
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

          // done = Math.random() < (0.02 * Math.sqrt(river.getRiverPoints().size()));

          if (loc.x < 0 || loc.x >= width || loc.y < 0 || loc.y >= height) {
            break;
          }

          done = (!cells[loc.x][loc.y].isLand());

          if (!done)
            river.addRiverPoint(next);
        }

        river.setCentral();

        for (River.RiverPoint riverPoint : river.getRiverPoints()) {
          Point loc = riverPoint.point;

          cells[loc.x][loc.y].setRiverPoint(riverPoint);
        }
      }

      return rivers;
    }

    void createTerrain(WorldCell[][] cells) {
      generated = new boolean[width][height];

      if (area < 4) return;

      int masses = 1;
      if (area > MASSES_THRESHOLD)
        masses += (int)(MAX_ADDITIONAL_MASSES * Math.random());

      double maxDist = Math.sqrt(2.5 * area) / (double) masses;

      for (int mass = 0; mass < masses; mass++) {

        if (plateType == PlateType.OCEANIC) break;

        boolean found = false;
        Point potential = randomPoint();

        while (!found) {
          potential = randomPoint(leftmost, rightmost, topmost, bottommost);
          found = onPlate(potential) && !generated[potential.x][potential.y];
        }

        generateTerrain(cells, maxDist, potential);
      }

      for (int x = leftmost; x <= rightmost; x++) {
        for (int y = topmost; y <= bottommost; y++) {
          if (cells[x][y] == null)
            cells[x][y] = new WorldCell(WorldCell.Type.SEA, world, new Point(x, y));
        }
      }
    }

    void generateTerrain(WorldCell[][] cells,
                      double maxDist, Point origin) {
      List<Point> potentials = new ArrayList<>();

      for (int x = leftmost; x <= rightmost; x++) {
        for (int y = topmost; y <= bottommost; y++) {
          Point p = new Point(x, y);

          if (onPlate(p) && !generated[p.x][p.y])
            potentials.add(p);
        }
      }

      potentials.sort(Comparator.comparingDouble(p -> MathExt.distance(p, origin)));

      for (Point p : potentials) {
        boolean allowed = MathExt.pointAllowance(p, origin,
                leftmost, rightmost, topmost, bottommost,
                4, (Point c) -> cells[c.x][c.y] != null);

        if (allowed) {
          generated[p.x][p.y] = true;

          if (landLikelihood(MathExt.distance(origin, p), maxDist)) {
            cells[p.x][p.y] = new WorldCell(WorldCell.Type.PLAIN, world, new Point(p));
          }
        }
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

      generate(origin.x, origin.y, plates, index, maxDist);

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
      generated[at.x][at.y] = true;
      pointList.add(at);

      List<Point> potentials = new ArrayList<>();

      for (int x = leftmost + 1; x < rightmost; x++) {
        for (int y = topmost + 1; y < bottommost; y++) {
          if (MathExt.distance(at, new Point(x, y)) != 0.)
            potentials.add(new Point(x, y));
        }
      }

      potentials.sort(Comparator.comparingDouble(p -> MathExt.distance(at, p)));

      for (Point p : potentials) {
        if (pointList.size() > MAX_ENCLOSED) return;

        if (claimed(plates, index, p.x, p.y) || pointList.contains(p) ||
                grid[p.x][p.y])
          continue;

        Point[] surroundingP = MathExt.getSurrounding(p);

        for (Point sp : surroundingP) {
          if (sp.x > leftmost && sp.x < rightmost &&
                  sp.y > topmost && sp.y < bottommost && pointList.contains(sp)) {
            pointList.add(p);
            break;
          }
        }
      }
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

    private void generate(int x, int y, TectonicPlate[] plates, int index,
                          double maxDist) {
      Point origin = new Point(x, y);

      List<Point> potentials = new ArrayList<>();

      int xMin = x - (int)(maxDist + 1), xMax = x + (int)(maxDist + 1),
              yMin = y - (int)(maxDist + 1), yMax = y + (int)(maxDist + 1);

      xMin = xMin < 0 ? 0 : xMin;
      yMin = yMin < 0 ? 0 : yMin;
      xMax = xMax + 1 >= width ? width - 1 : xMax;
      yMax = yMax + 1 >= height ? height - 1 : yMax;

      for (int xCur = xMin; xCur <= xMax; xCur++) {
        for (int yCur = yMin; yCur <= yMax; yCur++) {
          potentials.add(new Point(xCur, yCur));
        }
      }

      potentials.sort(Comparator.comparingDouble(point ->
              MathExt.distance(point, origin)));

      for (Point p : potentials) {
        boolean allowed = MathExt.pointAllowance(p, origin,
                0, width - 1, 0, height - 1,
                4, (Point c) -> grid[c.x][c.y]);

        if (!claimed(plates, index, p.x, p.y) && allowed &&
                assignToPlate(maxDist, p.x, p.y)) {
          addToGrid(p.x, p.y);
        }
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

    State generateState(World world, WorldCell[][] cells) {
      State state = new State(world);

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

      settlements.sort(Comparator.comparingInt(
              x -> x.getSetupPower() * -1
      ));

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
              rawPower += Randoms.bounded(1, 4);
              break;
            case PLAIN:
              rawPower++;
              break;
            case MOUNTAIN:
              rawPower += 3;
              break;
            case HILL:
              rawPower += Randoms.bounded(0, 3);
              break;
            case DESERT:
              rawPower += Randoms.bounded(0, 2);
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
