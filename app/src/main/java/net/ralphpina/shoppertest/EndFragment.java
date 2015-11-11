package net.ralphpina.shoppertest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.ralphpina.shoppertest.api.Client;
import net.ralphpina.shoppertest.events.EventBusMethod;
import net.ralphpina.shoppertest.events.QuestionAnsweredEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class EndFragment extends Fragment {

    @Bind(R.id.quiz_results)
    public TextView mQuizResults;

    public EndFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_end, container, false);
        ButterKnife.bind(this, root);
        setAnswerCount();
        return root;
    }

    private void setAnswerCount() {
        final Client client = Client.get();
        mQuizResults.setText(QuizApplication.get()
                                            .getString(R.string.quiz_result,
                                                       client.getCorrectAnswers(),
                                                       client.getQuestions()
                                                             .size()));
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @EventBusMethod
    public void onEventMainThread(QuestionAnsweredEvent event) {
        setAnswerCount();
    }
}
