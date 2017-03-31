export default class PropertyParser {

    static parse(obj, schema) {
        let props = {};
        for (let propertyName in schema) {
            if (schema.hasOwnProperty(propertyName)) {
               props[propertyName] = PropertyParser.parseProperty(obj, schema, propertyName);
            }
        }
        return props;
    }

    static parseProperty(obj, schema, propertyName) {
        let propertyDescription = schema[propertyName];
        if (propertyName in obj) {
            let [success, errors] = PropertyParser.validate(obj[propertyName], propertyDescription);
            if (success) {
                return obj[propertyName];
            } else {
                throw new Error(errors);
            }
        }
        if ('default' in propertyDescription) {
            return propertyDescription.default;
        }
        let propertyIsRequired = !('required' in propertyDescription)
            || ('required' in propertyDescription && propertyDescription.required === true);
        if (propertyIsRequired) {
            throw new Error(`Component ${this.constructor.name} must have parameter ${propertyName}`);
        }
    }

    static validate(obj, schema) {
        if ('type' in schema) {
            if (!(typeof obj === schema.type)) {
                return [false, `type must be ${schema.type}, but is ${typeof obj}`];
            }
        }
        return [true, null];
    }
}