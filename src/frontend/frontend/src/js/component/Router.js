export default class Router {
    static job(jobId) {
        return `/job/${jobId}`;
    }

    static build(buildId) {
        return `/build/${buildId}`;
    }

    static jobStop(jobId) {
        return `/job/${jobId}/stop`;
    }
}