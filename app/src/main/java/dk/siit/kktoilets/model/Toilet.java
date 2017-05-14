package dk.siit.kktoilets.model;

public class Toilet {
    private String name;
    private Geometry geometry;
    private Properties properties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        if(getProperties().getToilet_type()!=null) {
            stringBuffer.append(getProperties().getToilet_type());
            stringBuffer.append(" ");
        }
        if(getProperties().getVejnavn()!=null) {
            stringBuffer.append(getProperties().getVejnavn());
            stringBuffer.append(" ");
        }
        if(getProperties().getBydel()!=null) {
            stringBuffer.append(getProperties().getBydel());
            stringBuffer.append(" ");
        }
        if(getProperties().getToilet_aaben_tid()!=null) {
            stringBuffer.append(getProperties().getToilet_aaben_tid());
        }
        return stringBuffer.toString();
    }
}
