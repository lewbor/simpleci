import $ from 'jquery';
import Selector from './Selector';
import Component from './Component';

class Builder {

    constructor(components) {
        this.components = components;
    }

    bootstrap(parameters) {
        var componentMap = this._buildComponentMap();
        $(document).on(Component.events.INIT_COMPONENTS, (event, node) => {
            var $node = $(node);
            var componentNodes = $node.find(Selector.component()).addBack(Selector.component());
            var componentDescriptions = [];

            $.each(componentNodes, function (idx, node) {
                var $node = $(node);
                componentDescriptions.push({
                    node: $node,
                    name: Selector.componentName($node)
                });
            });

            function getComponentHierarhy(component) {
                var currentComponent = component;
                var hierarchy = [];
                while (currentComponent.id !== 'Component' && '__proto__' in currentComponent && currentComponent.__proto__ !== null) {
                    hierarchy.push(currentComponent);
                    currentComponent = currentComponent.__proto__;
                }
                return hierarchy;
            }

            $.each(componentDescriptions, (idx, componentDescr) => {
                var nodeComponentNames = componentDescr.name.split(' ');
                var fullNodeNames = nodeComponentNames;
                var nodeComponents = [];

                $.each(nodeComponentNames, (idx, componentName) => {
                    if (componentName in componentMap) {
                        var component = componentMap[componentName];
                        var componentHierarchy = getComponentHierarhy(component);
                        var componentHierachyNames = componentHierarchy.map(hirComponent => Builder._componentName(hirComponent));
                        nodeComponents.push(component);
                        fullNodeNames = fullNodeNames.concat(componentHierachyNames);
                    } else {
                        console.log(`Component ${componentName} does not register in component list`);
                    }
                });

                componentDescr.components = nodeComponents;
                componentDescr.fullNodeNames = $.unique(fullNodeNames);
            });

            // Doing separate pass to make component find each other while initializing
            $.each(componentDescriptions, function (idx, componentDescr) {
                componentDescr.node.attr('data-widget', componentDescr.fullNodeNames.join(' '));
            });

            $.each(componentDescriptions, function (idx, componentDescr) {
                $.each(componentDescr.components, function (idx, component) {
                    console.log('Attach component ' + component.name);
                    let componentInstance = new component(componentDescr.node);
                    console.log(`Component ${Builder._componentName(component)} had been initialized`);
                });
            });

            $.each(componentDescriptions, function (idx, componentDescr) {
                componentDescr.node.trigger(Component.events.AFTER_INIT_ALL_COMPONENTS);
            });


            if ('afterComponentCreated' in parameters) {
                parameters.afterComponentCreated.call(null, $node);
            }

        });
        return this;
    }


    init($node) {
        $(document).trigger(Component.events.INIT_COMPONENTS, $node);
        return this;
    }


    static _componentName(component) {
        let className = component.id;
        return className.replace(/\.?([A-Z])/g, function (x, y) {
            return "_" + y.toLowerCase()
        }).replace(/^_/, "");
    }

    _buildComponentMap() {
        var componentMap = {};
        $.each(this.components, (idx, component) => {
            var name = Builder._componentName(component);
            if (name in componentMap) {
                throw new Error('Component ' + name + ' was already registered');
            }
            componentMap[name] = component;
        });
        return componentMap;
    }

}

export default Builder;
