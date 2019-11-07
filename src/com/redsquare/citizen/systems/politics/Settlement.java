package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.PlaceNameGenerator;
import com.redsquare.citizen.systems.language.Word;
import com.redsquare.citizen.systems.structures.SettlementLayout;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.Formatter;
import com.redsquare.citizen.util.MathExt;
import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.worldgen.WorldCell;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Settlement {
  private static final int PROXIMITY_FACTOR = 10;

  private Word name;
  private final Word originalName;
  private final Culture foundingCulture;
  private Language nativeDialect;

  private State state;
  private Set<Settlement> vassals = new HashSet<>();
  private Settlement liege;
  private House rulingHouse;
  private int setupPower;
  private double economy = 1.0;

  private WorldCell cell;

  private Point location;
  private SettlementLayout layout;

  public Settlement(Point location, State state) {
    this.location = location;
    this.state = state;

    this.layout = null;

    this.name = PlaceNameGenerator.generateRandomName(
            2, 4, state.getLanguage().getPhonology());
    this.originalName = name;
    this.foundingCulture = state.getCulture();
    this.nativeDialect = state.getLanguage();

    setupPower = 0;
  }

  public SettlementLayout getLayout() {
    if (layout == null) layout = SettlementLayout.generate(this);

    return layout;
  }

  public State getState() {
    return state;
  }

  public String getName() {
    return name.toString();
  }

  public int getSetupPower() {
    return setupPower;
  }

  public Point getLocation() {
    return location;
  }

  public void accruePower(int rawPower) {
    double newEconomy = (0.9 * economy) + (Math.random() * 0.2 * economy);
    economy = Math.min(Math.max(1/2.0, newEconomy), 2/1.0);

    setupPower += (int)(economy * rawPower);
  }

  public void setWorldCell(WorldCell cell) {
    this.cell = cell;
  }

  /** Conquest system function for state reassignment */
  void setState(State state) {
    this.state = state;
    vassals.forEach(x -> x.setState(state));
  }

  public Settlement getLiege() {
    return liege;
  }

  Set<Settlement> getVassals() {
    return vassals;
  }

  void addVassals(Set<Settlement> newVassals) {
    vassals.addAll(newVassals);
    newVassals.forEach(x -> x.liege = this);
  }

  public void addVassal(Settlement newVassal) {
    vassals.add(newVassal);
    newVassal.liege = this;
  }

  private void removeVassal(Settlement vassal) {
    vassals.remove(vassal);
  }

  void resetVassals() {
    vassals = new HashSet<>();
  }

  private boolean isCapital() {
    return state.getCapital().equals(this);
  }

//  public boolean isLiege() {
//    return !vassals.isEmpty();
//  }

  void removeLiege() {
    liege = null;
  }

  public void macroUpdate(GameDate date) {
    // TODO
    double prob = Math.random();
    if (prob < 0.01) secede(date);
    else if (prob < 0.04) {
      double d = Double.MAX_VALUE;
      Settlement nearestForeign = null;

      List<Settlement> all = new ArrayList<>(state.getWorld().allSettlements());
      all.sort(Comparator.comparingDouble(
              x -> MathExt.distance(x.location, this.location)
      ));

      for (Settlement s : all) {
        if (!s.state.equals(state)) {
          nearestForeign = s;
          d = MathExt.distance(location, s.location);
          break;
        }
      }

      // Only consolidate if there aren't many settlements in the state closer to the target

      if (nearestForeign != null) {
        Set<Settlement> others = state.settlements();
        final int allowance = others.size() / PROXIMITY_FACTOR;
        int closer = 0;
        for (Settlement o : state.settlements()) {
          if (!o.equals(this) && d >
                  MathExt.distance(o.location, nearestForeign.location))
            closer++;
        }
        if (closer <= allowance) consolidate(nearestForeign, date);
      }
    }
  }

  private void incorporate(Settlement s, int pl) {
    if (s.liege != null) s.liege.removeVassal(s);

    if (pl <= 2) {
      /* If the incorporated settlement was a capital or
       * regional capital, make it a regional capital */
      state.getCapital().addVassal(s);

      /* If incorporated was capital, make have its provinces
       * retain provincial status */
      if (pl == 1) {
        Set<Settlement> vassals = new HashSet<>(s.vassals);

        for (Settlement v : vassals) {
          if (v.vassals.size() > 0) {
            s.removeVassal(v);
            state.getCapital().addVassal(v);
          }
        }
      }
    } else if (powerLevel() < 3) {
      /* If the town that conquered isn't lowest-level, make incorporated
       * town a vassal */
      addVassal(s);
    } else {
      /* Otherwise make it a vassal of the regional capital */
      getLiege().addVassal(s);
    }

    s.adjustDepth();
  }

  private void adjustDepth() {
    while (powerLevel() > 3) {
      liege.removeVassal(this);
      liege.getLiege().addVassal(this);
    }
    List<Settlement> vs = new ArrayList<>(vassals);

    for (Settlement v : vs) {
      v.adjustDepth();
    }
  }

  private void consolidate(Settlement newVassal, GameDate date) {
    int pl = newVassal.powerLevel();
    // FAIL CASE
    if ((pl > powerLevel() && powerLevel() == 3) ||
            newVassal.setupPower > setupPower * Randoms.bounded(0.7, 2.0)) return;

    boolean wasCapital = newVassal.isCapital();
    State from = newVassal.getState();
    newVassal.setState(state);
    newVassal.rename();
    incorporate(newVassal, pl);

    // If the city was a capital or a regional capital,
    // then restructure the consuming and the losing states
    if (pl < 3) {
      from.administrativeRestructuring();
      state.administrativeRestructuring();
    }

    state.getWorld().processConsolidation(from, state, wasCapital);

    GameDebug.printMessage(this + " consolidated " + newVassal +
            " into " + state + " in " + date.year, GameDebug::printDebug);
  }

  private void rename() {
    // TODO: Potentially transliterate instead
    this.name = PlaceNameGenerator.generateRandomName(
            2, 4, state.getLanguage().getPhonology());
    vassals.forEach(Settlement::rename);
  }

  private void revertToOriginalName() {
    this.name = originalName;
    vassals.forEach(Settlement::revertToOriginalName);
  }

  private void secede(GameDate date) {
    if (isCapital() || powerLevel() == 3) return;

    if (liege != null) {
      liege.removeVassal(this);
      removeLiege();
    }

    revertToOriginalName();

    State newState = State.fromSecession(this, nativeDialect,
            foundingCulture, state);

    setState(newState);
    state.administrativeRestructuring();
    state.getWorld().addState(state);

    GameDebug.printMessage(this + " seceded from " + state +
            " to form " + newState + " in " + date.year, GameDebug::printDebug);
  }

  public Settlement regionCapital() {
    switch (powerLevel()) {
      case 1:
      case 2:
        return this;
      default:
        return liege;
    }
  }

  public int powerLevel() {
    if (isCapital()) return 1;

    if (liege.isCapital()) {
      if (!vassals.isEmpty()) return 2;
      return 3;
    }

    return 1 + liege.powerLevel();
  }

  @Override
  public int hashCode() {
    return (7 * location.x) + location.y;
  }

  @Override
  public String toString() {
    return Formatter.properNoun(name.toString()) + "; in " +
            state.toString() + " at (" + location.x + ", " + location.y + ")";
  }
}
