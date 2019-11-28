package com.redsquare.citizen.systems.language.sentences;

public class Sentence {
  public final NounPhrase nounPhrase;
  public final VerbPhrase verbPhrase;

  public Sentence(NounPhrase nounPhrase, VerbPhrase verbPhrase) {
    this.nounPhrase = nounPhrase;
    this.verbPhrase = verbPhrase;
  }
}
