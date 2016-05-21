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

import nz.net.ultraq.thymeleaf.decorators.xml.XmlElementDecorator;
import nz.net.ultraq.thymeleaf.internal.MetaClass;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Text;

/**
 * A decorator specific to processing an HTML BODY element.
 *
 * @author Emanuel Rabina
 */
public class HtmlBodyDecorator extends XmlElementDecorator {

    /**
     * Decorate the BODY part. This step merges the decorator and content BODY
     * attributes, ensuring only that a BODY element actually exists in the
     * result. The bulk of the body decoration is actually performed by the
     * fragment replacements.
     *
     * @param decoratorHtml Decorator's HTML element.
     * @param contentBody	Content's BODY element.
     */
    @Override
    public void decorate(Element decoratorHtml, Element contentBody) {
        // If the page has no BODY, then we don't need to do anything
        if (contentBody == null) {
            return;
        }

        // If the decorator has no BODY, we can just copy the page BODY
        Element decoratorBody = MetaClass.findElement(decoratorHtml, "body");
        if (decoratorBody == null) {
            decoratorHtml.addChild(contentBody);
            decoratorHtml.addChild(new Text(System.getProperty("line.separator")));
            return;
        }

        super.decorate(decoratorBody, contentBody);
    }

}
