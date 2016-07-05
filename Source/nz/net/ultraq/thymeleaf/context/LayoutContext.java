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
package nz.net.ultraq.thymeleaf.context;

import java.util.HashMap;
import lombok.SneakyThrows;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;

/**
 * Map of data and values that are passed around the layout dialect, exposed so
 * that others can make use of the properties.
 *
 * @author Emanuel Rabina
 */
@SuppressWarnings({"serial", "CloneableImplementsClone"})
public class LayoutContext extends HashMap<String, Object> {

    private static final String CONTEXT_KEY = "layout";

    /**
     * Retrieve the layout dialect context specific to the given Thymeleaf
     * context. If none exists, a new collection is created, applied to the
     * Thymeleaf context, and returned.
     *
     * @param context
     * @return A new or existing layout dialect context for the context.
     */
    @SneakyThrows
    public static LayoutContext forContext(IContext context) {
        Object dialectContext = context.getVariable(CONTEXT_KEY);

        // Error if something has already taken this value.  Hopefully there aren't
        // any collisions, but this name isn't exactly rare, so it *just* might
        // happen.
        if (dialectContext != null) {
            try {
                return (LayoutContext) dialectContext;
            } catch (ClassCastException ex) {
                throw new Exception(
                        "Name collision on the Thymeleaf processing context.  "
                        + "An object with the key \"layout\" already exists, but is needs to be free for the Layout Dialect to work."
                );
            }
        }

        LayoutContext layoutContext = new LayoutContext();
        ((IEngineContext) context).setVariable(CONTEXT_KEY, layoutContext);
        return layoutContext;
    }

}
