package com.redsquare.citizen.systems.language.sentences;

import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.Word;

import java.util.ArrayList;
import java.util.List;

public class VerbAndNounVP extends VerbPhrase {
  private final VerbPhrase verb;
  private final NounPhrase noun;

  public VerbAndNounVP(VerbPhrase verb, NounPhrase noun) {
    this.verb = verb;
    this.noun = noun;
  }

  @Override
  public List<Word> getWords(Language language, NounPhrase subject) {
    List<Word> words = new ArrayList<>();
    words.addAll(verb.getWords(language, subject));
    words.addAll(noun.getWords(language));
    return words;
  }
}
