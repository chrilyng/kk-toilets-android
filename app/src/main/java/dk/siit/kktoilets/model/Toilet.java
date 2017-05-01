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
        return getProperties().getVejnavn() + " " + getProperties().getToilet_type() + " " + getProperties().getBydel() +
                " " + getProperties().getToilet_aaben_tid();
    }
}
