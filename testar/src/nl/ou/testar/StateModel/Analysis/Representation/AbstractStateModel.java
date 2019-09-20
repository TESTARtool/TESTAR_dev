package nl.ou.testar.StateModel.Analysis.Representation;

import java.util.List;
import java.util.Set;

public class AbstractStateModel {

    private String applicationVersion;

    private String applicationName;

    private String modelIdentifier;

    private Set abstractionAttributes;

    private List<TestSequence> sequences;

    public AbstractStateModel(String applicationName, String applicationVersion, String modelIdentifier, Set abstractionAttributes, List<TestSequence> sequences) {
        this.applicationVersion = applicationVersion;
        this.applicationName = applicationName;
        this.modelIdentifier = modelIdentifier;
        this.abstractionAttributes = abstractionAttributes;
        this.sequences = sequences;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getModelIdentifier() {
        return modelIdentifier;
    }

    public void setModelIdentifier(String modelIdentifier) {
        this.modelIdentifier = modelIdentifier;
    }

    public Set getAbstractionAttributes() {
        return abstractionAttributes;
    }

    public void setAbstractionAttributes(Set abstractionAttributes) {
        this.abstractionAttributes = abstractionAttributes;
    }

    public List<TestSequence> getSequences() {
        return sequences;
    }

    public void setSequences(List<TestSequence> sequences) {
        this.sequences = sequences;
    }

    /**
     * This method will return a string containing the abstraction attributes used in creating the model.
     * @return
     */
    public String getAbstractionAttributesAsString() {
        return (String)abstractionAttributes.stream().sorted().reduce("", (base, string) -> base.equals("") ? string : base + ", " + string);
    }
}
