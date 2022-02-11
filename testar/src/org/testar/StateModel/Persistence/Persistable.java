package org.testar.statemodel.persistence;

public interface Persistable {

    /**
     * This method should return true if it is possible to delay persistence to after a test sequence has finished.
     * @return
     */
    boolean canBeDelayed();

}
