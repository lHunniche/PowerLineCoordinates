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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class HttpCoordinateManager
{
    private static HttpCoordinateManager ourInstance = new HttpCoordinateManager();
    public static ArrayList<Coordinate> coordinates = new ArrayList<>();
    public static int errorCount = 0;
    public static int danishNodes = 0;
    public static int notDanishNodes = 0;


    public static void cacheCoordinatesFromId(long nodeId)
    {
        String stringNodeId = nodeId + "";
        URL url = null;
        try
        {
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("https://www.openstreetmap.org/api/0.6/node/" + stringNodeId);

            String requestUrl = sBuilder.toString();
            url = new URL(requestUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            int status = con.getResponseCode();
            if (status != 200)
            {
                errorCount++;
                return;
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
            for (int i = 0; i < list.getLength(); i++)
            {
                Node node = list.item(i);
                String lat = node.getAttributes().getNamedItem("lat").getNodeValue();
                String lon = node.getAttributes().getNamedItem("lon").getNodeValue();

                NodeList children = node.getChildNodes();
                for (int j = 0; i < children.getLength(); j++)
                {
                    if (children.item(j) != null)
                    {
                        if (children.item(j).getAttributes() != null)
                        {
                            System.out.println(children.item(j).getAttributes().item(0).getNodeValue());
                            System.out.println(children.item(j).getAttributes().item(1).getNodeValue());
                        }
                    }

                }
//                String powerType = node.getFirstChild().getAttributes()''
//                System.out.println(powerType);


                if (lat != null && lon != null /*&& (powerType != null && powerType.equalsIgnoreCase("tower"))*/)
                {
                    new Coordinate()
                    {{
                        setLatitude(lat);
                        setLongitude(lon);
                        setNodeId(nodeId);
                        if (DenmarkChecker.isThisDenmark(this))
                        {
                            coordinates.add(this);
                            setDenmark(true);
                            danishNodes++;
                        }
                        else
                        {
                            notDanishNodes++;
                        }
                    }};
                }


            }
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

    public static int getTotalCount()
    {
        return danishNodes + notDanishNodes + errorCount;
    }

    public static void main(String[] args)
    {
        cacheCoordinatesFromId(2232529747L);
    }
}
