package nl.ou.testar.reporter.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * StorageProperties
 */
@ConfigurationProperties("storage")
public class StorageProperties {
    private String location = "images";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
