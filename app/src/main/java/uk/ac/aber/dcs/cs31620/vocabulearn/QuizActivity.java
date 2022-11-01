package uk.ac.aber.dcs.cs31620.vocabulearn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.res.ColorStateList;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import uk.ac.aber.dcs.cs31620.vocabulearn.data.QuizDatabaseHelper;



public class QuizActivity extends AppCompatActivity {
	//Instance variables
	private ArrayList<Question> wordsList;
	int points =0;
	TextView textWordView;
	RadioButton radioButtonA, radioButtonB;
	RadioGroup radioGroup1;
	Button nextButton;
	private ColorStateList textColorDefaultRadioButton;
	private int wordCounter;
	private int wordCountTotal;
	private Question currentWords;
	private TextView textViewPoints;
	private TextView textViewWordCount;
	private TextView textViewCountdown;
	public static final String POINTS_PLUS = "pointsPlus";
	private boolean answered;
	private long backPressedTime;
	private static final long COUNTDOWN_IN_MILLIS = 30000;
	private ColorStateList textColorDefaultCountDown;
	private CountDownTimer countDownTimer;
	private long timeLeftInMilis;
	private static final String POINTS_KEY = "pointsKey";
	private static final String WORD_COUNT_KEY = "wordCountKey";
	private static final String MILLIS_LEFT_KEY = "millisLeftKey";
	private static final String ANSWERED_KEY = "answeredKey";
	private static final String WORDS_LIST_KEY = "wordListKey";

	/**
	 * Initialises all the variables from the xml file.
	 * @param savedInstanceState saves the instance state so that when the screen is rotated the state of the counter and other widgets dont change
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		//code is executed if no saved instance state. if quiz is left it will also be null, only not null if we have state to restart

		textWordView = findViewById(R.id.text_view_word);
		textViewPoints = findViewById(R.id.text_view_points);
		textViewWordCount = findViewById(R.id.text_view_question_count);
		textViewCountdown = findViewById(R.id.text_view_countdown);

		TextView textViewLanguage = findViewById(R.id.text_view_language);

		radioGroup1 = findViewById(R.id.radio_group);
		radioButtonA= findViewById(R.id.radio0);
		radioButtonB =findViewById(R.id.radio1);
		nextButton = findViewById(R.id.button_confirm_next);

		textColorDefaultRadioButton = radioButtonA.getTextColors();
		textColorDefaultCountDown = textViewCountdown.getTextColors();

		Intent intent = getIntent();
		int languageID = intent.getIntExtra(MainScreenActivity.LANGUAGE_ID, 0);
		String languageName = intent.getStringExtra(MainScreenActivity.LANGUAGE_NAME);
		textViewLanguage.setText("Language: " +languageName);

		if(savedInstanceState ==null) {
			QuizDatabaseHelper databaseHelper = QuizDatabaseHelper.getInstance(this);
			wordsList = databaseHelper.getQuestions(languageID);
			wordsList = databaseHelper.getAllQuestions();
			wordCountTotal = wordsList.size();
			Collections.shuffle(wordsList);
			setQuestionNoView();
		} else {
			wordsList = savedInstanceState.getParcelableArrayList(WORDS_LIST_KEY);
			wordCountTotal = wordsList.size();
			wordCounter = savedInstanceState.getInt(WORD_COUNT_KEY);
			currentWords = wordsList.get(wordCounter - 1);
			points = savedInstanceState.getInt(POINTS_KEY);
			timeLeftInMilis = savedInstanceState.getLong(MILLIS_LEFT_KEY);
			answered = savedInstanceState.getBoolean(ANSWERED_KEY);
			//when word response is not answered resumed where is left off before rotated device
			if (!answered) {
				startCountDown();
			} else {
				updateCountDownText();
				showSolution();
			}

		}
		//button logic
		nextButton.setOnClickListener(new View.OnClickListener() {
			/**
			 * If the button is clicked and no radio button is selected then the program will send a toast bar
			 * message saying that an option must be selected to proceed.
			 * @param v
			 */
			@Override
			public void onClick(View v) {
				if(!answered) {
					if(radioButtonA.isChecked() || radioButtonB.isChecked()) {
						checkAnswer();
					} else {
						Toast.makeText(QuizActivity.this, "Please select an answer!",Toast.LENGTH_SHORT).show();
					}
				} else {
					setQuestionNoView();
				}
			}
		});
	}

	/**
	 *
	 * @param menu object
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_quiz, menu);
		return true;
	}

	/**
	 * If the answer select is true the counter timer is paused  and the points is incremented by 1
	 */
	@SuppressLint("SetTextI18n")
	private void checkAnswer() {
		answered = true;
		countDownTimer.cancel();
		RadioButton radioButtonSelected = findViewById(radioGroup1.getCheckedRadioButtonId());
		int answerNumber = radioGroup1.indexOfChild(radioButtonSelected) + 1;

		if(answerNumber == currentWords.getAnswer()) {
		points++;
		textViewPoints.setText("points: "+ points);
		}
		showSolution();

	}

	/**
	 * This method sets all the radio buttons to default red however if the correct answer is selected
	 * and the answer corresponds to the answer in the question method then it highlights the radio button green
	 */
	@SuppressLint("SetTextI18n")
	private void showSolution() {
		radioButtonA.setTextColor(Color.RED);
		radioButtonB.setTextColor(Color.RED);
		switch(currentWords.getAnswer()) {
			case 1:
				radioButtonA.setTextColor(Color.GREEN);
				textWordView.setText("Answer 1 is correct!");
				break;
			case 2:
				radioButtonB.setTextColor(Color.GREEN);
				textWordView.setText("Answer 2 is correct!");
				break;
		}
		if(wordCounter < wordCountTotal) {
			nextButton.setText("Next");
		} else {
			nextButton.setText("Finish");
		}
	}

	/**
	 * This method creates a countdown timer object where everyone 1000 milliseconds is one second
	 * This de increments the timer by a second every second.
	 */
	private void startCountDown() {
		countDownTimer = new CountDownTimer(timeLeftInMilis, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
			timeLeftInMilis = millisUntilFinished;
			updateCountDownText();
			}

			@Override
			public void onFinish() {
			timeLeftInMilis = 0;
			updateCountDownText();
			//if answer isnt selected and seconds = 0 answer state is saved
			checkAnswer();
			}
		}.start();
	}

	/**
	 * This is where the countdown text is updates and formatted
	 */
	private void updateCountDownText(){
		int minutes = (int)(timeLeftInMilis / 1000) / 60;
		int seconds = (int)(timeLeftInMilis/ 1000) % 60;

		String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
		textViewCountdown.setText(timeFormatted);
		if (timeLeftInMilis <10000) {
			textViewCountdown.setTextColor(Color.RED);
		} else {
			textViewCountdown.setTextColor(textColorDefaultCountDown);
		}
	}

	/**
	 * This method handles the Question 1/5 text on the xml file. where the question is = current question out of max questions which is 5
	 */
	@SuppressLint("SetTextI18n")
	private void setQuestionNoView() {
		radioButtonA.setTextColor(textColorDefaultRadioButton);
		radioButtonB.setTextColor(textColorDefaultRadioButton);
		radioGroup1.clearCheck();

		if (wordCounter < wordCountTotal) {
			currentWords = wordsList.get(wordCounter);

			textWordView.setText(currentWords.getWord());
			radioButtonA.setText(currentWords.getOptionA());
			radioButtonB.setText(currentWords.getOptionB());
			wordCounter++;
			textViewWordCount.setText("Question: " + wordCounter + "/" + wordCountTotal);
			answered = false;
			nextButton.setText("Confirm");

			timeLeftInMilis = COUNTDOWN_IN_MILLIS;
			startCountDown();
		} else {
			quizFinish();
		}
		}


		private void quizFinish() {
		//finish quiz and send result to starting screen activity for highscore
		Intent resultIntent = new Intent();
		resultIntent.putExtra(POINTS_PLUS, points);
		setResult(RESULT_OK, resultIntent);

		finish();

	}
	//if back button is pressed twice within 3 seconds activity is left.
	@Override

	public void onBackPressed() {
		if(backPressedTime + 3000 > System.currentTimeMillis()) {
			finish();
		} else {
			//Toast message which is a fade away android message on the screen
			Toast.makeText(this,"Press back once more to finish", Toast.LENGTH_SHORT).show();
		}

		backPressedTime = System.currentTimeMillis();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		//used to restore questionslist
		super.onSaveInstanceState(outState);
		outState.putInt(POINTS_KEY, points);
		outState.putInt(WORD_COUNT_KEY, wordCounter);
		outState.putLong(MILLIS_LEFT_KEY, timeLeftInMilis);
		outState.putBoolean(ANSWERED_KEY, answered);
		outState.putParcelableArrayList(WORDS_LIST_KEY, wordsList);

	}

	@Override
	protected void onDestroy() {
		//countdown timer canceled if activity is not running
		super.onDestroy();
		if(countDownTimer != null) {
			countDownTimer.cancel();
		}

	}
}
