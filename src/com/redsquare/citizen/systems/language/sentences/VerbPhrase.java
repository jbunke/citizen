package com.redsquare.citizen.systems.language.sentences;

import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.Word;

import java.util.List;

public abstract class VerbPhrase {

  public static final short PAST_TENSE = 0, PRESENT_TENSE = 1, FUTURE_TENSE = 2;

  public abstract List<Word> getWords(Language language, NounPhrase subject);
}
