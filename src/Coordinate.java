public class Coordinate
{
    private String latitude = "";
    private String longitude = "";
    private long nodeId = -1;
    private boolean isDenmark = false;

    public String getLatitude()
    {
        return latitude;
    }

    public boolean isDenmark()
    {
        return isDenmark;
    }

    public void setDenmark(boolean denmark)
    {
        isDenmark = denmark;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public long getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(long nodeId)
    {
        this.nodeId = nodeId;
    }
}
