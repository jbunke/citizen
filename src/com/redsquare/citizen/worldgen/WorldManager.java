package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.entity.*;
import com.redsquare.citizen.entity.collision.CollisionManager;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.MathExt;
import com.redsquare.citizen.util.Randoms;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class WorldManager {
  private boolean worldStarted = false;

  private final World world;
  private GameDate date;
  private final Set<Person> people;
  private Player player;

  // DEBUG INFO
  private int entityCount = 0;

  private WorldManager(World world) {
    this.world = world;
    this.date = new GameDate(1, 600);
    this.people = new HashSet<>();
  }

  static WorldManager init(World world) {
    return new WorldManager(world);
  }

  /** !!! CURRENT SPAWN FUNCTION */
  public void startOfGameSimulation(int years) {
    if (worldStarted) return;
    worldStarted = true;

    initialPopulation();

    simulateYears(years);
    generatePlayer();

    populateCellsAroundPlayer();

    // TODO: Spawn test entities after this point
    ItemEntity.itemEntityCreateTest(world);
    Building.generate(new WorldPosition(player.position().world(), new Point(1, 1),
            WorldPosition.centralWithinSubCell(), world, null));
  }

  private void initialPopulation() {
    // TODO
    for (Settlement settlement : world.allSettlements()) {
      int toPopulate = MathExt.bounded((int)(settlement.getSetupPower() *
              Randoms.bounded(0.5, 2.0)), 10, 20); // TODO: MAX: 150

      for (int i = 0; i < toPopulate; i++) {
        int age = Randoms.bounded(16, 91);
        Sex sex = Math.random() < 0.5 ? Sex.MALE : Sex.FEMALE;

        Person person = Person.create(sex, new GameDate(Randoms.bounded(1,
                GameDate.STANDARD_DAYS_IN_YEAR + 1), date.year - age),
                settlement, world);
        people.add(person);

      }
    }
  }

  private void generatePlayer() {
    this.player = Player.temp(world, new GameDate(date.day, date.year - 20));
  }

  private void populateCellsAroundPlayer() {
    int xMin = Math.max(player.position().world().x - 1, 0);
    int yMin = Math.max(player.position().world().y - 1, 0);
    int xMax = Math.min(player.position().world().x + 1, world.getWidth() - 1);
    int yMax = Math.min(player.position().world().y + 1, world.getHeight() - 1);

    for (int x = xMin; x <= xMax; x++) {
      for (int y = yMin; y <= yMax; y++) {
        world.getCell(x, y).populateSubCells();
      }
    }
  }

  void simulateYears(int years) {
    for (int i = 0; i < years; i++) {
      yearUpdate();
    }
  }

  private void yearUpdate() {
    // TODO
    for (Person person : people) {
      person.getPsychology().macroUpdate(date);
    }

    for (Settlement settlement : world.allSettlements()) {
      settlement.macroUpdate(date);
    }

    date = GameDate.incrementYear(date);
  }

  public void update() {
    populateCellsAroundPlayer();

    Set<Entity> entities = entitiesCloseBy();

    if (entityCount != entities.size()) {
      entityCount = entities.size();
      GameDebug.printMessage(
              "Processing: " + entityCount + " entities",
              GameDebug::printDebug);
    }

    for (Entity entity : entities)
      entity.update();

    collisionCheck(entities);
  }

  private void collisionCheck(Set<Entity> entities) {
    for (Entity a : entities) {
      for (Entity b : entities) {
        if (!a.equals(b)) {
          CollisionManager.check(a, b);
        }
      }
    }
  }

  private Set<Entity> entitiesCloseBy() {
    Set<Entity> entities = new HashSet<>();

    for (int x = Math.max(0, player.position().world().x - 1);
         x < Math.min(world.getWidth(), player.position().world().x + 2);
         x++) {
      for (int y = Math.max(0, player.position().world().y - 1);
           y < Math.min(world.getHeight(), player.position().world().y + 2);
           y++) {
        entities.addAll(world.getCell(x, y).getEntities());
      }
    }

    return entities;
  }

  public Player getPlayer() {
    return player;
  }
}
