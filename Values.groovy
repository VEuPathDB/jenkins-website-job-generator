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
      webapp : "eupathdb",
      sld : "eupathdb",
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
    SchistoDB : [
      webapp : "schisto",
      sld: "schistodb",
      tld : "net",
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
    VEuPathDB : [
//      scmSchedule : scmScheduleNightlyLate,
      scmSchedule : 'H H(3-4) * * *',
      webapp : "veupathdb",
      sld : "veupathdb",
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
        'projectsDir':'\$WORKSPACE', 'baseurl':"http://${host}.${sld}.${tld}",
        'webappname':"${webapp}.integrate", 'testnames':'"Integration"', 'msTimeout':"30000")
      buildFile 'EbrcWebsiteCommon/Watar/build.xml'
    }
  }

  static public def testngStepForQa = { host, model, webapp, sld, tld ->
    return {
      targets(['cleantestresults', 'cleaninstall', 'testbynames'])
      props('proj':'EbrcWebsiteCommon', 'comp':'Watar', 'targetDir':'\$WORKSPACE/test_home',
        'projectsDir':'\$WORKSPACE', 'baseurl':"http://${host}.${sld}.${tld}",
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

    export REPOSRC=https://github.com/EuPathDB-Infra/wdk-api-test
    export LOCALREPO=wdk-api-test
    git clone \$REPOSRC \$LOCALREPO || (cd \$LOCALREPO ; git pull)

    # domains are substituted here via groovy, to keep the if logic in bash,
    # but this ends up looking weird in the generated job... sorry.
    if [[ "${sld}" == "eupathdb" || "${sld}" == "clinepidb" ]]
    then
        export SCHEME="https"
    else
        export SCHEME="http"
    fi

    # follow redirect to get full url.  /service does not pass through redirect
    export SITE_PATH=\$(curl -s -I \$SCHEME://${host}.${sld}.${tld} | awk '/Location/{printf \$2}' | tr -d '[:space:]' )

    cd wdk-api-test
    ./run -c -a \$API_CREDS

    """
    .stripIndent()
  }

/** ********************************************************************************
Cache building step
******************************************************************************** **/

  static public def cacheStep = { host, model, webapp, sld, tld ->
    return {
         trigger('site-cache-build') {
            condition('SUCCESS')
            parameters {
              predefinedProp('SITE_NAME', "${host}.${sld}.${tld}")
            }
         }
      }
  }


/** ********************************************************************************
Extended Email
******************************************************************************** **/
  static public def integrateExtendedEmail = { delegate ->
    delegate.extendedEmail {
      recipientList('aurreco@uga.edu')
      defaultSubject('$DEFAULT_SUBJECT')
      defaultContent('${JELLY_SCRIPT,template="eupath-email-ext"}')
      contentType('text/plain')
      triggers {
        unstable {
          subject('$PROJECT_DEFAULT_SUBJECT')
          content('$PROJECT_DEFAULT_CONTENT')
          sendTo {
            requester()
          }
        }
        failure {
          subject('$PROJECT_DEFAULT_SUBJECT')
          content('$PROJECT_DEFAULT_CONTENT')
          sendTo {
            developers()
            requester()
            culprits()
          }
        }
      }
    }
  } // integrateExtendedEmail closure

  static public def qaExtendedEmail = { delegate ->
    delegate.extendedEmail {
      recipientList('robert.belnap@uga.edu,aurreco@uga.edu')
      defaultSubject('$DEFAULT_SUBJECT')
      defaultContent('${JELLY_SCRIPT,template="eupath-email-ext"}')
      contentType('text/plain')
      triggers {
        unstable {
          subject('$PROJECT_DEFAULT_SUBJECT')
          content('$PROJECT_DEFAULT_CONTENT')
          sendTo {
            requester()
            recipientList()
          }
        }
        failure {
          subject('$PROJECT_DEFAULT_SUBJECT')
          content('$PROJECT_DEFAULT_CONTENT')
          sendTo {
            requester()
            recipientList()
          }
        }
      }
    }
  } // qaExtendedEmail

  static public def wwwExtendedEmail = { delegate ->
    delegate.extendedEmail {
      recipientList('robert.belnap@uga.edu,aurreco@uga.edu')
      defaultSubject('$DEFAULT_SUBJECT')
      defaultContent('${JELLY_SCRIPT,template="eupath-email-ext"}')
      contentType('text/plain')
      triggers {
        unstable {
          subject('$PROJECT_DEFAULT_SUBJECT')
          content('$PROJECT_DEFAULT_CONTENT')
          sendTo {
            requester()
            recipientList()
          }
        }
        failure {
          subject('$PROJECT_DEFAULT_SUBJECT')
          content('$PROJECT_DEFAULT_CONTENT')
          sendTo {
            requester()
            recipientList()
          }
        }
      }
    }
  } // wwwExtendedEmail

  static public def maintExtendedEmail = { delegate ->
    delegate.extendedEmail {
      recipientList('aurreco@uga.edu')
      defaultSubject('$DEFAULT_SUBJECT')
      defaultContent('${JELLY_SCRIPT,template="eupath-email-ext"}')
      contentType('text/plain')
      triggers {
        unstable {
          subject('$PROJECT_DEFAULT_SUBJECT')
          content('$PROJECT_DEFAULT_CONTENT')
          sendTo {
            requester()
            recipientList()
          }
        }
        failure {
          subject('$PROJECT_DEFAULT_SUBJECT')
          content('$PROJECT_DEFAULT_CONTENT')
          sendTo {
            requester()
            recipientList()
          }
        }
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
  static public def scmScheduleAsap = 'H/5 * * * *'
  static public def scmScheduleNightly = 'H H(0-2) * * *'
  static public def scmScheduleNightlyLate = 'H H(3-4) * * *'
  static public def scmScheduleYearly = '@yearly'

/** ********************************************************************************
JABBER CONTACTS
******************************************************************************** **/
  static public def jabberContactsIntegrate = ['caurreco@gmail.com']
  static public def jabberContactsProduction = ['caurreco@gmail.com']


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
      scmSchedule : scmScheduleYearly,
      ignorePostCommitHooks : 'false',
      quietPeriod : 180,
      checkoutRetryCount : 1,
      scmSchedule : scmScheduleAsap,
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
      label : 'pineapple',
      timeout : 30,
      checkoutRetryCount : 1,
      rebuilderStep : rebuilderStepForMaint,
      ignorePostCommitHooks : 'true',
      extendedEmail : maintExtendedEmail,
      jabberContacts : jabberContactsIntegrate,
      jabberNotification: jabberNotificationWww,
      logRotator : [7, -1, -1, -1],
      description : maintDescription(),
    ],
    feature : [
      /** redmine #18965 **/
      label : 'pine',
      timeout : 60,
      checkoutRetryCount : 1,
      scmSchedule : scmScheduleNightly,
      rebuilderStep: rebuilderStepForQa,
      ignorePostCommitHooks : 'true',
      extendedEmail : maintExtendedEmail,
      jabberContacts : jabberContactsIntegrate,
      jabberNotification: jabberNotificationWww,
      logRotator : [7, -1, -1, -1],
      description : featureDescription(),
    ],
    a2 : [
      label : 'fir',
      timeout : 60,
      checkoutRetryCount : 1,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      extendedEmail : qaExtendedEmail,
      jabberContacts: jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    q1 : [
      label : 'watermelon',
      timeout : 90,
      scmSchedule : scmScheduleNightly,
      checkoutRetryCount : 1,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      cacheStep: cacheStep,
      extendedEmail : qaExtendedEmail,
      jabberContacts: jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    q2 : [
      label : 'fir',
      timeout : 60,
      scmSchedule : scmScheduleNightly,
      checkoutRetryCount : 1,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      cacheStep: cacheStep,
      extendedEmail : qaExtendedEmail,
      jabberContacts: jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    l1 : [
      label : 'myrtle',
      rebuilderStep: rebuilderStepForBeta,
      cacheStep: cacheStep,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      extendedEmail : wwwExtendedEmail,
      jabberContacts : jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    l2 : [
      label : 'pine',
      rebuilderStep: rebuilderStepForBeta,
      cacheStep: cacheStep,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      extendedEmail : wwwExtendedEmail,
      jabberContacts : jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    b1 : [
      label : 'myrtle',
      rebuilderStep: rebuilderStepForBeta,
      cacheStep: cacheStep,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      extendedEmail : wwwExtendedEmail,
      jabberContacts : jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    b2 : [
      label : 'pine',
      rebuilderStep: rebuilderStepForBeta,
      cacheStep: cacheStep,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      extendedEmail : wwwExtendedEmail,
      jabberContacts : jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    w1 : [
      label : 'watermelon',
      rebuilderStep: rebuilderStepForWww,
      checkoutRetryCount : 1,
      logRotator : [-1, 50, -1, -1],
      extendedEmail : wwwExtendedEmail,
      jabberContacts : jabberContactsProduction,
      jabberNotification: jabberNotificationWww,
    ],
    w2 : [
      label : 'fir',
      rebuilderStep: rebuilderStepForWww,
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
    'install'             : 'https://github.com/VEuPathDB/install/trunk@HEAD',
    'ApiCommonDatasets'   : 'https://github.com/VEuPathDB/ApiCommonDatasets/trunk@HEAD',
    'ApiCommonModel'      : 'https://github.com/VEuPathDB/ApiCommonModel/trunk@HEAD',
    'ApiCommonPresenters' : 'https://github.com/VEuPathDB/ApiCommonPresenters/trunk@HEAD',
    'ApiCommonWebService' : 'https://github.com/VEuPathDB/ApiCommonWebService/trunk@HEAD',
    'ApiCommonWebsite'    : 'https://github.com/VEuPathDB/ApiCommonWebsite/trunk@HEAD',
    'CBIL'                : 'https://github.com/VEuPathDB/CBIL/trunk@HEAD',
    'EbrcModelCommon'     : 'https://github.com/VEuPathDB/EbrcModelCommon/trunk@HEAD',
    'EbrcWebsiteCommon'   : 'https://github.com/VEuPathDB/EbrcWebsiteCommon/trunk@HEAD',
    'EbrcWebSvcCommon'    : 'https://github.com/VEuPathDB/EbrcWebSvcCommon/trunk@HEAD',
    'FgpUtil'             : 'https://github.com/VEuPathDB/FgpUtil/trunk@HEAD',
    'JBrowse'             : 'https://github.com/VEuPathDB/JBrowse/trunk@HEAD',
    'GBrowse'             : 'https://github.com/VEuPathDB/GBrowse/trunk@HEAD',
    'install'             : 'https://github.com/VEuPathDB/install/trunk@HEAD',
    'ReFlow'              : 'https://github.com/VEuPathDB/ReFlow/trunk@HEAD',
    'WDK'                 : 'https://github.com/VEuPathDB/WDK/trunk@HEAD',
    'WDKClient'           : 'https://github.com/VEuPathDB/WDKClient/trunk@HEAD',
    'WDKWebsite'          : 'https://github.com/VEuPathDB/WDKWebsite/trunk@HEAD',
    'WSF'                 : 'https://github.com/VEuPathDB/WSF/trunk@HEAD',
  ]).asImmutable()

  static public def svnWdkTemplateLocations = ([
    'CBIL'                :   'https://github.com/VEuPathDB/CBIL/trunk@HEAD',
    'FgpUtil'             :   'https://github.com/VEuPathDB/FgpUtil/trunk@HEAD',
    'install'             :   'https://github.com/VEuPathDB/install/trunk@HEAD',
    'WDK'                 :   'https://github.com/VEuPathDB/WDK/trunk@HEAD',
    'WSF'                 :   'https://github.com/VEuPathDB/WSF/trunk@HEAD',
    'WDKTemplateSite'     :   'https://github.com/VEuPathDB/WDKTemplateSite/trunk@HEAD',
    'EbrcWebsiteCommon'   :   'https://github.com/VEuPathDB/EbrcWebsiteCommon/trunk@HEAD',
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
See <a href="https://wiki.apidb.org/index.php/JenkinsWebsiteBuilds">JenkinsWebsiteBuilds wiki</a> for build overview.
<p>
<font color='red'>This project configuration is auto-generated by
<a href="/${thisProject.url}">${thisProject.displayName}</a>. <br>
Manual changes to SCM locations are persistent but other configuration changes made through
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
Manual changes to SCM locations are persistent but other configuration changes made through
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
Manual changes to SCM locations are persistent but other configuration changes made through
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
