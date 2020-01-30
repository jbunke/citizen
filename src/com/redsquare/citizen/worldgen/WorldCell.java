package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.entity.Entity;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.util.ColorMath;
import com.redsquare.citizen.util.MathExt;
import com.redsquare.citizen.util.Sets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorldCell {
  private boolean populated = false;

  private final Point location;
  private final World world;

  private final Set<Entity> entities;

  private final CellLandType[][] surroundings;
  private final CellLandType[][] chunks;
  private WorldSubCell[][] subCells;

  private boolean generated;
  private int elevation = 0;
  private CellLandType cellLandType;
  private Region region;
  private Settlement settlement = null;
  private Settlement province = null;
  private River.RiverPoint riverPoint = null;

  WorldCell(CellLandType cellLandType, World world, Point location) {
    this.location = location;
    this.world = world;

    this.entities = new HashSet<>();

    generated = false;
    this.cellLandType = cellLandType;
    region = Region.TEMPERATE;

    surroundings = new CellLandType[3][3];
    chunks = new CellLandType[16][16];
  }

  public Point getLocation() {
    return location;
  }

  public void populateSubCells() {
    // TODO temp fix
    if (populated) return;
    populated = true;

    subCells =
            new WorldSubCell[WorldPosition.CELLS_IN_WORLD_CELL_DIM]
                    [WorldPosition.CELLS_IN_WORLD_CELL_DIM];

    for (int x = 0; x < WorldPosition.CELLS_IN_WORLD_CELL_DIM; x++) {
      for (int y = 0; y < WorldPosition.CELLS_IN_WORLD_CELL_DIM; y++) {
        subCells[x][y] = new WorldSubCell(new Point(x, y), this, cellLandType);
      }
    }
  }

  public WorldSubCell getSubCell(int x, int y) {
    return (x >= 0 && x < WorldPosition.CELLS_IN_WORLD_CELL_DIM &&
            y >= 0 && y < WorldPosition.CELLS_IN_WORLD_CELL_DIM) ? subCells[x][y] : null;
  }

  public void addEntity(Entity e) {
    entities.add(e);
  }

  public void removeEntity(Entity e) {
    entities.remove(e);
  }

  public Set<Entity> getEntities() {
    return entities;
  }

  void populateProvince(Settlement province) {
    this.province = province;
  }

  void populateSettlement(Settlement settlement) {
    this.settlement = settlement;
    settlement.setWorldCell(this);
  }

  boolean hasSettlement() {
    return settlement != null;
  }

  Settlement getSettlement() {
    return settlement;
  }

  boolean isGenerated() {
    return generated;
  }

  Settlement getProvince() {
    return province;
  }

  void setRegion(Region region) {
    this.region = region;
  }

  River.RiverPoint getRiverPoint() {
    return riverPoint;
  }

  void setRiverPoint(River.RiverPoint riverPoint) {
    this.riverPoint = riverPoint;
  }

  void setCellLandType(CellLandType cellLandType) {
    this.cellLandType = cellLandType;
  }

  void setElevation(int elevation) {
    this.elevation = elevation;
  }

  void generate() {
    // Phase 1: Generate surroundings
    generateSurroundings();

    // Phase 2: Generate sub-cells

    // Phase 3: Superimpose potential settlement
    if (settlement != null) {

    }

    generated = true;
  }

  // Generation helpers
  private void generateSurroundings() {
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        WorldCell cell =
                world.getCell((location.x - 1) + x, (location.y - 1) + y);

        surroundings[x][y] = cell != null ? cell.cellLandType : CellLandType.NONE;
      }
    }
  }

  private BufferedImage chunkImage() {
    StringBuilder filename =
            new StringBuilder("res/img_assets/world_setup/chunks/");

    // Determine if central is land or sea
    if (surroundings[1][1].isLand()) {
      // Land central
      // TODO
      if (allMatch(true, Set.of(
              Surroundings.ML, Surroundings.MR
      )) || allMatch(true, Set.of(
              Surroundings.TM, Surroundings.BM
      )))
        filename.append("plain_land");
      else filename.append("indeterminate");
    } else {
      // Sea central
      if (allMatch(false, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.TR,
              Surroundings.BL, Surroundings.BM, Surroundings.BR
      )) || allMatch(false, Set.of(
              Surroundings.TL, Surroundings.ML, Surroundings.BL,
              Surroundings.TR, Surroundings.MR, Surroundings.BR
      )))
        filename.append("plain_water");
      else if (allMatch(true, Set.of(
              Surroundings.TM, Surroundings.ML,
              Surroundings.MR, Surroundings.BM
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.BR
      )))
        filename.append("tl_to_br_diag_water");
      else if (allMatch(true, Set.of(
              Surroundings.TM, Surroundings.ML,
              Surroundings.MR, Surroundings.BM
      )) && allMatch(false, Set.of(
              Surroundings.TR, Surroundings.BL
      )))
        filename.append("tr_to_bl_diag_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.ML, Surroundings.BL,
              Surroundings.TR, Surroundings.MR, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TM, Surroundings.BM
      )))
        filename.append("top_to_bottom_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.TR,
              Surroundings.BL, Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.ML, Surroundings.MR
      )))
        filename.append("left_to_right_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.MR,
              Surroundings.BL, Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.ML, Surroundings.TR
      )))
        filename.append("straight_diag_across_tr_water");
      else if (allMatch(true, Set.of(
              Surroundings.ML, Surroundings.TM, Surroundings.TR,
              Surroundings.BL, Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.MR
      )))
        filename.append("straight_diag_across_tl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.MR,
              Surroundings.BL, Surroundings.BM, Surroundings.TR
      )) && allMatch(false, Set.of(
              Surroundings.ML, Surroundings.BR
      )))
        filename.append("straight_diag_across_br_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.TR,
              Surroundings.ML, Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.BL, Surroundings.MR
      )))
        filename.append("straight_diag_across_bl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.ML,
              Surroundings.MR, Surroundings.BL, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TR, Surroundings.BM
      )))
        filename.append("straight_diag_down_tr_water");
      else if (allMatch(true, Set.of(
              Surroundings.TR, Surroundings.TM, Surroundings.ML,
              Surroundings.MR, Surroundings.BL, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.BM
      )))
        filename.append("straight_diag_down_tl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.MR,
              Surroundings.BL, Surroundings.BM, Surroundings.ML
      )) && allMatch(false, Set.of(
              Surroundings.BR, Surroundings.TM
      )))
        filename.append("straight_diag_down_br_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TR, Surroundings.ML,
              Surroundings.MR, Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.BL, Surroundings.TM
      )))
        filename.append("straight_diag_down_bl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM,
              Surroundings.BL, Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.ML, Surroundings.TR, Surroundings.MR
      )))
        filename.append("l_across_tr_water");
      else if (allMatch(true, Set.of(
              Surroundings.TM, Surroundings.TR,
              Surroundings.BL, Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.MR, Surroundings.ML
      )))
        filename.append("l_across_tl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM,
              Surroundings.BL, Surroundings.BM, Surroundings.TR
      )) && allMatch(false, Set.of(
              Surroundings.ML, Surroundings.BR, Surroundings.MR
      )))
        filename.append("l_across_br_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.TR,
              Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.BL, Surroundings.MR, Surroundings.ML
      )))
        filename.append("l_across_bl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.ML,
              Surroundings.MR, Surroundings.BL, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TR, Surroundings.BM, Surroundings.TM
      )))
        filename.append("l_down_tr_water");
      else if (allMatch(true, Set.of(
              Surroundings.TR, Surroundings.ML,
              Surroundings.MR, Surroundings.BL, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.BM, Surroundings.TM
      )))
        filename.append("l_down_tl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.MR,
              Surroundings.BL, Surroundings.ML
      )) && allMatch(false, Set.of(
              Surroundings.BR, Surroundings.TM, Surroundings.BM
      )))
        filename.append("l_down_br_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TR, Surroundings.ML,
              Surroundings.MR, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.BL, Surroundings.TM, Surroundings.BM
      )))
        filename.append("l_down_bl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.BL,
              Surroundings.BM
      )) && allMatch(false, Set.of(
              Surroundings.TR, Surroundings.ML, Surroundings.BR
      )))
        filename.append("t_opening_right_water");
      else if (allMatch(true, Set.of(
              Surroundings.TR, Surroundings.TM, Surroundings.BR,
              Surroundings.BM
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.MR, Surroundings.BL
      )))
        filename.append("t_opening_left_water");
      else if (allMatch(true, Set.of(
              Surroundings.ML, Surroundings.BL, Surroundings.MR,
              Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.TR, Surroundings.BM
      )))
        filename.append("t_opening_up_water");
      else if (allMatch(true, Set.of(
              Surroundings.ML, Surroundings.TL, Surroundings.MR,
              Surroundings.TR
      )) && allMatch(false, Set.of(
              Surroundings.BL, Surroundings.BR, Surroundings.TM
      )))
        filename.append("t_opening_down_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.ML, Surroundings.TM,
              Surroundings.BL, Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TR, Surroundings.MR
      )))
        filename.append("half_l_across_tr_water");
      else if (allMatch(true, Set.of(
              Surroundings.TM, Surroundings.MR, Surroundings.TR,
              Surroundings.BL, Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.ML
      )))
        filename.append("half_l_across_tl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.ML, Surroundings.TM,
              Surroundings.BL, Surroundings.BM, Surroundings.TR
      )) && allMatch(false, Set.of(
              Surroundings.BR, Surroundings.MR
      )))
        filename.append("half_l_across_br_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TM, Surroundings.TR,
              Surroundings.BM, Surroundings.MR, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.BL, Surroundings.ML
      )))
        filename.append("half_l_across_bl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.BM, Surroundings.ML,
              Surroundings.MR, Surroundings.BL, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TR, Surroundings.TM
      )))
        filename.append("half_l_down_tr_water");
      else if (allMatch(true, Set.of(
              Surroundings.TR, Surroundings.BM, Surroundings.ML,
              Surroundings.MR, Surroundings.BL, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.TM
      )))
        filename.append("half_l_down_tl_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TR, Surroundings.MR,
              Surroundings.BL, Surroundings.TM, Surroundings.ML
      )) && allMatch(false, Set.of(
              Surroundings.BR, Surroundings.BM
      )))
        filename.append("half_l_down_br_water");
      else if (allMatch(true, Set.of(
              Surroundings.TL, Surroundings.TR, Surroundings.ML,
              Surroundings.MR, Surroundings.TM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.BL, Surroundings.BM
      )))
        filename.append("half_l_down_bl_water");
      else if (allMatch(true,
              Surroundings.allBut(Set.of(Surroundings.MR))) &&
              allMatch(false, Set.of(
                      Surroundings.MR
      )))
        filename.append("one_opening_right_water");
      else if (allMatch(true,
              Surroundings.allBut(Set.of(Surroundings.ML))) &&
              allMatch(false, Set.of(
                      Surroundings.ML
      )))
        filename.append("one_opening_left_water");
      else if (allMatch(true,
              Surroundings.allBut(Set.of(Surroundings.TM))) &&
              allMatch(false, Set.of(
                      Surroundings.TM
      )))
        filename.append("one_opening_up_water");
      else if (allMatch(true,
              Surroundings.allBut(Set.of(Surroundings.BM))) &&
              allMatch(false, Set.of(
              Surroundings.BM
      )))
        filename.append("one_opening_down_water");
      else if (allMatch(true, Set.of(
              Surroundings.TM, Surroundings.TL, Surroundings.MR,
              Surroundings.BM, Surroundings.BL
      )) && allMatch(false, Set.of(
              Surroundings.TR, Surroundings.BR
      )))
        filename.append("wide_opening_right_water");
      else if (allMatch(true, Set.of(
              Surroundings.TM, Surroundings.TR, Surroundings.MR,
              Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.BL
      )))
        filename.append("wide_opening_left_water");
      else if (allMatch(true, Set.of(
              Surroundings.ML, Surroundings.BL, Surroundings.MR,
              Surroundings.BM, Surroundings.BR
      )) && allMatch(false, Set.of(
              Surroundings.TL, Surroundings.TR
      )))
        filename.append("wide_opening_up_water");
      else if (allMatch(true, Set.of(
              Surroundings.ML, Surroundings.TL, Surroundings.MR,
              Surroundings.BM, Surroundings.TR
      )) && allMatch(false, Set.of(
              Surroundings.BL, Surroundings.BR
      )))
        filename.append("wide_opening_down_water");
      else filename.append("indeterminate");
    }

    filename.append(".png");

    try {
      return ImageIO.read(new File(filename.toString()));
    } catch (IOException e) {
      return new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    }
  }

  private enum Surroundings {
    TL, TM, TR,
    ML, MM, MR,
    BL, BM, BR;

    static Set<Surroundings> allBut(Set<Surroundings> bar) {
      Set<Surroundings> middleIncluded = Sets.union(bar, Set.of(MM));
      return Sets.difference(
              Set.of(TL, TM, TR, ML, MM, MR, BL, BM, BR), middleIncluded);
    }

    static Point coords(Surroundings s) {
      switch (s) {
        case TL:
          return new Point(0, 0);
        case TM:
          return new Point(1, 0);
        case TR:
          return new Point(2, 0);
        case ML:
          return new Point(0, 1);
        case MM:
          return new Point(1, 1);
        case MR:
          return new Point(2, 1);
        case BL:
          return new Point(0, 2);
        case BM:
          return new Point(1, 2);
        case BR:
        default:
          return new Point(2, 2);
      }
    }
  }

  private boolean allMatch(boolean isLand, Set<Surroundings> cells) {
    for (Surroundings cell : cells) {
      Point p = Surroundings.coords(cell);
      if (surroundings[p.x][p.y].isLand() != isLand) return false;
    }
    return true;
  }

  public enum Region {
    POLAR, TEMPERATE, SUBTROPICAL, TROPICAL
  }

  public enum CellLandType {
    PLAIN, DESERT, SEA, SHALLOW, BEACH, MOUNTAIN, HILL, FOREST, // MAIN TYPES
    NONE; // TECHNICAL TYPE

    boolean isLand() {
      switch (this) {
        case SHALLOW:
        case SEA:
        case NONE:
          return false;
        case PLAIN:
        case DESERT:
        case FOREST:
        case MOUNTAIN:
        case HILL:
        case BEACH:
        default:
          return true;
      }
    }
  }

  CellLandType getCellLandType() {
    return cellLandType;
  }

  Region getRegion() {
    return region;
  }

  int getElevation() {
    return elevation;
  }

  boolean isLand() {
    return cellLandType.isLand();
  }

  static Color getMapColor(CellLandType cellLandType, Region region) {
    Color c = new Color(0, 0, 0);

    switch (cellLandType) {
      case SHALLOW:
        switch (region) {
          case POLAR:
            c = new Color(40,
                    150 + (int)(10 * Math.random()),
                    200 + (int)(10 * Math.random()));
            break;
          case TROPICAL:
            c = new Color(0,
                    140, 190 + (int)(15 * Math.random()));
            break;
          default:
            c = new Color(0,
                    120, 180 + (int)(15 * Math.random()));
        }
        break;
      case SEA:
        switch (region) {
          case POLAR:
            c = new Color(0,
                    95 + (int)(10 * Math.random()),
                    160 + (int)(10 * Math.random()));
            break;
          case TROPICAL:
            c = new Color(0,
                    105 + (int)(10 * Math.random()),
                    155 + (int)(10 * Math.random()));
            break;
          default:
            c = new Color(0,
                    100 + (int)(10 * Math.random()),
                    150 + (int)(10 * Math.random()));
        }
        break;
      case FOREST:
        switch (region) {
          case POLAR:
            c = new Color(40 + (int)(30 * Math.random()),
                    80 + (int)(30 * Math.random()), 25);
            break;
          case TROPICAL:
          case SUBTROPICAL:
            c = new Color(10,
                    90 + (int)(30 * Math.random()), 5);
            break;
          default:
            c = new Color(20,
                    85 + (int)(30 * Math.random()), 10);
        }
        break;
      case DESERT:
        switch (region) {
          case POLAR:
            c = new Color(200 + (int)(30 * Math.random()),
                    200 + (int)(30 * Math.random()), 220);
            break;
          default:
            c = new Color(120 + (int)(30 * Math.random()),
                    96 + (int)(30 * Math.random()), 10);
        }
        break;
      case PLAIN:
        switch (region) {
          case POLAR:
            c = new Color(200 + (int)(30 * Math.random()),
                    200 + (int)(30 * Math.random()), 220);
            break;
          case TROPICAL:
          case SUBTROPICAL:
            c = new Color(80,
                    125 + (int)(30 * Math.random()), 15);
            break;
          default:
            c = new Color(80,
                    115 + (int)(30 * Math.random()), 15);
        }
        break;
      case MOUNTAIN:
        c = new Color(100 + (int)(30 * Math.random()),
                100 + (int)(30 * Math.random()),
                100 + (int)(30 * Math.random()));
        break;
      case HILL:
        c = new Color(77,
                90 + (int)(30 * Math.random()), 15);
        break;
      case BEACH:
        c = new Color(150 + (int)(30 * Math.random()),
                145 + (int)(30 * Math.random()), 100);
        break;
    }

    return c;
  }
}
