package org.testar.statemodel.persistence.orientdb.Extractor;

import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exception.ExtractionException;
import org.testar.statemodel.persistence.orientdb.Entity.DocumentEntity;

public interface EntityExtractor<T> {

    T extract(DocumentEntity entity, AbstractStateModel abstractStateModel) throws ExtractionException;

}
