organizationFolder('lsst-ts') {
  organizations {
    github {
      repoOwner('lsst-ts')
      credentialsId('github-lsstsadmin')
    }
  }

  /* XXX Unable to figure out how to use interval()...
  triggers {
    periodicFolderTrigger {
      interval("1 day")
            <spec>* * * * *</spec>
            <interval>60000</interval>
      interval("86400")
            <spec>H H * * *</spec>
            <interval>2592000000</interval>
      interval("86400000")
            <spec>H H * * *</spec>
            <interval>2592000000</interval>
    }
  }
  */

  projectFactories {
    workflowMultiBranchProjectFactory {
      scriptPath('Jenkinsfile')
    }
  }

  configure { node ->
    def triggers = node / 'triggers'
    triggers << 'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger' {
      /* selecting 1 day in the gui results in (yes, it looks crazy):
      <spec>H H/4 * * *</spec>
      <interval>86400000</interval>
      */
      spec('H H/4 * * *')
      interval(86400000)
    }

    def traits = node / 'navigators' / 'org.jenkinsci.plugins.github__branch__source.GitHubSCMNavigator' / 'traits'
    traits << 'org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait' {
      // "exclude branches that are also files as PRs"
      strategyId(1)
    }
    traits << 'org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait' {
      // "The current pull request revision"
      strategyId(2)
    }
    traits << 'org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait' {
      // "The current pull request revision"
      strategyId(2)
      trust(class: 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission')
    }
  }
}
