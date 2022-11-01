package uk.ac.aber.dcs.cs31620.vocabulearn;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable {
	//Instange variables
	private int ID;
	private String word;
	private String optionA;
	private String optionB;
	private int answer;
	private int languageID;

	public Question() {
	}

	public Question(String word, String optionA, String optionB, int answer, int languageID) {

		this.word = word;
		this.optionA = optionA;
		this.optionB = optionB;
		this.answer = answer;
		this.languageID = languageID;
	}

	protected Question(Parcel in) {
		ID = in.readInt();
		word = in.readString();
		optionA = in.readString();
		optionB = in.readString();
		answer = in.readInt();
		languageID = in.readInt();
	}

	public static final Creator<Question> CREATOR = new Creator<Question>() {
		@Override
		public Question createFromParcel(Parcel in) {
			return new Question(in);
		}

		@Override
		public Question[] newArray(int size) {
			return new Question[size];
		}
	};
// Getters and setters

	public String getWord() {
		return word;
	}
	public String getOptionA() {
		return optionA;
	}
	public String getOptionB() {
		return optionB;
	}
	public int getAnswer() {
		return answer;
	}
	public void setId(int id)
	{
		ID=id;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}
	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}
	public void setAnswer(int answer) {
		this.answer = answer;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(ID);
		dest.writeString(word);
		dest.writeString(optionA);
		dest.writeString(optionB);
		dest.writeInt(answer);
		dest.writeInt(languageID);
	}

	public int getLanguageID() {
		return languageID;
	}

	public void setLanguageID(int languageID) {
		this.languageID = languageID;
	}
}
