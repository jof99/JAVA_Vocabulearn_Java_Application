package uk.ac.aber.dcs.cs31620.vocabulearn;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aber.dcs.cs31620.vocabulearn.data.QuizDatabaseHelper;

public class AddQuestionsActivity extends AppCompatActivity {
    TextView allLanguages;
    EditText add_question_word;
    EditText option_1;
    EditText option_2;
    EditText answer;
    EditText language_id;
    List<Language> languageList = new ArrayList<>();
    Button enterButton;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);

        add_question_word = findViewById(R.id.add_question_word);
        option_1 = findViewById(R.id.option_1);
        option_2 = findViewById(R.id.option_2);
        answer = findViewById(R.id.translation_word);
        language_id = findViewById(R.id.language_id);
        enterButton = findViewById(R.id.enter_button);
        allLanguages = findViewById(R.id.all_languages);

        allLanguages.setMovementMethod(new ScrollingMovementMethod());
        languageList = QuizDatabaseHelper.getInstance(this).getAllLanguages();
        allLanguages.setText("Categories where first category is ID 1... \n" + languageList.toString());

        enterButton.setOnClickListener(view -> {


            if(fieldsFilled()){
                Question question = new Question(add_question_word.getText().toString(),option_1.getText().toString(),option_2.getText().toString(),Integer.parseInt(answer.getText().toString()),Integer.parseInt(language_id.getText().toString()));
                addQuestion(question);
            }

        });



    }
    private boolean fieldsFilled(){

        if(language_id.getText().toString().equals("") || answer.getText().toString().equals("") || add_question_word.getText().toString().equals("") || option_1.getText().toString().equals("") || option_2.getText().toString().equals("")){
            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }

    }
    private void addQuestion(Question question) {
        boolean wrongAnswer = false;
        boolean wrongLanguageID = false;

        if (Integer.parseInt(answer.getText().toString())>3 || Integer.parseInt(answer.getText().toString())<1){
            wrongAnswer = true;
        }


        if (Integer.parseInt(language_id.getText().toString())>QuizDatabaseHelper.getInstance(this).getAllLanguages().size() || Integer.parseInt(language_id.getText().toString())==0){
            wrongLanguageID = true;
        }

        if (wrongAnswer) {
            Toast.makeText(this, "Invalid Answer Number", Toast.LENGTH_SHORT).show();
        }else

        if (wrongLanguageID) {
            Toast.makeText(this, "Invalid Category ID", Toast.LENGTH_SHORT).show();
        }


        else {

            Toast.makeText(this, "Question Added", Toast.LENGTH_SHORT).show();
            QuizDatabaseHelper.getInstance(this).addQuestion((question));
            Intent addIntent = new Intent(AddQuestionsActivity.this, AddQuestionsActivity.class);
            finish();
            startActivity(addIntent);


        }
    }

}
