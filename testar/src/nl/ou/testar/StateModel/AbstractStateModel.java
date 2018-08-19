package nl.ou.testar.StateModel.AbstractStateModel;

import java.util.HashSet;
import java.util.Set;

public class AbstractStateGraph {

    // this should contain a hash to uniquely identify the elements that were `used` in the abstraction level of the model
    private String abstractionLevelIdentifier;

    private Set<AbstractStateTransition> stateTransitions;

    private Set<AbstractState> states;

    /**
     * constructor
     * @param abstractionLevelIdentifier
     */
    private AbstractStateGraph(String abstractionLevelIdentifier) {
        this.abstractionLevelIdentifier = abstractionLevelIdentifier;
        // both sets are empty when the model is just created
        stateTransitions = new HashSet<>();
        states = new HashSet<>();
        initGraph();
    }

    /**
     * initialization code for the graph should go in this method
     */
    private void initGraph() {
        // add code here to initialize the graph, such as loading a graph from disk/database/external storage
    }

}
