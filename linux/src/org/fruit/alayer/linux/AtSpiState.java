package org.fruit.alayer.linux;


import org.fruit.Util;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.WidgetIterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;


/**
 * Represents the state of an application - it is a wrapper around AT-SPI nodes of an application providing certain
 * properties Testar uses to define state.
 */
public class AtSpiState extends AtSpiWidget implements State {


    //region Constructors


    /**
     * Create the root of the State tree.
     * @param root The AtSpiElement that is linked to the root.
     */
    AtSpiState(AtSpiElement root) {
        super(null, null, root);
        this.root = this;
    }


    //endregion


    //region Iterable<Widget implementation


    /**
     * Gets an iterator to iterate over a widget tree starting at the current widget as the root.
     * @return An iterator to iterate over a State (widget tree) starting at the current widget as root.
     */
    @Override
    public Iterator<Widget> iterator() {
        return new WidgetIterator(this);
    }


    //endregion


    //region Serializable functionality


    // Used to determine the class during serialization.
    private static final long serialVersionUID = 753654888555111999L;


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


    //region Object overrides


    @Override
    public String toString(){ return Util.treeDesc(this, 2, Tags.Role, Tags.Title); }


    //endregion


}