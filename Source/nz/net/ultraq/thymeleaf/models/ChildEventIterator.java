/*
 * Copyright 2016, Emanuel Rabina (http://www.ultraq.net.nz/)
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

import java.util.Iterator;
import nz.net.ultraq.thymeleaf.internal.MetaProvider;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.ITemplateEvent;

/**
 * An iterator over a model's child events, if that model represents an element
 * with open/close tags at either end.
 *
 * Models returned by this iterator are also aware of their position within the
 * event queue of the parent model, accessible via their {@code index} property.
 *
 * @author Emanuel Rabina
 */
public class ChildEventIterator implements Iterator<ITemplateEvent> {

    private final IModel parent;

    private int currentIndex = 1;  // Starts after the root element

    /**
     * Constructor, sets the model to iterate over.
     *
     * @param parent
     */
    public ChildEventIterator(IModel parent) {
        this.parent = parent;
    }

    /**
     * Returns whether or not there is another event to be retrieved.
     *
     * @return {@code true} if there are more events to process.
     */
    @Override
    public boolean hasNext() {
        return currentIndex < (parent.size() - 1);
    }

    /**
     * Returns the next event of this model.
     *
     * @return The next event.
     */
    @Override
    public ITemplateEvent next() {
        ITemplateEvent event = parent.get(currentIndex);
        MetaProvider.INSTANCE.setProperty(event, "index", currentIndex++);
        return event;
    }

    /**
     * Not applicable for this iterator.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
