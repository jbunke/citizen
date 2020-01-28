package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.systems.politics.Culture;
import org.junit.Test;

public class ReligionTests {

  @Test
  public void createReligion() {
    NonDeisticReligion ndr = new NonDeisticReligion(Culture.generate());

    System.out.println(ndr);
  }
}
