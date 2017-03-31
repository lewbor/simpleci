import 'sockjs-client';
import Centrifuge from 'centrifuge';

let singleton = Symbol();

export default class CentrifugoManager {

    constructor() {
        this.connect();
    }

    connect() {
        this.jobListeners = {};
        this.projectListeners = {};
        this.subscribers = [];

        this.centrifuge = new Centrifuge({
            url: '/centrifugo/connection',
            user: SimpleciApp['centrifugo']['user_id'],
            token: SimpleciApp['centrifugo']['token'],
            timestamp: SimpleciApp['centrifugo']['timestamp'],
            debug: false,
            resubscribe: false,
            authEndpoint: "/api/centrifugo_auth"
        });
        this.centrifuge.on('error', function (errorMsg) {
            console.log(errorMsg);
        });
        this.centrifuge.on('connect', () => {
            console.log("Connecting to centrifugo");
            this.subscribers.forEach(subscriber => subscriber());
        });
        this.centrifuge.connect();
    }

    subscribeJob(jobId, handler) {
        if (!(jobId in this.jobListeners)) {
            this.jobListeners[jobId] = [];
            let subscriber = () => this.centrifuge.subscribe('$job.' + jobId, data => {
                this.jobListeners[jobId].forEach(jobHandler => jobHandler(data));
            });
            this.subscribers.push(subscriber);
        }
        this.jobListeners[jobId].push(handler);
    }

    subscribeProject(projectId, handler) {
        if (!(projectId in this.projectListeners)) {
            this.projectListeners[projectId] = [];
            let self = this;
            let subscriber = () => self.centrifuge.subscribe('$project.' + projectId, data => {
                self.projectListeners[projectId].forEach(projectHandler => projectHandler(data));
            });
            if (this.centrifuge.isConnected()) {
                subscriber();
            } else {
                this.subscribers.push(subscriber);
            }
        }
        this.projectListeners[projectId].push(handler);
    }

    static get instance() {
        if (!this[singleton]) {
            this[singleton] = new CentrifugoManager();
        }
        return this[singleton];
    }
}
