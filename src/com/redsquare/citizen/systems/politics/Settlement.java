package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.language.PlaceNameGenerator;
import com.redsquare.citizen.systems.language.Word;
import com.redsquare.citizen.systems.structures.SettlementLayout;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.Formatter;
import com.redsquare.citizen.worldgen.WorldCell;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Settlement {
  private Word name;

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

  boolean isCapital() {
    return state.getCapital().equals(this);
  }

  public boolean isLiege() {
    return !vassals.isEmpty();
  }

  void removeLiege() {
    liege = null;
  }

  public void macroUpdate(GameDate date) {
    // TODO
    if (Math.random() < 0.1) secede();
  }

  private void secede() {
    if (isCapital() || powerLevel() == 3) return;

    if (liege != null) {
      liege.removeVassal(this);
      removeLiege();
    }

    State newState = State.fromSecession(this, state.getLanguage(),
            state.getCulture(), state);

    setState(newState);
    state.getWorld().addState(state);
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
