export default class Selector {
    static component() {
        if (arguments.length == 0) {
            return '[data-widget]';
        } else if (arguments.length == 1) {
            return '[data-widget~=' + arguments[0] + ']';
        } else {
            throw new Error('Unknown variant of function called');
        }

    }

    static componentName($node) {
        return $node.data('widget');
    }
}


