package com.tillman.malik.triviaworldtour;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class StartScreen extends AppCompatActivity {
    /*--Globals*/
    /*Modes*/
    final public static int IMPENDING = 0;
    final public static int WEATHERS = 1;
    final public static int POPULATIONS = 2;
    final public static int CAPITALS = 3;

    /*We use this for the background gradient effect*/
    private static ConstraintLayout mainLayout;

    /*Content Bg Changes onMode Changes*/
    private static ImageView modeBGs;

    /*Total Questions to Generate*/
    final public static int total = 15;

    /*Global Fonts*/
    public static Typeface GROBOLD, JANDA, HEAVITAS, BEBAS;

    /*Panel for Our Play Action*/
    private SlidingUpPanelLayout slidingUpPanelLayout;

    /*Media Handlers*/
    Uri video, music;
    MediaPlayer media_player;

    /*Bundled Game Mode*/
    private static int game_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start_screen);
        initiateElements();
        executeMedia();
        setListeners();

    }

    public void initiateElements(){
        /*Todo:Change modes here*/
        game_mode = WEATHERS;

        /*Gradient bg*/
        mainLayout = findViewById(R.id.main_content);
        setMode(this, game_mode);

        /*Contend Background*/
        modeBGs = findViewById(R.id.modes_bg);

        /*Drawers*/
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        slidingUpPanelLayout = findViewById(R.id.sliding_up_panel);

        /*Settings View Pager*/
        ViewPager mViewPager = findViewById(R.id.viewPager);
        setViewPager(mViewPager);

        /*Tabs for View Pager*/
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.settings_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.stats_icon);

        /*Set Global Fonts*/
        GROBOLD = Typeface.createFromAsset(getAssets(), "fonts/grobold.ttf");
        JANDA = Typeface.createFromAsset(getAssets(), "fonts/janda.ttf");
        BEBAS = Typeface.createFromAsset(getAssets(), "fonts/bebas.ttf");
        HEAVITAS = Typeface.createFromAsset(getAssets(), "fonts/heavitas.ttf");
    }

    /**
     * Set Mode
     * Here we set the game mode and set the background animation list based on that game mode.
     *
     * @param mode: Game mode that will determine BG color
     * @param context: Environment data
     */
    public static void setMode(Context context, int mode){
        /*Animations Variable*/
        AnimationDrawable animationDrawable;
        int fadeIn = 5000, fadeOut = 5000;

        /*Set Mode*/
        int prevMode = game_mode;
        if(mode == IMPENDING) game_mode = POPULATIONS;
        else game_mode = mode;

        /*Set BG*/
        switch (mode){
            case WEATHERS:
                if(mode!=prevMode){
                    modeBGs.setImageDrawable(context.getDrawable(R.drawable.weather_bg));
                    mainLayout.setBackground(context.getDrawable(R.drawable.weathers_gradient_list));
                    animationDrawable = (AnimationDrawable) mainLayout.getBackground();
                    animationDrawable.setEnterFadeDuration(fadeIn);
                    animationDrawable.setExitFadeDuration(fadeOut);
                    animationDrawable.start();
                }
                break;
            case POPULATIONS:
                modeBGs.setImageDrawable(context.getDrawable(R.drawable.populations_bg));
                mainLayout.setBackground(context.getDrawable(R.drawable.populations_gradient_list));
                animationDrawable = (AnimationDrawable) mainLayout.getBackground();
                animationDrawable.setEnterFadeDuration(fadeIn);
                animationDrawable.setExitFadeDuration(fadeOut);
                animationDrawable.start();
                break;
            case IMPENDING:
                if(mode!=prevMode){
                    modeBGs.setImageDrawable(context.getDrawable(R.drawable.impending_bg));
                    mainLayout.setBackground(context.getDrawable(R.drawable.impending_gradient_list));
                    animationDrawable = (AnimationDrawable) mainLayout.getBackground();
                    animationDrawable.setEnterFadeDuration(fadeIn);
                    animationDrawable.setExitFadeDuration(fadeOut);
                    animationDrawable.start();
                }
        }
    }

    /**
     * Execute Media
     * Fetches media from source and begins async task to play media in separate
     * background thread.
     */
    public void executeMedia(){
        /*Design Media URL*/
        video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.bgloop);
        music = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.musicloop);

        media_player = new MediaPlayer();

        /*Fetch Media*/
        try{
            media_player.setDataSource(this, music);
            //media_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            media_player.prepare();
            media_player.setLooping(true); //Loops song
            /*Todo*/
            //background_video.setVideoURI(video);
        }catch (Exception e) {
            /*HANDLE ERROR APPROPRIATELY*/
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        /*Thread Handler*/
        AsyncTask.execute(new Runnable(){
            @Override
            public void run() {
                /*Execute media*/
                /*Todo*/
                //background_video.requestFocus();
                //background_video.start();
                media_player.start();

                /*Loop Video*/
                /*Todo*/
                //background_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                //{
                //    @Override
                //    public void onPrepared(MediaPlayer mp) {
                //        mp.setLooping(true);
                //        mp.start();

                //   }
                //});
            }
        });
    }

    /**
     * Get Font
     * Here we return a desired custom font from our font repository.
     *
     * @param context:
     * @param fontWanted: desired font
     * @return Typeface of font desired
     */
    public static Typeface getFont(Context context, String fontWanted){
        return Typeface.createFromAsset(context.getAssets(), "fonts/" + fontWanted + ".ttf");
    }

    private void setViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Settings());
        adapter.addFragment(new Stats());

        viewPager.setAdapter(adapter);
    }

    public void setListeners(){
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(slidingUpPanelLayout.getPanelState()==SlidingUpPanelLayout.PanelState.EXPANDED){
                    slidingUpPanelLayout.setTouchEnabled(false);

                    Intent dataService = new Intent(StartScreen.this, DataService.class);
                    dataService.putExtra("game_mode", game_mode);
                    startService(dataService);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Questions.play.performClick();
        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();
    }

}
