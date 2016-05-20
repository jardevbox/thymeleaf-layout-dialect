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
package nz.net.ultraq.thymeleaf.includes;

import java.util.List;
import java.util.Map;
import nz.net.ultraq.thymeleaf.fragments.FragmentFinder;
import nz.net.ultraq.thymeleaf.fragments.FragmentMap;
import nz.net.ultraq.thymeleaf.fragments.FragmentMapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

/**
 * Similar to Thymeleaf's <tt>th:include</tt>, but allows the passing of entire
 * element fragments to the included template. Useful if you have some HTML that
 * you want to reuse, but whose contents are too complex to determine or
 * construct with context variables alone.
 *
 * @author Emanuel Rabina
 */
public class IncludeProcessor extends AbstractAttrProcessor {

	public static final String PROCESSOR_NAME_INCLUDE = "include";

	/**
	 * Constructor, sets this processor to work on the 'include' attribute.
	 */
	public IncludeProcessor() {

		super(PROCESSOR_NAME_INCLUDE);
	}

	/**
	 * Locates the specified page and includes it into the current template.
	 *
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return Result of the processing.
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Locate the page and fragment to include
		List<Node> includeFragments = new FragmentFinder(arguments)
				.findFragments(element.getAttributeValue(attributeName));

		// Gather all fragment parts within the include element, scoping them to
		// this element
		Map<String, Element> elementFragments = new FragmentMapper().map(element.getElementChildren());
		FragmentMap.updateForNode(arguments, element, elementFragments);

		// Replace the children of this element with those of the include page
		// fragments.  The 'container' element is copied from how Thymeleaf does
		// it's include processor, which is to maintain internal structures like
		// local variables
		element.clearChildren();
		if (includeFragments != null && !includeFragments.isEmpty()) {
			Element containerElement = new Element("container");
			includeFragments.forEach(includeFragment -> {
				containerElement.addChild(includeFragment);
				containerElement.extractChild(includeFragment);
			});
			containerElement.getChildren().forEach(extractedChild -> {
				element.addChild(extractedChild);
			});
		}

		element.removeAttribute(attributeName);
		return ProcessorResult.OK;
	}

	@Override
	public int getPrecedence() {
		return 1;
	}

}
