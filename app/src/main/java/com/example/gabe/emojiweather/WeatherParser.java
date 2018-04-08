package com.example.gabe.emojiweather;

/**
 * Class for parsing the weather data
 */
public class WeatherParser
{
    //Unparsed string data is passed, which returns a string array
    public String[] getWeather(String data)
    {
        try
        {
                //Creates an empty string array for returning
                String[] output = new String[3];

                //If the passed data contains any errors, return dummy data to left the app know there's an error
                if(data.contains("\"error\""))
                {
                    output[0] = "Error";
                    output[1] = "666";
                    output[2] = "Invalid Zipcode!";
                    return output;
                }

                //Parsing for Weather description
                String[] parts = data.split("weather\":\"");
                parts = parts[1].split("\",");
                String weatherCond = parts[0];
                output[0] = weatherCond;

                //Parsing for Temperature data in F
                parts = data.split("temp_f\":");
                parts = parts[1].split(",");
                String weatherTemp = parts[0];
                output[1] = weatherTemp;

                //Temporary parsing for City Name
                parts = data.split("city\":\"");
                parts = parts[1].split("\",");
                String cityName = parts[0];
                output[2] = cityName;
                return output;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}