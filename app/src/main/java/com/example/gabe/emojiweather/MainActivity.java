package com.example.gabe.emojiweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Main class file for the project. Runs the main activity of the application in the foreground.
 */
public class MainActivity extends AppCompatActivity
{
    //Different editable views found in the activity_main.xml file
    private TextView cityText;
    private TextView tempText;
    private TextView weatherText;
    private TextView trafficText;
    private TextView emojiText;
    private Button button;

    //Strings for holding user input and returned data from API calling
    String resultString = "";
    String zipcode = "";

    /**
     * Method that runs on first creation of the app which initializes the view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Sets the view to use the activity main XML file
        setContentView(R.layout.activity_main);
        cityText = (TextView) findViewById(R.id.cityView);
        tempText = (TextView) findViewById(R.id.temperatureView);
        weatherText = (TextView) findViewById(R.id.weatherView);
        trafficText = (TextView) findViewById(R.id.trafficView);
        emojiText = (TextView) findViewById(R.id.emojiView);

        //Calls the zipcode popup method
        enterZipPrompt();

        //Enables listening on the refresh button
        refreshButton();

    }

    /**
     * Method for taking in user input via a popup prompt
     */
    protected void enterZipPrompt()
    {
        //Initializes popup by asking the user for their input
        AlertDialog.Builder popup = new AlertDialog.Builder(this);
        popup.setTitle("Enter your Michigan zipcode" + " \uD83D\uDC3B");

        //Ignores clicking away from the popup
        popup.setCancelable(false);

        //Set up the input to the temporary view field
        final EditText userData = new EditText(this);

        //Sets user input to a keypad for numerical values (zipcodes)
        userData.setInputType(InputType.TYPE_CLASS_PHONE);

        //Limits user input to 5 characters long, with a message
        userData.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        userData.setHint("Enter a valid 5-digit zipcode");
        popup.setView(userData);

        //Adds listeners for the okay button and details different scenarios
        popup.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Sets the user input into a string, then converts it to an integer for comparisons
                String stringZip = userData.getText().toString();
                int intZip = Integer.parseInt(stringZip);

                //Strings needs to be 5 numbers long AND be within the range 48001-49971
                if(stringZip.length() == 5 && (intZip >= 48001 && intZip <= 49971))
                {
                    //Store zipcode if true, and execute API calls
                    zipcode = userData.getText().toString();
                    new WeatherConnect().execute();
                    new TrafficConnect().execute();
                }
                //If the zipcode is too short, retry the the zip prompt
                else if(stringZip.length() < 5)
                {
                    Toast.makeText(getApplicationContext(), "Entry was too short. Please try again.", Toast.LENGTH_SHORT).show();
                    enterZipPrompt();
                }
                //If the zipcode is outside the range, try again
                else
                {
                    Toast.makeText(getApplicationContext(), "Entry was not a valid Michigan zipcode. Please try again.", Toast.LENGTH_SHORT).show();
                    enterZipPrompt();
                }

            }
        });
        popup.show();
    }

    //Listens to user input on the button, which reruns the zipcode prompt action
    protected void refreshButton()
    {
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View myView)
            {
                enterZipPrompt();
            }
        });
    }

    /**
     * Class responsible for calling the weather API, which runs the task asynchronously in the background thread.
     * Both the parameter and progress fields are null and it returns a JSONObject
     */
    class WeatherConnect extends AsyncTask<Void, Void, JSONObject>
    {
        //URL string is combined with the zipcode user input and the API key
        private String api_URL = ("https://api.wunderground.com/api/" + "48a6ddb9b2e805f6" + "/conditions/q/" + zipcode + ".json");

        //Establishes connection to the API via the network connection class and URL string
        @Override
        protected JSONObject doInBackground(Void... args)
        {
            NetworkConnection networkCon = new NetworkConnection();
            return networkCon.networkConnection(api_URL);
        }

        //Makes decisions on the data returned back from the JSON object in the weather parisng class file
        @Override
        protected void onPostExecute(JSONObject json) {
            //Use JSON result to display in TextView, assuming the json is not null
            if (json != null) {
                {
                    //Pass string data to the weather parser class file
                    resultString = json.toString();
                    WeatherParser parserWeather = new WeatherParser();

                    //Create an string array from the returned parsed weather string data
                    //The weather parser returns a string array, thus the returned data is set to one
                    String[] parsedData = parserWeather.getWeather(resultString);



                    //Checks the first element of the data array, and prints text/emojis corresponding to the data
                    if(parsedData[0].contains("Cloudy") || parsedData[0].contains("Overcast"))
                    {
                        weatherText.setText("It's cloudy outside ☁");
                    }
                    else if(parsedData[0].contains("Fog") || parsedData[0].contains("Hazy"))
                    {
                        weatherText.setText("It's foggy outside \uD83C\uDF2B");
                    }
                    else if(parsedData[0].contains("Snow") || parsedData[0].contains("Sleet") || parsedData[0].contains("Flurries"))
                    {
                        weatherText.setText("It's snowing ❄");
                    }
                    else if(parsedData[0].contains("Rain") || parsedData[0].contains("Storms"))
                    {
                        weatherText.setText("It's raining outside ☔");
                    }
                    else if(parsedData[0].contains("Error"))
                    {
                        weatherText.setText("❌" + parsedData[0] + " ❌");
                    }
                    else
                    {
                        weatherText.setText("It's probably sunny outside ☀");
                    }

                    //Converts the second element of the array, which holds the temperature value to a double
                    double tempValue = Double.parseDouble(parsedData[1]);

                    //Sets the temperature field to the returned value
                    tempText.setText(tempValue + " °F");

                    //Sets the city name to the returned value from the third element which stores city name
                    cityText.setText(parsedData[2] + " Weather");

                    //Determines which emoji to print given the temperature range
                    if(tempValue >= 80 && tempValue < 200)
                    {
                        emojiText.setText("\uD83D\uDE05");
                    }
                    else if(tempValue >= 65 && tempValue < 80)
                    {
                        emojiText.setText("\uD83D\uDE03");
                    }
                    else if(tempValue >= 32 && tempValue < 65)
                    {
                        emojiText.setText("\uD83D\uDE28");
                    }
                    else if(tempValue < 32)
                    {
                        emojiText.setText("\uD83D\uDE30");
                    }
                    else if (parsedData[1].equals("666"))
                    {
                        emojiText.setText("Error!");
                    }
                    else
                    {
                        emojiText.setText("\uD83D\uDE30");
                    }
                }
            }
        }
    }

    /**
     * Class responsible for calling the traffic API, which runs the task asynchronously in the background thread.
     * Both the parameter and progress fields are null and it returns a JSONObject
     */
    class TrafficConnect extends AsyncTask<Void, Void, JSONObject>
    {
        //API URL for calling data from Oakland County (Default)
        //https://www.mapquestapi.com/traffic/v2/incidents?&outFormat=json&boundingBox=42.740708808266845%2C-83.06625366210936%2C42.53689200787315%2C-83.51909637451172&filters=construction%2Cincidents&key=" + API_KEY

        //API URL for calling data from Detroit
        //https://www.mapquestapi.com/traffic/v2/incidents?&outFormat=json&boundingBox=42.43232607079181%2C-82.8324508666992%2C42.23334735634176%2C-83.26332092285156&filters=construction%2Cincidents&key=

        String api_KEY2 = "hH7c33NIQMCZEDqBA9DSDobxcnjprlu4";
        private String api_URL2 = ("https://www.mapquestapi.com/traffic/v2/incidents?&outFormat=json&boundingBox=42.740708808266845%2C-83.06625366210936%2C42.53689200787315%2C-83.51909637451172&filters=construction%2Cincidents&key=" + api_KEY2);

        @Override
        protected JSONObject doInBackground(Void... args)
        {
            NetworkConnection networkCon = new NetworkConnection();
            return networkCon.networkConnection(api_URL2);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            //Use JSON result to display in TextView assuming not null
            if (json != null) {
                {
                    resultString = json.toString();

                    //Runs traffic parser from class file, returns data and prints it to the text view
                    TrafficParser parserTraffic = new TrafficParser();
                    String parsedData = parserTraffic.getTraffic(resultString);
                    //trafficText.setBackgroundColor(getResources().getColor(color.holo_blue_dark));
                    trafficText.setText(parsedData);
                }
            }
        }
    }
}