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
     */
    @Override
    public void merge(IModel targetModel, IModel sourceModel) {
        // Because we're basically replacing targetModel with sourceModel, we'll
        // lose the attributes in the target.  So, create a copy of those attributes
        // for that merge after.
        IModel targetInitialRootElement = modelFactory.createModel(targetModel.get(0));

        // TODO: Shouldn't all this be done with the structureHandler?  I should
        //       make another code branch that does that, and then I can compare.
        // Replace the target model with the source model
        MetaClass.replaceModel(targetModel, sourceModel);

        new AttributeMerger(modelFactory).merge(targetModel, targetInitialRootElement);
    }

}
