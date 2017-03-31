import Component from '../lib/Component';
import CentrifugoManager from './CentrifugoManager';
import Router from './Router';

export default class JobStopper extends Component {

    static get id() {
        return 'JobStopper';
    }

    propTypes() {
        return {
            job: {type: 'number'}
        }
    }

    init() {
        super.init();
        this.$node.on('click', (e) => {
            e.preventDefault();
            this.$node.attr('class', 'glyphicon glyphicon-refresh glyphicon-spin');
            var url = Router.jobStop(this.props.job);
            $.get(url)
                .done(function () {
                    console.log("Job stop request successfully send");
                })
                .fail(function () {
                    console.log("Jos stop request error");
                });
        });

    }


}