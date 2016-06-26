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
package nz.net.ultraq.thymeleaf;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import nz.net.ultraq.thymeleaf.decorators.DecorateProcessor;
import nz.net.ultraq.thymeleaf.decorators.DecoratorProcessor;
import nz.net.ultraq.thymeleaf.decorators.SortingStrategy;
import nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor;
import nz.net.ultraq.thymeleaf.decorators.strategies.AppendingStrategy;
import nz.net.ultraq.thymeleaf.fragments.FragmentProcessor;
import nz.net.ultraq.thymeleaf.includes.InsertProcessor;
import nz.net.ultraq.thymeleaf.includes.ReplaceProcessor;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Dialect for making use of template/layout decorator pages with Thymeleaf.
 *
 * @author Emanuel Rabina
 */
public class LayoutDialect extends AbstractProcessorDialect {

    public static final String DIALECT_NAME = "Layout";
    public static final String DIALECT_PREFIX = "layout";
    public static final int DIALECT_PRECEDENCE = 10;

    private final SortingStrategy sortingStrategy;

    /**
     * Constructor, configure the layout dialect with the given values.
     *
     * @param sortingStrategy
     */
    public LayoutDialect(SortingStrategy sortingStrategy) {
        super(DIALECT_NAME, DIALECT_PREFIX, DIALECT_PRECEDENCE);
        this.sortingStrategy = sortingStrategy;
    }

    public LayoutDialect() {
        this(new AppendingStrategy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        // TODO: Many of the underlying classes don't respect the runtime-configured
        //       dialect prefix, so I'll need to do something about that later.
        //       https://github.com/ultraq/thymeleaf-layout-dialect/issues/103

        return new LinkedHashSet<>(Arrays.<IProcessor>asList(
                // Processors available in the HTML template mode
                new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix),
                new DecorateProcessor(TemplateMode.HTML, dialectPrefix, sortingStrategy),
                new DecoratorProcessor(TemplateMode.HTML, dialectPrefix, sortingStrategy),
                new nz.net.ultraq.thymeleaf.includes.IncludeProcessor(TemplateMode.HTML, dialectPrefix),
                new InsertProcessor(TemplateMode.HTML, dialectPrefix),
                new ReplaceProcessor(TemplateMode.HTML, dialectPrefix),
                new FragmentProcessor(TemplateMode.HTML, dialectPrefix),
                new TitlePatternProcessor(TemplateMode.HTML, dialectPrefix),
                // Processors available in the XML template mode
                new StandardXmlNsTagProcessor(TemplateMode.XML, dialectPrefix),
                new DecorateProcessor(TemplateMode.XML, dialectPrefix, sortingStrategy),
                new DecoratorProcessor(TemplateMode.XML, dialectPrefix, sortingStrategy),
                new nz.net.ultraq.thymeleaf.includes.IncludeProcessor(TemplateMode.XML, dialectPrefix),
                new InsertProcessor(TemplateMode.XML, dialectPrefix),
                new ReplaceProcessor(TemplateMode.XML, dialectPrefix),
                new FragmentProcessor(TemplateMode.XML, dialectPrefix)
        ));
    }

}
