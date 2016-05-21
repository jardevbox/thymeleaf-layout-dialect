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
package nz.net.ultraq.thymeleaf;

import java.util.HashMap;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.VariablesMap;

/**
 * Map of data and values that are passed around the layout dialect, exposed so
 * that others can make use of the properties.
 *
 * @author Emanuel Rabina
 */
public class LayoutDialectContext extends HashMap<String, Object> {

    private static final String CONTEXT_KEY = "layout";

    /**
     * Retrieve the layout dialect context specific to the given Thymeleaf
     * context. If none exists, a new collection is created, applied to the
     * Thymeleaf context, and returned.
     *
     * @param context
     * @return A new or existing layout dialect context for the context.
     */
    public static LayoutDialectContext forContext(IContext context) {

        VariablesMap<String, Object> variables = context.getVariables();
        Object dialectContext = variables.get(CONTEXT_KEY);

        // Error if something has already taken this value.  Hopefully there
        // aren't any collisions, but this name isn't exactly rare, so it *just*
        // might happen.
        if (dialectContext != null && !(dialectContext instanceof LayoutDialectContext)) {
            throw new Error("Name collision on the Thymeleaf processing "
                    + "context.  An object with the key \"layout\" exists, but is needed "
                    + "by the Layout Dialect to work");
        }

        if (dialectContext == null) {
            dialectContext = new LayoutDialectContext();
            variables.put(CONTEXT_KEY, dialectContext);
        }

        return (LayoutDialectContext) dialectContext;
    }
}
