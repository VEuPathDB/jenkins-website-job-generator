public class Values {

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
    
          # rename jenkins supplied environment vars to what the build requires
          export GITHUB_USERNAME="\$GITHUB_READONLY_USR"
          export GITHUB_TOKEN="\$GITHUB_READONLY_PSW"

          /usr/local/bin/rebuilder ${host}.${sld}.${tld} \\
            --skip-scm-update --non-interactive \\
            --m2-repo /var/www/${host}.${sld}.${tld}/project_home/.m2/repository \\
            --yarn-cache /var/www/${host}.${sld}.${tld}/project_home/.cache/yarn \\
            --gusjvmopts '-Dlog4j.configuration=file:${host}.${sld}.${tld}/project_home/WDK/Model/config/log4j.info.properties'
          
          # this would only be needed if we reenable the step below 
          #sleep 15
             
          # cache public strategy results (redmine #18944) with non-debug logging
          #source /var/www/${host}.${sld}.${tld}/etc/setenv
          #if [[ -e "\$GUS_HOME/bin/wdkRunPublicStrats" ]]; then
            #export GUSJVMOPTS='-Dlog4j.configuration=file:\$PROJECT_HOME/WDK/Model/config/log4j.info.properties'
            # disable wdkRunPublicStrats until slow queries in Fungi,plasmo,tritryp can be examined (9/11/2017)
            #wdkRunPublicStrats -model ${model}
          #fi
    """
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
    
          # \$HOME/bin/rebuilder-jenkins ${host}.${sld}.${tld} --webapp ${model}:${webapp}
          
          # rename jenkins supplied environment vars to what the build requires
          export GITHUB_USERNAME="\$GITHUB_READONLY_USR"
          export GITHUB_TOKEN="\$GITHUB_READONLY_PSW"
          
          /usr/local/bin/rebuilder ${host}.${sld}.${tld} \\
            --skip-scm-update --non-interactive --publish-docs \\
            --m2-repo /var/www/${host}.${sld}.${tld}/project_home/.m2/repository \\
            --yarn-cache /var/www/${host}.${sld}.${tld}/project_home/.cache/yarn \\
            --webapp ${model}:${webapp} \\
            --gusjvmopts '-Dlog4j.configuration=file:${host}.${sld}.${tld}/project_home/WDK/Model/config/log4j.info.properties'
          sleep 15
    
          # cache public strategy results (redmine #18944) with non-debug logging
           source /var/www/${host}.${sld}.${tld}/etc/setenv
          if [[ -e "\$GUS_HOME/bin/wdkRunPublicStrats" ]]; then
            export GUSJVMOPTS='-Dlog4j.configuration=file:\$PROJECT_HOME/WDK/Model/config/log4j.info.properties'
            # disable wdkRunPublicStrats until slow queries in Fungi,plasmo,tritryp can be examined (9/11/2017)
            # wdkRunPublicStrats -model ${model}
          fi
    """
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
    echo "This is the api testing step for ${host}.${sld}.${tld}"

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
    time wdkCacheSeeder

    echo "Site cache build finished"
    """
    .stripIndent()
  }

/** ********************************************************************************
Sitesearch step
******************************************************************************** **/

  static public def sitesearchStep = { host, model, webapp, sld, tld, lifecycle ->
    // don't run update for portal
    if( model != "EuPathDB") {
      return """
          # only run if container_env exists (this restricts currently to
          # ApicommonWebsite sites, which isn't strictly "sitesearch enabled sites",
          # but since the script requires it, it is a harmless check regardless)
      
          if [ -e /var/www/${host}.${sld}.${tld}/gus_home/config/$model/container_env ]
          then
            source /var/www/${host}.${sld}.${tld}/etc/setenv

            case "$lifecycle" in
              beta)
                IMAGE_BRANCH=beta
                ;;
              dev)
                IMAGE_BRANCH=latest
                ;;
              qa)
                IMAGE_BRANCH=qa
                ;;
              prod)
                IMAGE_BRANCH=prod
                ;;
            esac
            
            echo "image branch is \${IMAGE_BRANCH}"
            echo "core is \${CORE}"
            
            podman pull docker.io/veupathdb/site-search-data:\$IMAGE_BRANCH || { echo "problem pulling veupathdb/site-search-data:\$IMAGE_BRANCH"; exit -1; }
            
            podman run --rm \
              --sysctl net.ipv6.conf.all.disable_ipv6=1 \\
              --network=pasta:"--map-host-loopback=169.254.1.2" \\
              --env TNS_ADMIN=/jdbc/network/admin \\
              --env-file=/var/www/${host}.${sld}.${tld}/gus_home/config/${model}/container_env \\
              --add-host=solr-sitesearch-${lifecycle}.local.apidb.org:169.254.1.2 \\
              --volume=\$ORACLE_HOME/network/admin/ldap.ora:/jdbc/network/admin/ldap.ora \\
              -it docker.io/veupathdb/site-search-data:\$IMAGE_BRANCH \\
              presenter_update.sh
          fi
      """
    }
    else {
      return null
    }
  }

  static public def sitesearchStepForWww = { host, model, webapp, sld, tld ->
    return sitesearchStep.call(host, model, webapp, sld, tld, "prod")
  }

  static public def sitesearchStepForBeta = { host, model, webapp, sld, tld ->
    return sitesearchStep.call(host, model, webapp, sld, tld, "beta")
  }

  static public def sitesearchStepForQa = { host, model, webapp, sld, tld ->
    return sitesearchStep.call(host, model, webapp, sld, tld, "qa")
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


    notifications['begin'] = """\
      script {
        slackResponse = slackSend(
          channel: "${channel}",
          message: "Starting Job '\${env.JOB_NAME} [\${env.BUILD_NUMBER}]' Check console output at \${env.BUILD_URL}"
        )
      }
    """.stripIndent()

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
      //logRotator(daysToKeepInt, numToKeepInt, artifactDaysToKeepInt, artifactNumToKeepInt)
      logRotator : [7, -1, -1, -1], // OPTIONAL

******************************************************************************** **/

  static public def hostSpecificConfig = [
    beta : [
      label : 'palm',
      folder: 'site-builds/beta',
      scmSchedule : scmScheduleNightly,
      ignorePostCommitHooks : true,
      rebuilderStep: rebuilderStepForBeta,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
//      sitesearchStep: sitesearchStepForBeta,
      pipelineNotification: pipelineNotificationEveryBuild,
      slackChannel: "#alert-build-livesite",
      githubPush: false,
    ],
    w5 : [
      label : 'webtest',
      folder: 'site-builds/prod',
      rebuilderStep: rebuilderStepForWww,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      sitesearchStep: sitesearchStepForWww,
      pipelineNotification: pipelineNotificationEveryBuild,
      slackChannel: "#alert-build-livesite-test",
      githubPush: false,
    ],
//    integrate : [
//      label : 'pineapple',
//      folder: 'site-builds/integrate',
//      timeout : 30,
//      quietPeriod : 180,
//      checkoutRetryCount : 1,
//      rebuilderStep : rebuilderStepForIntegration,
//      testngStep : testngStepForIntegration,
//      //logRotator(daysToKeepInt, numToKeepInt, artifactDaysToKeepInt, artifactNumToKeepInt)
//      logRotator : [7, -1, -1, -1],
//      pipelineNotification: Values.pipelineNotificationChangeOnly,
//      slackChannel: "#alert-build-integration",
//      githubPush: true,
//    ],
//    feature : [
//      /** redmine #18965 **/
//      label : 'fir',
//      folder: 'site-builds/feature',
//      timeout : 60,
//      checkoutRetryCount : 1,
//      scmSchedule : scmScheduleNightly,
//      rebuilderStep: rebuilderStepForQa,
//      ignorePostCommitHooks : 'true',
//      logRotator : [7, -1, -1, -1],
//      description : featureDescription(),
//      githubPush: false,
//    ],
//    q1 : [
//      label : 'watermelon',
//      folder: 'site-builds/qa',
//      timeout : 90,
//      scmSchedule : scmScheduleNightly,
//      checkoutRetryCount : 1,
//      rebuilderStep: rebuilderStepForQa,
//      testngStep: testngStepForQa,
//      apitestStep: apitestStepForQa,
//      cacheStep: cacheStep,
//      sitesearchStep: sitesearchStepForQa,
//      pipelineNotification: pipelineNotificationChangeOnly,
//      slackChannel: "#alert-build-qa",
//      githubPush: false,
//    ],
//    q2 : [
//      label : 'fir',
//      folder: 'site-builds/qa',
//      timeout : 90,
//      scmSchedule : scmScheduleNightly,
//      checkoutRetryCount : 1,
//      rebuilderStep: rebuilderStepForQa,
//      testngStep: testngStepForQa,
//      apitestStep: apitestStepForQa,
//      cacheStep: cacheStep,
//      sitesearchStep: sitesearchStepForQa,
//      pipelineNotification: pipelineNotificationChangeOnly,
//      slackChannel: "#alert-build-qa",
//      githubPush: false,
//    ],
//    b1 : [
//      label : 'watermelon',
//      folder: 'site-builds/beta',
//      rebuilderStep: rebuilderStepForBeta,
//      cacheStep: cacheStep,
//      checkoutRetryCount : 1,
//      logRotator : [-1, 50, -1, -1],
//      githubPush: false,
//    ],
//    b2 : [
//      label : 'cedar',
//      folder: 'site-builds/beta',
//      rebuilderStep: rebuilderStepForBeta,
////      cacheStep: cacheStep,
//      checkoutRetryCount : 1,
//      logRotator : [-1, 50, -1, -1],
//      pipelineNotification: pipelineNotificationEveryBuild,
//      slackChannel: "#alert-build-livesite-test",
//      githubPush: false,
//    ],
//    w1 : [
//      label : 'watermelon',
//      folder: 'site-builds/prod',
//      rebuilderStep: rebuilderStepForWww,
//      checkoutRetryCount : 1,
//      logRotator : [-1, 50, -1, -1],
//      sitesearchStep: sitesearchStepForWww,
//      pipelineNotification: pipelineNotificationEveryBuild,
//      slackChannel: "#alert-build-livesite",
//      githubPush: false,
//    ],
//    w2 : [
//      label : 'fir',
//      folder: 'site-builds/prod',
//      rebuilderStep: rebuilderStepForWww,
//      checkoutRetryCount : 1,
//      logRotator : [-1, 50, -1, -1],
//      sitesearchStep: sitesearchStepForWww,
//      pipelineNotification: pipelineNotificationEveryBuild,
//      slackChannel: "#alert-build-livesite",
//      githubPush: false,
//    ],
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

} // Values class
