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
package nz.net.ultraq.thymeleaf.fragments;

import nz.net.ultraq.thymeleaf.expressions.ExpressionProcessor;
import nz.net.ultraq.thymeleaf.models.ElementMerger;
import nz.net.ultraq.thymeleaf.models.ModelExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * This processor serves a dual purpose: to mark sections of the template that
 * can be replaced, and to do the replacing when they're encountered.
 *
 * @author Emanuel Rabina
 */
public class FragmentProcessor extends AbstractAttributeModelProcessor {

	private static final Logger logger = LoggerFactory.getLogger(FragmentProcessor.class);

	public static final String PROCESSOR_NAME = "fragment";
	public static final int PROCESSOR_PRECEDENCE = 1;

	/**
	 * Constructor, sets this processor to work on the 'fragment' attribute.
	 *
	 * @param templateMode
	 * @param dialectPrefix
	 */
	public FragmentProcessor(TemplateMode templateMode, String dialectPrefix) {
		super(templateMode, dialectPrefix, null, false, PROCESSOR_NAME, true, PROCESSOR_PRECEDENCE, true);
	}

	/**
	 * Includes or replaces the content of fragments into the corresponding
	 * fragment placeholder.
	 *
	 * @param context
	 * @param model
	 * @param attributeName
	 * @param attributeValue
	 * @param structureHandler
	 */
	@Override
	protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName,
			String attributeValue, IElementModelStructureHandler structureHandler) {

		// Emit a warning if found in the <head> section
		if (getTemplateMode() == TemplateMode.HTML) {
			if (context.getElementStack().stream().anyMatch(element -> "head".equals(element.getElementCompleteName()))) {
				logger.warn("You don't need to put the layout:fragment attribute into the <head> "
						+ "section - the decoration process will automatically copy the <head> "
						+ "section of your content templates into your layout page.");
			}
		}

		// Locate the fragment that corresponds to this decorator/include fragment
		String fragmentName = new ExpressionProcessor(context).process(attributeValue);
		IModel fragment = FragmentMap.get(context).get(fragmentName);

		// Replace this model with the fragment
		if (ModelExtensions.asBoolean(fragment)) {
			new ElementMerger(context.getModelFactory()).merge(model, fragment);
		}
	}

}
