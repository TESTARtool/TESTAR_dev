package org.testar.statemodelling.persistence.orientdb.Extractor;

import org.testar.statemodelling.AbstractStateModel;
import org.testar.statemodelling.exception.ExtractionException;
import org.testar.statemodelling.persistence.orientdb.Entity.DocumentEntity;

public interface EntityExtractor<T> {

    T extract(DocumentEntity entity, AbstractStateModel abstractStateModel) throws ExtractionException;

}
