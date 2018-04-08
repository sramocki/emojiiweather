package com.example.gabe.emojiweather;
import android.widget.TextView;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Class for parsing the traffic data
 */
public class TrafficParser
{

    //Unparsed string data is passed, which returns a string
    public String getTraffic(String data)
    {
        try
        {
            String output = "";

            //Parsing for Weather description, split by using the short description of the event
            String[] parts = data.split("shortDesc\":\"");


            //Iterates through split traffic reports and returns to the main thread
            for (int i = 1; i < parts.length; i++)
            {
                String[] incidentTypeCatcher = parts[i-1].split("\"type\":");
                String incidentType = "";

                //Identifies type of traffic reports
                //Case 1 is construction
                //Case 2 is generic event
                //Case 3 is congestion
                //Case 4 is accident
                switch(incidentTypeCatcher[incidentTypeCatcher.length-1].substring(0,1))
                {
                    case "1": incidentType = "\uD83D\uDEA6" + "Construction" + "\uD83D\uDEA6";
                            break;
                    case "2": incidentType = "\uD83D\uDEA6" + "Event" + "\uD83D\uDEA6";
                    break;
                    case "3": incidentType = "\uD83D\uDEA6" + "Congestion"+ "\uD83D\uDEA6";
                    break;
                    case "4": incidentType = "\uD83D\uDEA6" +"Accident" + "\uD83D\uDEA6";
                    break;
                    default: break;
                }

                //Limits reports to four entries
                if(i < 4)
                {
                    String[] temp = parts[i].split("\",\"fullDesc\"");
                    if (temp[0].contains("\\")){
                        String[] tempTwo = temp[0].split("\\\\");
                        temp[0] = tempTwo[0] + tempTwo[1];
                    }

                    //Removes duplicate entries
                    if(temp[0].contains("  "))
                    {
                        temp[0] = "";
                    }
                    else
                        {

                            output = output +  incidentType +"\n" + temp[0] + "\n\n";
                        }


                }
            }
            if(output == "")
            {
                return "No Traffic Incidents Found!";
            }
            return output;
        }
        catch (Exception e){
            e.printStackTrace();

            return "Exception!";
        }
    }
}