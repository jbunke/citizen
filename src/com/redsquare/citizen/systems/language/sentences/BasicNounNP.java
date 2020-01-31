package com.redsquare.citizen.systems.language.sentences;

import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.Meaning;
import com.redsquare.citizen.systems.language.Word;

import java.util.ArrayList;
import java.util.List;

public class BasicNounNP extends NounPhrase {
  private final Meaning noun;
  private final boolean definite;
  private final Meaning[] adjectives;
  private final int number;

  public BasicNounNP(boolean definite, int number, Meaning[] adjectives, Meaning noun) {
    this.definite = definite;
    this.number = number;
    this.adjectives = adjectives;
    this.noun = noun;
  }

  @Override
  public List<Word> getWords(Language language) {
    List<Word> words = new ArrayList<>();
    Word article = language.getGrammar().getArticle(number, definite, noun);
    words.add(article);

    for (Meaning adjective : adjectives) words.add(language.lookUpWord(adjective));

    Word nounWord = number > 1 ?
            language.getGrammar().pluralForm(noun) :
            language.lookUpWord(noun);
    words.add(nounWord);

    return words;
  }

  @Override
  public int number() {
    return number;
  }

  @Override
  public int person(Language language) {
    return language.getGrammar().getPerson(number, noun);
  }
}
