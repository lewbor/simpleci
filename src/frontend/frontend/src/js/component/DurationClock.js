import Component from '../lib/Component';

export default class DurationClock extends Component {
    static get id() {
        return 'DurationClock';
    }

    propTypes() {
        return {
            started: {},
            current: {}
        }
    }

    init() {
        super.init();

        let started = this.props.started;
        let now = this.props.current;

        this.diff = now - started;
        this.increment = 0;

        let updateTime = () => {
            let currentDuration = this.diff + this.increment;
            let formattedDuration = this.formatSeconds(currentDuration);
            this.$node.html(formattedDuration);

            this.increment++;
        };

        updateTime();
        setInterval(updateTime, 1000);
    }

    formatSeconds(currentDuration) {
        let minutes = Math.floor(currentDuration / 60);
        let seconds = currentDuration % 60;

        if(minutes == 0) {
            return `${seconds} sec`;
        } else {
            return `${minutes} min ${seconds} sec`;
        }
    }

}