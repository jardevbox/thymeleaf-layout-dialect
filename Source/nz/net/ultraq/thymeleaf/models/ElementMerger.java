/*
 * Copyright 2015, Emanuel Rabina (http://www.ultraq.net.nz/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nz.net.ultraq.thymeleaf.models;

import nz.net.ultraq.thymeleaf.internal.MetaClass;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;

/**
 * Merges an element and all its children into an existing element.
 *
 * @author Emanuel Rabina
 */
public class ElementMerger implements ModelMerger {

    private final IModelFactory modelFactory;

    /**
     * Constructor, sets up the attribute merger tools.
     *
     * @param modelFactory
     */
    public ElementMerger(IModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    /**
     * Replace the content of the target element, with the content of the source
     * element.
     *
     * @param targetModel
     * @param sourceModel
     * @return Model that is the result of the merge.
     */
    @Override
    public IModel merge(IModel targetModel, IModel sourceModel) {
        // If one of the parameters is missing return a copy of the other, or
        // nothing if both parameters are missing.
        if (!MetaClass.asBoolean(targetModel) || !MetaClass.asBoolean(sourceModel)) {
            return MetaClass.asBoolean(targetModel) ? targetModel.cloneModel() : MetaClass.asBoolean(sourceModel) ? sourceModel.cloneModel() : null;
        }

        // The result we want is basically the source model, but with the target
        // models root element attributes
        IModel targetInitialRootElement = modelFactory.createModel(MetaClass.first(targetModel));
        return new AttributeMerger(modelFactory).merge(sourceModel, targetInitialRootElement);
    }

}
