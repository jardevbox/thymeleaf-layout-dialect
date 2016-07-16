/*
 * Copyright 2013, Emanuel Rabina (http://www.ultraq.net.nz/)
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
package nz.net.ultraq.thymeleaf.decorators.html;

import nz.net.ultraq.thymeleaf.decorators.Decorator;
import nz.net.ultraq.thymeleaf.models.AttributeMerger;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;

/**
 * A decorator specific to processing an HTML {@code <body>} element.
 *
 * @author Emanuel Rabina
 */
@lombok.experimental.ExtensionMethod(nz.net.ultraq.thymeleaf.internal.MetaClass.class)
public class HtmlBodyDecorator implements Decorator {

    private final IModelFactory modelFactory;

    /**
     * Constructor, sets up the element decorator context.
     *
     * @param modelFactory
     */
    public HtmlBodyDecorator(IModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    /**
     * Decorate the {@code <body>} part.
     *
     * @param targetBodyModel
     * @param sourceBodyModel
     * @return Result of the decoration.
     */
    @Override
    public IModel decorate(IModel targetBodyModel, IModel sourceBodyModel) {
        // If one of the parameters is missing return a copy of the other, or
        // nothing if both parameters are missing.
        if (!targetBodyModel.asBoolean() || !sourceBodyModel.asBoolean()) {
            return targetBodyModel.asBoolean() ? targetBodyModel.cloneModel() : sourceBodyModel.asBoolean() ? sourceBodyModel.cloneModel() : null;
        }
        return new AttributeMerger(modelFactory).merge(targetBodyModel, sourceBodyModel);
    }

}
