package org.testar.statemodelling.persistence.orientdb.Hydrator;

import org.testar.statemodelling.exception.HydrationException;
import org.testar.statemodelling.persistence.orientdb.Entity.DocumentEntity;

public interface EntityHydrator<T extends DocumentEntity> {

    /**
     * This method will populate the target object with data extracted from the source object.
     * @param target
     * @param source
     * @return
     */
    public void hydrate(T target, Object source) throws HydrationException;

}
