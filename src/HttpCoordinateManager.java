import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.json.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class HttpCoordinateManager
{
    private static HttpCoordinateManager ourInstance = new HttpCoordinateManager();

    private HttpCoordinateManager()
    {

    }

    public static HttpCoordinateManager getInstance()
    {
        if (ourInstance == null)
        {
            ourInstance = new HttpCoordinateManager();
        }
        return ourInstance;
    }

    public static void testHttpCall()
    {
        URL url = null;
        try
        {
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("https://www.openstreetmap.org/api/0.6/node/2232529792");

            // First parameter
            //sBuilder.append("id_token=");

            String requestUrl = sBuilder.toString();
            url = new URL(requestUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            int status = con.getResponseCode();
            if (status != 200)
            {
                System.out.println("Der skete en fejl");
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

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler()
            {
                @Override
                public void warning(SAXParseException exception) throws SAXException
                {

                }

                @Override
                public void error(SAXParseException exception) throws SAXException
                {

                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException
                {

                }
            });



            Document doc = builder.parse(new InputSource(new StringReader(content.toString())));
            doc.normalize();

            NodeList list = doc.getElementsByTagName("node");
            for(int i = 0; i < list.getLength(); i++)
            {
                Node node = list.item(i);
                String lat = node.getAttributes().getNamedItem("lat").getNodeValue();
                String lon = node.getAttributes().getNamedItem("lon").getNodeValue();
                System.out.println(lat);
                System.out.println(lon);

            }

            // Convert content from BufferedReader into a JSON-Object.
//            JSONTokener tokener = new JSONTokener(content.toString());
//            JSONObject json = new JSONObject(tokener);
//            String aud = json.getString("aud");


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
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }


        //TODO: Log an error

    }
}