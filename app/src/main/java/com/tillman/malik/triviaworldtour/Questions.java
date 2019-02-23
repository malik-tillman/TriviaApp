package com.tillman.malik.triviaworldtour;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class Questions extends Fragment {
    /*View for our fragment, we must go through this to access fragment xml elements*/
    View rootView;

    /*Views for our answers, buttons, and question*/
    TextView[] answerViews = new TextView[4];
    ImageButton[] buttons = new ImageButton[4];
    TextView question;

    /*Banners for incorrect or correct discourse*/
    ImageView incorrect_banner, correct_banner;

    /*Views for our essential player informers*/
    TextView timer, points, questionLeft, questionCorrect, correctAnswer;

    public static TextView play;

    /*Timer*/
    CountDownTimer countDownTimer;

    /*Timer Set Code Constants*/
    final int START = 1,
              END   = 0;

    /*Animations*/
    Animation fade_animation;

    /*Progress Bar*/
    ProgressBar progressBar;

    /*DataService Gains*/
    ArrayList<String> questionValues;
    ArrayList<Integer> rightAnswerValues, BanswerValues, CanswerValues, DanswerValues;

    /*Game Info*/
    int current_question = 0;
    int mCorrect = 0;
    int mWrong = 0;
    int mLeft = StartScreen.total;
    int mQuestionsAnswered = 0;
    int attempt = 0;
    int game_mode;
    int current_points = 0;
    int timeLeft = 20;
    String correctQuestions = "/" + StartScreen.total + " Correct";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Register Broadcast Manager for Intent Bundle Transaction*/
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                broadcastReceiver, new IntentFilter("intentKey")
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*Inflate the Layout for This Fragment*/
        rootView = inflater.inflate(R.layout.fragment_questions, container, false);
        return rootView;
    }

    /**
     * Initiation of Fragment Elements
     * Here we initiate our fragment elements and set necessary listeners
     */
    public void initiateElements() {
        /*Quesion and Answer Views*/
        question = rootView.findViewById(R.id.question);
        answerViews[0] = rootView.findViewById(R.id.answer1);
        answerViews[1] = rootView.findViewById(R.id.answer2);
        answerViews[2] = rootView.findViewById(R.id.answer3);
        answerViews[3] = rootView.findViewById(R.id.answer4);

        /*Buttons*/
        buttons[0] = rootView.findViewById(R.id.button1);
        buttons[1] = rootView.findViewById(R.id.button2);
        buttons[2] = rootView.findViewById(R.id.button3);
        buttons[3] = rootView.findViewById(R.id.button4);

        /*Banners*/
        incorrect_banner = rootView.findViewById(R.id.incorrect_banner);
        correct_banner = rootView.findViewById(R.id.correct_banner);

        /*Main Player Informers*/
        timer = rootView.findViewById(R.id.timer);
        points = rootView.findViewById(R.id.points);
        questionLeft = rootView.findViewById(R.id.questions_left);
        questionCorrect = rootView.findViewById(R.id.questions_correct);
        correctAnswer = rootView.findViewById(R.id.correctAnswer);

        /*Set Defaults for Player Informers*/
        String sQuestionLeft = mLeft + " Left";
        questionLeft.setText(sQuestionLeft);
        String sQuestionCorrect = mCorrect + correctQuestions;
        questionCorrect.setText(sQuestionCorrect);

        /*This allows the player to back out*/
        play = rootView.findViewById(R.id.play_title);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endSession();
            }
        });

        /*Animations*/
        fade_animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);

        /*Set Typeface*/
        Typeface GROBOLD = Typeface.createFromAsset(getActivity().getAssets(), "fonts/grobold.ttf");
        Typeface JANDA = Typeface.createFromAsset(getActivity().getAssets(), "fonts/janda.ttf");
        timer.setTypeface(GROBOLD);
        question.setTypeface(GROBOLD);
        points.setTypeface(JANDA);
        correctAnswer.setTypeface(GROBOLD);

        /*Progress for Timer*/
        progressBar = rootView.findViewById(R.id.timer_progress);
        progressBar.setMax(20);
        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress());
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * Renderer
     * Here we render our view and generate questions and answers
     */
    private void render(){
        /*Render Question*/
        int correctAnswer = renderQuestion();

        /*Set Button Listeners | Possible State Change on Correct onClick*/
        setButtonListeners(correctAnswer);

        /*Make View Visible if Not Already Visible*/
        RelativeLayout question_view = rootView.findViewById(R.id.question_view);
        if(question_view.getVisibility()==View.GONE) {
            timer.setVisibility(View.VISIBLE);
            points.setVisibility(View.VISIBLE);
            timer.startAnimation(fade_animation);
            points.startAnimation(fade_animation);
            question_view.setVisibility(View.VISIBLE);
        }

        /*Start Timer | Possible State Change onTickFinish (or onFinish)*/
        final String time = "20\"";
        timer.setText(time);
        if(correct_banner.getVisibility()==View.VISIBLE || incorrect_banner.getVisibility()==View.VISIBLE)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    timer(START);
                }
            }, 2000);
        else
            timer(START);

    }

    /**
     * Render Question
     * Here we parse our Data Bundles to extract the question and answer pairs. We will set our
     * UI elements accordingly.
     *
     * @return correctPos: The correct answer's position
     */
    private int renderQuestion(){
        /*Set Question*/
        question.setText(questionValues.get(current_question));

        /*Format Answers Based on :game_mode:*/
        String[] formattedAnswers = new String[4];
        switch (game_mode){
            case StartScreen.WEATHERS:
                formattedAnswers[0] = rightAnswerValues.get(current_question) + "°F";
                formattedAnswers[1] = BanswerValues.get(current_question) + "°F";
                formattedAnswers[2] = CanswerValues.get(current_question) + "°F";
                formattedAnswers[3] = DanswerValues.get(current_question) + "°F";
                break;
            case StartScreen.POPULATIONS:
                formattedAnswers[0] = NumberFormat.getNumberInstance(Locale.US).format(rightAnswerValues.get(current_question)) + " People";
                formattedAnswers[1] = NumberFormat.getNumberInstance(Locale.US).format(BanswerValues.get(current_question)) + " People";
                formattedAnswers[2] = NumberFormat.getNumberInstance(Locale.US).format(CanswerValues.get(current_question)) + " People";
                formattedAnswers[3] = NumberFormat.getNumberInstance(Locale.US).format(DanswerValues.get(current_question)) + " People";
        }

        /*Insert at Random Position*/
        Random random = new Random();
        int correctPos = random.nextInt(4);

        /*Insert Wrong Answer*/
        switch (correctPos){
            case 0: answerViews[correctPos].setText(formattedAnswers[0]);
                answerViews[1].setText(formattedAnswers[1]);
                answerViews[2].setText(formattedAnswers[2]);
                answerViews[3].setText(formattedAnswers[3]);
                break;
            case 1: answerViews[correctPos].setText(formattedAnswers[0]);
                answerViews[0].setText(formattedAnswers[1]);
                answerViews[2].setText(formattedAnswers[2]);
                answerViews[3].setText(formattedAnswers[3]);
                break;
            case 2: answerViews[correctPos].setText(formattedAnswers[0]);
                answerViews[1].setText(formattedAnswers[1]);
                answerViews[0].setText(formattedAnswers[2]);
                answerViews[3].setText(formattedAnswers[3]);
                break;
            case 3: answerViews[correctPos].setText(formattedAnswers[0]);
                answerViews[1].setText(formattedAnswers[1]);
                answerViews[2].setText(formattedAnswers[2]);
                answerViews[0].setText(formattedAnswers[3]);
                break;
        }

        /*Return Correct Answer Position*/
        return correctPos;
    }

    /**
     * Set Timer
     * This method will handle starting and ending the timer. Instead of calling CountDownTimer.cancel(),
     * we call timer(<timer-set-code>)
     *
     * @param timerCode: The code we will use to identify what work needs to be done.
     */
    private void timer(int timerCode){
        switch (timerCode){
            case START:
                /*Check if Timer Exists*/
                if(countDownTimer == null){
                    /*Start New Timer*/
                    /*20,000 Milliseconds = 20 Seconds*/
                    /*1,000 Milliseconds = 1 Second*/
                    countDownTimer = new CountDownTimer(20000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            /*Display Time Left*/
                            String time = String.valueOf((Math.round(millisUntilFinished * 0.001f))) + "\"";
                            timeLeft = Math.round(millisUntilFinished * 0.001f);
                            timer.setText(time);

                            /*Set Progress Bar Based on Build API*/
                            /*:.setProgress: Only Usable with API <=24*/
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                progressBar.setProgress(timeLeft, true);
                        }

                        @Override
                        public void onFinish() {
                            /*Next {incorrect}*/
                            next(0);
                        }
                    }.start();
                }
                else {
                    /*Start Existing Timer*/
                    countDownTimer.start();
                }
                break;
            case END:
                /*Check if Timer Exists Then Cancel it*/
                if(countDownTimer != null) countDownTimer.cancel();

                /*Delete Timer in Instance*/
                countDownTimer = null;
                break;
        }
    }

    /**
     * Set Button Listeners
     * With the use of of looped listener, we set listeners for our image buttons and do necessary
     * process.
     * @param correctAnswer : Location of correct answer
     */
    private void setButtonListeners(final int correctAnswer){
        /*Looped Listener*/
        for(int i=0; i<4; i++){
            /*The Index in Our Button Array*/
            final int index = i;

            /*Set On Click Listeners*/
            buttons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Set Click Image Resource*/
                    buttons[index].setImageResource(R.drawable.quest_clicked);

                    /*Delay Discourse*/
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            /*If Correct Button Clicked
                              Cancel Timer, Set Points, Set Resource, and Go To Next(Correct)*/
                            if(correctAnswer==index){
                                /*Disable Buttons*/
                                for(ImageButton button: buttons) button.setClickable(false);

                                /*Reset Timer*/
                                timer(END);

                                /*Set Button to Correct Status*/
                                buttons[index].setImageResource(R.drawable.quest_correct);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        /*Next Question with Correct Code*/
                                        switch (attempt){
                                            case 0:
                                                /*Reset Attempt Ticker*/
                                                next(1);
                                                break;
                                            case 1:
                                                /*Reset Attempt Ticker*/
                                                attempt=0;
                                                next(2);
                                                break;
                                            case 2:
                                                /*Reset Attempt Ticker*/
                                                attempt=0;
                                                next(3);
                                                break;
                                        }
                                    }
                                }, 500);
                            }
                            /*If Incorrect Button Clicked
                              Add an Attempt and Check For Current Attempt*/
                            else {
                                /*Add Attempt*/
                                attempt++;

                                /*On Third Attempt
                                  Cancel Timer, Set Resource, Next Question Incorrect Code*/
                                if(attempt == 3){
                                    /*Disable Buttons*/
                                    for(ImageButton button: buttons) button.setClickable(false);

                                    /*Reset Attempt*/
                                    attempt=0;

                                    /*Reset Timer*/
                                    timer(END);

                                    /*Set Button to Incorrect Status*/
                                    buttons[index].setImageResource(R.drawable.quest_incorrect);

                                    /*Set Points Incorrect */
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            next(0);
                                        }
                                    }, 300);
                                }
                                /*On Other Attempt
                                  Just Set Incorrect Click Resource*/
                                else {
                                    buttons[index].setImageResource(R.drawable.quest_incorrect);
                                    buttons[index].setClickable(false);
                                }
                            }
                        }
                    }, 100);
                }
            });
        }

        /*Non-Looped Listener*/
        /*For demonstration on reducing recursive boilerplate code*/
        //
//        buttons[0].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buttons[0].setImageResource(R.drawable.quest_clicked);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(correctAnswer==0){
//                            buttons[0].setImageResource(R.drawable.quest_correct);
//                        }
//                        else {
//                            buttons[0].setImageResource(R.drawable.quest_incorrect);
//                        }
//                    }
//                }, 200);
//            }
//        });
//
//        buttons[1].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buttons[1].setImageResource(R.drawable.quest_clicked);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(correctAnswer==1){
//                            buttons[1].setImageResource(R.drawable.quest_correct);
//                        }
//                        else {
//                            buttons[1].setImageResource(R.drawable.quest_incorrect);
//                        }
//                    }
//                }, 200);
//            }
//        });
//
//        buttons[2].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buttons[2].setImageResource(R.drawable.quest_clicked);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(correctAnswer==2){
//                            buttons[2].setImageResource(R.drawable.quest_correct);
//                        }
//                        else {
//                            buttons[2].setImageResource(R.drawable.quest_incorrect);
//                        }
//                    }
//                }, 200);
//            }
//        });
//
//        buttons[3].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buttons[3].setImageResource(R.drawable.quest_clicked);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(correctAnswer==3){
//                            buttons[3].setImageResource(R.drawable.quest_correct);
//                        }
//                        else {
//                            buttons[3].setImageResource(R.drawable.quest_incorrect);
//                        }
//                    }
//                }, 200);
//            }
//        });
    }

    /**
     * Next
     * Here we handle all processes to go to the next question.
     * @param correctness: Integer representing correct status of answer.
     *                     0 : Wrong answer
     *                     1 : Great Correct | Top Possible Score is 500
     *                     2 : Good  Correct | Top Possible Score is 300
     *                     3 : OK    Correct | Top Possible Score is 100
     */
    private void next(int correctness){
        /*Increment Current Question*/
        current_question++;
        mQuestionsAnswered++;

        /*Set Points*/
        setPoints(correctness);

        /*Check if There Are More Questions*/
        /*If Not, End the Session*/
        if(current_question>=StartScreen.total)
            endSession();

        /*If Not Continue as Planned*/
        else{
            /*Flash Appropriate Banner*/
            if(correctness>0)
                flashBanner(1);
            else
                flashBanner(0);

            /*Reset Buttons' Image Resources*/
            for(ImageButton button : buttons)
                button.setImageResource(R.drawable.quest_not);

            /*Render Next Question*/
            render();
        }
    }

    /**
     * Set Points
     * Here we'll calculate the points based on the Correctness Code
     * @param pointCode: Correctness Code
     */
    private void setPoints(int pointCode){
        /*Message Codes*/
        final int NOPOINTS = 0;
        final int GREAT = 1;
        final int GOOD = 2;
        final int OK = 3;

        /*If Less Than 5 Seconds Left Set Code to Third*/
        if(timeLeft<5&&pointCode!=0)
            pointCode = OK;

        /*Switch Case Handles Points Calculation*/
        Random randomPointAddition = new Random(25);
        switch (pointCode){
            case NOPOINTS:
                /*Set Points Text*/
                String no_points = "No Points Earned";
                points.setText(no_points);
                points.startAnimation(fade_animation);
                break;
            case GREAT:
                /*Set Points Text*/
                String congrats = "GREAT WORK!!";
                points.setText(congrats);
                points.startAnimation(fade_animation);

                /*Calculate Points*/
                int mPoints = (timeLeft * 25) + randomPointAddition.nextInt(25);
                current_points += mPoints;
                break;
            case GOOD:
                /*Set Points Text*/
                String congrats1 = "Good Job!";
                points.setText(congrats1);
                points.startAnimation(fade_animation);

                /*Calculate Points*/
                mPoints = (timeLeft * 25) - 125 + randomPointAddition.nextInt(25);
                current_points += mPoints;
                break;
            case OK:
                /*Set Points Text*/
                String congrats2 = "That Was Close";
                points.setText(congrats2);
                points.startAnimation(fade_animation);

                /*Calculate Points*/
                mPoints = (timeLeft * 25) - 225 + randomPointAddition.nextInt(25);
                current_points += mPoints;
                break;
        }

        /*Set Correct*/
        if(pointCode!=NOPOINTS){
            /*Set Questions Correct*/
            mCorrect++;
            String mCorrectFormatted = mCorrect + correctQuestions;
            questionCorrect.setText(mCorrectFormatted);
        }

        /*Set Left*/
        mLeft--;
        String mLeftFormatted = mLeft +  " Left";
        questionLeft.setText(mLeftFormatted);

        /*Update Points*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String points_value = "Points: " + current_points;
                points.setText(points_value);
                points.startAnimation(fade_animation);
            }
        }, 500);
    }

    /**
     * End Session
     * Reset all values and close question view
     */
    public void endSession(){
        /*Save Stats*/
        Stats.bakeGP();
        Stats.bakeAnswered(mQuestionsAnswered);
        Stats.bakeCorrect(mCorrect);
        Stats.bakeWrong(mWrong);
        Stats.bakePoints(current_points);
        Stats.checkTopScore(current_points);
        Stats.update();


        /*Reset Logic Values*/
        current_question = 0;
        mCorrect = 0;
        mWrong = 0;
        mLeft = StartScreen.total;
        attempt = 0;
        current_points = 0;
        timeLeft = 20;
        timer(END);

        /*Set UI Defaults*/
        progressBar.setProgress(100);
        for(ImageButton imageButton : buttons)
            imageButton.setImageResource(R.drawable.quest_not);
        points.setText("Points: 0");

        /*Set View Visibilities*/
        timer.setVisibility(View.GONE);
        points.setVisibility(View.GONE);
        RelativeLayout questionView = rootView.findViewById(R.id.question_view);
        questionView.setVisibility(View.GONE);

        /*Set Panel State to Down*/
        SlidingUpPanelLayout slidingUpPanelLayout = getActivity().findViewById(R.id.sliding_up_panel);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        slidingUpPanelLayout.setTouchEnabled(true);
    }

    /**
     * Removes most UI elements and displays according banner.
     * After 5000 milliseconds (5 seconds), banner will be removed and replaced
     * by UI elements.
     *
     * @param correctness : [right|wrong] answer banner to be displayed
     */
    public void flashBanner(int correctness){
        /*Flash Wrong Answer Banner*/
        String cAnswer;
        switch (correctness) {
            case 0: /*Incorrect State*/
                mWrong++;
                /*Show Banner*/
                incorrect_banner.setVisibility(View.VISIBLE);
                incorrect_banner.startAnimation(fade_animation);

                /*Display Correct Answer*/
                cAnswer = "Correct Answer: " + rightAnswerValues.get(current_question-1);
                correctAnswer.setText(cAnswer);
                correctAnswer.setVisibility(View.VISIBLE);
                correctAnswer.startAnimation(fade_animation);

                /*Disable Views*/
                for(TextView answerView : answerViews) answerView.setVisibility(View.GONE);
                for(ImageButton button : buttons) button.setVisibility(View.GONE);
                question.setVisibility(View.GONE);

                /*Discard Banner*/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*Discard of Banner and Correct Answer*/
                        incorrect_banner.setVisibility(View.GONE);
                        correctAnswer.setVisibility(View.GONE);

                        /*Relive Views*/
                        for(TextView answerView : answerViews) answerView.setVisibility(View.VISIBLE);
                        for(ImageButton button : buttons) button.setVisibility(View.VISIBLE);
                        question.setVisibility(View.VISIBLE);
                    }
                }, 2000);
                break;

            case 1:/*Correct State*/
                /*Show Banner*/
                correct_banner.setVisibility(View.VISIBLE);
                correct_banner.startAnimation(fade_animation);

                /*Display Correct Answer*/
                cAnswer = "Correct Answer " + rightAnswerValues.get(current_question-1);;
                correctAnswer.setText(cAnswer);
                correctAnswer.setVisibility(View.VISIBLE);
                correctAnswer.startAnimation(fade_animation);

                /*Disable Views*/
                for(TextView answerView : answerViews) answerView.setVisibility(View.GONE);
                for(ImageButton button : buttons) button.setVisibility(View.GONE);
                question.setVisibility(View.GONE);

                /*Discard Banner*/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*Discard of Banner*/
                        correct_banner.setVisibility(View.GONE);
                        correctAnswer.setVisibility(View.GONE);

                        /*Relive Views*/
                        for(TextView answerView : answerViews) answerView.setVisibility(View.VISIBLE);
                        for(ImageButton button : buttons) button.setVisibility(View.VISIBLE);
                        question.setVisibility(View.VISIBLE);
                    }
                }, 2000);
                break;

            default:
                throw new NullPointerException("Wrong Banner");
        }
    }

    /**
     * Broadcast Receiver for Intent Data Service
     * Here we handle the initial communication with this fragment. Once the Data Service broadcasts
     * it's data, we will retrieve the Bundles and store the data to be used in our fragment.
     *
     * We expect to retrieve 5 bundles:
     * questionValues  - All the questions
     * right_answer    - All the right answers
     * wrong_answer{n} - Wrong answer container
     * game_mode       - Game mode we are expecting
     *
     * We then initiate our fragments elements and render the display
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*Get Bundled data from service*/
            Bundle bundle = intent.getBundleExtra("bundle");

            /*Store Bundled data*/
            questionValues = bundle.getStringArrayList("questionValues");
            rightAnswerValues = bundle.getIntegerArrayList("right_answer");
            BanswerValues = bundle.getIntegerArrayList("wrong_answer1");
            CanswerValues = bundle.getIntegerArrayList("wrong_answer2");
            DanswerValues = bundle.getIntegerArrayList("wrong_answer3");
            game_mode = bundle.getInt("game_mode");

            /*Render Display*/
            initiateElements();
            render();
        }
    };
}
