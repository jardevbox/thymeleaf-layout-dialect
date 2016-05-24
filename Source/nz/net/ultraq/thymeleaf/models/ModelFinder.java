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

import java.util.Collections;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.model.IModel;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;

/**
 * Hides all the scaffolding business required to retrieve models.
 *
 * @author Emanuel Rabina
 */
public class ModelFinder {

    final ITemplateContext context;
    final TemplateMode templateMode;

    /**
     * Constructor, set the template context we're working in.
     *
     * @param context
     * @param templateMode
     */
    public ModelFinder(ITemplateContext context, TemplateMode templateMode) {
        this.context = context;
        this.templateMode = templateMode;
    }

    /**
     * Return a model for any arbitrary item in a template.
     *
     * @param templateName
     * @param selector A Thymeleaf DOM selector, which in turn is an AttoParser
     * DOM selector. See the Appendix in the Using Thymeleaf docs for the DOM
     * selector syntax.
     * @return Model for the selected template and element.
     */
    public IModel find(String templateName, String selector) {
        TemplateModel model = context.getConfiguration().getTemplateManager().parseStandalone(context,
                templateName, !StringUtils.isEmpty(selector) ? Collections.singleton(selector) : null, templateMode, true, true);

        // Clone model so that we have a mutable model instance.  Also strips the
        // template start/end events from the event queue.
        return model.cloneModel();
    }

    public IModel find(String templateName) {
        return find(templateName, null);
    }

    /**
     * Return the fragment model specified by the given fragment name
     * expression.
     *
     * @param templateName
     * @param fragmentName
     * @param dialectPrefix
     * @return Fragment matching the fragment specification.
     */
    public IModel findFragment(String templateName, String fragmentName, String dialectPrefix) {
        return find(templateName, "//[" + dialectPrefix + ":fragment='"
                + fragmentName + "' or data-" + dialectPrefix + "-fragment='"
                + fragmentName + "']");
    }

    /**
     * Return the template model specified by the given template name.
     *
     * @param templateName
     * @return Template model matching the fragment specification.
     */
    public IModel findTemplate(String templateName) {
        return find(templateName);
    }

}
