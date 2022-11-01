package uk.ac.aber.dcs.cs31620.vocabulearn.data;

import android.provider.BaseColumns;

public class QuizContract {
//This is the words table column names variables
    public static class WordsTable implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String WORD = "word";
        public static final String OPTION_A_KEY = "optionA"; //option a
        public static final String OPTION_B_KEY = "optionB"; //option b
        public static final String TRANSLATED_WORD = "translated_word"; //correct word
        public static final String KEY_ID = "id";
    }
//This is where the language table column name and variables have been defined
    public static class LanguageTable implements BaseColumns {
        public static final String TABLE_NAME = "language";
        public static final String COLUMN_NAME = "name";
    }


}
