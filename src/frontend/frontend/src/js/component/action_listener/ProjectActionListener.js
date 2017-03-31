import Component from '../../lib/Component';
import CentrifugoManager from '../CentrifugoManager';

export default class ProjectActionListener extends Component {
    static get id() {
        return 'ProjectActionListener';
    }

    propTypes() {
        return {
            project: {type: 'number'}
        }
    }

    init() {
        super.init();

        const centrifugo = CentrifugoManager.instance;
        centrifugo.subscribeProject(this.props.project, data => {
                    location.reload();
        });
    };

}

