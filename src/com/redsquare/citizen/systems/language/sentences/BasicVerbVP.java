package com.redsquare.citizen.systems.language.sentences;

import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.Meaning;
import com.redsquare.citizen.systems.language.Word;

import java.util.ArrayList;
import java.util.List;

public class BasicVerbVP extends VerbPhrase {
  private final short tense;
  private final Meaning[] adverbs;
  private final Meaning verb;

  public BasicVerbVP(short tense, Meaning[] adverbs, Meaning verb) {
    this.tense = tense;
    this.adverbs = adverbs;
    this.verb = verb;
  }

  @Override
  public List<Word> getWords(Language language, NounPhrase subject) {
    List<Word> words = new ArrayList<>();

    for (Meaning adverb : adverbs) words.add(language.lookUpWord(adverb));

    Word verb = language.getGrammar().conjugate(language.lookUpWord(this.verb),
            tense, subject.person(language));
    words.add(verb);

    return words;
  }
}
