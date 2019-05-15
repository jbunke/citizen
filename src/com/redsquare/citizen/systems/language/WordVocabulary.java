package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.GameDebug;

import java.util.*;

public class WordVocabulary {
  private final Map<Meaning, Word> wordDictionary;
  private final Map<Word, Meaning> semanticDictionary;

  private WordVocabulary(PhoneticVocabulary v) {
    wordDictionary = new HashMap<>();
    semanticDictionary = new HashMap<>();

    generateVocabulary(v);

    GameDebug.printMessage("Vocabulary construction complete",
            GameDebug::printDebug);
  }

  static WordVocabulary generate(PhoneticVocabulary v) {
    return new WordVocabulary(v);
  }

  private void generateVocabulary(PhoneticVocabulary v) {
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
                      }, 1.0, new double[] { 0.5, 1.0 }, // 0.5
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
            case MAN:
            case WOMAN:
              generateNonCoreFor(new Meaning[] {
                              Meaning.MAN, Meaning.WOMAN
                      }, 1.0, new double[] { 0.5, 1.0 }, // 0.5
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
                      }, 1.0, new double[] { 0.5, 0.7, 1.0 }, // 0.7
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
          }
        }
      }
    }
  }

  private void generateNonCoreFor(Meaning[] meanings, double originalProb,
                                  double[] probs, Word[][] options,
                                  Set<Word> usedWords, Set<Meaning> skips,
                                  PhoneticVocabulary v) {
    if (Math.random() < originalProb) {
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
                                 PhoneticVocabulary v) {
    for (Meaning meaning : meanings) {
      Word candidate = null;
      boolean violates = true;

      while (violates) {
        candidate = Word.generateRandomWord(2, 5, v);
        violates = usedWords.contains(candidate);
      }

      usedWords.add(candidate);
      wordDictionary.put(meaning, candidate);
      semanticDictionary.put(candidate, meaning);
    }
  }
}