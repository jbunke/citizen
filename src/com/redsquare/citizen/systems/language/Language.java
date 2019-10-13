package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.util.Randoms;

import java.util.HashSet;
import java.util.Set;

public class Language {
  private final Language ancestor;

  private final Phonology phonology;
  private final WritingSystem writingSystem;
  private final WordVocabulary words;
  private final Grammar grammar;

  private final Word name;

  private Language() {
    ancestor = null;

    phonology = Phonology.generate();
    writingSystem = WritingSystem.generate(phonology);
    grammar = Grammar.generate(phonology);
    words = WordVocabulary.generate(phonology);

    name = words.lookUp(Meaning.THIS_LANGUAGE);
  }

  private Language(WritingSystem writingSystem) {
    ancestor = null;

    this.phonology = writingSystem.phonology;
    this.writingSystem = writingSystem;
    grammar = Grammar.generate(phonology);
    words = WordVocabulary.generate(phonology);

    name = words.lookUp(Meaning.THIS_LANGUAGE);
  }

  private Language(Language ancestor) {
    this.ancestor = ancestor;

    this.phonology = ancestor.phonology;

    int numSoundShifts = Randoms.bounded(1, 6);
    Set<SoundShift> soundShifts = new HashSet<>();

    while (soundShifts.size() < numSoundShifts) {
      double odds = Math.random();
      SoundShift toAdd;

      if (odds < 0.6) {
        toAdd = SoundShift.vowelSoundShift(phonology);
      } else if (odds < 0.83) {
        toAdd = SoundShift.prefixSoundShift(phonology);
      } else {
        toAdd = SoundShift.suffixSoundShift(phonology);
      }

      if (!toAdd.from.equals("NULL") && !toAdd.to.equals("NULL"))
        soundShifts.add(toAdd);
    }

    /* Whether to
     *   - Keep the ancestral writing system (Sanskrit -> Hindi [Devanagari])
     *   - Modify the ancestral writing system (Latin -> France)
     *          [Latin alphabet + diacritical marks]
     *   - Adopting a new writing system (Sanskrit -> Hindustani -> Urdu)
     *          [Arabic-inspired system adopted for liturgical reasons]
     * */
    double newWS = Math.random();
    if (newWS < 0.6) {
      this.writingSystem = ancestor.writingSystem;
    } else if (newWS < 0.85) {
      this.writingSystem = ancestor.writingSystem; // TODO: .modify();
    } else {
      this.writingSystem = WritingSystem.generate(phonology,
              ancestor.writingSystem.type);
    }

    this.grammar = ancestor.grammar;
    this.words = ancestor.words.divergentDaughterVocabulary(
            soundShifts, phonology);

    name = words.lookUp(Meaning.THIS_LANGUAGE);
  }

  public Language daughterLanguage() {
    return new Language(this);
  }

  public static Language generate() {
    return new Language();
  }

  public static Language generate(WritingSystem ws) {
    return new Language(ws);
  }

  public Phonology getPhonology() {
    return phonology;
  }

  public WritingSystem getWritingSystem() {
    return writingSystem;
  }

  public Word getName() {
    return name;
  }

  public Word lookUpWord(Meaning meaning) {
    return words.lookUp(meaning);
  }
}
