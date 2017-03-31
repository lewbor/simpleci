import $ from 'jquery';
import ComponentFinder from './ComponentFinder';
import PropertyParser from './PropertyParser';

var EVENTS = {
    INIT_COMPONENTS: 'init_components',
    AFTER_INIT_ALL_COMPONENTS: 'after_init_all_components',
    REMOVE: 'remove'
};

class Component {
    constructor($node) {
        this.$node = $node;
        this.$node.on(EVENTS.REMOVE, (e) => {
            e.stopPropagation();
            this.destroy();
        });

        var parameters = $node.data();
        delete parameters.widget;
        this.props = this.parseParameters(parameters);

        this.init();
    }

    static get events() {
        return EVENTS;
    }

    static get id() {
        return 'Component';
    }

    parseParameters(parameters) {
        return PropertyParser.parse(parameters, this.propTypes());
    }

    propTypes() {
        // abstract method
        return {};
    }

    init() {
        // abstract method
    }

    destroy() {
        let children = ComponentFinder.all_childrens(this.$node);
        children.forEach(function ($componentNode) {
            $componentNode.triggerHandler(EVENTS.REMOVE);
        });
        this.$node.unbind(EVENTS.REMOVE);
        this.$node.remove();
        console.log(`Destroyed component ${this.constructor.name}`);
    }

}

export default Component;