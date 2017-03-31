import $ from 'jquery';
import bootbox from 'bootbox';
import bootstrap from 'bootstrap';
import Component from '../lib/Component';

export default class ConfirmationLink extends Component {
    static get id() {
        return 'ConfirmationLink';
    }

    propTypes() {
        return {
            message: {}
        }
    }

    init() {
        super.init();
        var url = this.$node.attr("href");
        this.$node.on('click', (e) => {
            e.preventDefault();
            bootbox.confirm(this.props.message, function (result) {
                if (result) {
                    document.location.href = url;
                }
            });
        });
    }
}