<?php

namespace AppBundle\Settings\Project\Gitlab;

use AppBundle\Settings\Project\ProjectCreateForm;
use AppBundle\System\Api\GitlabApi;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormView;
use Symfony\Component\OptionsResolver\OptionsResolver;

class GitlabProjectForm extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('name', TextType::class)
            ->add('description', TextareaType::class, ['required' => false])
            ->add('project_identity', ChoiceType::class, ['required' => true])
            ->add('repository_url', ChoiceType::class, ['required' => true])
            ->add('add_deploy_key', CheckboxType::class, ['required' => false, 'data' => false]);


        $builder->get('project_identity')->resetViewTransformers();
        $builder->get('repository_url')->resetViewTransformers();

    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setRequired('api')
            ->setAllowedTypes('api', GitlabApi::class);
    }

    public function buildView(FormView $view, FormInterface $form, array $options)
    {
        /** @var \AppBundle\System\Api\GitlabApi $api */
        $api = $options['api'];
        $projects = $api->projects();

        $projectsDescription = [];
        foreach ($projects as $project) {
            $projectsDescription[$project['path_with_namespace']] = [
                'name' => $project['name_with_namespace'],
                'description' => $project['description'],
                'repo_urls' => [$project['ssh_url_to_repo'], $project['http_url_to_repo']]];
        }

        $view->vars['attr'] = [
            'data-widget' => 'gitlab_project',
            'data-projects' => json_encode($projectsDescription)];
    }

    public function getParent()
    {
        return ProjectCreateForm::class;
    }


}