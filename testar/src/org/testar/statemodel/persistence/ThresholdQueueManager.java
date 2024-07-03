package org.testar.statemodel.persistence;

import org.testar.statemodel.*;
import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.exceptions.InvalidEventException;
import org.testar.statemodel.sequence.Sequence;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.statemodel.sequence.SequenceNode;
import org.testar.statemodel.sequence.SequenceStep;
import org.testar.statemodel.util.EventHelper;

import java.util.ArrayDeque;

public class ThresholdQueueManager extends QueueManager implements PersistenceManager, StateModelEventListener {
    private final int threshold;

    public ThresholdQueueManager(PersistenceManager persistenceManager, EventHelper eventHelper, boolean hybridMode) {
        this(persistenceManager, eventHelper, hybridMode, 100);
    }
    public ThresholdQueueManager(PersistenceManager persistenceManager, EventHelper eventHelper, boolean hybridMode, int threshold) {
        super(persistenceManager, eventHelper, hybridMode);
        this.threshold = threshold;
    }

    private void processQueue(){
        if (!queue.isEmpty()) {
            int nrOfItemsProcessed = 0;
            int totalNrOfItems = queue.size();
            System.out.println("QUEUE contains " + totalNrOfItems);
            QueueVisualizer visualizer = new QueueVisualizer("Processing persistence queue");
            visualizer.updateMessage("Processing persistence queue : " + nrOfItemsProcessed + " / " + totalNrOfItems + " processed");
            while (!queue.isEmpty()) {
                queue.remove().run();
                nrOfItemsProcessed++;
                visualizer.updateMessage("Processing persistence queue : " + nrOfItemsProcessed + " / " + totalNrOfItems + " processed");
            }
            visualizer.stop();
        }
    }

    @Override
    public void processRequest(Runnable runnable, Persistable persistable) {
        queue.add(runnable);
        if (queue.size()>threshold) {
            processQueue();
        }
    }
}
