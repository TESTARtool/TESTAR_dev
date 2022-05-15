package nl.ou.testar.resolver;

import com.google.common.collect.ImmutableSet;
import nl.ou.testar.StateModel.Exception.StateModelException;
import org.fruit.monkey.ReplayStateModelUtil;
import org.fruit.monkey.orientdb.OrientDBService;
import org.testar.CodingManager;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.Type;
import org.testar.statemodel.StateModelManager;

import java.util.Iterator;
import java.util.Set;

public class OrientDBSerialResolver extends SerialResolver<OrientDBService> {

    private Set<String> sequenceIdsToReplay;
    private StateModelManager stateModelManager;
    private Iterator<String> sequenceIdsIterator;
    private int actionCount, replayActionCount;
    boolean success;
    boolean faultySequence;
    String sequenceIdentifier;

    String replayName;
    String replayVersion;
    String replayModelIdentifier;

    public OrientDBSerialResolver(OrientDBService service, Settings settings, StateModelManager stateModelManager) {
        super(service, settings);
        this.stateModelManager = stateModelManager;
    }

    @Override
    public void startReplay(String reportTag) throws Exception {

        replayName = settings.get(ConfigTags.ReplayApplicationName,"");
        replayVersion = settings.get(ConfigTags.ReplayApplicationVersion,"");
        replayModelIdentifier = ReplayStateModelUtil.getReplayModelIdentifier(stateModelManager, replayName, replayVersion);

        if(!settings.get(ConfigTags.ReplayModelSequenceId,"").isEmpty()) {
            sequenceIdsToReplay = ImmutableSet.of(settings.get(ConfigTags.ReplayModelSequenceId));

            String msg = String.format("Specific TestSequence %s for AbstractStateModel (%s, %s)", settings.get(ConfigTags.ReplayModelSequenceId), replayName, replayVersion);
            System.out.println(msg);
        }
        else if(!settings.get(ConfigTags.ReplayModelSequenceTime,"").isEmpty()) {
            sequenceIdsToReplay = ImmutableSet.of(ReplayStateModelUtil.getReplaySequenceIdentifierByTime(stateModelManager, settings.get(ConfigTags.ReplayModelSequenceTime)));

            String msg = String.format("Specific TestSequence TIME %s for AbstractStateModel (%s, %s)", settings.get(ConfigTags.ReplayModelSequenceTime), replayName, replayVersion);
            System.out.println(msg);
        }
        else {
            sequenceIdsToReplay = ReplayStateModelUtil.getReplayAllSequenceIdFromModel(stateModelManager, replayModelIdentifier, replayName, replayVersion);

            String msg = String.format("%s TestSequences found for AbstractStateModel (%s, %s)", sequenceIdsToReplay.size(), replayName, replayVersion);
            System.out.println(msg);
        }

        sequenceIdsIterator = sequenceIdsToReplay.iterator();
    }

    @Override
    public Set<Action> deriveActions(SUT system, State state) {
        if (actionCount >= replayActionCount) {
            return nextResolver().deriveActions(system, state);
        }
        return super.deriveActions(system, state);
    }

    @Override
    protected Action nextAction(SUT system, State state) {
        actionCount++;
        String actionSequence = sequenceIdentifier + "-" + actionCount + "-" + sequenceIdentifier + "-" + (actionCount+1);
        try {
            // Get the counter of the action step
            // We need to do this becaus
            // e one model contains multiple sequences
            String concreteActionId = ReplayStateModelUtil.getReplayConcreteActionStep(stateModelManager, actionSequence);
            String actionDescriptionReplay = ReplayStateModelUtil.getReplayActionDescription(stateModelManager, actionSequence);


            // Now we get the AbstractActionId of the model that contains this counter action step
            // This is the action we want to replay and we need to search in the state
            String abstractActionReplayId = ReplayStateModelUtil.getReplayAbstractActionIdFromConcreteAction(stateModelManager, replayModelIdentifier, concreteActionId);
            // Derive Actions of the current State
            Set<Action> actions = nextResolver().deriveActions(system, state);
            buildStateActionsIdentifiers(state, actions);

            // Now lets see if current state contains the action we want to replay
            Action actionToReplay = null;
            // First, use the AbstractIDCustom of current state actions to find the action we want to replay
            for(Action a : actions) {
                if(a.get(Tags.AbstractIDCustom, "").equals(abstractActionReplayId)) {
                    actionToReplay = a;
                    // For Type actions we need to type the same text
                    if(actionToReplay.get(Tags.Role, ActionRoles.Action).toString().contains("Type")) {
                        actionToReplay = actionTypeToReplay(actionToReplay, actionDescriptionReplay);
                    }
                    break;
                }
            }
            return actionToReplay;
        } catch (StateModelException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean moreActions(State state) {
        return (success && !faultySequence && actionCount < replayActionCount);
    }

    @Override
    public boolean moreSequences() {
        if (sequenceIdsIterator != null && sequenceIdsIterator.hasNext()) {
            sequenceIdentifier = sequenceIdsIterator.next();
            try {
                replayActionCount = ReplayStateModelUtil.getReplayActionStepsCount(stateModelManager, sequenceIdentifier);
                success = true;
                actionCount = 0;

                return true;
            } catch (StateModelException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Use CodingManager to create the Actions identifiers:
     * ConcreteID, ConcreteIDCustom, AbstractID, AbstractIDCustom
     *
     * @param state
     * @param actions
     */
    protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
        CodingManager.buildIDs(state, actions);
    }

    /**
     * Replay a Type action requires to reuse the same text.
     *
     * @param actionToReplay
     * @param actionDescriptionReplay
     * @return
     */
    private Action actionTypeToReplay(Action actionToReplay, String actionDescriptionReplay) {
        for(Action compAct : ((CompoundAction)actionToReplay).getActions()) {
            if(compAct instanceof Type) {
                //Type 'kotrnrls' into 'Editor de texto
                String replayText = actionDescriptionReplay.substring(6);
                replayText = replayText.substring(0, replayText.indexOf("'"));
                ((Type)compAct).setText(replayText);
                actionToReplay.set(Tags.Desc, actionDescriptionReplay);
            }
        }
        return actionToReplay;
    }
}
