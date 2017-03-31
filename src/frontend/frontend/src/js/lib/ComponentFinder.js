import $ from 'jquery';
import Selector from './Selector';


var parentComponent = function ($node, componentName) {
    return $node.closest(Selector.component(componentName));
};

var allChildrenComponents = function ($node) {
    var node = $node.get(0);
    var components = [];

    function traversal(node) {
        for (var i = 0; i < node.childNodes.length; i++) {
            var childElement = node.childNodes[i];
            if (childElement.nodeType != 1) {
                continue;
            }
            if (childElement.hasAttribute('data-widget')) {
                components.push($(childElement));
                break;
            }
            traversal(childElement);
        }
    }

    traversal(node);
    return components;
};

var namedChildrenComponents = function ($node, componentName) {
    var node = $node.get(0);
    var components = [];

    function traversal(node) {
        for (var i = 0; i < node.childNodes.length; i++) {
            var childElement = node.childNodes[i];
            if (childElement.nodeType != 1) {
                continue;
            }
            if (childElement.hasAttribute('data-widget')) {
                var $component = $(childElement);
                if ($component.is(Selector.component(componentName))) {
                    components.push($component);
                    continue;
                }
                if (!(childElement.hasAttribute('role') && childElement.getAttribute('role') == 'presentation')) {
                    break;
                }
            }
            traversal(childElement);
        }
    }

    traversal(node);
    return components;
};

var childrenComponents = function () {
    if (arguments.length == 1) {
        return allChildrenComponents(arguments[0]);
    } else if (arguments.length == 2) {
        return namedChildrenComponents(arguments[0], arguments[1]);
    } else {
        throw new Error('Unknown variant of function');
    }

};

var singleChildren = function ($node, componentName) {
    var components = childrenComponents($node, componentName);
    if (components.length != 1) {
        return null;
    }
    return components[0];
};


var ComponentFinder = {
    all_childrens: childrenComponents,
    children: singleChildren,
    parent: parentComponent
};

export default ComponentFinder;

