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
import java.util.concurrent.atomic.AtomicInteger;

public class HttpCoordinateManager
{
    private static HttpCoordinateManager ourInstance = new HttpCoordinateManager();
    public static ArrayList<Coordinate> coordinates = new ArrayList<>();
    public static final AtomicInteger errorCount = new AtomicInteger(0);
    public static final AtomicInteger danishNodes = new AtomicInteger(0);
    public static final AtomicInteger notDanishNodes = new AtomicInteger(0);


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
                errorCount.getAndIncrement();
                return;
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
                    errorCount.getAndIncrement();
                }

                @Override
                public void error(SAXParseException exception) throws SAXException
                {

                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException
                {
                    errorCount.getAndIncrement();
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
                boolean isPowerLine = false;
                for (int j = 0; j < children.getLength(); j++)
                {
                    if (children.item(j) != null && children.item(j).getAttributes() != null && children.item(j).getAttributes().item(0) != null)
                    {
                        if (children.item(j).getAttributes().item(0).getNodeValue().contentEquals("power"))
                        {
                            isPowerLine = true;
                        }
                    }

                }


                if (lat != null && lon != null && isPowerLine)
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
                            danishNodes.getAndIncrement();
                        }
                        else
                        {
                            notDanishNodes.getAndIncrement();
                        }
                    }};
                }
                else
                {
                    errorCount.getAndIncrement();
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
        return danishNodes.get() + notDanishNodes.get() + errorCount.get();
    }

    public static void main(String[] args)
    {
        cacheCoordinatesFromId(2232529747L);
    }
}
