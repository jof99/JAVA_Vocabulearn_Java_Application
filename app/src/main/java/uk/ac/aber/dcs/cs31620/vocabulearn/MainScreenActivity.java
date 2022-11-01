package uk.ac.aber.dcs.cs31620.vocabulearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.util.List;
import uk.ac.aber.dcs.cs31620.vocabulearn.data.QuizDatabaseHelper;
public class MainScreenActivity extends AppCompatActivity {
public static final String LANGUAGE_ID = "languageID";
public static final String LANGUAGE_NAME = "languageName";
private Spinner spinnerLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_screen);
        spinnerLanguage = findViewById(R.id.spinner_language);
        loadLanguages();

        Button buttonStart = findViewById(R.id.button_start);
        Button buttonAddLanguages = findViewById(R.id.button_add_languages);
        Button buttonAddQuestions = findViewById(R.id.button_add_questions);

        buttonAddQuestions.setOnClickListener(v -> addQuestions());

        buttonAddLanguages.setOnClickListener(v -> addLanguages());

        buttonStart.setOnClickListener(v -> startGame());
        }
    private void addQuestions() {
        Intent intent = new Intent(MainScreenActivity.this,AddQuestionsActivity.class);
        startActivity(intent);
    }
    private void addLanguages() {
        Intent intent = new Intent(MainScreenActivity.this,AddLanguageActivity.class);
        startActivity(intent);
    }

    /**
     * This method loads all the languages into the spinner.
     */
    private void loadLanguages() {
    QuizDatabaseHelper databaseHelper = QuizDatabaseHelper.getInstance(this);
    List<Language> languages = databaseHelper.getAllLanguages();
    ArrayAdapter<Language> adapterLanguages = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,languages);
    adapterLanguages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerLanguage.setAdapter(adapterLanguages);


}
    private void startGame() {
        Language selectedLanguage = (Language) spinnerLanguage.getSelectedItem();
        String languageName = selectedLanguage.getName();
        int languageID = selectedLanguage.getId();
        //go to the quiz activity with data
        Intent intent = new Intent(MainScreenActivity.this, QuizActivity.class);
        intent.putExtra(LANGUAGE_ID, languageID);
        intent.putExtra(LANGUAGE_NAME, languageName);
        //gets result back from activity passing a result code for activity of result
    }



}