import BaseProjectForm from './BaseProjectForm';

export default class GitlabProject extends BaseProjectForm {
    static get id() {
        return 'GitlabProject';
    }

    formName() {
        return 'gitlab_project_form';
    }
}

