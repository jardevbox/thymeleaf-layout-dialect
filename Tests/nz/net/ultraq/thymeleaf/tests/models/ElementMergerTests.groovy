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

package nz.net.ultraq.thymeleaf.tests.models

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.models.ElementMerger
import nz.net.ultraq.thymeleaf.models.ModelBuilder

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.thymeleaf.TemplateEngine
import org.thymeleaf.model.IModelFactory
import org.thymeleaf.templatemode.TemplateMode
import static org.junit.Assert.*

/**
 * Tests for the element merger.
 * 
 * @author Emanuel Rabina
 */
class ElementMergerTests {

	private static ModelBuilder modelBuilder
	private static IModelFactory modelFactory

	private ElementMerger elementMerger

	/**
	 * Set up, create a template engine.
	 */
	@BeforeClass
	static void setupThymeleafEngine() {

		def templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
		modelFactory = templateEngine.configuration.getModelFactory(TemplateMode.HTML)
		modelBuilder = new ModelBuilder(modelFactory, templateEngine.configuration.elementDefinitions, TemplateMode.HTML)
	}

	/**
	 * Set up, create a new attribute merger.
	 */
	@Before
	void setupElementMerger() {

		elementMerger = new ElementMerger(modelFactory)
	}

	/**
	 * Test that the merger merges the source elements into the target.
	 */
	@Test
	void mergeElements() {

		def source = modelBuilder.build {
			section {
				header()
			}
		}
		def target = modelBuilder.build {
			div {
				p('Hello')
			}
		}

		def result = elementMerger.merge(target, source)
		assertTrue(source == result)
	}

	/**
	 * Test that the merger merges the source root element attributes into the
	 * target root element attributes.
	 */
	@Test
	void mergeRootAttributes() {

		def source = modelBuilder.build {
			div(id: 'source-id')
		}
		def target = modelBuilder.build {
			div(id: 'target-id')
		}

		def result = elementMerger.merge(target, source)
		assertTrue(source == result)
	}

	/**
	 * Test that the merging of the root element attributes doesn't mess with the
	 * root element type (a plain old attribute merger would do that).
	 */
	@Test
	void retainSourceShape() {

		def source = modelBuilder.build {
			section {
				header()
			}
		}
		def target = modelBuilder.build {
			div(standalone: true)
		}

		def result = elementMerger.merge(target, source)
		assertTrue(source == result)
	}
}
