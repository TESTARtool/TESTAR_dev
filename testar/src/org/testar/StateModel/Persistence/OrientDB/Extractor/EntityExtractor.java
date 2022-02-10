package org.testar.StateModel.Persistence.OrientDB.Extractor;

import org.testar.StateModel.AbstractStateModel;
import org.testar.StateModel.Exception.ExtractionException;
import org.testar.StateModel.Persistence.OrientDB.Entity.DocumentEntity;

public interface EntityExtractor<T> {

    T extract(DocumentEntity entity, AbstractStateModel abstractStateModel) throws ExtractionException;

}
