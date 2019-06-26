package com.redsquare.citizen.systems.language;

import java.util.*;

public class WordVocabulary {
  private final Map<Meaning, Word> wordDictionary;
  private final Map<Word, Meaning> semanticDictionary;

  private WordVocabulary(Phonology v) {
    wordDictionary = new HashMap<>();
    semanticDictionary = new HashMap<>();

    generateVocabulary(v);
  }

  static WordVocabulary generate(Phonology v) {
    return new WordVocabulary(v);
  }

  Word lookUp(Meaning meaning) {
    if (wordDictionary.containsKey(meaning))
      return wordDictionary.get(meaning);

    return null;
  }

  private void generateVocabulary(Phonology v) {
    Meaning[] allMeanings = Meaning.values();
    Set<Word> usedWords = new HashSet<>();

    // populate core concepts first
    for (Meaning meaning : allMeanings) {
      if (meaning.getDegree() == 0) {
        Word candidate = null;
        boolean violates = true;

        while (violates) {
          candidate = Word.generateRandomWord(1, 3, v);
          violates = usedWords.contains(candidate);
        }

        usedWords.add(candidate);
        wordDictionary.put(meaning, candidate);
        semanticDictionary.put(candidate, meaning);
      }
    }

    Set<Meaning> skips = new HashSet<>();

    // generate semantically logical words for secondary non-core concepts
    for (int degree = 1; degree <= 2; degree++) {
      for (Meaning meaning : allMeanings) {
        if (meaning.getDegree() == degree && !skips.contains(meaning)) {
          switch (meaning) {
            case BOY:
            case GIRL:
              generateNonCoreFor(new Meaning[] {
                              Meaning.BOY, Meaning.GIRL
                      }, 0.5, new double[] { 0.5, 1.0 }, // 0.5
                      new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.YOUNG),
                                              wordDictionary.get(Meaning.MALE)),
                                      Word.compound(wordDictionary.get(Meaning.YOUNG),
                                              wordDictionary.get(Meaning.FEMALE))
                              },
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.MALE),
                                              wordDictionary.get(Meaning.YOUNG)),
                                      Word.compound(wordDictionary.get(Meaning.FEMALE),
                                              wordDictionary.get(Meaning.YOUNG))
                              }
                      }, usedWords, skips, v);
              break;
            case FATHER:
            case MOTHER:
              generateNonCoreFor(new Meaning[] {
                              Meaning.FATHER, Meaning.MOTHER
                      }, 1.0, new double[] { 0.5, 1.0 }, // 0.5
                      new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.PARENT),
                                              wordDictionary.get(Meaning.MALE)),
                                      Word.compound(wordDictionary.get(Meaning.PARENT),
                                              wordDictionary.get(Meaning.FEMALE))
                              },
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.MALE),
                                              wordDictionary.get(Meaning.PARENT)),
                                      Word.compound(wordDictionary.get(Meaning.FEMALE),
                                              wordDictionary.get(Meaning.PARENT))
                              }
                      }, usedWords, skips, v);
              break;
            case SON:
            case DAUGHTER:
              generateNonCoreFor(new Meaning[] {
                              Meaning.SON, Meaning.DAUGHTER
                      }, 0.7, new double[] { 0.5, 1.0 }, // 0.5
                      new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.CHILD_OF),
                                              wordDictionary.get(Meaning.MALE)),
                                      Word.compound(wordDictionary.get(Meaning.CHILD_OF),
                                              wordDictionary.get(Meaning.FEMALE))
                              },
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.MALE),
                                              wordDictionary.get(Meaning.CHILD_OF)),
                                      Word.compound(wordDictionary.get(Meaning.FEMALE),
                                              wordDictionary.get(Meaning.CHILD_OF))
                              }
                      }, usedWords, skips, v);
              break;
            case BROTHER:
            case SISTER:
              generateNonCoreFor(new Meaning[] {
                              Meaning.BROTHER, Meaning.SISTER
                      }, 0.7, new double[] { 0.5, 1.0 }, // 0.5
                      new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.SIBLING),
                                              wordDictionary.get(Meaning.MALE)),
                                      Word.compound(wordDictionary.get(Meaning.SIBLING),
                                              wordDictionary.get(Meaning.FEMALE))
                              },
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.MALE),
                                              wordDictionary.get(Meaning.SIBLING)),
                                      Word.compound(wordDictionary.get(Meaning.FEMALE),
                                              wordDictionary.get(Meaning.SIBLING))
                              }
                      }, usedWords, skips, v);
              break;
            case MAN:
            case WOMAN:
              generateNonCoreFor(new Meaning[] {
                              Meaning.MAN, Meaning.WOMAN
                      }, 0.5, new double[] { 0.5, 1.0 }, // 0.5
                      new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.PERSON),
                                              wordDictionary.get(Meaning.MALE)),
                                      Word.compound(wordDictionary.get(Meaning.PERSON),
                                              wordDictionary.get(Meaning.FEMALE))
                              },
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.MALE),
                                              wordDictionary.get(Meaning.PERSON)),
                                      Word.compound(wordDictionary.get(Meaning.FEMALE),
                                              wordDictionary.get(Meaning.PERSON))
                              }
                      }, usedWords, skips, v);
              break;
            case BOYFRIEND:
            case GIRLFRIEND:
              // PREREQUISITES: BOY GIRL MAN WOMAN
              generateNonCoreFor(new Meaning[] {
                              Meaning.BOYFRIEND, Meaning.GIRLFRIEND
                      }, 0.7, new double[] { 0.5, 0.7, 1.0 }, // 0.7
                      new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.MALE),
                                              wordDictionary.get(Meaning.LOVER)),
                                      Word.compound(wordDictionary.get(Meaning.FEMALE),
                                              wordDictionary.get(Meaning.LOVER))
                              },
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.BOY),
                                              wordDictionary.get(Meaning.FRIEND)),
                                      Word.compound(wordDictionary.get(Meaning.GIRL),
                                              wordDictionary.get(Meaning.FRIEND))
                              },
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.MAN),
                                              wordDictionary.get(Meaning.LOVER)),
                                      Word.compound(wordDictionary.get(Meaning.WOMAN),
                                              wordDictionary.get(Meaning.LOVER))
                              }
                      }, usedWords, skips, v);
              break;
            case DISTANT:
              generateNonCoreFor(new Meaning[] { Meaning.DISTANT }, 0.6,
                      new double[] { 1.0 }, new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.OPPOSITE),
                                              wordDictionary.get(Meaning.PROXIMAL))
                              }
                      }, usedWords, skips, v);
              break;
            case ENEMY:
              generateNonCoreFor(new Meaning[] { Meaning.ENEMY }, 0.4,
                      new double[] { 1.0 }, new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.OPPOSITE),
                                              wordDictionary.get(Meaning.FRIEND))
                              }
                      }, usedWords, skips, v);
              break;
            case THIS_LANGUAGE:
              generateNonCoreFor(new Meaning[] { Meaning.THIS_LANGUAGE }, 1.0,
                      new double[] { 0.5, 1.0 }, new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.THESE_PEOPLE),
                                              wordDictionary.get(Meaning.LANGUAGE))
                              },
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.LANGUAGE),
                                              wordDictionary.get(Meaning.THESE_PEOPLE))
                              }
                      }, usedWords, skips, v);
              break;
            case CAPITAL:
              generateNonCoreFor(new Meaning[] { Meaning.CAPITAL }, 0.6,
                      new double[] { 0.4, 1.0 }, new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.GREAT_COMP),
                                              wordDictionary.get(Meaning.CITY))
                              },
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.MAIN),
                                              wordDictionary.get(Meaning.CITY))
                              }
                      }, usedWords, skips, v);
              break;
            case THIS_STATE:
              generateNonCoreFor(new Meaning[] { Meaning.THIS_STATE }, 1.0,
                      new double[] { 0.5, 1.0 }, new Word[][] {
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.THESE_PEOPLE),
                                              wordDictionary.get(Meaning.STATE))
                              },
                              new Word[] {
                                      Word.compound(wordDictionary.get(Meaning.STATE),
                                              wordDictionary.get(Meaning.THESE_PEOPLE))
                              }
                      }, usedWords, skips, v);
              break;
          }
        }
      }
    }
  }

  private void generateNonCoreFor(Meaning[] meanings, double derivedProb,
                                  double[] probs, Word[][] options,
                                  Set<Word> usedWords, Set<Meaning> skips,
                                  Phonology v) {
    if (Math.random() < derivedProb) {
      Word[] words = new Word[meanings.length];

      double prob = Math.random();

      for (int i = 0; i < probs.length; i++) {
        if (prob < probs[i]) {
          System.arraycopy(options[i], 0, words, 0, words.length);
          break;
        }
      }

      usedWords.addAll(Arrays.asList(words));

      for (int i = 0; i < words.length; i++) {
        wordDictionary.put(meanings[i], words[i]);
        semanticDictionary.put(words[i], meanings[i]);
      }
    } else {
      generateOriginalsFor(meanings, usedWords, v);
    }

    skips.addAll(Arrays.asList(meanings));
  }

  private void generateOriginalsFor(Meaning[] meanings, Set<Word> usedWords,
                                 Phonology v) {
    for (Meaning meaning : meanings) {
      Word candidate = null;
      boolean violates = true;

      while (violates) {
        candidate = Word.generateRandomWord(1, 4, v);
        violates = usedWords.contains(candidate);
      }

      usedWords.add(candidate);
      wordDictionary.put(meaning, candidate);
      semanticDictionary.put(candidate, meaning);
    }
  }
}
