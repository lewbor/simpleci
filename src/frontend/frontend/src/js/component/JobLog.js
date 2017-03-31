import $ from 'jquery';
import 'ansi_up';
import Component from '../lib/Component';
import CentrifugoManager from './CentrifugoManager';
import Terminal from './Terminal';

export default class JobLog extends Component {
    static get id() {
        return 'JobLog';
    }

    propTypes() {
        return {
            job: {type: 'number'},
            status: {}
        }
    }

    init() {
        super.init();
        this.terminal = new Terminal(this.$node.find('.build_log'));

        this.state = {
            buildConn: null,
            allBuildsConn: null,
            jobStatus: '',
            needScroll: true
        };


        const jobId = this.props.job;
        this.state.jobStatus = this.props.status;

        const scrollCheckbox = this.$node.find('#follow_log_checkbox');
        scrollCheckbox.on('change', () => {
            const needScroll = scrollCheckbox.prop('checked');
            this.state.needScroll = needScroll;
            if (needScroll) {
                this.scrollToEnd();
            }
        });

        const scrollSection = this.$node.find('.follow_log');
        const startTopOffset = scrollSection.offset().top;
        window.onscroll = function () {
            const scrolled = window.pageYOffset || document.documentElement.scrollTop;
            const newTop = (scrolled - startTopOffset) > 0 ? scrolled - startTopOffset + 10 : 0;
            scrollSection.css('top', newTop + 'px');
        };

        $.get('/job/' + jobId + '/log', data => {
            this.showProcessData(data);
        });

        let centrifugo = CentrifugoManager.instance;
        centrifugo.subscribeJob(jobId, message => this.showProcessData(message['data']));

    }

    showProcessData(data) {
        this.terminal.append(data);
        this.scrollToEnd();
    };

    scrollToEnd() {
        if (this.state.needScroll) {
            $('html, body').scrollTop(this.$node.height());
        }
    };
}
