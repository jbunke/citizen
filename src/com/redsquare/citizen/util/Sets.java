package com.redsquare.citizen.util;

import java.util.HashSet;
import java.util.Set;

public class Sets {
  public static <T> Set<T> difference(Set<T> from, Set<T> without) {
    Set<T> set = new HashSet<>();

    for (T element : from)
      if (!without.contains(element)) set.add(element);

    return set;
  }

  public static <T> Set<T> union(Set<T> a, Set<T> b) {
    Set<T> union = new HashSet<>();

    a.forEach(x -> {
      if (!union.contains(x)) union.add(x);
    });
    b.forEach(x -> {
      if (!union.contains(x)) union.add(x);
    });

    return union;
  }

  public static <T> Set<T> union(Set<Set<T>> sets) {
    Set<T> union = new HashSet<>();

    for (Set<T> set : sets) {
      union = union(union, set);
    }

    return union;
  }

  /* For example:
   * Intersection Person A's children Set<Person> and Person B's children
   * Set<Person> will return the children that they share as parents */
  public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
    Set<T> intersection = new HashSet<>();

    a.forEach(x -> {
      if (b.contains(x)) intersection.add(x);
    });

    return intersection;
  }

  public static <T> Set<T> intersection(Set<Set<T>> sets) {
    Set<T> intersection = new HashSet<>();

    for (Set<T> set : sets) {
      intersection = intersection(intersection, set);
    }

    return intersection;
  }
}
