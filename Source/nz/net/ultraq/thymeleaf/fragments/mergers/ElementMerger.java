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
package nz.net.ultraq.thymeleaf.fragments.mergers;

import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;

/**
 * Merges an element and all its children into an existing element.
 *
 * @author Emanuel Rabina
 */
public class ElementMerger extends AttributeMerger {

	/**
	 * Flag for indicating that the merge is over a root element, in which some
	 * special rules apply.
	 */
	private final boolean rootElementMerge;

	public ElementMerger() {
		this(false);
	}

	public ElementMerger(boolean rootElementMerge) {
		this.rootElementMerge = rootElementMerge;
	}

	/**
	 * Replace the content of the target element, with the content of the source
	 * element.
	 *
	 * @param targetElement
	 * @param sourceElement
	 */
	@Override
	public void merge(Element targetElement, Element sourceElement) {

		// Create a new merged element to mess with
		Node mergedNode = sourceElement.cloneNode(null, false);
		if (!rootElementMerge) {
			Element mergedElement = (Element) mergedNode;
			mergedElement.clearAttributes();
			targetElement.getAttributeMap().values().forEach(attribute
					-> mergedElement.setAttribute(attribute.getNormalizedName(), attribute.getValue())
			);
			super.merge(mergedElement, sourceElement);
		}
		targetElement.clearChildren();
		targetElement.addChild(mergedNode);
		targetElement.getParent().extractChild(targetElement);
	}

}