package com.redsquare.citizen.systems.literature;

import com.redsquare.citizen.entity.Person;
import com.redsquare.citizen.systems.language.Word;
import com.redsquare.citizen.systems.language.sentences.Sentence;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.Formatter;

import java.util.List;

public class Book {
  private final Person[] authors;
  private final Word[] title;
  private final LitCategory category;
  private final List<Sentence[][]> chapters; // Sentence[] is a paragraph; (Sentence[])[] is a chapter
  private final int publicationYear;

  public enum LitCategory {
    RELIGIOUS_TEXT, FOLKLORE, HISTORY, PHILOSOPHY
  }

  private Book(final Person[] authors, final Word[] title,
               final LitCategory category, final List<Sentence[][]> chapters,
               final GameDate current) {
    this.authors = authors;
    this.title = title;
    this.category = category;
    this.chapters = chapters;

    this.publicationYear = current.year;
  }

  @Override
  public String toString() {
    StringBuilder titleString = new StringBuilder();
    StringBuilder authorship = new StringBuilder();

    for (Word word : title) {
      titleString.append(Formatter.properNoun(word.toString()));
      titleString.append(" ");
    }

    for (int i = 0; i < authors.length; i++) {
      if (i > 0)
        authorship.append(", ");

      authorship.append(authors[i].toString());
    }

    return titleString.toString() + "by " + authorship.toString() +
            " (" + publicationYear + ")";
  }
}
