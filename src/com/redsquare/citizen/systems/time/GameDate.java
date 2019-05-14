package com.redsquare.citizen.systems.time;

public class GameDate {
  public final int day;
  public final int year;

  public GameDate(int day, int year) {
    this.day = day;
    this.year = year;
  }

  public String written(Calendar c) {
    return c.yearDay(day) + " " + year;
  }

  public static GameDate increment(GameDate from, final int DAYS_IN_YEAR) {
    int day = from.day + 1;
    int year = from.year;
    if (day > DAYS_IN_YEAR) {
      day = 1;
      year++;
    }

    return new GameDate(day, year);
  }
}
