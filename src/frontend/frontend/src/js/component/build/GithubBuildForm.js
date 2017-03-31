import $ from 'jquery';
import BaseBuildForm from './BaseBuildForm';

export default class GithubBuildForm extends BaseBuildForm {

    static get id() {
        return 'GithubBuildForm';
    }

    formName() {
        return 'github_build_form';
    }
}
