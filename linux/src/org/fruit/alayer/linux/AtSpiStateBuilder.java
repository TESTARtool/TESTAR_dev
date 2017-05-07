package org.fruit.alayer.linux;


import org.fruit.Assert;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.linux.util.GdkHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.*;


/**
 * Represents an object that builds up a tree around AT-SPI elements that Testar uses to determine an application's state.
 */
public class AtSpiStateBuilder implements StateBuilder {


    //region Global variables


    private static final double _defaultTimeOut = 10;
    private static final int _defaultThreadPoolCount = 1;
    private static final double _secondsToMilliseconds = 1000.0;


    private boolean _disposed = false;


    //endregion


    //region Properties


    private final double _timeOut;


    private transient ExecutorService _executorService;


    //endregion


    //region Constructors


    /**
     * Default constructor.
     */
    public AtSpiStateBuilder() { this(_defaultTimeOut); }


    /**
     * Constructs a new state builder with a time out.
     * @param timeOut The time after which a state request will time out in seconds.
     */
    public AtSpiStateBuilder(double timeOut) {


        Assert.isTrue(timeOut > 0);
        this._timeOut = timeOut;


        // Needed to be able to schedule asynchornous tasks conveniently.
        _executorService = Executors.newFixedThreadPool(_defaultThreadPoolCount);


    }


    //endregion


    //region StateBuilder implementation


    /**
     * Builds the current state tree for the supplied SUT.
     * @param system The SUT for which to build the current state tree.
     * @return The current state of the SUT.
     * @throws StateBuildException Thrown when an error occurs with regards to concurrency.
     */
    @Override
    public State apply(SUT system) throws StateBuildException {


        try {
            Future<AtSpiState> future = _executorService.submit(new AtSpiStateFetcher(system));
            return future.get((long)(_timeOut * _secondsToMilliseconds), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new StateBuildException(e);
        } catch (TimeoutException e) {


            // Create the default state - start with a default root element.
            AtSpiRootElement atSpiRootElement = new AtSpiRootElement();
            atSpiRootElement.isRunning = system.isRunning();
            atSpiRootElement.timeStamp = System.currentTimeMillis();
            atSpiRootElement.hasStandardKeyboard = system.get(Tags.StandardKeyboard, null) != null;
            atSpiRootElement.hasStandardMouse = system.get(Tags.StandardMouse, null) != null;


            // Instead of the bounding box of the application use the screen bounding box - no clue why though.
            atSpiRootElement.boundingBoxOnScreen = GdkHelper.getScreenBoundingBox();


            // Convert the root element to a State of an application.
            AtSpiState defaultNotRespondingState = new AtSpiState(atSpiRootElement);


            // Basically set the state manually to not responding.
            defaultNotRespondingState.set(Tags.Role, Roles.Process);
            defaultNotRespondingState.set(Tags.NotResponding, true);


            return defaultNotRespondingState;


        }


    }


    //endregion


    //region Other needed functionality


    /**
     * Cleans up/ disposes of this instance - can be used if you don't want to rely on the GC to dispose of this object.
     */
    public void dispose() {


        // Only dispose of this object if it hasn't been disposed of before - NullPointerExceptions might happen otherwise.
        if (_disposed) {
            return;
        }


        // Stop concurrency.
        _executorService.shutdown();
        _disposed = true;


    }


    /**
     * Called by the Garbage Collector at some point to clean up this object.
     */
    public void finalize() {
        dispose();
    }


    //endregion


    //region Serializable functionality


    // Used to determine the class during serialization.
    private static final long serialVersionUID = 777888999111555444L;


    // Most likely used to serialize and deserialize an instance of this class - don't know if this is used by Testar though.



    /**
     * Serialize an instance of this object.
     * @param oos The outputstream to write to.
     * @throws IOException An IO error occurred.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }


    /**
     * Deserialize an instance of this object.
     * @param ois The inputstream to write to.
     * @throws IOException An IO error occurred.
     * @throws ClassNotFoundException Class could not be found.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        ois.defaultReadObject();
    }


    //endregion


}