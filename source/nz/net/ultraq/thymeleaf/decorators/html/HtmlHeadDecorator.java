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

import java.util.Iterator;
import nz.net.ultraq.thymeleaf.decorators.Decorator;
import nz.net.ultraq.thymeleaf.decorators.SortingStrategy;
import nz.net.ultraq.thymeleaf.internal.Extensions;
import nz.net.ultraq.thymeleaf.internal.ITemplateEventPredicate;
import nz.net.ultraq.thymeleaf.models.AttributeMerger;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;

/**
 * A decorator specific to processing an HTML {@code <head>} element.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class HtmlHeadDecorator implements Decorator {

    private static IModel titleRetriever(IModel headModel, ITemplateEventPredicate isTitle) {
        return Extensions.asBoolean(headModel) ? Extensions.findModel(headModel, isTitle) : null;
    }

    private final ITemplateContext context;
    private final SortingStrategy sortingStrategy;

    /**
     * Constructor, sets up the decorator context.
     *
     * @param context
     * @param sortingStrategy
     */
    public HtmlHeadDecorator(ITemplateContext context, SortingStrategy sortingStrategy) {
        this.context = context;
        this.sortingStrategy = sortingStrategy;
    }

    /**
     * Decorate the {@code <head>} part.
     *
     * @param targetHeadModel
     * @param sourceHeadModel
     * @return Result of the decoration.
     */
    @Override
    @SuppressWarnings("deprecation")
    public IModel decorate(IModel targetHeadModel, IModel sourceHeadModel) {
        // If none of the parameters are present, return nothing
        if (!Extensions.asBoolean(targetHeadModel) && !Extensions.asBoolean(sourceHeadModel)) {
            return null;
        }

        IModelFactory modelFactory = context.getModelFactory();
        ITemplateEventPredicate isTitle = event -> Extensions.isOpeningElementOf(event, "title");

        // New head model based off the target being decorated
        IModel resultHeadModel = new AttributeMerger(context).merge(targetHeadModel, sourceHeadModel);

        // Get the source and target title elements to pass to the title decorator
        IModel resultTitle = new HtmlTitleDecorator(context).decorate(
                titleRetriever(targetHeadModel, isTitle),
                titleRetriever(sourceHeadModel, isTitle)
        );
        if (Extensions.asBoolean(resultTitle)) {

            // TODO: Pure hack for retaining 2.x compatibility, remove the <head> from the layout :/
            if (sortingStrategy instanceof nz.net.ultraq.thymeleaf.decorators.strategies.AppendingStrategy
                    || sortingStrategy instanceof nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy) {
                Extensions.removeModel(resultHeadModel, Extensions.findIndexOf(resultHeadModel, isTitle));
            }

            int targetTitleIndex = sortingStrategy.findPositionForModel(resultHeadModel, resultTitle);
            if (isTitle.test(resultHeadModel.get(targetTitleIndex))) {
                Extensions.replaceModel(resultHeadModel, targetTitleIndex, resultTitle);
            } else {
                Extensions.insertModelWithWhitespace(resultHeadModel, targetTitleIndex, resultTitle, modelFactory);
            }
        }

        // Merge the rest of the source <head> elements with the target <head>
        // elements using the current merging strategy
        if (Extensions.asBoolean(sourceHeadModel) && Extensions.asBoolean(targetHeadModel)) {
            for (Iterator<IModel> it = Extensions.childModelIterator(sourceHeadModel); it.hasNext();) {
                IModel model = it.next();
                if (isTitle.test(Extensions.first(model))) {
                    continue;
                }
                Extensions.insertModelWithWhitespace(resultHeadModel,
                        sortingStrategy.findPositionForModel(resultHeadModel, model),
                        model, modelFactory);
            }
        }
        return resultHeadModel;
    }

}
