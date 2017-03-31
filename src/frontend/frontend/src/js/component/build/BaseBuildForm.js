import $ from 'jquery';
import Component from '../../lib/Component';

export default class BaseBuildForm extends Component {

    propTypes() {
        return {
            branches: {}
        }
    }

    formName() {
        throw new Error("formName method must be overrided");
    }

    static formatCommit(commit){
        return commit.id.substr(0,7) + ' - ' + commit.date + ' - ' + commit.message.substr(0, 100);
    };

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
        let branchInput = this.$node.find(`#${formName}_branch`);
        let commitInput = this.$node.find(`#${formName}_commit`);

        let branchSelector = BaseBuildForm.selectBasedOnAttributes(branchInput);
        branchInput.replaceWith(branchSelector);

        let commitSelector = BaseBuildForm.selectBasedOnAttributes(commitInput);
        commitInput.replaceWith(commitSelector);

        let branches = this.props.branches;
        $.each(branches, function (branch, commits) {
            branchSelector
                .append($("<option></option>")
                    .attr("value", branch)
                    .text(branch));
        });
        branchSelector.on('change', function () {
            var branch = this.value;
            var commits = branches[branch];

            commitSelector
                .find('option')
                .remove();
            commits.forEach(function (commit) {
                commitSelector
                    .append($("<option></option>")
                        .attr("value", commit.id)
                        .text(BaseBuildForm.formatCommit(commit)));
            })
        });

        branchSelector.trigger('change');
    };

}
