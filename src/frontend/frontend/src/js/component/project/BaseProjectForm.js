import $ from 'jquery';
import Component from '../../lib/Component';

export default class BaseProjectForm extends Component {

    propTypes() {
        return {
            projects: {}
        }
    }

    formName() {
        throw new Error("formName method must be overrided");
    }

    static selectBasedOnAttributes(element) {
        const select = $('<select/>');
        select.attr({
            'id': element.attr('id'),
            'name': element.attr('name'),
            'class': element.attr('class'),
            'required': element.attr('required')
        });
        return select;
    }

    init() {
        super.init();
        let formName = this.formName();
        const projectInput = this.$node.find(`#${formName}_project_identity`);
        const nameInput = this.$node.find(`#${formName}_name`);
        const descriptionInput = this.$node.find(`#${formName}_description`);
        const repositoryUrlInput = this.$node.find(`#${formName}_repository_url`);

        this.projects = this.props.projects;

        let projectSelector = BaseProjectForm.selectBasedOnAttributes(projectInput);
        projectInput.replaceWith(projectSelector);

        $.each(this.projects, function (id, project) {
            projectSelector
                .append($("<option></option>")
                    .attr("value", id)
                    .text(project.name));
        });


        let repositoryUrlSelector = BaseProjectForm.selectBasedOnAttributes(repositoryUrlInput);
        repositoryUrlInput.replaceWith(repositoryUrlSelector);

        let that = this;
        projectSelector.on('change', function () {
            var projectId = this.value;
            var project = that.projects[projectId];
            nameInput.val(project.name);
            descriptionInput.val(project.description);

            repositoryUrlSelector
                .find('option')
                .remove();
            project.repo_urls.forEach(function (repoUrl) {
                repositoryUrlSelector
                    .append($("<option></option>")
                        .attr("value", repoUrl)
                        .text(repoUrl));
            })
        });

        projectSelector.trigger('change');
    };
}

