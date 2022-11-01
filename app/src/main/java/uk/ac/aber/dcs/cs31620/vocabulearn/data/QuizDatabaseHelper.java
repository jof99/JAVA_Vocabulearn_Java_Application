package uk.ac.aber.dcs.cs31620.vocabulearn.data;

import static uk.ac.aber.dcs.cs31620.vocabulearn.data.QuizContract.WordsTable.TRANSLATED_WORD;
import static uk.ac.aber.dcs.cs31620.vocabulearn.data.QuizContract.WordsTable.KEY_ID;
import static uk.ac.aber.dcs.cs31620.vocabulearn.data.QuizContract.WordsTable.OPTION_A_KEY;
import static uk.ac.aber.dcs.cs31620.vocabulearn.data.QuizContract.WordsTable.OPTION_B_KEY;
import static uk.ac.aber.dcs.cs31620.vocabulearn.data.QuizContract.WordsTable.WORD;
import static uk.ac.aber.dcs.cs31620.vocabulearn.data.QuizContract.WordsTable.TABLE_NAME;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import uk.ac.aber.dcs.cs31620.vocabulearn.Language;
import uk.ac.aber.dcs.cs31620.vocabulearn.Question;

import java.util.ArrayList;
import java.util.List;

public class QuizDatabaseHelper extends SQLiteOpenHelper {
	// This database version is where if you want to update the tables you would increment version number and call the
	// onUpgrade method.
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "vocabulearn";
	private SQLiteDatabase dbase;
	private static QuizDatabaseHelper instance;

	//Singleton
	//We don't want to create no objects but return the same one.

	/**
	 * This method takes the database name and version
	 * @param context contains all relevant data for the onUpgrade method
	 */
	private QuizDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * accessing quiz db helper from multiple threads where the method is access without created database object
	 * @param context contains all relevant data for program lifecycle.
	 * @return a single instance of the conexct
	 */

	public static synchronized  QuizDatabaseHelper getInstance(Context context) {
		if(instance == null) {
			//used through application lifecycle and not one single activity
			instance = new QuizDatabaseHelper(context.getApplicationContext());
		}
		return instance;
	}


	/**
	 * This method creates both of the relevant tables in an SQLite database which is the languages table
	 * for when a user wants to define a language and its translation and the table that stores the languages and tables
	 * linked through a foreign key.
	 * @param db database instance
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		dbase=db;
		final String sqlCreateLanguageTable = "CREATE TABLE " +
				QuizContract.LanguageTable.TABLE_NAME + "( " +
				QuizContract.LanguageTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				QuizContract.LanguageTable.COLUMN_NAME + " TEXT " +
				")";


		String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
				+ QuizContract.WordsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + WORD
				+ " TEXT, " + TRANSLATED_WORD + " TEXT, "+ OPTION_A_KEY +" TEXT, "
				+ OPTION_B_KEY +" TEXT, " + KEY_ID  + " INTEGER, "
				+ "FOREIGN KEY(" + QuizContract.WordsTable.KEY_ID + ") REFERENCES "
				+ QuizContract.LanguageTable.TABLE_NAME + "(" + QuizContract.LanguageTable._ID + ")"
				+ "ON DELETE CASCADE"
				+ ")";

		db.execSQL(sqlCreateLanguageTable);
		db.execSQL(sqlCreateTable);
		populateLanguageTable();
		populateWordsTable();
		//db.close();
	}

	/**
	 * This method will be called when the user wants to add a language category of their own
	 * @param language language object
	 */
	public void addLanguage(Language language) {
		//No recursion as it is not called within the onCreate method.
	dbase = getWritableDatabase();
	insertLanguage(language);
	}

	/**
	 * This method allows the user to add a word/question it's corresponding options, answer and language category id.
	 * @param question question object
	 */
	public void addQuestion(Question question) {
		dbase = getWritableDatabase();
		insertQuestion(question);
	}

	/**
	 * This method inserts the language into the database
	 * @param language language object
	 */
	private void insertLanguage(Language language) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(QuizContract.LanguageTable.COLUMN_NAME, language.getName());
		dbase.insert(QuizContract.LanguageTable.TABLE_NAME,null,contentValues);

	}

	/**
	 * This method pre populates the languages drop down menu with all the available but hard coded languages
	 */
	private void populateLanguageTable() {
		Language c1 = new Language("FRENCH");
		insertLanguage(c1);
	}

	/**
	 * This  method populates the tables with hard coded questions answers and language categories
	 */
	private void populateWordsTable()
	{
		Question q1=new Question("FRENCH, A is Correct!","A", "B ", 1, Language.FRENCH);
		this.insertQuestion(q1);
	}

	/**
	 *  This is the on upgrade method for when you want to upgrade the database tables
	 *  without uninstalling and re installing the app
	 * @param db database instance
	 * @param oldV old version
	 * @param newV new version
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		//delete table queries
		db.execSQL("DROP TABLE IF EXISTS " + QuizContract.LanguageTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// Create tables again
		onCreate(db);
	}
	// Adding new question
	public void insertQuestion(Question question) {
		//SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(WORD, question.getWord());
		contentValues.put(TRANSLATED_WORD, question.getAnswer());
		contentValues.put(OPTION_A_KEY, question.getOptionA());
		contentValues.put(OPTION_B_KEY, question.getOptionB());
		contentValues.put(KEY_ID, question.getLanguageID());
		// Inserting Row
		dbase.insert(TABLE_NAME, null, contentValues);
	}
	//Enables the database foreign key.
	@Override
	public void onConfigure(SQLiteDatabase db) {
		db.setForeignKeyConstraintsEnabled(true);
	}

	/**
	 * The method get the corresponding questions to the base on the language id
	 * @param languageID variable
	 * @return returns a list of questions based on the language ID
	 */
	@SuppressLint("Range")
	public ArrayList<Question> getQuestions(int languageID) {
		ArrayList<Question> questionList = new ArrayList<>();
		dbase = getReadableDatabase();
		String selection = KEY_ID + " = ? ";
		String[] selectionArgs = new String[]{String.valueOf(languageID)};

		Cursor cursor = dbase.query(
				TABLE_NAME,
				null,
				selection,
				selectionArgs,
				null,
				null,
				null
		);

		if (cursor.moveToFirst()) {
			do {
				Question question = new Question();
				question.setId(cursor.getInt(cursor.getColumnIndex(QuizContract.WordsTable._ID)));
				question.setWord(cursor.getString(cursor.getColumnIndex(WORD)));
				question.setOptionA(cursor.getString(cursor.getColumnIndex(OPTION_A_KEY)));
				question.setOptionB(cursor.getString(cursor.getColumnIndex(OPTION_B_KEY)));
				question.setAnswer(cursor.getInt(cursor.getColumnIndex(TRANSLATED_WORD)));
				question.setLanguageID(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
				questionList.add(question);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return questionList;
	}

	/**
	 * Retrieve all the questions
	 * @return a list of questions
	 */
	@SuppressLint("Range")
	public ArrayList<Question> getAllQuestions() {
		ArrayList<Question> questionList = new ArrayList<>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;
		dbase=this.getReadableDatabase();
		Cursor cursor = dbase.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Question question = new Question();
				question.setId(cursor.getInt(cursor.getColumnIndex(QuizContract.WordsTable._ID)));
				question.setWord(cursor.getString(1));
				question.setAnswer(cursor.getInt(2));
				question.setOptionA(cursor.getString(3));
				question.setOptionB(cursor.getString(4));
				question.setLanguageID(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
				questionList.add(question);
			} while (cursor.moveToNext());
		}
		// return quest list
		return questionList;
	}
//read all categories of db and returns them as a list

	/**
	 * Retrieve all the languages for mainly for the dropdown menu
	 * @return a list of languages
	 */
	@SuppressLint("Range")
	public List<Language> getAllLanguages() {
		List<Language> languageList = new ArrayList<>();
		dbase = getReadableDatabase();
		Cursor cursor = dbase.rawQuery("SELECT * FROM " + QuizContract.LanguageTable.TABLE_NAME, null);
		if (cursor.moveToFirst()) {
			do {
				Language language = new Language();
				language.setId(cursor.getInt(cursor.getColumnIndex(QuizContract.LanguageTable._ID)));
				language.setName(cursor.getString(cursor.getColumnIndex(QuizContract.LanguageTable.COLUMN_NAME)));
				languageList.add(language);
			} while(cursor.moveToNext());
		}
		cursor.close();
		return languageList;
	}

}
