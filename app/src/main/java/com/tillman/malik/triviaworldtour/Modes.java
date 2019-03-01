package com.tillman.malik.triviaworldtour;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import com.bumptech.glide.Glide;

@SuppressLint("ClickableViewAccessibility")
public class Modes extends Fragment {
    /*Our Fragment View*/
    private View rootView;

    /*Gif View for Our Animated Views*/
    private ImageView gifView;

    /*Mode List Position*/
    private int modePosition = 0;
    Boolean goingLeft = false;

    /*Container for Our Navigators*/
    private RelativeLayout leftNavigations, rightNavigations;

    /*Device Width*/
    private int dWidth;

    /*Text Containers*/
    private TextView title, summary, header, modesTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_modes, container, false);
        initiateRootView();

        return rootView;
    }

    private void initiateRootView() {
        /*Get Screen Width*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity()
                .getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        dWidth = displayMetrics.widthPixels;

        /*Set Navigators Animation*/
        setNavigatorTouchAnimation();

        /*Set Texts*/
        title = rootView.findViewById(R.id.title);
        header = rootView.findViewById(R.id.header);
        modesTitle = rootView.findViewById(R.id.modes_title);
        summary = rootView.findViewById(R.id.mode_summary);

        title.setTypeface(StartScreen.getFont(getContext(), "grobold"));
        header.setTypeface(StartScreen.getFont(getContext(), "janda"));
        modesTitle.setTypeface(StartScreen.getFont(getContext(), "janda"));
        summary.setTypeface(StartScreen.getFont(getContext(), "janda"));

        /*Set Gifs*/
        handleGifTouch();

        if (dWidth >= 1440)
            Glide.with(getActivity())
                    .load(getResources().getIdentifier("weathers_1000", "drawable", getActivity().getPackageName()))
                    .into(gifView);

        else if (dWidth > 720)
            Glide.with(getActivity())
                    .load(getResources().getIdentifier("weathers_500", "drawable", getActivity().getPackageName()))
                    .into(gifView);
        else {
            Glide.with(getActivity())
                    .load(getResources().getIdentifier("weathers_250", "drawable", getActivity().getPackageName()))
                    .into(gifView);

            /*Set Text Sizes*/
            summary.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    (summary.getTextSize()*.4f)
            );
        }
    }

    /**
     * Set Navigator Touch Listeners
     * Here we set up the onTouchListeners for our Gif Navigators
     */
    private void setNavigatorTouchAnimation() {
        leftNavigations = rootView.findViewById(R.id.left_navigation_container);
        rightNavigations = rootView.findViewById(R.id.right_navigation_container);

        /*Place Scale Animators*/
        leftNavigations.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleXBy(-.2f).setDuration(200).start();
                    v.animate().scaleYBy(-.2f).setDuration(200).start();

                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(1000).start();
                    v.animate().scaleY(1f).setDuration(1000).start();

                    return cycleGif(getContext(), getActivity(),true);
                }

                return false;
            }
        });

        rightNavigations.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*User Action*/
                int action = event.getAction();

                /*If Down, We Will Just Scale the Button Down*/
                if (action == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleXBy(-.2f).setDuration(400).start();
                    v.animate().scaleYBy(-.2f).setDuration(400).start();

                    return true;
                }
                /*When They Let Go, We Will Scale Back Up and Cycle Gifs*/
                else if (action == MotionEvent.ACTION_UP) {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(1000).start();
                    v.animate().scaleY(1f).setDuration(1000).start();

                    return cycleGif(getContext(), getActivity(), false);
                }

                return false;
            }
        });

    }

    private void handleGifTouch() {
        gifView = rootView.findViewById(R.id.modesGif);

        gifView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*User Action*/
                int action = event.getAction();

                /*If Down, We Will Just Scale the Button Down*/
                if (action == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleXBy(-.2f).setDuration(400).start();
                    v.animate().scaleYBy(-.2f).setDuration(400).start();

                    return true;
                }
                /*When They Let Go, We Will Scale Back Up and Cycle Gifs*/
                else if (action == MotionEvent.ACTION_UP) {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(1000).start();
                    v.animate().scaleY(1f).setDuration(1000).start();

                    if(goingLeft)
                        cycleGif(getContext(), getActivity(), true);
                    else
                        cycleGif(getContext(), getActivity(), false);
                    return false;
                }

                return false;
            }
        });
    }

    /**
     * Cycle Gif
     * Based on nav clicked, this method will cycle through our gifs. We also set our mode based
     * on the mode focused.
     *
     * @param leftClicked : Lets us know what navigator was clicked
     * @return an arbitrary true
     */
    private boolean cycleGif(Context context, Activity activity, boolean leftClicked) {
        /*This Defines How Far You Can Go in the List*/
        int maxListAmount = 2;

        /*We Check for What Nav was Clicked*/
        if(leftClicked){
            /*Move Back in List*/
            modePosition--;

            /*Assure Position is Never Less Than 0*/
            if(modePosition<0) modePosition=0;
            else /*Determine Position and Render Gif*/
                switch (modePosition) {
                    case 0: /*Weather <- Population*/
                        renderModeView(context, activity, StartScreen.WEATHERS, StartScreen.POPULATIONS);
                        break;
                    case 1: /*Population <- Impending*/
                        renderModeView(context, activity, StartScreen.POPULATIONS, StartScreen.IMPENDING);
                        break;
            }
        }
        else {
            /*Move Forward in List*/
            modePosition++;

            /*Assure Position is Never More Than Our Amount Of Items*/
            if (modePosition > maxListAmount) modePosition = maxListAmount;
            else/*Determine Position and Render Gif*/
                switch (modePosition) {
                    case 1: /*Population <- Weather*/
                        renderModeView(context, activity, StartScreen.POPULATIONS, StartScreen.WEATHERS);
                        break;
                    case 2: /*Impending <- Population*/
                        renderModeView(context, activity, StartScreen.IMPENDING, StartScreen.POPULATIONS);
                        break;
                }
        }

        return false;
    }

    /**
     * Render the Mode View -
     *      Here we set the mode that will be played, set the specific texts for each mode, set UI
     *      color themes, and start the gif view.
     *
     * @param context:
     * @param activity:
     * @param currentMode: Mode we will be playing
     */
    private void renderModeView(Context context, Activity activity, int currentMode, int previousMode){
        /*Set List Direction*/
        if(currentMode==StartScreen.WEATHERS) goingLeft = false;
        if(currentMode==StartScreen.IMPENDING) goingLeft = true;

        /*Android Doesn't Like it When we Put Raw Strings in setText()*/
        String newHeader, newModeTitle, newSummary, mode;
        String[][] modeSpecificTexts = getSpecificTexts(activity);

        /*Set Texts for View*/
        newHeader = modeSpecificTexts[currentMode][0];
        newModeTitle = modeSpecificTexts[currentMode][1];
        newSummary = modeSpecificTexts[currentMode][2];
        header.setText(newHeader);
        modesTitle.setText(newModeTitle);
        summary.setText(newSummary);

        /*Set Color Themes*/
        int[][] modeSpecificColors = getSpecificColors(activity);
        title.setTextColor(modeSpecificColors[currentMode][0]);
        header.setTextColor(modeSpecificColors[currentMode][1]);
        modesTitle.setTextColor(modeSpecificColors[currentMode][2]);
        summary.setTextColor(modeSpecificColors[currentMode][3]);

        /*Set Navigators*/
        LottieAnimationView[][] navigators = getSpecificNavigotor(activity);
        navigators[currentMode][0].setVisibility(View.VISIBLE);
        navigators[currentMode][1].setVisibility(View.VISIBLE);
        navigators[previousMode][0].setVisibility(View.INVISIBLE);
        navigators[previousMode][1].setVisibility(View.INVISIBLE);

        /*Check For End or Start Of List*/
        if(currentMode==StartScreen.WEATHERS) leftNavigations.setVisibility(View.INVISIBLE);
        else leftNavigations.setVisibility(View.VISIBLE);

        if(currentMode==StartScreen.IMPENDING) rightNavigations.setVisibility(View.INVISIBLE);
        else rightNavigations.setVisibility(View.VISIBLE);

        /*Set Mode Gif Resource Getter and Set Mode To Be Played*/
        switch (currentMode){
            case StartScreen.WEATHERS:
                mode = "weathers";
                StartScreen.setMode(context, StartScreen.WEATHERS);
                break;
            case StartScreen.POPULATIONS:
                mode = "populations";
                StartScreen.setMode(context, StartScreen.POPULATIONS);
                break;
            case StartScreen.IMPENDING:
                mode = "impending";
                StartScreen.setMode(context, StartScreen.IMPENDING);
                break;
            default:
                mode = "weathers";
                StartScreen.setMode(context, StartScreen.WEATHERS);
        }

        /*Set Gif View based on display size*/
        if (dWidth >= 1440)
            Glide.with(activity)
                    .load(getResources().getIdentifier(mode + "_1000", "drawable", activity.getPackageName()))
                    .into(gifView);

        else if (dWidth > 720)
            Glide.with(activity)
                    .load(getResources().getIdentifier(mode + "_500", "drawable", activity.getPackageName()))
                    .into(gifView);
        else
            Glide.with(activity)
                    .load(getResources().getIdentifier(mode + "_250", "drawable", activity.getPackageName()))
                    .into(gifView);
    }

    /**
     * Set Specific Texts -
     * Here we set the texts values for each mode.
     *
     * Setting the Texts -
     *      We store the texts in a two dimensional array called <modeSpecificTexts>.
     *
     *      Suppose n is our mode code
     *      [n][0]: defines the header
     *      [n][1]: defines the mode title text for index n
     *      [n][2]: defines the summary text for index n
     *
     *     Example visualization of modeSpecificTexts
     *   |-------------------------------------------|
     *   |         0          ||         1           |
     *   |0                   ||                     |
     *   |   impending_title  ||  impending_summary  |
     *   |--------------------||---------------------|
     *   |                    ||                     |
     *   |1                   ||                     |
     *   |   weather_title    ||   weather_summary   |
     *   |--------------------||---------------------|
     *   |                    ||                     |
     *   |2                   ||                     |
     *   |  populations_title || populations_summary |
     *   |-------------------------------------------|
     *
     *   Todo -
     *    When adding a new mode, make sure to add n index and populate it with the correct info.
     *
     * @param activity:
     * @return Two dimensional array holding all the text data
     */
    private String[][] getSpecificTexts(Activity activity){
        String[][] modeSpecificTexts = new String[3][3];

        /*Set Impending*/
        modeSpecificTexts[0][0] = activity.getResources().getString(R.string.impending_header);
        modeSpecificTexts[0][1] = activity.getResources().getString(R.string.impending_title);
        modeSpecificTexts[0][2] = activity.getResources().getString(R.string.impending_summary);

        /*Set Weathers*/
        modeSpecificTexts[1][0] = activity.getResources().getString(R.string.header);
        modeSpecificTexts[1][1] = activity.getResources().getString(R.string.weathers_title);
        modeSpecificTexts[1][2] = activity.getResources().getString(R.string.weather_summary);

        /*Set Populations*/
        modeSpecificTexts[2][0] = activity.getResources().getString(R.string.header);
        modeSpecificTexts[2][1] = activity.getResources().getString(R.string.populations_title);
        modeSpecificTexts[2][2] = activity.getResources().getString(R.string.population_summary);

        return modeSpecificTexts;
    }

    private int[][] getSpecificColors(Activity activity){
        int[][] modeSpecificColors = new int[3][4];

        /*Set Impending Color Theme*/
        modeSpecificColors[0][0] = activity.getResources().getColor(R.color.impending_title);
        modeSpecificColors[0][1] = activity.getResources().getColor(R.color.impending_header);
        modeSpecificColors[0][2] = activity.getResources().getColor(R.color.impending_mode_title);
        modeSpecificColors[0][3] = activity.getResources().getColor(R.color.impending_summary);

        /*Set Weather Color Theme*/
        modeSpecificColors[1][0] = activity.getResources().getColor(R.color.weathers_title);
        modeSpecificColors[1][1] = activity.getResources().getColor(R.color.weathers_header);
        modeSpecificColors[1][2] = activity.getResources().getColor(R.color.weathers_mode_title);
        modeSpecificColors[1][3] = activity.getResources().getColor(R.color.weathers_summary);

        /*Set Populations Color Theme*/
        modeSpecificColors[2][0] = activity.getResources().getColor(R.color.populations_title);
        modeSpecificColors[2][1] = activity.getResources().getColor(R.color.populations_header);
        modeSpecificColors[2][2] = activity.getResources().getColor(R.color.populations_mode_title);
        modeSpecificColors[2][3] = activity.getResources().getColor(R.color.populations_summary);

        return modeSpecificColors;
    }

    private LottieAnimationView[][] getSpecificNavigotor(Activity activity){
        LottieAnimationView[][] modeSpecificNavigotor = new LottieAnimationView[3][2];

        /*Set Impending Navigator*/
        modeSpecificNavigotor[0][0] = rootView.findViewById(R.id.left_impending_navigation);
        modeSpecificNavigotor[0][1] = rootView.findViewById(R.id.right_impending_navigation);

        /*Set Weather Navigator*/
        modeSpecificNavigotor[1][0] = rootView.findViewById(R.id.left_weather_navigation);
        modeSpecificNavigotor[1][1] = rootView.findViewById(R.id.right_weather_navigation);

        /*Set Population Navigator*/
        modeSpecificNavigotor[2][0] = rootView.findViewById(R.id.left_population_navigation);
        modeSpecificNavigotor[2][1] = rootView.findViewById(R.id.right_population_navigation);

        return modeSpecificNavigotor;
    }
}



