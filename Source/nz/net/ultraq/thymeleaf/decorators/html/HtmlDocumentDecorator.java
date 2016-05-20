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
import nz.net.ultraq.thymeleaf.internal.MetaClass;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;

/**
 * A decorator made to work over whole HTML pages. Decoration will be done in 2
 * phases: a special one for the HEAD element, and a generic one for the BODY
 * element.
 *
 * @author Emanuel Rabina
 */
public class HtmlDocumentDecorator extends XmlDocumentDecorator {

	final SortingStrategy sortingStrategy;

	public HtmlDocumentDecorator(SortingStrategy sortingStrategy) {
		this.sortingStrategy = sortingStrategy;
	}

	/**
	 * Decorate an entire HTML page.
	 *
	 * @param decoratorHtml Decorator's HTML element.
	 * @param contentHtml	Content's HTML element.
	 */
	@Override
	public void decorate(Element decoratorHtml, Element contentHtml) {

		new HtmlHeadDecorator(sortingStrategy).decorate(decoratorHtml, MetaClass.findElement(contentHtml, "head"));
		new HtmlBodyDecorator().decorate(decoratorHtml, MetaClass.findElement(contentHtml, "body"));

		// Set the doctype from the decorator if missing from the content page
		Document decoratorDocument = (Document) decoratorHtml.getParent();
		Document contentDocument = (Document) contentHtml.getParent();
		if (contentDocument.getDocType() == null && decoratorDocument.getDocType() != null) {
			contentDocument.setDocType(decoratorDocument.getDocType());
		}

		super.decorate(decoratorHtml, contentHtml);
	}
}
