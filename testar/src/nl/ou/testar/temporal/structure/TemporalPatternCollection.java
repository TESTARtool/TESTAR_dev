package nl.ou.testar.temporal.structure;

import nl.ou.testar.temporal.scratch.TemporalPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//@JsonRootName(value="TemporalProperties")
public class TemporalPatternCollection extends TemporalBean {

    private List<TemporalPattern> patterns;
    private String formatVersion="20190603";



    public TemporalPatternCollection(String applicationName, String applicationVersion, String modelIdentifier, Set abstractionAttributes) {
        super(applicationName, applicationVersion, modelIdentifier, abstractionAttributes);
        this.patterns = new ArrayList<TemporalPattern>();
    }

    public TemporalPatternCollection() {
        super();
    }


    public List<TemporalPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<TemporalPattern> patterns) {
        this.patterns = patterns;
    }
    public String getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }



    //custom
    public void addPattern(TemporalPattern pat){ patterns.add(pat);
    }

    public void removePattern(TemporalPattern pat) { patterns.remove(pat); }


}