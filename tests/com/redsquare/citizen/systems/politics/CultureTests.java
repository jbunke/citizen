package com.redsquare.citizen.systems.politics;

import org.junit.Test;

public class CultureTests {

  @Test
  public void createCulture() {
    Culture culture = Culture.generate();

    System.out.println(culture);
  }
}
