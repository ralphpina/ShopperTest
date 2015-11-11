package net.ralphpina.shoppertest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import net.ralphpina.shoppertest.api.Client;
import net.ralphpina.shoppertest.db.DbHelper;
import net.ralphpina.shoppertest.events.EventBusMethod;
import net.ralphpina.shoppertest.events.ForwardEvent;
import net.ralphpina.shoppertest.events.SelectionEvent;
import net.ralphpina.shoppertest.timer.TimerUtils;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class MainActivity extends AppCompatActivity {

    private final static int TIMER_UPDATE_RATE_MS = 1000;
    private final static int TEST_TIME_MS         = 60 * 2 * 1000;

    @Bind(R.id.toolbar)
    public Toolbar                mToolbar;
    @Bind(R.id.container)
    public ScrollControlViewPager mViewPager;
    @Bind(R.id.fab)
    public FloatingActionButton   mFab;
    @Bind(R.id.timer)
    public TextView               mTimer;
    @Bind(R.id.question_count)
    public TextView               mQuestionCount;

    private ScheduledExecutorService mScheduledExecutorService;
    private Future<?>                mFutureSampler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mScheduledExecutorService = Executors.newScheduledThreadPool(1);

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(sectionsPagerAdapter);
        mViewPager.setPagingEnabled(false);
        mViewPager.addOnPageChangeListener(new OnPageChange());
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault()
                .register(this);
        updateState();
    }

    private void updateState() {
        final long currentTime = Calendar.getInstance(Locale.getDefault())
                                         .getTimeInMillis();
        final long startedTime = DbHelper.get()
                                         .getTimerStart();
        final long elapsedTimeMillis = currentTime - startedTime;
        if (elapsedTimeMillis < (TEST_TIME_MS)) {
            // this thing is going!
            mFutureSampler = startTimer(elapsedTimeMillis);
            mFab.setImageResource(R.mipmap.ic_check_white_36dp);
            mViewPager.setCurrentItem(DbHelper.get()
                                              .getLastQuestion() + 1); // +1 accounts for start fragment
        } else {
            DbHelper.get()
                    .clearData();
            cancelTimer();
            navigateToBeginning();
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault()
                .unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mScheduledExecutorService.shutdown();
        super.onDestroy();
    }

    // ===== VIEW PAGER ============================================================================

    public class OnPageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position,
                                   float positionOffset,
                                   int positionOffsetPixels) {
            // noop
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) { // first page
                mQuestionCount.setVisibility(GONE);
            } else if (position == 1) { // first question
                setQuestionCount(position);
                Client.get()
                      .clearAnswerCache();
            } else if (position == getPagesCount() - 1) { // last item
                mQuestionCount.setVisibility(View.GONE);
                DbHelper.get()
                        .clearData();
                cancelTimer();
            } else { // another question
                setQuestionCount(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // noop
        }

        private void setQuestionCount(int position) {
            mQuestionCount.setVisibility(VISIBLE);
            mQuestionCount.setText(QuizApplication.get()
                                                  .getString(R.string.question_count,
                                                             position,
                                                             Client.get()
                                                                   .getQuestions()
                                                                   .size()));
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            // load questions initially
            Client.get()
                  .getQuestions();
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new BeginFragment();
            } else if (position == getPagesCount() - 1) {
                return new EndFragment();
            } else {
                return QuestionFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            return getPagesCount();
        }
    }

    private boolean isBeforeTest() {
        return mViewPager.getCurrentItem() == 0;
    }

    private boolean isAfterTest() {
        return mViewPager.getCurrentItem() == getPagesCount() - 1;
    }

    private int getPagesCount() {
        return Client.get()
                     .getQuestions()
                     .size() + 2; // first and last
    }

    // ===== FAB ===================================================================================

    @OnClick(R.id.fab)
    public void onClickFab() {
        if (isBeforeTest()) {
            mFutureSampler = startTimer(0);
            mFab.setImageResource(R.mipmap.ic_check_white_36dp);
            navigateOneForward();
        } else if (isAfterTest()) {
            navigateToBeginning();
        } else {
            EventBus.getDefault()
                    .post(new SelectionEvent(mViewPager.getCurrentItem())); // first question is at 2nd page
        }
    }

    // ===== TIMER =================================================================================

    private Future<?> startTimer(long elapsedTimeMS) {
        DbHelper.get()
                .setTimerStart(Calendar.getInstance(Locale.getDefault())
                                       .getTimeInMillis());
        return mScheduledExecutorService.scheduleAtFixedRate(new SamplingRunner(elapsedTimeMS),
                                                             0,
                                                             TIMER_UPDATE_RATE_MS,
                                                             MILLISECONDS);
    }

    private void cancelTimer() {
        if (mFutureSampler != null) {
            mFutureSampler.cancel(true);
        }
        mTimer.setText(TimerUtils.getHumanReadableTimeElapsed(0));
    }

    private class SamplingRunner implements Runnable {

        private long millisecondsElapsed;

        public SamplingRunner(long millisecondsElapsed) {
            this.millisecondsElapsed = millisecondsElapsed;
        }

        @Override
        public void run() {
            millisecondsElapsed += (float) TIMER_UPDATE_RATE_MS;

            if (millisecondsElapsed >= TEST_TIME_MS) {
                // test is done
                if (mFutureSampler != null) {
                    mFutureSampler.cancel(true);
                }
                millisecondsElapsed = 0;
            }
            updateTimer();
        }

        private void updateTimer() {
            mTimer.post(new Runnable() {
                @Override
                public void run() {
                    mTimer.setText(TimerUtils.getHumanReadableTimeElapsed(
                            millisecondsElapsed / 1000));
                    if (millisecondsElapsed == 0) {
                        DbHelper.get()
                                .clearData();
                        navigateToBeginning();
                        showDialog();
                    }
                }
            });
        }

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.time_expired)
               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                   }
               });
        builder.create()
               .show();
    }

    // ===== EVENTS ================================================================================

    @EventBusMethod
    public void onEventMainThread(ForwardEvent event) {
        navigateOneForward();
    }

    private void navigateOneForward() {
        DbHelper.get()
                .setLastQuestion(mViewPager.getCurrentItem());
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    public void navigateToBeginning() {
        mFab.setImageResource(R.mipmap.ic_arrow_forward_white_36dp);
        mViewPager.setCurrentItem(0);
    }
}
