import 'jquery';
import 'bootstrap';


import Builder from './lib/Builder';

import Sidebar from './component/Sidebar';
import SidebarToggle from './component/SidebarToggle';
import ConfirmationLink from './component/ConfirmationLink';
import CodeFormatter from './component/CodeFormatter';
import JobLog from './component/JobLog';
import JobStopper from './component/JobStopper';
import ProjectActionListener from './component/action_listener/ProjectActionListener';
import GitlabBuildForm from './component/build/GitlabBuildForm';
import GithubBuildForm from './component/build/GithubBuildForm';
import GithubProject from './component/project/GithubProject';
import GitlabProject from './component/project/GitlabProject';
import DurationClock from './component/DurationClock';

$(document).ready(function () {
    let components = [Sidebar, SidebarToggle, ConfirmationLink, JobLog, ProjectActionListener,
        GitlabBuildForm, GithubBuildForm,
        GithubProject, GitlabProject, JobStopper, CodeFormatter, DurationClock];
    let builder = new Builder(components);
    builder.bootstrap({
        afterComponentCreated: function (node) {
            node.attr('data-initialization-state', 'complete');
        }
    })
        .init($(document.body));
});