package nz.net.ultraq.thymeleaf.internal;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.thymeleaf.dom.AbstractTextNode;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;

/**
 *
 * @author zhanhb
 */
public class MetaClass {

	public static <T> int lastIndexOf(List<T> list, Predicate<? super T> match) {
		ListIterator<T> it = list.listIterator(list.size());

		while (it.hasPrevious()) {
			if (match.test(it.previous())) {
				return it.nextIndex();
			}
		}
		return -1;
	}

	/**
	 * Searches this and all children of this element for an element of the
	 * given name.
	 *
	 * @param name
	 * @return The matching element, or <tt>null</tt> if no match was found.
	 */
	public static Element findElement(Element delegate, String name) {
		Function<Element, Element>[] search = new Function[1];
		search[0] = element -> {
			if (Objects.equals(element.getOriginalName(), name)) {
				return element;
			}
			return element.getElementChildren().stream().map(search[0]).filter(Objects::nonNull)
					.findFirst().orElse(null);
		};
		return search[0].apply(delegate);
	}

	/**
	 * Returns an attribute processor's value, checks both prefix:processor and
	 * data-prefix-processor variants.
	 *
	 * @param prefix
	 * @param name
	 * @return The value of the matching processor, or <tt>null</tt> if the
	 * processor doesn't exist on the element.
	 */
	public static String getAttributeValue(Element delegate, String prefix, String name) {
		String attributeValue = delegate.getAttributeValue(prefix + ":" + name);
		if (attributeValue == null || attributeValue.isEmpty()) {
			attributeValue = delegate.getAttributeValue("data-" + prefix + "-" + name);
		}
		return attributeValue;
	}

	/**
	 * Removes an attribute processor from this element, checks both
	 * prefix:processor and data-prefix-processor variants.
	 *
	 * @param prefix
	 * @param name
	 */
	public static void removeAttribute(Element delegate, String prefix, String name) {
		delegate.removeAttribute(prefix + ":" + name);
		delegate.removeAttribute("data-" + prefix + "-" + name);
	}

	/**
	 * Inserts a child node, creating a whitespace node before it so that it
	 * appears in line with all the existing children.
	 *
	 * @param child Node to add.
	 * @param index Node position.
	 */
	public static void insertChildWithWhitespace(Element delegate, Node child, int index) {
		if (child != null) {
			NestableNode parent = delegate.getParent();
			Text whitespace;
			if (parent instanceof Document) {
				whitespace = new Text("\n\t");
			} else {
				List<Node> parentChildren = parent.getChildren();
				Node get = parentChildren.get(parentChildren.indexOf(delegate) - 1);
				whitespace = new Text(((AbstractTextNode) get).getContent() + '\t');
			}
			delegate.insertChild(index, whitespace);
			delegate.insertChild(index + 1, child);
		}
	}

	/**
	 * Removes a child node and the whitespace node immediately before so that
	 * the area doesn't appear too messy.
	 *
	 * @param child Node to remove
	 */
	public static void removeChildWithWhitespace(Element delegate, Node child) {
		if (child != null) {
			List<Node> children = delegate.getChildren();
			int index = children.indexOf(child);
			delegate.removeChild(index);
			if (index > 0) {
				delegate.removeChild(index - 1);
			}
		}
	}

	/**
	 * Returns whether or not this node represents collapsible whitespace.
	 *
	 * @return <tt>true</tt> if this is a collapsible text node.
	 */
	public static boolean isWhitespaceNode(Node delegate) {
		return delegate instanceof Text && ((Text) delegate).getContent().trim().isEmpty();
	}

	/**
	 * Returns whether or not an attribute is an attribute processor of the
	 * given name, checks both prefix:processor and data-prefix-processor
	 * variants.
	 *
	 * @param prefix
	 * @param name
	 * @return <tt>true</tt> if this attribute is an attribute processor of the
	 * matching name.
	 */
	public static boolean equalsName(Attribute delegate, String prefix, String name) {
		String originalName = delegate.getOriginalName();
		return Objects.equals(originalName, prefix + ":" + name) || Objects.equals(originalName, "data-" + prefix + "-" + name);
	}

	/**
	 * Returns whether or not the element has an attribute processor, checks
	 * both prefix:processor and data-prefix-processor variants.
	 *
	 * @param prefix
	 * @param name
	 * @return <tt>true</tt> if the processor exists on the element.
	 */
	public static boolean hasAttribute(Element delegate, String prefix, String name) {
		return delegate.hasAttribute(prefix + ":" + name)
				|| delegate.hasAttribute("data-" + prefix + "-" + name);
	}

}