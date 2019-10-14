package com.redsquare.citizen.systems.time;

public class GameDate {
  public static final int STANDARD_DAYS_IN_YEAR = 10;

  public final int day;
  public final int year;

  public GameDate(int day, int year) {
    this.day = day;
    this.year = year;
  }

  public String written(Calendar c) {
    return c.yearDay(day) + " " + year;
  }

  public static GameDate incrementYear(GameDate from) {
    return new GameDate(from.day, from.year + 1);
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

  public static int yearsBetween(GameDate first, GameDate second) {
    return (first.year - second.year) - (second.day > first.day ? 1 : 0);
  }

  public static GameDate priorEvent(GameDate d1, GameDate d2) {
    if (d1.year < d2.year) return d1;
    else if (d2.year < d1.year) return d2;
    else return d1.day <= d2.day ? d1 : d2;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GameDate)) return false;

    GameDate comp = (GameDate) obj;

    return year == comp.year && day == comp.day;
  }

  @Override
  public int hashCode() {
    return year + day;
  }
}
