import $ from 'jquery';
import Prism from 'prismjs';
import 'prismjs/components/prism-json';
import Component from '../lib/Component';

export default class CodeFormatter extends Component {
    static get id() {
        return 'CodeFormatter';
    }

    init() {
        super.init();
        var text = this.$node.html();
        var code = JSON.stringify(JSON.parse(text), null, '  ');
        var html = Prism.highlight(code, Prism.languages.json);

        var codeNode = $('<code/>').attr('class', 'language-json');
        codeNode.append(html);
        var preNode = $('<pre/>').append(codeNode);
        this.$node.html(preNode);
    };

}

