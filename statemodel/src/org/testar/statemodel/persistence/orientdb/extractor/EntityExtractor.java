/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.extractor;

import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exceptions.ExtractionException;
import org.testar.statemodel.persistence.orientdb.entity.DocumentEntity;

public interface EntityExtractor<T> {

    T extract(DocumentEntity entity, AbstractStateModel abstractStateModel) throws ExtractionException;

}
