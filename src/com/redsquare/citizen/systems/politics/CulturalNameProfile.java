package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.Meaning;
import com.redsquare.citizen.systems.language.Name;
import com.redsquare.citizen.systems.language.Word;
import com.redsquare.citizen.systems.language.sentences.BasicNounNP;
import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class CulturalNameProfile {
  private final List<Name> unisexNames;
  private final List<Name> femaleNames;
  private final List<Name> maleNames;

  private CulturalNameProfile(Language language) {
    this.unisexNames = new ArrayList<>();
    this.maleNames = new ArrayList<>();
    this.femaleNames = new ArrayList<>();

    populateNames(language);
  }

  private void populateNames(Language language) {
    final int NAMES = 100;

    // UNISEX LOOP
    for (int a = 0; a < 3; a++) {
      Name.NameGender gender;
      List<Name> nameList;

      switch (a) {
        case 0:
          gender = Name.NameGender.U;
          nameList = unisexNames;
          break;
        case 1:
          gender = Name.NameGender.F;
          nameList = femaleNames;
          break;
        case 2:
        default:
          gender = Name.NameGender.M;
          nameList = maleNames;
          break;
      }

      for (int i = 0; i < NAMES; i++) {
        if (i < 50) {
          Word name = Word.generateRandomWord(1, 4,
                  language.getPhonology());
          nameList.add(Name.generate(name, null, gender, language));
        } else {
          Meaning adjective = Sets.randomEntry(
                  Meaning.getAll(Meaning.LexClass.ADJECTIVE));
          Meaning noun;

          switch (gender) {
            case F:
              noun = Sets.randomEntry(Set.of(
                      Meaning.WOMAN, Meaning.GIRL, Meaning.PERSON, Meaning.LOVE,
                      Meaning.LOVER, Meaning.CHILD, Meaning.MOTHER
              ));
              break;
            case M:
              noun = Sets.randomEntry(Set.of(
                      Meaning.MAN, Meaning.BOY, Meaning.PERSON, Meaning.LOVE,
                      Meaning.LOVER, Meaning.CHILD, Meaning.FATHER
              ));
              break;
            case U:
            default:
              noun = Sets.randomEntry(Set.of(
                      Meaning.PERSON, Meaning.LOVE, Meaning.LOVER, Meaning.CHILD
              ));
              break;
          }

          Word name = Word.compound(language.lookUpWord(adjective),
                  language.lookUpWord(noun));
          nameList.add(Name.generate(name,
                  new BasicNounNP(false, 1,
                          new Meaning[] { adjective }, noun),
                  gender, language));
        }
      }
    }
  }

  static CulturalNameProfile generate(Language language) {
    return new CulturalNameProfile(language);
  }

  private Name getNameFromList(final List<Name> nameList) {
    nameList.sort(Comparator.comparingDouble(Name::getPopularity));

    for (Name name : nameList) {
      if (Randoms.prob(0.1))
        return name;
    }

    return nameList.get(nameList.size() - 1);
  }

  public Name getFemaleName() {
    if (Randoms.random())
      return getNameFromList(unisexNames);
    return getNameFromList(femaleNames);
  }

  public Name getMaleName() {
    if (Randoms.random())
      return getNameFromList(unisexNames);
    return getNameFromList(maleNames);
  }
}
