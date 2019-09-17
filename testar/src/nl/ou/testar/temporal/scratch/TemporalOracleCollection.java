package nl.ou.testar.temporal.scratch;

import nl.ou.testar.temporal.structure.TemporalBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//@JsonRootName(value="TemporalProperties")
public class TemporalOracleCollection extends TemporalBean {

    private List<String> apOfProperties; //AP<digits> to widget property map:
    private List<TemporalOracle> propertyCollection;
    private String formatVersion="20190603";

    public String get_formatVersion() {
        return formatVersion;
    }

    public void set_formatVersion(String _formatVersion) {
        this.formatVersion = _formatVersion;
    }





    public TemporalOracleCollection(String applicationName, String applicationVersion, String modelIdentifier, Set abstractionAttributes) {
        super(applicationName, applicationVersion, modelIdentifier, abstractionAttributes);
        this.apOfProperties = new ArrayList<>();
        this.propertyCollection = new ArrayList<TemporalOracle>();

    }

    public TemporalOracleCollection() {
        super();
    }


    public List<String> getApOfProperties() {
        return apOfProperties;
    }

    public void setApOfProperties(List<String> apOfProperties) {
        this.apOfProperties = apOfProperties;
    }

    public List<TemporalOracle> getPropertyCollection() {
        return propertyCollection;
    }

    public void setPropertyCollection(List<TemporalOracle> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }


    //custom
    public void addOracle(TemporalOracle to) {
        propertyCollection.add(to);
    }

    public void removeOracle(TemporalOracle to) { propertyCollection.remove(to); }


}