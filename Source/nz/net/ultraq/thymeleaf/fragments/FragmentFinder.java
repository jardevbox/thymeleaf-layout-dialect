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
package nz.net.ultraq.thymeleaf.fragments;

import java.util.List;
import org.thymeleaf.Arguments;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.dom.Node;
import org.thymeleaf.standard.fragment.StandardFragment;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;

import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX_LAYOUT;
import static nz.net.ultraq.thymeleaf.fragments.FragmentProcessor.PROCESSOR_NAME_FRAGMENT;

/**
 * Hides all the scaffolding business required to find a fragment or fragment
 * template.
 *
 * @author Emanuel Rabina
 */
public class FragmentFinder {

    final Arguments arguments;

    public FragmentFinder(Arguments arguments) {
        this.arguments = arguments;
    }

    /**
     * Computes the fragment for the given fragment spec, returning the
     * Thymeleaf fragment object representing a fragment.
     *
     * @param fragmentSpec
     * @return Thymeleaf fragment object.
     */
    private StandardFragment computeFragment(String fragmentSpec) {

        return StandardFragmentProcessor.computeStandardFragmentSpec(
                arguments.getConfiguration(), arguments, fragmentSpec,
                DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_FRAGMENT);
    }

    /**
     * Returns the fragment(s) specified by the given fragment spec string.
     *
     * @param fragmentSpec
     * @return List of fragment nodes matching the fragment specification.
     */
    public List<Node> findFragments(String fragmentSpec) {

        return computeFragment(fragmentSpec).extractFragment(
                arguments.getConfiguration(), arguments, arguments.getTemplateRepository());
    }

    /**
     * Return the template specified by the given fragment spec string.
     *
     * @param fragmentSpec
     * @return Template matching the fragment specification.
     */
    public Template findFragmentTemplate(String fragmentSpec) {
        return arguments.getTemplateRepository().getTemplate(new TemplateProcessingParameters(
                arguments.getConfiguration(), computeFragment(fragmentSpec).getTemplateName(), arguments.getContext()));
    }

}
