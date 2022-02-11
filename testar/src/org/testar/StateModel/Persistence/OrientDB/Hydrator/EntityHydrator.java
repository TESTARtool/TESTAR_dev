package org.testar.statemodel.persistence.orientdb.Hydrator;

import org.testar.statemodel.exception.HydrationException;
import org.testar.statemodel.persistence.orientdb.Entity.DocumentEntity;

public interface EntityHydrator<T extends DocumentEntity> {

    /**
     * This method will populate the target object with data extracted from the source object.
     * @param target
     * @param source
     * @return
     */
    public void hydrate(T target, Object source) throws HydrationException;

}
