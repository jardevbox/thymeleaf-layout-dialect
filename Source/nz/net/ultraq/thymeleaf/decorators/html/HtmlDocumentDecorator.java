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

import nz.net.ultraq.thymeleaf.decorators.SortingStrategy;
import nz.net.ultraq.thymeleaf.decorators.xml.XmlDocumentDecorator;
import nz.net.ultraq.thymeleaf.internal.Extensions;
import nz.net.ultraq.thymeleaf.internal.ITemplateEventPredicate;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;

/**
 * A decorator made to work over an HTML document. Decoration for a document
 * involves 2 sub-decorators: a special one for the {@code <head>} element, and
 * a standard one for the {@code <body>} element.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class HtmlDocumentDecorator extends XmlDocumentDecorator {

    private final SortingStrategy sortingStrategy;

    /**
     * Constructor, apply the given sorting strategy to the decorator.
     *
     * @param context
     * @param sortingStrategy
     */
    public HtmlDocumentDecorator(ITemplateContext context, SortingStrategy sortingStrategy) {
        super(context);
        this.sortingStrategy = sortingStrategy;
    }

    /**
     * Decorate an entire HTML page.
     *
     * @param targetDocumentModel
     * @param sourceDocumentModel
     * @return Result of the decoration.
     */
    @Override
    public IModel decorate(IModel targetDocumentModel, IModel sourceDocumentModel) {
        IModelFactory modelFactory = context.getModelFactory();
        IModel resultDocumentModel = targetDocumentModel.cloneModel();
        // Head decoration
        ITemplateEventPredicate headModelFinder = event -> {
            return event instanceof IOpenElementTag && "head".equals(((IElementTag) event).getElementCompleteName());
        };
        IModel targetHeadModel = Extensions.findModel(resultDocumentModel, headModelFinder);
        IModel resultHeadModel = new HtmlHeadDecorator(context, sortingStrategy).decorate(targetHeadModel,
                Extensions.findModel(sourceDocumentModel, headModelFinder)
        );
        if (Extensions.asBoolean(resultHeadModel)) {
            if (Extensions.asBoolean(targetHeadModel)) {
                Extensions.replaceModel(resultDocumentModel, Extensions.indexOf(resultDocumentModel, targetHeadModel), resultHeadModel);
            } else {
                Extensions.insertModelWithWhitespace(resultDocumentModel, Extensions.findIndexOf(resultDocumentModel, event -> {
                    return (event instanceof IOpenElementTag && "body".equals(((IElementTag) event).getElementCompleteName()))
                            || (event instanceof ICloseElementTag && "html".equals(((IElementTag) event).getElementCompleteName()));
                }) - 1, resultHeadModel, modelFactory);
            }
        }

        // Body decoration
        ITemplateEventPredicate bodyModelFinder = event -> {
            return event instanceof IOpenElementTag && "body".equals(((IElementTag) event).getElementCompleteName());
        };
        IModel targetBodyModel = Extensions.findModel(resultDocumentModel, bodyModelFinder);
        IModel resultBodyModel = new HtmlBodyDecorator(context).decorate(targetBodyModel,
                Extensions.findModel(sourceDocumentModel, bodyModelFinder)
        );
        if (Extensions.asBoolean(resultBodyModel)) {
            if (Extensions.asBoolean(targetBodyModel)) {
                Extensions.replaceModel(resultDocumentModel, Extensions.indexOf(resultDocumentModel, targetBodyModel), resultBodyModel);
            } else {
                Extensions.insertModelWithWhitespace(resultDocumentModel, Extensions.findIndexOf(resultDocumentModel, event -> {
                    return event instanceof ICloseElementTag && "html".equals(((IElementTag) event).getElementCompleteName());
                }) - 1, resultBodyModel, modelFactory);
            }
        }

        return super.decorate(resultDocumentModel, sourceDocumentModel);
    }

}
