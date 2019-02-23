package com.tillman.malik.triviaworldtour;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

public class DataService extends IntentService {
    /*Bundles We'll Broadcast Holding Question and Answer Data*/
    Bundle bundle;

    /*Game Mode Which Will Determine How We Search for Data*/
    int game_mode;

    /*Cities For Weather Data Search*/
    public static String[]  cities = {
            "Paris",
            "London",
            "New York",
            "Berlin",
            "Tokyo",
            "Moscow",
            "Rome",
            "Venice",
            "Toronto",
            "Lagos",
            "Accra",
            "Cape Town",
            "Dubai",
            "Hong Kong",
            "Bangkok",
            "Mumbai",
            "Chicago",
            "Houston",
            "San Francisco"
    };

    /*Countries For Population Data Search*/
    public static String[] countries = {
            "France",
            "United Kingdom",
            "United States",
            "Germany",
            "Japan",
            "Cuba",
            "Finland",
            "Italy",
            "Canada",
            "Nigeria",
            "Ghana",
            "South Africa",
            "United Arab Emirates",
            "China",
            "Thailand",
            "India",
            "Poland",
            "Vietnam",
            "Slovenia",
            "Middle Africa",
            "Guyana",
            "Eastern Africa",
            "Czech Republic",
            "AFRICA",
            "EUROPE"
    };

    /**Constructor
     * Empty we do nothing here
     */
    public DataService(){
        super("DataService");
    }

    /**On Handle Intent
     * Once this service is called upon, we connect to our REST api and retrieve all the questions
     * and answers for the following game session. This service will send a broadcast that will
     * wake up our question fragment, which will handle the data retrieved.
     *
     * @param intent: Intent that calls this intent. We get our start-up data from him
     */
    @Override
    protected void onHandleIntent(Intent intent){
        /*Get Extras Bundles from Intent Call*/
        Bundle extras = intent.getExtras();
        Intent broadcastIntent = new Intent("intentKey");
        bundle = new Bundle();

        /*Get Game Mode*/
        game_mode = extras.getInt("game_mode");
        bundle.putInt("game_mode", game_mode);

        /*Generate Data Based on Game Mode*/
        switch (game_mode){
            case 1:
                getQuestionAnswerBundle(StartScreen.total);
                break;
            case 2:
                getQuestionAnswerBundle(StartScreen.total);
                break;
        }

        /*Prepare Data for Broadcast*/
        broadcastIntent.putExtra("bundle", bundle);

        /*Send Local Broadcast*/
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    /**Get Question Answer Bundles
     * Here we will dynamically generate questions based on game mode and other random values.
     *
     * Todo: Adding new modes requires you to updated the following switch statement:
     *       1. Populating location codes list
     *       2. Getting length of location array
     *       3. Assuring no duplicate values
     *       4. Formatting questions
     *
     * @param questionAmount: Amount of questions we need to generate
     */
    private void getQuestionAnswerBundle(int questionAmount){
        /*Data Bundles and location codes*/
        ArrayList<Integer> location_codes = null,
                           rightAnswers   = new ArrayList<>(),
                           wrongAnswers1  = new ArrayList<>(),
                           wrongAnswers2  = new ArrayList<>(),
                           wrongAnswers3  = new ArrayList<>();
        ArrayList<String>  questions      = new ArrayList<>();


        /*Unique Index for Wrong Answer*/
        int uniqueIndex = 0;

        /*Populate Location Codes*/
        /*Todo:Add Feature <Modes> Here*/
        switch(game_mode){
            case StartScreen.WEATHERS:
                location_codes = getLocationCodes(questionAmount, cities.length);
                break;
            case StartScreen.POPULATIONS:
                location_codes = getLocationCodes(questionAmount, countries.length);
                break;
        }

        /*Get All Questions and Answers*/
        for(int i = 0; i<=questionAmount-1; i++){
            /*Get Answers*/
            int[] cachedData = new int[4];
            cachedData[0] = getData(location_codes.get(i));

            /*Get Wrong Answers*/
            for(int r = 0; r<3; r++){
                /*Length of Locations Array*/
                int length = 0;

                /*Random Data Fetch*/
                /*Todo:Add Feature <Modes> Here*/
                switch (game_mode){
                    case StartScreen.WEATHERS:
                        length = cities.length;
                        break;
                    case StartScreen.POPULATIONS:
                        length = countries.length;
                        break;
                }

                /*Array for Our Random Locations List*/
                int[] locations = new int[length];

                /*Store All Possible Codes*/
                for (int s=0; s<length; s++) locations[s] = s+1;

                /*Shuffle Those Codes*/
                shuffle(locations);

                /*Increment Our Index if it is the Same As The Correct Answer's Index Code*/
                if(uniqueIndex==location_codes.get(i)) uniqueIndex++;

                /*Get Data from Dynamic Index*/
                cachedData[r+1] = getData(locations[uniqueIndex]);
                uniqueIndex++;
            }

            /*Reset Unique Index*/
            uniqueIndex=0;

            /*Temp Bus for Ease of Access*/
            int a1 = cachedData[0], a2 = cachedData[1], a3 = cachedData[2], a4 = cachedData[3];

            /*Assure No Duplicate Values*/
            /*Todo:Add Feature <Modes> Here*/
            switch (game_mode){
                case StartScreen.WEATHERS:
                    if(a1==a2 || a2==a3 || a2==a4) cachedData[1]+=15;
                    if(a1==a3 || a2==a3 || a3==a4) cachedData[2]+=20;
                    if(a1==a4 || a2==a4 || a3==a4) cachedData[3]+=7;
                    break;
                case StartScreen.POPULATIONS:
                    if(a1==a2 || a2==a3 || a2==a4) cachedData[1]+=15000;
                    if(a1==a3 || a2==a3 || a3==a4) cachedData[2]+=20000;
                    if(a1==a4 || a2==a4 || a3==a4) cachedData[3]+=7000;
                    break;
            }

            /*Store Answers*/
            rightAnswers.add(cachedData[0]);
            wrongAnswers1.add(cachedData[1]);
            wrongAnswers2.add(cachedData[2]);
            wrongAnswers3.add(cachedData[3]);


            /*Set Random Case for Random Question Spawn*/
            int randomCase = new Random().nextInt(3);

            /*This Will be Our Final Answer Formatted*/
            String formattedQuestion = null;

            /*Format Random Question Based on Mode
            * We do <location_array>[location_codes.get(i)-1] so that we access the correct index
            * in the location array with the API ready ID.
            *
            * In our SQL Database, New York's ID is <3>
            *     So our API call would be /api/<table>/3
            *
            *     But as we know, arrays index start at 0...
            * So here we convert our API ready index into an array search ready
            *
            * location_codes.get(3) = New York in API
            * but
            * int i = 3
            * city[i] = Berlin
            *
            * So
            *
            * int i = 3 (-1)
            * city[i=2] = New York
            *
            * Todo:Add Feature <Modes> Here*/
            switch (randomCase){
                case 0:
                    switch (game_mode){
                        case StartScreen.WEATHERS:
                            formattedQuestion = "What is the weather in " + cities[location_codes.get(i)-1] +"?";
                            break;
                        case StartScreen.POPULATIONS:
                            formattedQuestion = "How many people live in " + countries[location_codes.get(i)-1] +"?";
                            break;
                    }
                    break;

                case 1:
                    switch (game_mode){
                        case StartScreen.WEATHERS:
                            formattedQuestion = "Can you guess the weather in " + cities[location_codes.get(i)-1] +"?";
                            break;
                        case StartScreen.POPULATIONS:
                            formattedQuestion = "What is the population in " + countries[location_codes.get(i)-1] +"?";
                            break;
                    }
                    break;

                case 2:
                    switch (game_mode){
                        case StartScreen.WEATHERS:
                            formattedQuestion = "Which is the current weather in " + cities[location_codes.get(i)-1] +"?";
                            break;
                        case StartScreen.POPULATIONS:
                            formattedQuestion = "Which is the current population in " + countries[location_codes.get(i)-1] +"?";
                            break;
                    }
                    break;
            }

            /*Store Formatted Question*/
            questions.add(formattedQuestion);
        }
        /*Log {Delete Pre-Deployment}*/
        Log.i("=======", questions.toString());
        Log.i("=======", rightAnswers.toString());
        Log.i("=======", wrongAnswers1.toString());
        Log.i("=======", wrongAnswers2.toString());
        Log.i("=======", wrongAnswers3.toString());

        /*Store Answers and Questions in Bundle*/
        bundle.putStringArrayList("questionValues", questions);
        bundle.putIntegerArrayList("right_answer", rightAnswers);
        bundle.putIntegerArrayList("wrong_answer1", wrongAnswers1);
        bundle.putIntegerArrayList("wrong_answer2", wrongAnswers2);
        bundle.putIntegerArrayList("wrong_answer3", wrongAnswers3);
    }

    /**
     * Get Question Data
     * Connect to custom asp.net REST api to fetch real-time json with needed question data.
     *
     * @param code: Routing ID, must not be in array index form
     * @return data for based on game mode.
     */
    private int getData(int code){
        /*Variable for Storing Data*/
        int data = 205;

        /*URL for Our Connection*/
        URL url = null;

        /*Json Document Retrieved from API Call*/
        String jsonFeed = "";

        /*Object Selection from JSON Document*/
        String objectToFetch = "";

        try{
            /*Set URL and Object Selection Based on Game Mode*/
            switch (game_mode){
                case StartScreen.WEATHERS:
                    url = new URL("http://192.168.1.189/api/Tadcityweathers/" + code);
                    objectToFetch = "curTemp";
                    break;

                case StartScreen.POPULATIONS:
                    url = new URL("http://192.168.1.189/api/TadcountryPopulations/" + code);
                    objectToFetch = "population";
                    break;
            }

            /*Open Connection*/
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            /*If Successfull Connection, We'll Get the Input Stream and Parse Out Our Data*/
            if(connection.getResponseCode() == 200){
                /*Retrieve Input Stream*/
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                jsonFeed = reader.readLine();
                JSONObject jsonObject = new JSONObject(jsonFeed);

                /*Select and Store Data Object*/
                data = jsonObject.getInt(objectToFetch);
            }

            /*Important to Prevent Memory Leaks*/
            connection.disconnect();
        }
        /*Don't forget to do some error handling*/
        catch (MalformedURLException e){

        }
        catch (IOException e){

        }
        catch (JSONException e){

        }

        /*Return Data*/
        return data;
    }

    /**Get City Codes
     * Generates a list of non repeating random city location codes in http routing ready form.
     *
     * @param amount: specified number of codes in the list
     * @param length: length of location array
     * @return list of non repeating codes
     */
    private ArrayList<Integer> getLocationCodes(int amount, int length){
        /*Initiate Array With the Size of Location Array Depending on Mode*/
        int[] codes = new int[length];

        /*Store All Possible Codes*/
        for (int i = 0; i<length; i++) codes[i] = i+1;

        /*Shuffle Those Codes*/
        shuffle(codes);

        /*Truncate List to Desired Amount*/
        ArrayList<Integer> location_codes = new ArrayList<>();
        for (int i = 0; i<amount; i++) location_codes.add(codes[i]);

        /*Return Codes*/
        return location_codes;
    }

    /**Shuffle
     * Implementation of a simple Fisher-Yates Shuffle Algorithm
     *
     * @param array: array to shuffle
     */
    private void shuffle(int[] array) {
        /*Random Index to Swap With*/
        Random random = new Random();

        /*For Each Element (starting from the last index) swap that index with a random index*/
        int length = array.length;
        for (int index = length; index > 1; index--) swap(array, index-1, random.nextInt(index));
    }

    /**Swap
     * Standard implementation of fisher-yates shuffle algorithm. Swap index in the array with a
     * random index in the array
     *
     * @param array: array that we are shuffling
     * @param ci: current index
     * @param ri: random index
     */
    private void swap(int[] array, int ci, int ri) {
        /*Store Current Index's Value*/
        int temp = array[ci];

        /*Set Current Index to Random Index*/
        array[ci] = array[ri];

        /*Set Random Index to Our Stored Value*/
        array[ri] = temp;
    }
}
