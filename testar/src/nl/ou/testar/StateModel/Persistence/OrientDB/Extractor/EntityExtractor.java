package nl.ou.testar.StateModel.Persistence.OrientDB.Extractor;

import nl.ou.testar.StateModel.Exception.ExtractionException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.DocumentEntity;

public interface EntityExtractor<T> {

    public T extract(DocumentEntity entity) throws ExtractionException;

}
