package org.testar.statemodel.persistence.orientdb.extractor;

import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exceptions.ExtractionException;
import org.testar.statemodel.persistence.orientdb.entity.DocumentEntity;

public interface EntityExtractor<T> {

    T extract(DocumentEntity entity, AbstractStateModel abstractStateModel) throws ExtractionException;

}
