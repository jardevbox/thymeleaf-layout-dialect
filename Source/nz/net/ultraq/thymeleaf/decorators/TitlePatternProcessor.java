/*
 * Copyright 2012, Emanuel Rabina (http://www.ultraq.net.nz/)
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
package nz.net.ultraq.thymeleaf.decorators;

import java.util.concurrent.atomic.AtomicBoolean;
import nz.net.ultraq.thymeleaf.context.LayoutContext;
import nz.net.ultraq.thymeleaf.expressions.ExpressionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;
import org.unbescape.html.HtmlEscape;

/**
 * Allows for greater control of the resulting {@code <title>} element by
 * specifying a pattern with some special tokens. This can be used to extend the
 * layout's title with the content's one, instead of simply overriding it.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class TitlePatternProcessor extends AbstractAttributeTagProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TitlePatternProcessor.class);

    @Deprecated
    private static final String PARAM_TITLE_DECORATOR = "$DECORATOR_TITLE";
    private static final String PARAM_TITLE_CONTENT = "$CONTENT_TITLE";
    private static final String PARAM_TITLE_LAYOUT = "$LAYOUT_TITLE";

    private static final AtomicBoolean warned = new AtomicBoolean();

    public static final String PROCESSOR_NAME = "title-pattern";
    public static final int PROCESSOR_PRECEDENCE = 1;

    public static final String CONTEXT_RESULTING_TITLE = "resultingTitle";

    public static final String CONTENT_TITLE_ATTRIBUTE = "data-layout-content-title";
    public static final String LAYOUT_TITLE_ATTRIBUTE = "data-layout-layout-title";

    private static String titleProcessor(String dataAttributeName, IProcessableElementTag tag, IElementTagStructureHandler structureHandler, ExpressionProcessor expressionProcessor) {
        String titleExpression = tag.getAttributeValue(dataAttributeName);
        if (!StringUtils.isEmpty(titleExpression)) {
            structureHandler.removeAttribute(dataAttributeName);
            return HtmlEscape.unescapeHtml(expressionProcessor.processAsString(titleExpression));
        }
        return null;
    }

    /**
     * Constructor, sets this processor to work on the 'title-pattern'
     * attribute.
     *
     * @param templateMode
     * @param dialectPrefix
     */
    public TitlePatternProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, null, false, PROCESSOR_NAME, true, PROCESSOR_PRECEDENCE, true);
    }

    /**
     * Process the {@code layout:title-pattern} directive, replaces the title
     * text with the titles from the content and layout pages.
     *
     * @param context
     * @param tag
     * @param attributeName
     * @param attributeValue
     * @param structureHandler
     */
    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
            AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        // Ensure this attribute is only on the <title> element
        if (!"title".equals(tag.getElementCompleteName())) {
            throw new IllegalArgumentException(attributeName + " processor should only appear in a <title> element");
        }

        String titlePattern = attributeValue;
        ExpressionProcessor expressionProcessor = new ExpressionProcessor(context);

        String contentTitle = titleProcessor(CONTENT_TITLE_ATTRIBUTE, tag, structureHandler, expressionProcessor);
        String layoutTitle = titleProcessor(LAYOUT_TITLE_ATTRIBUTE, tag, structureHandler, expressionProcessor);

        if (warned.compareAndSet(false, true) && !StringUtils.isEmpty(titlePattern) && titlePattern.contains(PARAM_TITLE_DECORATOR)) {
            logger.warn(
                    "The $DECORATOR_TITLE token is deprecated and will be removed in the next major version of the layout dialect.  "
                    + "Please use the $LAYOUT_TITLE token instead to future-proof your code.  "
                    + "See https://github.com/ultraq/thymeleaf-layout-dialect/issues/95 for more information."
            );
        }

        String title;
        if (!StringUtils.isEmpty(titlePattern) && !StringUtils.isEmpty(layoutTitle) && !StringUtils.isEmpty(contentTitle)) {
            title = titlePattern
                    .replace(PARAM_TITLE_LAYOUT, layoutTitle)
                    .replace(PARAM_TITLE_DECORATOR, layoutTitle)
                    .replace(PARAM_TITLE_CONTENT, contentTitle);
        } else {
            title = !StringUtils.isEmpty(contentTitle) ? contentTitle : !StringUtils.isEmpty(layoutTitle) ? layoutTitle : "";
        }

        structureHandler.setBody(title, false);

        // Save the title to the layout context
        LayoutContext layoutContext = LayoutContext.forContext(context);
        layoutContext.put(CONTEXT_RESULTING_TITLE, title);
    }

}
