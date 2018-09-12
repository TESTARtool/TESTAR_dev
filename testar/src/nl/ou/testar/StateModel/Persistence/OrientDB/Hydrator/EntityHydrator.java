package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.DocumentEntity;

public interface EntityHydrator<T extends DocumentEntity> {

    /**
     * This method will populate the target object with data extracted from the source object.
     * @param target
     * @param source
     * @return
     */
    public void hydrate(T target, Object source) throws HydrationException;

}
