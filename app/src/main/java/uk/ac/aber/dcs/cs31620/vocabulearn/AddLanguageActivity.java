package uk.ac.aber.dcs.cs31620.vocabulearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import uk.ac.aber.dcs.cs31620.vocabulearn.data.QuizDatabaseHelper;

public class AddLanguageActivity extends AppCompatActivity {

    Language language;
    Button enterButton;
    String formattedLanguage;
    TextView addLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_language);

        enterButton = findViewById(R.id.enter_button);
        addLanguage = findViewById(R.id.addCategory);

        enterButton.setOnClickListener(view -> {

            if(fieldFilled()) {
                String categoryFirstLetter = addLanguage.getText().toString().trim().toUpperCase().charAt(0)+"";

                String categoryRest = addLanguage.getText().toString().toLowerCase().trim().substring(1,addLanguage.getText().toString().trim().length());
                formattedLanguage = categoryFirstLetter + categoryRest;
                language = new Language(formattedLanguage);

                addLanguage(language);
            }

        });


    }

    private void addLanguage(Language language){
        boolean languageExists = false;
        List<Language> categoryList;
        categoryList = QuizDatabaseHelper.getInstance(this).getAllLanguages();
        for (Language currentCategory: categoryList) {
            if(currentCategory.getName().equals(language.getName())){
                languageExists = true;
            }
        }
        if(languageExists){
            Toast.makeText(this, "Category Exists Already!", Toast.LENGTH_SHORT).show();
        }else {
            QuizDatabaseHelper.getInstance(this).addLanguage((language));
            Toast.makeText(this, "Language Added Successfully", Toast.LENGTH_SHORT).show();
            Intent addIntent = new Intent(AddLanguageActivity.this,AddLanguageActivity.class);
            finish();
            startActivity(addIntent);
        }
    }

    private boolean fieldFilled(){

        if(addLanguage.getText().toString().equals("")){
            Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }

    }

}