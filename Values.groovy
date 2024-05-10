public class Values {

  /**
    * Read-only credentials for CBIL's subversion repo are registered in the
    * Jenkins Jenkins Credentials plugin. For this script to add them to SCM
    * entries for jenkins jobs we need the credential ID (not a username).
    * To find that ID, go to https://<jenkins.host>/credential-store/ and
    * navigate to the relevant global or restricted domain to find the
    * specific user for CBIL's subversion server. Vist the details page for
    * that user and note the UUID in the url (also shown under Advanced on
    * the Update page).
    *
    * Credentials are mapped to the local path in JobConfigurator.getSvnLocations().
  */
  static private def datasetSvnCredentialsId = '3cf5388f-54e2-491b-a7fc-83160dcab3e3'

  static public def modelSpecificConfig = [
    AmoebaDB : [
      webapp : "amoeba",
      sld: "amoebadb",
      tld : "org",
    ],
    ClinEpiDB : [
      webapp : "ce",
      sld : "clinepidb",
      tld : "org",
    ],
    CryptoDB : [
      webapp : "cryptodb",
      sld : "cryptodb",
      tld : "org",
    ],
    EuPathDB : [
      webapp : "veupathdb",
      sld : "veupathdb",
      tld : "org",
    ],
    FungiDB : [
      webapp : "fungidb",
      sld : "fungidb",
      tld : "org",
    ],
    Gates : [
      webapp : "ce",
      sld : "clinepidb",
      tld : "org",
    ],
    GiardiaDB : [
      webapp : "giardiadb",
      sld : "giardiadb",
      tld : "org",
    ],
    HostDB : [
      webapp : "hostdb",
      sld : "hostdb",
      tld : "org",
    ],
    ICEMR : [
      webapp : "ce",
      sld : "clinepidb",
      tld : "org",
    ],
    MicrobiomeDB : [
      webapp : "mbio",
      sld: "microbiomedb",
      tld : "org",
    ],
    MicrosporidiaDB : [
      webapp : "micro",
      sld: "microsporidiadb",
      tld : "org",
    ],
    OrthoMCL : [
      webapp : "orthomcl",
      sld: "orthomcl",
      tld : "org",
    ],
    PiroplasmaDB : [
      webapp : "piro",
      sld: "piroplasmadb",
      tld : "org",
    ],
    PlasmoDB : [
      webapp : "plasmo",
      sld: "plasmodb",
      tld : "org",
    ],
    ToxoDB : [
      webapp : "toxo",
      sld: "toxodb",
      tld : "org",
    ],
    TrichDB : [
      webapp : "trichdb",
      sld: "trichdb",
      tld : "org",
    ],
    TriTrypDB : [
      webapp : "tritrypdb",
      sld: "tritrypdb",
      tld : "org",
    ],
    VectorBase : [
      webapp : "vectorbase",
      sld: "vectorbase",
      tld : "org",
    ],
  ]



/** ********************************************************************************
REBUILDER
******************************************************************************** **/

  static public def rebuilderStepForIntegration = { host, model, webapp, sld, tld ->
    return """
      date > .hudsonTimestamp
      ulimit -u 4096
      ulimit -n 4096
      env
      # Restarting tomcat interferes with maint websites, so stop this.
      #sudo instance_manager stop ${model} force
      #sleep 5
      #sudo instance_manager start  ${model} verbose
      #sleep 15

      # Copy Conifer site vars file from source in to etc.
      src_yml="\$WORKSPACE/EbrcWebsiteCommon/Model/lib/conifer/roles/conifer/files/ebrc_prod_site_vars.yml"
      dest_yml="/var/www/${host}.${sld}.${tld}/etc/conifer_site_vars.yml"
      if [[ -f "\$src_yml" ]]; then
        cp "\$src_yml" "\$dest_yml"
        sed -i "1i# DO NOT EDIT!\\n# This file copied from\\n# \$src_yml,\\n# \$(date)\\n# by Jenkins\\n\\n" "\$dest_yml"
      fi

      \$HOME/bin/rebuilder-jenkins ${host}.${sld}.${tld} --webapp ${model}:${webapp}.integrate
      # give webapp time to reload before running tests
      sleep 30
    """
    .stripIndent()
  }


  /** Cristina maint.*.org websites for testing apicomm maintenance scripts, etc. **/
  static public def rebuilderStepForMaint = { host, model, webapp, sld, tld ->
    return """
      date > .hudsonTimestamp
      ulimit -u 4096
      ulimit -n 4096
      env

      # Copy Conifer site vars file from source in to etc.
      src_yml="\$WORKSPACE/EbrcWebsiteCommon/Model/lib/conifer/roles/conifer/files/ebrc_maint_site_vars.yml"
      dest_yml="/var/www/${host}.${sld}.${tld}/etc/conifer_site_vars.yml"
      if [[ -f "\$src_yml" ]]; then
        cp "\$src_yml" "\$dest_yml"
        sed -i "1i# DO NOT EDIT!\\n# This file copied from\\n# \$src_yml,\\n# \$(date)\\n# by Jenkins\\n\\n" "\$dest_yml"
      fi

      \$HOME/bin/rebuilder-jenkins ${host}.${sld}.${tld} --webapp ${model}:${webapp}.maint
    """
    .stripIndent()
  }


  static public def rebuilderStepForWdkTemplate = { host, model, webapp, sld, tld ->
    return """
      date > .hudsonTimestamp
      env
      sudo instance_manager stop ${model} force
      sleep 5
      sudo instance_manager start  ${model} verbose
      sleep 15
      \$HOME/bin/rebuilder-jenkins ${host}.apidb.org --webapp ${model}:${webapp} --ignore-ip
      \$HOME/bin/resetWdkPgTestDb
    """
    .stripIndent()
  }


  static public def rebuilderStepForQa = { host, model, webapp, sld, tld ->
    return """
      env

      # Copy Conifer site vars file from source in to etc.
      src_yml="\$WORKSPACE/EbrcWebsiteCommon/Model/lib/conifer/roles/conifer/files/ebrc_prod_site_vars.yml"
      dest_yml="/var/www/${host}.${sld}.${tld}/etc/conifer_site_vars.yml"
      if [[ -f "\$src_yml" ]]; then
        cp "\$src_yml" "\$dest_yml"
        sed -i "1i# DO NOT EDIT!\\n# This file copied from\\n# \$src_yml,\\n# \$(date)\\n# by Jenkins\\n\\n" "\$dest_yml"
      fi

      \$HOME/bin/rebuilder-jenkins ${host}.${sld}.${tld}

      # give webapp time to reload before running tests
      sleep 15

      ## cache public strategy results (redmine #18944) with non-debug logging
      ## Disabled: it seems of limited benefit for QA and it slows builds.
      ## It could be useful as a pre-release check of strategies but
      ## there's no useable reporting so failures will go unnoticed.
      #source /var/www/${host}.${sld}.${tld}/etc/setenv
      #if [[ -e "\$GUS_HOME/bin/wdkRunPublicStrats" ]]; then
      #  export GUSJVMOPTS='-Dlog4j.configuration=file:\$PROJECT_HOME/WDK/Model/config/log4j.info.properties'
      #  wdkRunPublicStrats -model ${model}
      #fi
    """
    .stripIndent()
  }

  static public def rebuilderStepWithJava7 = { host, model, webapp, sld, tld ->
    return """
      env
      # Using Java 7
      \$HOME/bin/rebuilder-jenkins ${host}.${sld}.${tld}  --java-home /usr/java/jdk1.7.0_80
    """
    .stripIndent()
  }

  static public def rebuilderStepForBeta = { host, model, webapp, sld, tld ->
    return """
      env

      # Copy Conifer site vars file from source in to etc.
      src_yml="\$WORKSPACE/EbrcWebsiteCommon/Model/lib/conifer/roles/conifer/files/ebrc_prod_site_vars.yml"
      dest_yml="/var/www/${host}.${sld}.${tld}/etc/conifer_site_vars.yml"
      if [[ -f "\$src_yml" ]]; then
        cp "\$src_yml" "\$dest_yml"
        sed -i "1i# DO NOT EDIT!\\n# This file copied from\\n# \$src_yml,\\n# \$(date)\\n# by Jenkins\\n\\n" "\$dest_yml"
      fi

      \$HOME/bin/rebuilder-jenkins ${host}.${sld}.${tld}
      sleep 15

      # cache public strategy results (redmine #18944) with non-debug logging
      source /var/www/${host}.${sld}.${tld}/etc/setenv
      if [[ -e "\$GUS_HOME/bin/wdkRunPublicStrats" ]]; then
        export GUSJVMOPTS='-Dlog4j.configuration=file:\$PROJECT_HOME/WDK/Model/config/log4j.info.properties'
        # disable wdkRunPublicStrats until slow queries in Fungi,plasmo,tritryp can be examined (9/11/2017)
        #wdkRunPublicStrats -model ${model}
      fi
    """
    .stripIndent()
  }

  static public def rebuilderStepForWww = { host, model, webapp, sld, tld ->
    return """
      env

      # Copy Conifer site vars file from source in to etc.
      src_yml="\$WORKSPACE/EbrcWebsiteCommon/Model/lib/conifer/roles/conifer/files/ebrc_prod_site_vars.yml"
      dest_yml="/var/www/${host}.${sld}.${tld}/etc/conifer_site_vars.yml"
      if [[ -f "\$src_yml" ]]; then
        cp "\$src_yml" "\$dest_yml"
        sed -i "1i# DO NOT EDIT!\\n# This file copied from\\n# \$src_yml,\\n# \$(date)\\n# by Jenkins\\n\\n" "\$dest_yml"
      fi

      \$HOME/bin/rebuilder-jenkins ${host}.${sld}.${tld} --webapp ${model}:${webapp}
      sleep 15

      # cache public strategy results (redmine #18944) with non-debug logging
      source /var/www/${host}.${sld}.${tld}/etc/setenv
      if [[ -e "\$GUS_HOME/bin/wdkRunPublicStrats" ]]; then
        export GUSJVMOPTS='-Dlog4j.configuration=file:\$PROJECT_HOME/WDK/Model/config/log4j.info.properties'
        # disable wdkRunPublicStrats until slow queries in Fungi,plasmo,tritryp can be examined (9/11/2017)
        #wdkRunPublicStrats -model ${model}
      fi
    """
    .stripIndent()
  }


/** ********************************************************************************
TEST NG
******************************************************************************** **/

  static public def testngStepForIntegration = { host, model, webapp, sld, tld ->
    return {
      targets(['cleantestresults', 'cleaninstall', 'testbynames'])
      props('proj':'EbrcWebsiteCommon', 'comp':'Watar', 'targetDir':'\$WORKSPACE/test_home',
        'projectsDir':'\$WORKSPACE', 'baseurl':"https://${host}.${sld}.${tld}",
        'webappname':"${webapp}.integrate", 'testnames':'"Integration"', 'msTimeout':"30000")
      buildFile 'EbrcWebsiteCommon/Watar/build.xml'
    }
  }

  static public def testngStepForQa = { host, model, webapp, sld, tld ->
    return {
      targets(['cleantestresults', 'cleaninstall', 'testbynames'])
      props('proj':'EbrcWebsiteCommon', 'comp':'Watar', 'targetDir':'\$WORKSPACE/test_home',
        'projectsDir':'\$WORKSPACE', 'baseurl':"https://${host}.${sld}.${tld}",
        'webappname':"${webapp}", 'testnames':'"QA"', 'msTimeout':"30000")
      buildFile 'EbrcWebsiteCommon/Watar/build.xml'
    }
  }

/** ********************************************************************************
Api testing for QA
******************************************************************************** **/

  static public def apitestStepForQa = { host, model, webapp, sld, tld ->
    return """
    echo "This is the  api testing step for ${host}.${sld}.${tld}"

    source /var/www/${host}.${sld}.${tld}/etc/setenv

    # rename jenkins supplied environment vars to what the build requires
    export GITHUB_USERNAME="\$GITHUB_READONLY_USR"
    export GITHUB_TOKEN="\$GITHUB_READONLY_PSW"

    time testRunner.sh ${model} https://${host}.${sld}.${tld} \$GUS_HOME/../html/test_output /tmp/test_${host}.${sld}.${tld} || true

    """
    .stripIndent()
  }

/** ********************************************************************************
Cache building step
******************************************************************************** **/

  static public def cacheStep = { host, model, webapp, sld, tld ->
    return """
    echo "This is the site cache build step for ${host}.${sld}.${tld}/${webapp}"

    source /var/www/${host}.${sld}.${tld}/etc/setenv
    time wdkCacheSeeder || true

    echo "Site cache build finished"
    """
    .stripIndent()
  }

/** ********************************************************************************
Sitesearch step
******************************************************************************** **/

  static public def sitesearchStepForWww = { host, model, webapp, sld, tld ->
    // don't run update for portal
    if( model != "EuPathDB") {
      return """
      # only run if container_env exists (this restricts currently to
      # ApicommonWebsite sites, which isn't strictly "sitesearch enabled sites",
      # but since the script requires it, it is a harmless check regardless)

      if [ -e /var/www/${host}.${sld}.${tld}/gus_home/config/*/container_env ]
      then
        sudo /usr/local/bin/jenkins_presenter_update.sh /var/www/${host}.${sld}.${tld} prod
      fi
      """
      .stripIndent()
    }
    else {
      return null
    }
  }

  static public def sitesearchStepForQa = { host, model, webapp, sld, tld ->
    // don't run update for portal
    if( model != "EuPathDB") {
      return """
      # only run if container_env exists (this restricts currently to
      # ApicommonWebsite sites, which isn't strictly "sitesearch enabled sites",
      # but since the script requires it, it is a harmless check regardless)

      if [ -e /var/www/${host}.${sld}.${tld}/gus_home/config/*/container_env ]
      then
        sudo /usr/local/bin/jenkins_presenter_update.sh /var/www/${host}.${sld}.${tld} qa
      fi
      """
      .stripIndent()
    }
    else {
      return null
    }
  }

/** ********************************************************************************
PIPELINE NOTIFICATIONS
******************************************************************************** **/


  static public def pipelineNotificationChangeOnly = { channel ->
    if ( channel == null ) return null

    def notifications = [:]

    notifications['begin'] = ''
    notifications['fixed'] = ''
    notifications['regression'] = ''
    notifications['success'] = ''
    notifications['unsuccessful'] = ''

    notifications['fixed'] = """
      script {
        def slackResponse = slackSend(
          channel: "${channel}",
          color: 'good',
          message: "\${currentBuild.currentResult}: Job '\${env.JOB_NAME} [\${env.BUILD_NUMBER}]' Check console output at \${env.BUILD_URL}"
        )
      }
"""

    notifications['regression'] = """
      script {
        def userIds = slackUserIdsFromCommitters()
        def userIdsString = userIds.collect { "<@\${it}>" }.join(' ')
        def blameMessage = ''
        if ( userIdsString ) {
            blameMessage = "Last Commits by: \${userIdsString}"
        }
    
        def slackResponse = slackSend(
          channel: "${channel}",
          color: 'danger',
          message: "\${currentBuild.currentResult}: Job '\${env.JOB_NAME} [\${env.BUILD_NUMBER}]' Check console output at \${env.BUILD_URL} \$blameMessage "
        )

      }
"""

    return notifications

    }

  static public def pipelineNotificationEveryBuild = { channel ->
    if ( channel == null ) return null

    def notifications = [:]

    notifications['begin'] = ''
    notifications['fixed'] = ''
    notifications['regression'] = ''
    notifications['success'] = ''
    notifications['unsuccessful'] = ''


    notifications['begin'] = """
      script {
        slackResponse = slackSend(
          channel: "${channel}",
          message: "Starting Job '\${env.JOB_NAME} [\${env.BUILD_NUMBER}]' Check console output at \${env.BUILD_URL}"
        )
      }
"""
    notifications['success'] = """
      script {
        slackResponse.addReaction("jenkins-success")
      }
"""
    notifications['unsuccessful'] = """
      script {
        slackResponse.addReaction("jenkins-failed")
      }
"""

    return notifications

    }




/** ********************************************************************************
SCM POLL SCHEDULE
******************************************************************************** **/
  static public def scmScheduleAsap = 'H/5 * * * *'
  static public def scmScheduleNightly = 'H H(0-2) * * *'
  static public def scmScheduleNightlyLate = 'H H(3-4) * * *'
  static public def scmScheduleYearly = '@yearly'


/** ********************************************************************************
CONFIGURATIONS PER HOST

      label : 'santol', // REQUIRED
      rebuilderStep : rebuilderStepForIntegration, // REQUIRED
      timeout : 20,  // OPTIONAL
      scmSchedule : scmScheduleAsap, // OPTIONAL
      ignorePostCommitHooks : 'true', // OPTIONAL. Default is 'true' if scmSchedule is set.
      quietPeriod : 180, // OPTIONAL
      checkoutRetryCount : 1, // OPTIONAL
      testngStep : testngStepForIntegration, // OPTIONAL
      jabberContacts : jabberContactsIntegrate, // OPTIONAL
      //logRotator(daysToKeepInt, numToKeepInt, artifactDaysToKeepInt, artifactNumToKeepInt)
      logRotator : [7, -1, -1, -1], // OPTIONAL
      extendedEmail : integrateExtendedEmail, // OPTIONAL
      jabberNotification: jabberNotificationIntegrate, // OPTIONAL

******************************************************************************** **/

  static public def hostSpecificConfig = [
    integrate : [
      label : 'pineapple',
      timeout : 30,
      quietPeriod : 180,
      checkoutRetryCount : 1,
      rebuilderStep : rebuilderStepForIntegration,
      testngStep : testngStepForIntegration,
      //logRotator(daysToKeepInt, numToKeepInt, artifactDaysToKeepInt, artifactNumToKeepInt)
      logRotator : [7, -1, -1, -1],
      pipelineNotification: Values.pipelineNotificationChangeOnly,
      slackChannel: "#alert-build-integration",
      pipelineJob: true,
      githubPush: true,
    ],
    maint : [
      /** redmine #18103 **/
      label : 'pineapple',
      timeout : 30,
      checkoutRetryCount : 1,
      rebuilderStep : rebuilderStepForMaint,
      ignorePostCommitHooks : 'true',
      logRotator : [7, -1, -1, -1],
      description : maintDescription(),
      pipelineJob: true,
      githubPush: false,
    ],
    feature : [
      /** redmine #18965 **/
      label : 'fir',
      timeout : 60,
      checkoutRetryCount : 1,
      scmSchedule : scmScheduleNightly,
      rebuilderStep: rebuilderStepForQa,
      ignorePostCommitHooks : 'true',
      logRotator : [7, -1, -1, -1],
      description : featureDescription(),
      pipelineJob: true,
      githubPush: false,
    ],
    a2 : [
      label : 'fir',
      timeout : 60,
      checkoutRetryCount : 1,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      pipelineJob: true,
      githubPush: false,
    ],
    q1 : [
      label : 'watermelon',
      timeout : 90,
      scmSchedule : scmScheduleNightly,
      checkoutRetryCount : 1,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      apitestStep: apitestStepForQa,
      cacheStep: cacheStep,
      sitesearchStep: sitesearchStepForQa,
      pipelineNotification: pipelineNotificationChangeOnly,
      slackChannel: "#alert-build-qa",
      pipelineJob: true,
      githubPush: false,

    ],
    q2 : [
      label : 'fir',
      timeout : 90,
      scmSchedule : scmScheduleNightly,
      checkoutRetryCount : 1,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      apitestStep: apitestStepForQa,
      cacheStep: cacheStep,
      sitesearchStep: sitesearchStepForQa,
      pipelineNotification: pipelineNotificationChangeOnly,
      slackChannel: "#alert-build-qa",
      pipelineJob: true,
      githubPush: false,
    ],
    b1 : [
      label : 'watermelon',
      rebuilderStep: rebuilderStepForBeta,
      cacheStep: cacheStep,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      pipelineJob: true,
      githubPush: false,
    ],
    b2 : [
      label : 'fir',
      rebuilderStep: rebuilderStepForBeta,
      cacheStep: cacheStep,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      pipelineJob: true,
      githubPush: false,
    ],
    w1 : [
      label : 'watermelon',
      rebuilderStep: rebuilderStepForWww,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      sitesearchStep: sitesearchStepForWww,
      pipelineNotification: pipelineNotificationEveryBuild,
      slackChannel: "#alert-build-livesite",
      pipelineJob: true,
      githubPush: false,
    ],
    w2 : [
      label : 'fir',
      rebuilderStep: rebuilderStepForWww,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      sitesearchStep: sitesearchStepForWww,
      pipelineNotification: pipelineNotificationEveryBuild,
      slackChannel: "#alert-build-livesite",
      pipelineJob: true,
      githubPush: false,
    ],
  ]


  /** ********************************************************************************
    Job Description
  ******************************************************************************** **/
  static public def stdDescription(jobName, dslJob) {

    def thisBuild = Thread.currentThread().executable // a hudson.model.FreeStyleBuild
    def thisProject = thisBuild.project // a hudson.model.FreeStyleProject

    return """
Website build for <a href='http://${jobName}'>http://${jobName}</a>
<p>
See <a href="https://wiki.apidb.org/index.php/JenkinsWebsiteBuilds">JenkinsWebsiteBuilds wiki</a> for build overview.
<p>
<font color='red'>This project configuration is auto-generated by
<a href="/${thisProject.url}">${thisProject.displayName}</a>. <br>
SCM values are configured at https://github.com/VEuPathDB/websiteconf Other changes made through
the web UI will be lost.</font> <br>
(Generated by <a href="/${thisBuild.url}">${thisBuild.displayName}<a/>)
"""
  }


  static public def maintDescription() {

    def thisBuild = Thread.currentThread().executable // a hudson.model.FreeStyleBuild
    def thisProject = thisBuild.project // a hudson.model.FreeStyleProject

    return """
Websites to support Cristina's site/database maintenance procedures.
<p>
See <a href="https://wiki.apidb.org/index.php/JenkinsWebsiteBuilds">JenkinsWebsiteBuilds wiki</a> for build overview.
<p>
<font color='red'>This project configuration is auto-generated by
<a href="/${thisProject.url}">${thisProject.displayName}</a>. <br>
SCM values are configured at https://github.com/VEuPathDB/websiteconf Other changes made through
the web UI will be lost.</font> <br>
(Generated by <a href="/${thisBuild.url}">${thisBuild.displayName}<a/>)
"""
  }

  static public def featureDescription() {

    def thisBuild = Thread.currentThread().executable // a hudson.model.FreeStyleBuild
    def thisProject = thisBuild.project // a hudson.model.FreeStyleProject

    return """
See <a href='https://wiki.apidb.org/index.php/FeatureWebsites'>FeatureWebsites wiki</a> for overview.
<p>
See <a href="https://wiki.apidb.org/index.php/JenkinsWebsiteBuilds">JenkinsWebsiteBuilds wiki</a> for build overview.
<p>
<font color='red'>This project configuration is auto-generated by
<a href="/${thisProject.url}">${thisProject.displayName}</a>. <br>
SCM values are configured at https://github.com/VEuPathDB/websiteconf Other changes made through
the web UI will be lost.</font> <br>
(Generated by <a href="/${thisBuild.url}">${thisBuild.displayName}<a/>)
"""
  }

  /** ********************************************************************************
    Disable QA Jobs
  ******************************************************************************** **/
  def disableQABuilds(model, sld, tld) {
    {project -> project/publishers/'hudson.plugins.parameterizedtrigger.BuildTrigger' {
        'configs'  {
          'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' {
            'configs' {
              'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters' {
                properties "JENKINS_JOBS=q1.${sld}.${tld} q2.${sld}.${tld}"
              }
            }
            projects '~disablejobs'
            condition 'FAILED'
          }
        }
      }
    }
  }



} // Values class
