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
      createJob(jobName)
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
          def svnDefaultLocations = Values.svnDefaultLocations
          map[jobName] = [
            label : hostconf['label'],
            description : hostconf['description'] ?: Values.stdDescription(jobName, "boo"),
            logRotator : hostconf['logRotator'] ?: [7, -1, -1, -1],
            disabled : existingJob ? existingJob.disabled : false,
            quietPeriod : hostconf['quietPeriod'] ?: null,
            checkoutRetryCount : hostconf['checkoutRetryCount'] ?: null,
            customWorkspace : '/var/www/' + jobName + '/project_home',
            scm : getSvnLocations(moduleLocations(jobName, existingJob, svnDefaultLocations)),
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
            jabberNotification : hostconf['jabberNotification'] ?
                  hostconf['jabberNotification'](hostconf['jabberContacts']) : null,
            extendedEmail : hostconf['extendedEmail'] ?: null,
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
          def svnDefaultLocations = conf['svnDefaultLocations'] ?: Values.svnDefaultLocations
          map[jobName] = [
            label : conf['label'],
            description : conf['description'] ?: Values.stdDescription(jobName, "boo"),
            logRotator : conf['logRotator'] ?: null,
            disabled : existingJob ? existingJob.disabled : false,
            quietPeriod : conf['quietPeriod'] ?: null,
            checkoutRetryCount : conf['checkoutRetryCount'] ?: null,
            customWorkspace : '/var/www/' + jobName + '/project_home',
            scm : getSvnLocations(moduleLocations(jobName, existingJob, svnDefaultLocations)),
            scmSchedule : conf['scmSchedule'] ?: null,
            ignorePostCommitHooks : conf['ignorePostCommitHooks'] ?: null,
            timeout : conf['timeout'] ?: null,
            rebuilderStep : conf['rebuilderStep'](host, model, webapp, sld, tld),
            testngStep : conf['testngStep'] ? conf['testngStep'](host, model, webapp, sld, tld) : null,
            cacheStep : conf['cacheStep'] ? conf['cacheStep'](host, model, webapp, sld, tld) : null,
            sitesearchStep : conf['sitesearchStep'] ? conf['sitesearchStep'](host, model, webapp, sld, tld) : null,

            jabberNotification : conf['jabberNotification'] ? conf['jabberNotification'](conf['jabberContacts']) : null,
            extendedEmail : conf['extendedEmail'] ?: null,
         ]

    }
  }

  public void createJob(jobName) {
    console.println "Creating " + jobName
    jobFactory.freeStyleJob(jobName) {
      wrappers {
        label masterMap[jobName]['label']

        disabled masterMap[jobName]['disabled'] ?: false

        description  masterMap[jobName]['description']

        if (masterMap[jobName]['logRotator'] != null) logRotator(masterMap[jobName]['logRotator'])

        if (masterMap[jobName]['quietPeriod'] != null) quietPeriod(masterMap[jobName]['quietPeriod'])

        if (masterMap[jobName]['checkoutRetryCount'] != null) checkoutRetryCount(masterMap[jobName]['checkoutRetryCount'])

        customWorkspace(masterMap[jobName]['customWorkspace'])
        scm masterMap[jobName]['scm']

        if (
          masterMap[jobName]['scmSchedule'] != null ||
          masterMap[jobName]['ignorePostCommitHooks'] != null
          ) {
          triggers {
            //scm(masterMap[jobName]['scmSchedule'])
            configure scmTrigger(
              masterMap[jobName]['scmSchedule'],
              masterMap[jobName]['ignorePostCommitHooks']
            )

          }
        }

        if (masterMap[jobName]['timeout'])
          timeout { absolute(masterMap[jobName]['timeout']) }


        jdk('(Default)')

        if (masterMap[jobName]['apitestStep'] != null) credentialsBinding { usernamePassword('API_CREDS', 'website_apitest_user') }

        steps {
          shell(masterMap[jobName]['rebuilderStep'])
          masterMap[jobName]['testngStep'] ? ant(masterMap[jobName]['testngStep']) : null
          masterMap[jobName]['apitestStep'] ? shell(masterMap[jobName]['apitestStep']) : null
          masterMap[jobName]['sitesearchStep'] ? shell(masterMap[jobName]['sitesearchStep']) : null
        }

        if (masterMap[jobName]['testngStep'] != null) configure testngPubliser()

        publishers {
           masterMap[jobName]['cacheStep'] ? downstreamParameterized(masterMap[jobName]['cacheStep']) : null
           masterMap[jobName]['extendedEmail'] ? masterMap[jobName]['extendedEmail'] (delegate) : null
           if (masterMap[jobName]['apitestStep'] != null) archiveJunit('wdk-api-test/build/test-results/test/*.xml')

        } // publishers

        if (masterMap[jobName]['jabberNotification'] != null) configure masterMap[jobName]['jabberNotification']

      } // wrappers
    } // job
  } //createJob


  /**
      <hudson.plugins.testng.Publisher plugin="testng-plugin@1.5">
        <reportFilenamePattern>test_home/results/**</reportFilenamePattern>
        <escapeTestDescp>true</escapeTestDescp>
        <escapeExceptionMsg>true</escapeExceptionMsg>
      </hudson.plugins.testng.Publisher>
  **/
  def testngPubliser() {
    {project -> project/publishers/'hudson.plugins.testng.Publisher' {
      reportFilenamePattern 'test_home/results/**'
      escapeTestDescp 'true'
      escapeExceptionMsg 'true'
    }
    }
  }

  def scmTrigger(crontab, bool) {
    {project -> project/triggers/'hudson.triggers.SCMTrigger' {
      crontab = crontab ?: ''
      bool = bool ?: 'true'
      spec crontab
      ignorePostCommitHooks bool
    }
    }
  }

  // convert SubversionSCM.ModuleLocation fields to a map
  def moduleLocations(jobName, job, svnDefaultLocations) {
    if (job == null) {
      console.println jobName + " is new, using default svn locations"
      return svnDefaultLocations
    }

    if ( ! job.scm.hasProperty('locations')) {
      console.println jobName + " exists, but no scm defined; using default svn locations"
      return svnDefaultLocations
    }

    def locations = [:]
    job.scm.locations.each{
      if ( ! it.local && ! it.remote) return
      locations.put(it.local, it.remote)
    }

    if (locations.size() == 0) {
      console.println jobName + " exists, but no valid svn locations; using default svn locations"
      return svnDefaultLocations
    }

    console.println "Existing job " + jobName + ", using existing svn locations"
    return locations
  }

  def getSvnLocations(svnLocations) {
    {it ->
        def installUrl = svnLocations['install']
        if (installUrl == null)
          throw new java.lang.NullPointerException("SCM location for 'install' is not defined'")
        def firstKey = svnLocations.find().key
        svn(svnLocations[firstKey], firstKey) { svnNode ->
          svnLocations.each { localValue, remoteValue ->
            if (localValue == firstKey) {
              svnNode / locations {
                'hudson.scm.SubversionSCM_-ModuleLocation' {
                  remote remoteValue
                  local localValue
                  if ( (localValue ==~ /.+(?:Presenters|Datasets)/) ) {
                    credentialsId Values.datasetSvnCredentialsId
                  }
                }
              }
            } else {
              svnNode / locations << 'hudson.scm.SubversionSCM_-ModuleLocation' {
                remote remoteValue
                local localValue
                if ( (localValue ==~ /.+(?:Presenters|Datasets)/) ) {
                  credentialsId Values.datasetSvnCredentialsId
                }
              }
            }

            svnNode / browser(class:"hudson.plugins.websvn2.WebSVN2RepositoryBrowser") {
              url "http://websvn.apidb.org/revision.php?repname=TBD"
              baseUrl "http://websvn.apidb.org"
              repname "repname=TBD&"
            }

        } // svnLocations.each

        svnNode / excludedRegions('/ApiCommonData/.*/Load/.*')

        // syntax learned from https://issues.jenkins-ci.org/browse/JENKINS-17513
        def updaterNode = svnNode / workspaceUpdater
        updaterNode.attributes().put('class','hudson.scm.subversion.UpdateWithCleanUpdater')

      } // svn()
    } // closure
  } // getSvnLocations()

}


