package net.ralphpina.shoppertest.api;

import java.util.List;

public class Question {

    private final String mName;
    private final List<String> mImages;
    private final int mCorrectAnswer;
    private int mAnswerSelected = -1;

    public Question(String name, List<String> images, int correctAnswer) {
        mName = name;
        mImages = images;
        mCorrectAnswer = correctAnswer;
    }

    public String getName() {
        return mName;
    }

    public List<String> getImages() {
        return mImages;
    }

    public int getCorrectAnswer() {
        return mCorrectAnswer;
    }

    public int getAnswerSelected() {
        return mAnswerSelected;
    }

    public void setAnswerSelected(int answerSelected) {
        mAnswerSelected = answerSelected;
    }

    public boolean isAnswerCorrect() {
        return mCorrectAnswer == mAnswerSelected;
    }
}
