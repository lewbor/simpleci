<?php

namespace AppBundle\Settings\Project\Github;

use AppBundle\Settings\Project\ProjectCreateForm;
use AppBundle\System\Api\GithubApi;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormView;
use Symfony\Component\OptionsResolver\OptionsResolver;

class GithubProjectForm extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('name', TextType::class)
            ->add('description', TextareaType::class, ['required' => false])
            ->add('project_identity', TextType::class, ['required' => true, 'label' => 'Project'])
            ->add('repository_url', TextType::class, ['required' => true])
            ->add('add_deploy_key', CheckboxType::class, ['required' => false, 'data' => false]);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver
            ->setRequired('api')
            ->setAllowedTypes('api', GithubApi::class);

    }

    public function buildView(FormView $view, FormInterface $form, array $options)
    {
        /** @var GithubApi $api */
        $api = $options['api'];

        $projects = $api->projects();
        $projectsDescription = [];
        foreach ($projects as $project) {
            $projectsDescription[$project['full_name']] = [
                'name' => $project['full_name'],
                'description' => $project['description'],
                'repo_urls' => [$project['ssh_url'], $project['clone_url']]];
        }

        $view->vars['attr'] = [
            'data-widget' => 'github_project',
            'data-projects' => json_encode($projectsDescription)];
    }

    public function getParent()
    {
        return ProjectCreateForm::class;
    }


}