import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DenmarkChecker
{
    public static boolean isThisDenmark(Coordinate coordinate)
    {
        String apiKey = "AIzaSyBIrkfftyfGqI4Exp2Vl1TPVpXrriPcfu4";
        URL url = null;
        try
        {
            String lat = coordinate.getLatitude();
            String lon = coordinate.getLongitude();
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("https://maps.googleapis.com/maps/api/geocode/json?latlng=");
            String latlon = lat + "," + lon;
            sBuilder.append(latlon);
            sBuilder.append("&key=");
            sBuilder.append(apiKey);

            String requestUrl = sBuilder.toString();
            url = new URL(requestUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            int status = con.getResponseCode();
            if (status != 200)
            {
                HttpCoordinateManager.errorCount++;
                return false;
                //TODO: Log an error

            }

            // Read the HTTP response into the BufferedReader
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
            {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            JSONObject jsonObject = new JSONObject(content.toString());

            JSONArray results = (JSONArray) jsonObject.get("results");
            String maybeDenmark = (String) results.getJSONObject(results.length() - 1).get("formatted_address");

            return maybeDenmark.contentEquals("Denmark");


            //TODO: Log an error


        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (ProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        HttpCoordinateManager.errorCount++;
        return false;
    }
}
