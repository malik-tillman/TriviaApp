package com.tillman.malik.triviaworldtour;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Stats extends Fragment {
    View view;

    static TextView playedView, answeredView, correctView, wrongView, topScoreView, pointsView;

    public static int gamesPlayed;
    public static int questionsAnswered;
    public static int correct;
    public static int wrong;
    public static int points;
    public static int topScore;

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_stats, container, false);

        initiate();
        setNextListeners();

        return view;
    }

    public static void bakeGP(){
        gamesPlayed++;
    }

    public static void bakeAnswered(int amount){
        questionsAnswered += amount;
    }

    public static void bakeCorrect(int amount){
        correct += amount;
    }

    public static void bakeWrong(int amount){
        wrong += amount;
    }

    public static void bakePoints(int amount) { points += amount;}

    public static void checkTopScore(int amount) { if(amount>topScore) topScore = amount; }

    private void initiate(){
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        /*Load Stats*/
        gamesPlayed = sharedPreferences.getInt("gamesPlayed", 0);
        questionsAnswered = sharedPreferences.getInt("mQuestionsAnswered", 0);
        correct = sharedPreferences.getInt("correctAnswer", 0);
        wrong = sharedPreferences.getInt("wrongAnswer", 0);
        points = sharedPreferences.getInt("totalPoints", 0);
        topScore = sharedPreferences.getInt("topScore", 0);

        /*Init Views for Stats*/
        playedView = view.findViewById(R.id.games_played_holder);
        answeredView = view.findViewById(R.id.questions_answered_holder);
        correctView = view.findViewById(R.id.correct_answers_holder);
        wrongView = view.findViewById(R.id.wrong_answers_holder);
        pointsView = view.findViewById(R.id.points_holder);
        topScoreView = view.findViewById(R.id.top_score_holder);

        update();
    }

    public static void update(){
        /*Save Stats*/
        editor = sharedPreferences.edit();
        editor.putInt("gamesPlayed", gamesPlayed);
        editor.putInt("mQuestionsAnswered", questionsAnswered);
        editor.putInt("correctAnswer", correct);
        editor.putInt("wrongAnswer", wrong);
        editor.putInt("totalPoints", points);
        editor.putInt("topScore", topScore);
        editor.apply();

        /*Set Stats*/
        playedView.setText(gamesPlayed+"");
        answeredView.setText(questionsAnswered+"");
        correctView.setText(correct+"");
        wrongView.setText(wrong+"");
        pointsView.setText(points+"");
        topScoreView.setText(topScore+"");
    }

    private void setNextListeners(){
        Button gotoSettings = view.findViewById(R.id.gotoSettings);
        Button gotoModes = view.findViewById(R.id.gotoModes);
        Button resetStats = view.findViewById(R.id.reset_stats);

//        gotoSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RelativeLayout settingsContent = getActivity().findViewById(R.id.settings_content),
//                        thisContent = getActivity().findViewById(R.id.stats_content);
//
//                settingsContent.setVisibility(View.VISIBLE);
//                thisContent.setVisibility(View.GONE);
//            }
//        });
//
//        gotoModes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RelativeLayout modesContent = getActivity().findViewById(R.id.modes_content),
//                        thisContent = getActivity().findViewById(R.id.stats_content);
//
//                modesContent.setVisibility(View.VISIBLE);
//                thisContent.setVisibility(View.GONE);
//            }
//        });

        resetStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gamesPlayed = 0;
                questionsAnswered = 0;
                correct = 0;
                wrong = 0;
                topScore = 0;
                points = 0;

                update();
            }
        });
    }

}
