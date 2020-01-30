package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.entity.Person;
import com.redsquare.citizen.entity.Sex;
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

  private final int[] givenNameRange;
  private final SurnameConvention surnameConvention;

  public enum SurnameConvention {
    SURNAME, BOTH_PARENTS, FIRST_NAME_PLUS_POSTNOMIAL, FIRST_NAME
  }

  private CulturalNameProfile(Language language) {
    this.unisexNames = new ArrayList<>();
    this.maleNames = new ArrayList<>();
    this.femaleNames = new ArrayList<>();

    int lesser = Randoms.prob(0.8) ? 1 : 2;
    this.givenNameRange = new int[] {
            lesser, lesser + Randoms.bounded(0, 2)
    };

    double prob = Randoms.bounded(0., 1.);

    if (prob < 0.5)
      this.surnameConvention = SurnameConvention.SURNAME;
    else if (prob < 4 / 6.)
      this.surnameConvention = SurnameConvention.BOTH_PARENTS;
    else if (prob < 5 / 6.)
      this.surnameConvention = SurnameConvention.FIRST_NAME;
    else
      this.surnameConvention = SurnameConvention.FIRST_NAME_PLUS_POSTNOMIAL;

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
      if (Randoms.prob(0.05))
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

  public int pickGivenNameAmount() {
    return Randoms.bounded(givenNameRange[0], givenNameRange[1] + 1);
  }

  public Word[] generateSurname(Person[] surnameGivers, Sex sex, Language language) {
    Word[] surnames = new Word[surnameGivers.length];

    for (int i = 0; i < surnameGivers.length; i++) {
      Word[] parentName = surnameGivers[i].getName();
      Word surname;

      switch (surnameConvention) {
        case SURNAME:
          surname = parentName[parentName.length - 1];
          break;
        case BOTH_PARENTS:
          surname = parentName[parentName.length - 2];
          break;
        case FIRST_NAME:
          surname = parentName[0];
          break;
        case FIRST_NAME_PLUS_POSTNOMIAL:
        default:
          switch (sex) {
            case MALE:
              surname = Word.compound(parentName[0],
                      language.lookUpWord(Meaning.SON));
              break;
            case FEMALE:
            default:
              surname = Word.compound(parentName[0],
                      language.lookUpWord(Meaning.DAUGHTER));
              break;
          }
          break;
      }

      surnames[i] = surname;
    }

    return surnames;
  }

  public SurnameConvention getSurnameConvention() {
    return surnameConvention;
  }
}
