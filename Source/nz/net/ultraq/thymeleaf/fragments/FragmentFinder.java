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
package nz.net.ultraq.thymeleaf.fragments;

import java.util.LinkedHashMap;
import java.util.Map;
import nz.net.ultraq.thymeleaf.includes.IncludeProcessor;
import nz.net.ultraq.thymeleaf.includes.ReplaceProcessor;
import nz.net.ultraq.thymeleaf.internal.MetaClass;
import nz.net.ultraq.thymeleaf.models.ModelFinder;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.ElementDefinition;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Searches for and returns layout dialect fragments within a given
 * scope/element.
 *
 * @author Emanuel Rabina
 */
public class FragmentFinder {

    private final ModelFinder modelFinder;
    private final String dialectPrefix;

    /**
     * Constructor, create a new fragment finder for the context and template
     * mode.
     *
     * @param context
     * @param templateMode
     * @param dialectPrefix
     */
    public FragmentFinder(ITemplateContext context, TemplateMode templateMode, String dialectPrefix) {
        this(new ModelFinder(context, templateMode), dialectPrefix);
    }

    /**
     * Constructor, create a new fragment finder using an existing model finder.
     *
     * @param modelFinder
     * @param dialectPrefix
     */
    public FragmentFinder(ModelFinder modelFinder, String dialectPrefix) {
        this.modelFinder = modelFinder;
        this.dialectPrefix = dialectPrefix;
    }

    /**
     * Find and return models for layout dialect fragments within the scope of
     * the given element, without delving into {@code layout:include} or
     * {@code layout:replace} elements, mapped by the name of each fragment.
     *
     * @param templateName Name of the current template
     * @param model Element whose children are to be searched.
     * @return Map of fragment names and their elements.
     */
    @SuppressWarnings("unchecked")
    public Map<String, TemplateModel> findFragments(String templateName, IModel model) {
        Map<String, TemplateModel> fragments = new LinkedHashMap<>();

        // TODO: Replace with some way of extracting models from models
        // NOTE: Using element definitions to match open and close tags, probably
        //       not going to work...  Other options include counting the level
        //       we're at.
        ElementDefinition[] insideLayoutElementDefinition = {null};

        MetaClass.each(model, e -> {
            if (e instanceof IOpenElementTag) {
                IOpenElementTag event = (IOpenElementTag) e;
                if (insideLayoutElementDefinition[0] == null) {
                    IAttribute fragmentAttribute = event.getAttribute(dialectPrefix, FragmentProcessor.PROCESSOR_NAME);
                    if (fragmentAttribute != null) {
                        String fragmentName = fragmentAttribute.getValue();
                        ((Map) fragments).put(fragmentName, modelFinder.findFragment(templateName, fragmentName, dialectPrefix));
                    }
                    if (isLayoutElement(event)) {
                        insideLayoutElementDefinition[0] = event.getElementDefinition();
                    }
                }
            } else if (e instanceof ICloseElementTag) {
                ICloseElementTag event = (ICloseElementTag) e;
                if (insideLayoutElementDefinition[0] == event.getElementDefinition()) {
                    insideLayoutElementDefinition[0] = null;
                }
            }
        });

        return fragments;
    }

    private boolean isLayoutElement(IProcessableElementTag elementTag) {
        return elementTag.hasAttribute(dialectPrefix, IncludeProcessor.PROCESSOR_NAME)
                || elementTag.hasAttribute(dialectPrefix, ReplaceProcessor.PROCESSOR_NAME);
    }

}
