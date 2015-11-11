package net.ralphpina.shoppertest;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.ralphpina.shoppertest.api.Client;
import net.ralphpina.shoppertest.events.EventBusMethod;
import net.ralphpina.shoppertest.events.ForwardEvent;
import net.ralphpina.shoppertest.events.QuestionAnsweredEvent;
import net.ralphpina.shoppertest.events.SelectionEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class QuestionFragment extends Fragment {

    private static final String EXTRA_POSITION = "net.ralphpina.shoppertest.QuestionFragment.EXTRA_POSITION";

    @Bind(R.id.root)
    public View mRoot;

    @Bind(R.id.name)
    public TextView  mName;
    @Bind(R.id.image_one)
    public ImageView mImageOne;
    @Bind(R.id.image_two)
    public ImageView mImageTwo;
    @Bind(R.id.image_three)
    public ImageView mImageThree;
    @Bind(R.id.image_four)
    public ImageView mImageFour;

    @Bind(R.id.checkbox1)
    CheckBox mCheckBox1;
    @Bind(R.id.checkbox2)
    CheckBox mCheckBox2;
    @Bind(R.id.checkbox3)
    CheckBox mCheckBox3;
    @Bind(R.id.checkbox4)
    CheckBox mCheckBox4;

    private int mPosition;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(int position) {
        QuestionFragment fragment = new QuestionFragment();
        fragment.setPosition(position);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, root);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(EXTRA_POSITION);
        }

        setUpView();
        setUpCheckBoxListeners();
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_POSITION, mPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault()
                .register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault()
                .unregister(this);
        super.onPause();
    }

    private void setUpView() {
        mName.setText(Client.get()
                            .getQuestion(mPosition - 1)
                            .getName());
        loadImageOne();
        loadImageTwo();
        loadImageThree();
        loadImageFour();
    }

    private void loadImageOne() {
        Picasso.with(QuizApplication.get())
               .load(Client.get()
                           .getQuestion(mPosition - 1)
                           .getImages()
                           .get(0))
               .fit()
               .into(mImageOne);
    }

    private void loadImageTwo() {
        Picasso.with(QuizApplication.get())
               .load(Client.get()
                           .getQuestion(mPosition - 1)
                           .getImages()
                           .get(1))
               .into(mImageTwo);
    }

    private void loadImageThree() {
        Picasso.with(QuizApplication.get())
               .load(Client.get()
                           .getQuestion(mPosition - 1)
                           .getImages()
                           .get(2))
               .into(mImageThree);
    }

    private void loadImageFour() {
        Picasso.with(QuizApplication.get())
               .load(Client.get()
                           .getQuestion(mPosition - 1)
                           .getImages()
                           .get(3))
               .into(mImageFour);
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    // ===== CHECKBOXES ============================================================================

    private void setUpCheckBoxListeners() {
        mCheckBox1.setOnCheckedChangeListener(new CheckBoxListener(0));
        mCheckBox2.setOnCheckedChangeListener(new CheckBoxListener(1));
        mCheckBox3.setOnCheckedChangeListener(new CheckBoxListener(2));
        mCheckBox4.setOnCheckedChangeListener(new CheckBoxListener(3));
    }

    private class CheckBoxListener implements CompoundButton.OnCheckedChangeListener {

        private final int mPosition;

        public CheckBoxListener(int position) {
            mPosition = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                boxChecked(mPosition);
            }
        }
    }

    public void boxChecked(int position) {
        if (position != 0) {
            mCheckBox1.setChecked(false);
        }
        if (position != 1) {
            mCheckBox2.setChecked(false);
        }
        if (position != 2) {
            mCheckBox3.setChecked(false);
        }
        if (position != 3) {
            mCheckBox4.setChecked(false);
        }
    }

    private int getCheckedBox() {
        if (mCheckBox1.isChecked()) {
            return 0;
        }
        if (mCheckBox2.isChecked()) {
            return 1;
        }
        if (mCheckBox3.isChecked()) {
            return 2;
        }
        if (mCheckBox4.isChecked()) {
            return 3;
        }
        return -1;
    }

    @OnClick({R.id.image_one, R.id.choice_container_1})
    public void onClickContainerOne() {
        mCheckBox1.setChecked(true);
    }

    @OnClick({R.id.image_two, R.id.choice_container_2})
    public void onClickContainerTwo() {
        mCheckBox2.setChecked(true);
    }

    @OnClick({R.id.image_three, R.id.choice_container_3})
    public void onClickContainerThree() {
        mCheckBox3.setChecked(true);
    }

    @OnClick({R.id.image_four, R.id.choice_container_4})
    public void onClickContainerFour() {
        mCheckBox4.setChecked(true);
    }

    // ===== EVENTS ================================================================================

    @EventBusMethod
    public void onEventMainThread(SelectionEvent event) {
        if (event.getPosition() == mPosition) {
            if (getCheckedBox() == -1) {
                Snackbar.make(mRoot, R.string.must_select_one, LENGTH_SHORT)
                        .show();
            } else {
                Client.get()
                      .getQuestion(mPosition - 1)
                      .setAnswerSelected(getCheckedBox());
                // clear check
                boxChecked(-1);
                EventBus.getDefault()
                        .post(new QuestionAnsweredEvent());
                EventBus.getDefault()
                        .post(new ForwardEvent());
            }
        }
    }
}
