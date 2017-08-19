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
  static private def datasetSvnCredentialsId = '4450087d-31ca-4ea4-b48a-cd9aa1ea99b9'

  static public def productSpecificConfig = [
    AmoebaDB : [
      webapp : "amoeba",
      tld : "org",
    ],
    ClinEpiDB : [
      webapp : "ce",
      tld : "org",
    ],
    CryptoDB : [
      webapp : "cryptodb",
      tld : "org",
    ],
    EuPathDB : [
      webapp : "eupathdb",
      tld : "org",
    ],
    FungiDB : [
      webapp : "fungidb",
      tld : "org",
    ],
    GiardiaDB : [
      webapp : "giardiadb",
      tld : "org",
    ],
    HostDB : [
      webapp : "hostdb",
      tld : "org",
    ],
    MicrobiomeDB : [
      webapp : "mbio",
      tld : "org",
    ],
    MicrosporidiaDB : [
      webapp : "micro",
      tld : "org",
    ],
    PiroplasmaDB : [
      webapp : "piro",
      tld : "org",
    ],
    PlasmoDB : [
      webapp : "plasmo",
      tld : "org",
    ],
    SchistoDB : [
      webapp : "schisto",
      tld : "net",
    ],
    ToxoDB : [
      webapp : "toxo",
      tld : "org",
    ],
    TrichDB : [
      webapp : "trichdb",
      tld : "org",
    ],
    TriTrypDB : [
      webapp : "tritrypdb",
      tld : "org",
    ],
  ]



/** ********************************************************************************
REBUILDER
******************************************************************************** **/

  static public def rebuilderStepForIntegration = { host, product, webapp, tld ->
    return """
      date > .hudsonTimestamp
      ulimit -u 4096
      ulimit -n 4096
      env
      # Restarting tomcat interferes with maint websites, so stop this.
      #sudo instance_manager stop ${product} force
      #sleep 5
      #sudo instance_manager start  ${product} verbose
      #sleep 15

      # Copy Conifer site vars file from source in to etc.
      src_yml="\$WORKSPACE/EbrcWebsiteCommon/Model/lib/conifer/roles/conifer/vars/ebrc_prod_site_vars.yml"
      dest_yml="/var/www/${host}.${product.toLowerCase()}.${tld}/etc/conifer_site_vars.yml"
      cp "\$src_yml" \
          "\$dest_yml"
          sed -i "1i# DO NOT EDIT!\n# This file copied from\n# \$src_yml,\n# $(date)\n# by Jenkins\n\n" "\$dest_yml"

      \$HOME/bin/rebuilder-jenkins ${host}.${product.toLowerCase()}.${tld} --webapp ${product}:${webapp}.integrate
      # give webapp time to reload before running tests
      sleep 30
    """
    .stripIndent()
  }


  /** Cristina maint.*.org websites for testing apicomm maintenance scripts, etc. **/
  static public def rebuilderStepForMaint = { host, product, webapp, tld ->
    return """
      date > .hudsonTimestamp
      ulimit -u 4096
      ulimit -n 4096
      env

      # Copy Conifer site vars file from source in to etc.
      src_yml="\$WORKSPACE/EbrcWebsiteCommon/Model/lib/conifer/roles/conifer/vars/ebrc_maint_site_vars.yml"
      dest_yml="/var/www/${host}.${product.toLowerCase()}.${tld}/etc/conifer_site_vars.yml"
      cp "\$src_yml" \
          "\$dest_yml"
          sed -i "1i# DO NOT EDIT!\n# This file copied from\n# \$src_yml,\n# $(date)\n# by Jenkins\n\n" "\$dest_yml"

      \$HOME/bin/rebuilder-jenkins ${host}.${product.toLowerCase()}.${tld} --webapp ${product}:${webapp}.maint
    """
    .stripIndent()
  }


  static public def rebuilderStepForWdkTemplate = { host, product, webapp, tld ->
    return """
      date > .hudsonTimestamp
      env
      sudo instance_manager stop ${product} force
      sleep 5
      sudo instance_manager start  ${product} verbose
      sleep 15
      \$HOME/bin/rebuilder-jenkins ${host}.apidb.org --webapp ${product}:${webapp} --ignore-ip
      \$HOME/bin/resetWdkPgTestDb
    """
    .stripIndent()
  }


  static public def rebuilderStepForQa = { host, product, webapp, tld ->
    return """
      env

      # Copy Conifer site vars file from source in to etc.
      src_yml="\$WORKSPACE/EbrcWebsiteCommon/Model/lib/conifer/roles/conifer/vars/ebrc_prod_site_vars.yml"
      dest_yml="/var/www/${host}.${product.toLowerCase()}.${tld}/etc/conifer_site_vars.yml"
      cp "\$src_yml" \
          "\$dest_yml"
          sed -i "1i# DO NOT EDIT!\n# This file copied from\n# \$src_yml,\n# $(date)\n# by Jenkins\n\n" "\$dest_yml"

      \$HOME/bin/rebuilder-jenkins ${host}.${product.toLowerCase()}.${tld}

      # give webapp time to reload before running tests
      sleep 15

      ## cache public strategy results (redmine #18944) with non-debug logging
      ## Disabled: it seems of limited benefit for QA and it slows builds.
      ## It could be useful as a pre-release check of strategies but
      ## there's no useable reporting so failures will go unnoticed.
      #source /var/www/${host}.${product.toLowerCase()}.${tld}/etc/setenv
      #if [[ -e "\$GUS_HOME/bin/wdkRunPublicStrats" ]]; then
      #  export GUSJVMOPTS='-Dlog4j.configuration=file:\$PROJECT_HOME/WDK/Model/config/log4j.info.properties'
      #  wdkRunPublicStrats -model ${product}
      #fi
    """
    .stripIndent()
  }

  static public def rebuilderStepWithJava7 = { host, product, webapp, tld ->
    return """
      env
      # Using Java 7
      \$HOME/bin/rebuilder-jenkins ${host}.${product.toLowerCase()}.${tld}  --java-home /usr/java/jdk1.7.0_80
    """
    .stripIndent()
  }

  static public def rebuilderStepForWww = { host, product, webapp, tld ->
    return """
      env
      \$HOME/bin/rebuilder-jenkins ${host}.${product.toLowerCase()}.${tld} --webapp ${product}:${webapp}
      sleep 15

      # cache public strategy results (redmine #18944) with non-debug logging
      source /var/www/${host}.${product.toLowerCase()}.${tld}/etc/setenv
      if [[ -e "\$GUS_HOME/bin/wdkRunPublicStrats" ]]; then
        export GUSJVMOPTS='-Dlog4j.configuration=file:\$PROJECT_HOME/WDK/Model/config/log4j.info.properties'
        wdkRunPublicStrats -model ${product}
      fi
    """
    .stripIndent()
  }


/** ********************************************************************************
TEST NG
******************************************************************************** **/

  static public def testngStepForIntegration = { host, product, webapp, tld ->
    return {
      targets(['cleantestresults', 'cleaninstall', 'testbynames'])
      props('proj':'EbrcWebsiteCommon', 'comp':'Watar', 'targetDir':'\$WORKSPACE/test_home',
        'projectsDir':'\$WORKSPACE', 'baseurl':"http://${host}.${product.toLowerCase()}.${tld}",
        'webappname':"${webapp}.integrate", 'testnames':'"Integration"', 'msTimeout':"30000")
      buildFile 'EbrcWebsiteCommon/Watar/build.xml'
    }
  }

  static public def testngStepForQa = { host, product, webapp, tld ->
    return {
      targets(['cleantestresults', 'cleaninstall', 'testbynames'])
      props('proj':'EbrcWebsiteCommon', 'comp':'Watar', 'targetDir':'\$WORKSPACE/test_home',
        'projectsDir':'\$WORKSPACE', 'baseurl':"http://${host}.${product.toLowerCase()}.${tld}",
        'webappname':"${webapp}", 'testnames':'"QA"', 'msTimeout':"30000")
      buildFile 'EbrcWebsiteCommon/Watar/build.xml'
    }
  }

/** ********************************************************************************
Extended Email
******************************************************************************** **/
  static public def integrateExtendedEmail = { delegate ->
    delegate.extendedEmail('mheiges@uga.edu', '$DEFAULT_SUBJECT', '${JELLY_SCRIPT,template="eupath-email-ext"}') {
        trigger(
          triggerName: 'Unstable',
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: false,
          sendToRequester: true,
          includeCulprits: false,
          sendToRecipientList: false,
        )
        trigger(
          triggerName: 'Failure',
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: true,
          sendToRequester: true,
          includeCulprits: true,
          sendToRecipientList: false,
        )
        configure { node ->
            node / contentType << 'default'
        }
    }
  } // integrateExtendedEmail closure

  static public def qaExtendedEmail = { delegate ->
    delegate.extendedEmail('mheiges@uga.edu', '$DEFAULT_SUBJECT', '${JELLY_SCRIPT,template="eupath-email-ext"}') {
        trigger(
          triggerName: 'Unstable',
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: false,
          sendToRequester: true,
          includeCulprits: false,
          sendToRecipientList: true,
        )
        trigger(
          triggerName: 'Failure',
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: false,
          sendToRequester: true,
          includeCulprits: false,
          sendToRecipientList: true,
        )
        configure { node ->
            node / contentType << 'default'
        }
    }
  } // qaExtendedEmail

  static public def wwwExtendedEmail = { delegate ->
    delegate.extendedEmail('mheiges@uga.edu', '$DEFAULT_SUBJECT', '${JELLY_SCRIPT,template="eupath-email-ext"}') {
        trigger(
          triggerName: 'Unstable',
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: false,
          sendToRequester: true,
          includeCulprits: false,
          sendToRecipientList: true,
        )
        trigger(
          triggerName: 'Failure',
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: false,
          sendToRequester: true,
          includeCulprits: false,
          sendToRecipientList: true,
        )
        configure { node ->
            node / contentType << 'default'
        }
    }
  } // wwwExtendedEmail

  static public def maintExtendedEmail = { delegate ->
    delegate.extendedEmail('aurreco@uga.edu', '$DEFAULT_SUBJECT', '${JELLY_SCRIPT,template="eupath-email-ext"}') {
        trigger(
          triggerName: 'Unstable',
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: false,
          sendToRequester: true,
          includeCulprits: false,
          sendToRecipientList: true,
        )
        trigger(
          triggerName: 'Failure',
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: false,
          sendToRequester: true,
          includeCulprits: false,
          sendToRecipientList: true,
        )
        configure { node ->
            node / contentType << 'default'
        }
    }
  } // qaExtendedEmail

/** ********************************************************************************
JABBER
******************************************************************************** **/
  static public def jabberNotificationIntegrate = { contacts ->
    if ( contacts == null ) return {}
    {
      project -> project/publishers/'hudson.plugins.jabber.im.transport.JabberPublisher' {
        targets {
          contacts.each { contact ->
           'hudson.plugins.im.DefaultIMMessageTarget' { value contact }
          }
        }
        strategy 'FAILURE_AND_FIXED'
        notifyOnBuildStart false
        notifySuspects false
        notifyCulprits false
        notifyFixers false
        notifyUpstreamCommitters false
      }
    }
  }

  static public def jabberNotificationWww = { contacts ->
    if ( contacts == null ) return {}
    {
      project -> project/publishers/'hudson.plugins.jabber.im.transport.JabberPublisher' {
        targets {
          contacts.each { contact ->
           'hudson.plugins.im.DefaultIMMessageTarget' { value contact }
          }
        }
        strategy 'ALL'
        notifyOnBuildStart false
        notifySuspects false
        notifyCulprits false
        notifyFixers false
        notifyUpstreamCommitters false
      }
    }
  }

/** ********************************************************************************
SCM POLL SCHEDULE
******************************************************************************** **/
  static public def scmScheduleAsap = '*/5 * * * *'
  static public def scmScheduleNightly = '0 3 * * *'
  static public def scmScheduleYearly = '@yearly'

/** ********************************************************************************
JABBER CONTACTS
******************************************************************************** **/
  static public def jabberContactsIntegrate = ['caurreco@gmail.com']
  static public def jabberContactsProduction = ['mheiges@apidb.org', 'caurreco@gmail.com']


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
      label : 'santol',
      timeout : 30,
      scmSchedule : scmScheduleYearly,
      ignorePostCommitHooks : 'false',
      quietPeriod : 180,
      checkoutRetryCount : 1,
      rebuilderStep : rebuilderStepForIntegration,
      testngStep : testngStepForIntegration,
      jabberContacts : jabberContactsIntegrate,
      //logRotator(daysToKeepInt, numToKeepInt, artifactDaysToKeepInt, artifactNumToKeepInt)
      logRotator : [7, -1, -1, -1],
      extendedEmail : integrateExtendedEmail,
      jabberNotification: jabberNotificationIntegrate,
    ],
    maint : [
      /** redmine #18103 **/
      label : 'santol',
      timeout : 30,
      checkoutRetryCount : 1,
      rebuilderStep : rebuilderStepForMaint,
      ignorePostCommitHooks : 'true',
      extendedEmail : maintExtendedEmail,
      jabberContacts : jabberContactsIntegrate,
      jabberNotification: jabberNotificationWww,
      logRotator : [7, -1, -1, -1],
      description : "Websites to support Cristina's site/database maintenance procedures",
    ],
    feature : [
      /** redmine #18965 **/
      label : 'pine',
      timeout : 30,
      checkoutRetryCount : 1,
      scmSchedule : scmScheduleNightly,
      rebuilderStep: rebuilderStepForQa,
      ignorePostCommitHooks : 'true',
      extendedEmail : maintExtendedEmail,
      jabberContacts : jabberContactsIntegrate,
      jabberNotification: jabberNotificationWww,
      logRotator : [7, -1, -1, -1],
      description : "See <a href='https://wiki.apidb.org/index.php/FeatureWebsites'>FeatureWebsites wiki</a> for overview.",
    ],
    a2 : [
      label : 'pine',
      timeout : 60,
      checkoutRetryCount : 1,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      extendedEmail : qaExtendedEmail,
      jabberContacts: jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    q1 : [
      label : 'myrtle',
      timeout : 60,
      scmSchedule : scmScheduleNightly,
      checkoutRetryCount : 1,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      extendedEmail : qaExtendedEmail,
      jabberContacts: jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    q2 : [
      label : 'pine',
      timeout : 60,
      scmSchedule : scmScheduleNightly,
      checkoutRetryCount : 1,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      extendedEmail : qaExtendedEmail,
      jabberContacts: jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    w1 : [
      label : 'myrtle',
      rebuilderStep: rebuilderStepForWww,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      extendedEmail : wwwExtendedEmail,
      jabberContacts : jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    w2 : [
      label : 'pine',
      rebuilderStep: rebuilderStepForWww,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      extendedEmail : wwwExtendedEmail,
      jabberContacts : jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    r28 : [
      label : 'myrtle',
      rebuilderStep: rebuilderStepWithJava7,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      extendedEmail : wwwExtendedEmail,
      jabberContacts : jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
  ]




  /** ********************************************************************************
    Default svn urls for jobs that do not have an existing SCM configuration.
  ******************************************************************************** **/
  static public def svnDefaultLocations = ([
    'install'             : 'https://cbilsvn.pmacs.upenn.edu/svn/gus/install/trunk',
    'ApiCommonDatasets'   : 'https://cbilsvn.pmacs.upenn.edu/svn/apidb/ApiCommonDatasets/trunk',
    'ApiCommonModel'      : 'https://cbilsvn.pmacs.upenn.edu/svn/apidb/ApiCommonModel/trunk',
    'ApiCommonPresenters' : 'https://cbilsvn.pmacs.upenn.edu/svn/apidb/ApiCommonPresenters/trunk',
    'ApiCommonWebService' : 'https://cbilsvn.pmacs.upenn.edu/svn/apidb/ApiCommonWebService/trunk',
    'ApiCommonWebsite'    : 'https://cbilsvn.pmacs.upenn.edu/svn/apidb/ApiCommonWebsite/trunk',
    'CBIL'                : 'https://cbilsvn.pmacs.upenn.edu/svn/gus/CBIL/trunk',
    'EbrcModelCommon'     : 'https://cbilsvn.pmacs.upenn.edu/svn/apidb/EbrcModelCommon/trunk',
    'EbrcWebsiteCommon'   : 'https://cbilsvn.pmacs.upenn.edu/svn/apidb/EbrcWebsiteCommon/trunk',
    'EbrcWebSvcCommon'    : 'https://cbilsvn.pmacs.upenn.edu/svn/apidb/EbrcWebSvcCommon/trunk',
    'FgpUtil'             : 'https://cbilsvn.pmacs.upenn.edu/svn/gus/FgpUtil/trunk',
    'GBrowse'             : 'https://cbilsvn.pmacs.upenn.edu/svn/apidb/GBrowse/trunk',
    'install'             : 'https://cbilsvn.pmacs.upenn.edu/svn/gus/install/trunk',
    'ReFlow'              : 'https://cbilsvn.pmacs.upenn.edu/svn/gus/ReFlow/trunk',
    'WDK'                 : 'https://cbilsvn.pmacs.upenn.edu/svn/gus/WDK/trunk',
    'WSF'                 : 'https://cbilsvn.pmacs.upenn.edu/svn/gus/WSF/trunk',
  ]).asImmutable()

  static public def svnWdkTemplateLocations = ([
    'CBIL'                :   'https://cbilsvn.pmacs.upenn.edu/svn/gus/CBIL/trunk',
    'FgpUtil'             :   'https://cbilsvn.pmacs.upenn.edu/svn/gus/FgpUtil/trunk',
    'install'             :   'https://cbilsvn.pmacs.upenn.edu/svn/gus/install/trunk',
    'WDK'                 :   'https://cbilsvn.pmacs.upenn.edu/svn/gus/WDK/trunk',
    'WSF'                 :   'https://cbilsvn.pmacs.upenn.edu/svn/gus/WSF/trunk',
    'WDKTemplateSite'     :   'https://cbilsvn.pmacs.upenn.edu/svn/gus/WDKTemplateSite/trunk',
    'EbrcWebsiteCommon'   :   'https://cbilsvn.pmacs.upenn.edu/svn/apidb/EbrcWebsiteCommon/trunk',
  ]).asImmutable()


  /** ********************************************************************************
    Job Description
  ******************************************************************************** **/
  static public def stdDescription(jobName, dslJob) {

    def thisBuild = Thread.currentThread().executable // a hudson.model.FreeStyleBuild
    def thisProject = thisBuild.project // a hudson.model.FreeStyleProject

    return """
Website build for <a href='http://${jobName}'>http://${jobName}</a>
<p>
See <a href="https://mango.ctegd.uga.edu/apiwiki/index.php/JenkinsWebsiteBuilds">JenkinsWebsiteBuilds wiki</a> for build overview.
<p>
<font color='red'>This project configuration is auto-generated by
<a href="/${thisProject.url}">${thisProject.displayName}</a>. <br>
Manual changes to SCM locations are persistent but other configuration changes made through
the web UI will be lost.</font> <br>
(Generated by <a href="/${thisBuild.url}">${thisBuild.displayName}<a/>)
"""
  }


  /** ********************************************************************************
    Disable QA Jobs
  ******************************************************************************** **/
  def disableQABuilds(product, tld) {
    {project -> project/publishers/'hudson.plugins.parameterizedtrigger.BuildTrigger' {
        'configs'  {
          'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' {
            'configs' {
              'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters' {
                properties "JENKINS_JOBS=q1.${product.toLowerCase()}.${tld} q2.${product.toLowerCase()}.${tld}"
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
