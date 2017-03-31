import $ from 'jquery';
import BaseBuildForm from './BaseBuildForm';

export default class GithubBuildForm extends BaseBuildForm {

    static get id() {
        return 'GitlabBuildForm';
    }

    formName() {
        return 'gitlab_build_form';
    }
}
