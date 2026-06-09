public class Values {

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
            --gusjvmopts '-Dlog4j.configuration=file:/var/www/${host}.${sld}.${tld}/project_home/WDK/Model/config/log4j.info.properties'
          
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
            --gusjvmopts '-Dlog4j.configuration=file:/var/www${host}.${sld}.${tld}/project_home/WDK/Model/config/log4j.info.properties'
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
    // We don't run sitesearch updates for dataExplorer
    if ( model == "ClinEpiDB") return null

    return """
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
        
        podman pull docker.io/veupathdb/site-search-nextflow:\$IMAGE_BRANCH || { echo "problem pulling veupathdb/site-search-nextflow:\$IMAGE_BRANCH"; exit -1; }
        podman pull docker.io/veupathdb/site-search-data:\$IMAGE_BRANCH || { echo "problem pulling veupathdb/site-search-data:\$IMAGE_BRANCH"; exit -1; }
        
        #start the podman socket so nextflow container can use it.
        systemctl --user start podman.socket
        
        OUTPUT_DIR=\$(mktemp -dt sitesearch-XXXXX)

        CONTAINER_ENV=/var/www/${host}.${sld}.${tld}/gus_home/config/$model/container_env

        podman run --rm \\
          --security-opt label=type:container_runtime_t \\
          --userns=keep-id:uid=1000,gid=1000 \\
          -v \${XDG_RUNTIME_DIR}/podman/podman.sock:/run/podman/podman.sock:z \\
          -e CONTAINER_HOST=unix:///run/podman/podman.sock \\
          -e IMAGE_BRANCH=\$IMAGE_BRANCH \\
          -e OUTPUT_DIR=\$OUTPUT_DIR \\
          -e ENV_FILE=\$CONTAINER_ENV \\
          -e CLEANUP=false -e UNCONFINED=false \\
          --env-file \$CONTAINER_ENV \\
          -v \$OUTPUT_DIR:\$OUTPUT_DIR:z \\
          -v \$CONTAINER_ENV:\$CONTAINER_ENV:z \\
          docker.io/veupathdb/site-search-nextflow:\$IMAGE_BRANCH runWebsiteBuild.sh
    """
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

/** ****************************************************************************
 AUTHORIZATION
 Optional. No params. Returns the data used to build the job's
 authorizationMatrix (see JobConfigurator.createPipelineJob).
 inherit           : false => nonInheriting(), true => inheriting()
 entries           : list of [name: <user-or-group>, permissions: [<perm>, ...]]
 **************************************************************************** **/
  static public def authorizationForQA = {
    return [
        inherit: true,
//        userPerms: [],
        groupPerms: [
            [ name: 'EuPathDBStaff', permissions: ['hudson.model.Item.Build', 'hudson.model.Item.Cancel'] ],
        ],
    ]
  }


  /** ********************************************************************************
    Job Description
  ******************************************************************************** **/
  static public def stdDescription(jobName, dslJob) {

    def thisBuild = Thread.currentThread().executable // a hudson.model.FreeStyleBuild
    def thisProject = thisBuild.project // a hudson.model.FreeStyleProject

    return """
      <h4>Website build for <a href='http://${jobName}'>http://${jobName}</a></h4>
      <p>
        See <a href="https://veupathdb.atlassian.net/wiki/spaces/SYSTEMS/pages/285933579/Adding+website+build+jobs+to+Jenkins">
        Adding website build jobs to Jenkins</a> page in Confluence for build overview.
      </p>
      <p>
        This project configuration is auto-generated by <a href="/${thisProject.url}">${thisProject.displayName}</a>
         in build <a href="/${thisBuild.url}">${thisBuild.displayName}<a/>
      </p>
      <p> SCM values are configured at <a href="https://github.com/VEuPathDB/websiteconf">
        https://github.com/VEuPathDB/websiteconf</a>.
      </p>
      <h3>Changes made through the web UI will be lost!</h3>
    """.stripIndent()
  }

} // Values class
