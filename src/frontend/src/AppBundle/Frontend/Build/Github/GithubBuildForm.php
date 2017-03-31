<?php


namespace AppBundle\Frontend\Build\Github;


use AppBundle\Model\Entity\Project;
use AppBundle\System\Api\GithubApi;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormView;
use Symfony\Component\OptionsResolver\OptionsResolver;

class GithubBuildForm extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {

        $builder
            ->add('branch', TextType::class, ['required' => true])
            ->add('commit', TextType::class, ['required' => true]);
    }

    public function buildView(FormView $view, FormInterface $form, array $options)
    {
        /** @var Project $project */
        $project = $options['project'];
        if (null === $project) {
            return;
        }

        /** @var GithubApi $api */
        $api = $options['api'];

        try {
            $branchCommits = $api->listBranchesAndCommits($project->getServerIdentity());

            $view->vars['attr'] = array_merge($view->vars['attr'], [
                'data-widget' => 'github_build_form',
                'data-branches' => json_encode($branchCommits)]);
        } catch (\Exception $e) {
            $view->vars['api_error_message'] = $e->getMessage();
        }
    }


    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver
            ->setRequired('project')
            ->setAllowedTypes('project', Project::class)
            ->setRequired('api')
            ->setAllowedTypes('api', GithubApi::class);
    }


}