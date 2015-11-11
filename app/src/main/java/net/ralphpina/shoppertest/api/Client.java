package net.ralphpina.shoppertest.api;

import android.support.annotation.Nullable;

import net.ralphpina.shoppertest.QuizApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private List<Question> mQuestions;

    private static Client mInstance;

    private Client() {
    }

    public static Client get() {
        if (mInstance == null) {
            mInstance = new Client();
        }
        return mInstance;
    }

    public List<Question> getQuestions() {
        // rudimentary cache
        if (mQuestions != null) {
            return mQuestions;
        }

        String jsonString = getJsonString();
        if (jsonString == null) {
            return null;
        }

        List<Question> questions = getQuestions(jsonString);
        if (questions == null) {
            return null;
        }

        mQuestions = questions;

        return mQuestions;
    }

    public Question getQuestion(int position) {
        return getQuestions().get(position);
    }

    @Nullable
    private List<Question> getQuestions(String jsonString) {
        List<Question> questions = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(jsonString);
            JSONArray array = response.getJSONArray("quiz_questions");
            JSONObject jsonObject;
            String name;
            List<String> images;
            int answer;
            for (int i = 0; i < array.length(); i++) {
                jsonObject = (JSONObject) array.get(i);
                name = getName(jsonObject);
                images = getImages(jsonObject);
                answer = getAnswer(jsonObject);
                questions.add(new Question(name, images, answer));
            }
        } catch (JSONException e) {
            return null;
        }
        return questions;
    }

    private String getName(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("name");
    }

    private List<String> getImages(JSONObject jsonObject) throws JSONException {
        JSONArray array = jsonObject.getJSONArray("images");
        List<String> images = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            images.add(array.getString(i));
        }
        return images;
    }

    private int getAnswer(JSONObject jsonObject) throws JSONException {
        return jsonObject.getInt("correct_answer");
    }

    @Nullable
    private String getJsonString() {
        StringBuilder buf = new StringBuilder();
        BufferedReader in = null;
        InputStream json = null;
        try {
            json = QuizApplication.get()
                                  .getAssets()
                                  .open("questions.json");
            in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                assert json != null;
                assert in != null;
                json.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buf.toString();
    }

    public void clearAnswerCache() {
        for (Question question : mQuestions) {
            question.setAnswerSelected(-1);
        }
    }

    public int getCorrectAnswers() {
        int correct = 0;
        for (Question question : mQuestions) {
            if (question.isAnswerCorrect()) {
                correct++;
            }
        }
        return correct;
    }
}
