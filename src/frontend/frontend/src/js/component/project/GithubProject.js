import BaseProjectForm from './BaseProjectForm';

export default class GithubProject extends BaseProjectForm {
    static get id() {
        return 'GithubProject';
    }

    formName() {
        return 'github_project_form';
    }
}

