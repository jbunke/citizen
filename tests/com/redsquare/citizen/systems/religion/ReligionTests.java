package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.systems.politics.Culture;
import org.junit.Test;

public class ReligionTests {

  @Test
  public void createReligion() {
    PolytheisticReligion religion = new PolytheisticReligion(Culture.generate());

    System.out.println(religion);
  }
}
