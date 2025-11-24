public class JobConfigurator {

  def jenkins = hudson.model.Hudson.instance
  def jobFactory
  java.io.PrintStream console
  def masterMap

  public JobConfigurator(jobFactory) {
    this.jobFactory = jobFactory
    /**
      Jenkins' Groovy plugin requirement: Get handle on out
      so println in Classes will send output to the script console,
      http://stackoverflow.com/questions/7742472/groovy-script-in-jenkins-println-output-disappears-when-called-inside-class-envi
      http://mriet.wordpress.com/2011/06/23/groovy-jenkins-system-script/
    **/
    this.console = jobFactory.getBinding().getVariable('out')
    this.masterMap = makeMasterMap()
  }

  public void createJobs() {
    console.println()
    masterMap.each {
      def jobName = it.key
      createPipelineJob(jobName)
    }
    console.println()
  }

  public Map makeMasterMap() {
    console.println()
    def map = [:]
    Sites.inclusiveHosts.each { host ->
        Sites.inclusiveModels.each { model ->
          def webapp = Values.modelSpecificConfig[model]['webapp']
          def sld = Values.modelSpecificConfig[model]['sld']
          def tld = Values.modelSpecificConfig[model]['tld']
          def jobName = "${host}.${sld}.${tld}"
          def existingJob = jenkins.getJob(jobName)
          def hostconf = Values.hostSpecificConfig[host]
          def rebuilderStep = hostconf['rebuilderStep'](host, model, webapp, sld, tld)
          map[jobName] = [
            label : hostconf['label'],
            folder: hostconf['folder'],
            description : hostconf['description'] ?: Values.stdDescription(jobName, "boo"),
            logRotator : hostconf['logRotator'] ?: [7, -1, -1, -1],
            disabled : existingJob ? existingJob.disabled : false,
            quietPeriod : hostconf['quietPeriod'] ?: null,
            checkoutRetryCount : hostconf['checkoutRetryCount'] ?: null,
            customWorkspace : '/var/www/' + jobName + '/project_home',
            scmSchedule : hostconf['scmSchedule'] ?: null,
            ignorePostCommitHooks : hostconf['ignorePostCommitHooks'] ?: null,
            timeout : hostconf['timeout'] ?: null,
            rebuilderStep : hostconf['rebuilderStep'](host, model, webapp, sld, tld),
            testngStep : hostconf['testngStep'] ?
                              hostconf['testngStep'](host, model, webapp, sld, tld) :
                              null,
            apitestStep : hostconf['apitestStep'] ?
                              hostconf['apitestStep'](host, model, webapp, sld, tld) :
                              null,
            cacheStep : hostconf['cacheStep'] ?
                              hostconf['cacheStep'](host, model, webapp, sld, tld) :
                              null,
            sitesearchStep : hostconf['sitesearchStep'] ?
                              hostconf['sitesearchStep'](host, model, webapp, sld, tld) :
                              null,
            slackChannel : hostconf['slackChannel'] ?: null,
            pipelineNotification : hostconf['pipelineNotification'] ? hostconf['pipelineNotification'](hostconf['slackChannel']) : null,
            githubPush : hostconf['githubPush'] ?: null,
          ]
        }
    }
    addCustomMaps(map)
    return map
  }


  public void addCustomMaps(map) {
    console.println()
    Sites.customJobs.each { jobName, conf ->
          if (conf == null) {
            map.remove(jobName)
            console.println 'Custom ' + jobName + ' is null, no conf generated.'
            return
          }
          def model = conf['model']
          def webapp = conf['webapp']
          def sld = conf['sld']
          def tld = conf['tld']
          def host = conf['host']
          def existingJob = jenkins.getJob(jobName)
          def rebuilderStep = conf['rebuilderStep'](host, model, webapp, sld, tld)
          map[jobName] = [
            label : conf['label'],
            folder: conf['folder'],
            description : conf['description'] ?: Values.stdDescription(jobName, "boo"),
            logRotator : conf['logRotator'] ?: null,
            disabled : existingJob ? existingJob.disabled : false,
            quietPeriod : conf['quietPeriod'] ?: null,
            checkoutRetryCount : conf['checkoutRetryCount'] ?: null,
            customWorkspace : '/var/www/' + jobName + '/project_home',
            scmSchedule : conf['scmSchedule'] ?: null,
            ignorePostCommitHooks : conf['ignorePostCommitHooks'] ?: null,
            timeout : conf['timeout'] ?: null,
            rebuilderStep : conf['rebuilderStep'](host, model, webapp, sld, tld),
            testngStep : conf['testngStep'] ? conf['testngStep'](host, model, webapp, sld, tld) : null,
            apitestStep : conf['apitestStep'] ? conf['apitestStep'](host, model, webapp, sld, tld) : null,
            cacheStep : conf['cacheStep'] ? conf['cacheStep'](host, model, webapp, sld, tld) : null,
            sitesearchStep : conf['sitesearchStep'] ? conf['sitesearchStep'](host, model, webapp, sld, tld) : null,

            slackChannel : conf['slackChannel'] ?: null,
            pipelineNotification : conf['pipelineNotification'] ? conf['pipelineNotification'](conf['slackChannel']) : null,
            githubPush : conf['githubPush'] ?: null,
         ]

    }
  }


  // helper class to transform freestyle job style ant definitions to scripted
  // pipeline definitions.  This lets us reuse the existing ant templating

  class pipelineAnt {
    private String prefix = 'ant'
    private String targetsArgs = ''
    private String propArgs = ''
    private String buildFileArgs = ''


    def targets( t ) {
      t.each { this.targetsArgs = this.targetsArgs + " ${it}"}
    }

    def props( p) {
      p.each { this.propArgs = this.propArgs + " -D${it.key}=${it.value}"}
    }

    def buildFile( b ) {
      this.buildFileArgs = ' -buildfile ' + b
    }

    def getCommand() {
      def command = prefix + buildFileArgs + propArgs + targetsArgs
      return command
    }
  }

  public void createPipelineJob(jobName) {

// Because we want to re-use logic from the old freestyle jobs, we split up the
// pipeline script to include the steps from the old jobs.  
//
// Some properties we apply to the job itself (logRotator, quietPeriod,
// scmSchedule, etc.) but most of the logic is in the pipeline script definition

    console.println "Creating pipeline job " + jobName


// We use rudimentary (i.e. awful) string substitution as a templating system.
// When everything moves to pipeline, this could be cleaned up.  Optional
// pieces are defined as 'snippets' which are then included into the final
// pipeline script.

// OPTIONS SNIPPET
// currently only used for timeout, could expand later

    def pipeline_options = ''
    if (masterMap[jobName]['timeout'] != null) { //timeout{ absolute(masterMap[jobName]['timeout']) }
      pipeline_options = """
  options {
    timeout(time: ${masterMap[jobName]['timeout']}, unit: 'MINUTES') 
  }
"""
    }

    def stage_siteconf_update = """
    stage('Update Siteconf') {
      steps {
        checkout(
          [
            \$class: 'GitSCM',
            branches: [[name: "*/master"]],
            extensions: [[
              \$class: 'RelativeTargetDirectory',
              relativeTargetDir: 'websiteconf'
            ]],
            userRemoteConfigs: [[
              credentialsId: 'e26340bf-7773-4c51-b74f-0e5a87abab22',
              url: 'git@github.com:VEuPathDB/websiteconf.git'
            ]]
          ]
        )
        sh '''
          cd websiteconf
          virtualenv -q -p python3 make_yaml_env 
          . ./make_yaml_env/bin/activate
          pip install -q -r requirements.txt
          ./make_yaml.py -m https://github.com/VEuPathDB/tsrc/raw/refs/heads/master/manifest.yml -o ../../etc/site-conf.yaml
        '''
      }
    }
"""

    // CHECKOUT SNIPPET
    def stage_checkout = """
    stage('Checkout') {
      steps {
        script{
          sh 'curl -z ../etc/site-conf.yaml -o ../etc/site-conf.yaml https://software.apidb.org/siteconf/site-conf.yaml'
          site_conf = readYaml file: '../etc/site-conf.yaml'

          for (project in site_conf["site_config"]["${jobName}"]["scm_conf"]) {
            checkout(
              [
                \$class: 'GitSCM', 
                branches: [[name: "*/\${project['branch']}"]], 
                extensions: [
                  [\$class: 'LocalBranch'],
                  [\$class: 'RelativeTargetDirectory',
                    relativeTargetDir: project['dest']]
                  ],
                  userRemoteConfigs: [[
                    credentialsId: 'e26340bf-7773-4c51-b74f-0e5a87abab22',
                    url: project['url']
                  ]]
              ]
            )
          }
        }
      }
    }
"""

// BUILD SNIPPET
    def stage_build = ''
    if (masterMap[jobName]['rebuilderStep'] != null) {
      stage_build = """
    stage('Build') {
      steps {
        sh '''
          ${masterMap[jobName]['rebuilderStep']}
        '''
      }
    }
"""
    }

// TEST SNIPPET
    def stage_test = ''
    def testng_snippet = ''
    def apitest_snippet = ''

    if (masterMap[jobName]['testngStep'] != null) {
      def pa = new pipelineAnt()
      def t = masterMap[jobName]['testngStep']
      t.delegate = pa
      t()
      def antCommand = t.getCommand()
      testng_snippet = """
        sh ''' ${antCommand} '''
        junit 'test_home/**/junitreports/*.xml'
      """
    }

    if (masterMap[jobName]['apitestStep'] != null) {
      apitest_snippet = "sh ''' ${masterMap[jobName]['apitestStep']} '''"
    }

    if (testng_snippet || apitest_snippet ) {
      stage_test = """
      stage('Test') {
          environment {
                GITHUB_READONLY = credentials('3cf5388f-54e2-491b-a7fc-83160dcab3e3')
            }
        steps {
        ${testng_snippet}
        ${apitest_snippet}
        }
      }
    """
    }

// SITESEARCH SNIPPET
    def stage_sitesearch = ''
    if (masterMap[jobName]['sitesearchStep'] != null) {
      stage_sitesearch = """
    stage('Sitesearch') {
      steps {
        sh '''
          ${masterMap[jobName]['sitesearchStep']}
        '''
      }
    }
"""
    }

// CACHE SNIPPET
    def stage_sitecache = ""
    if (masterMap[jobName]['cacheStep'] != null) {
      stage_sitecache = """
    stage('Sitecache') {
      steps {
        sh '''
          ${masterMap[jobName]['cacheStep']}
      '''
      }
    }
"""
    }
    

// Script definition - this brings together all the stages above, if defined

    def pscript = """
${masterMap[jobName]['pipelineNotification'] ? masterMap[jobName]['pipelineNotification']['begin'] : ''}
pipeline {
  agent {
    node {
      label "${masterMap[jobName]['label']}"
      customWorkspace "${masterMap[jobName]['customWorkspace']}"
    }
  }

  ${pipeline_options}

  stages {
    ${stage_siteconf_update}
    ${stage_checkout}
    ${stage_build}
    ${stage_test}
    ${stage_sitesearch}
    ${stage_sitecache}
  
  } 
  
  post {
    fixed {
      echo 'fixed!'
      ${masterMap[jobName]['pipelineNotification'] ? masterMap[jobName]['pipelineNotification']['fixed'] : ''}
    }
    regression { 
      echo 'failed after working :~('
      ${masterMap[jobName]['pipelineNotification'] ? masterMap[jobName]['pipelineNotification']['regression'] : ''}
    }
    success {
      echo 'I did it!  Yay!'
      ${masterMap[jobName]['pipelineNotification'] ? masterMap[jobName]['pipelineNotification']['success'] : ''}
    }
    unsuccessful {
      echo 'I failed :~('
      ${masterMap[jobName]['pipelineNotification'] ? masterMap[jobName]['pipelineNotification']['unsuccessful'] : ''}
    }
  }

}
"""

// The actual pipelinejob definition
    jobFactory.pipelineJob(masterMap[jobName]['folder'] + "/" + jobName) {
      disabled masterMap[jobName]['disabled'] ?: false
      description  masterMap[jobName]['description']

      properties {
          disableConcurrentBuilds()
      }

      if (masterMap[jobName]['logRotator'] != null) logRotator(masterMap[jobName]['logRotator'])

      if (masterMap[jobName]['quietPeriod'] != null) quietPeriod(masterMap[jobName]['quietPeriod'])

      if (masterMap[jobName]['githubPush'] != null ) {
        properties {
          pipelineTriggers {
            triggers {
              githubPush()
            }
          }
        }
      }

      if (
        masterMap[jobName]['scmSchedule'] != null ||
        masterMap[jobName]['ignorePostCommitHooks'] != null
        ) {
          properties {
            pipelineTriggers {
              triggers {
                pollSCM {
                  scmpoll_spec(masterMap[jobName]['scmSchedule'])
                  // Ignore changes notified by SCM post-commit hooks.
                  ignorePostCommitHooks(masterMap[jobName]['ignorePostCommitHooks'])
                }
              }
            }
          }
      }


      definition {
        cps {
           script(pscript)
           sandbox(true)
        }
      }
    }
  }

}


