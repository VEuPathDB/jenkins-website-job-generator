public class Values {


  static public def productSpecificConfig = [
    AmoebaDB : [
      webapp : "amoeba", 
    ],
    CryptoDB : [
      webapp : "cryptodb",
    ],
    EuPathDB : [
      webapp : "eupathdb",
    ],
    GiardiaDB : [
      webapp : "giardiadb",
    ],
    HostDB : [
      webapp : "hostdb",
    ],
    MicrosporidiaDB : [
      webapp : "micro",
    ],
    PiroplasmaDB : [
      webapp : "piro",
    ],
    PlasmoDB : [
      webapp : "plasmo",
    ],
    ToxoDB : [
      webapp : "toxo",
    ],
    TrichDB : [
      webapp : "trichdb",
    ],
    TriTrypDB : [
      webapp : "tritrypdb",
    ],
    FungiDB : [
      webapp : "fungidb"
    ],
  ]
  


/** ******************************************************************************** 
REBUILDER
******************************************************************************** **/

  static public def rebuilderStepForIntegration = { host, product, webapp ->
    return """
      date > .hudsonTimestamp
      env
      sudo instance_manager stop ${product} force
      sleep 5
      sudo instance_manager start  ${product}
      sleep 15
      \$HOME/bin/rebuilder-jenkins ${host}.${product.toLowerCase()}.org --webapp ${product}:${webapp}.integrate
    """
    .stripIndent()
  }


  static public def rebuilderStepForWdkTemplate = { host, product, webapp ->
    return """
      date > .hudsonTimestamp
      env
      sudo instance_manager stop ${product} force
      sleep 5
      sudo instance_manager start  ${product}
      sleep 15
      \$HOME/bin/rebuilder-jenkins ${host}.apidb.org --webapp ${product}:${webapp}.integrate
      resetWdkPgTestDb
    """
    .stripIndent()
  }


  static public def rebuilderStepForQa = { host, product, webapp ->
    return """
      env
      \$HOME/bin/rebuilder-jenkins ${host}.${product.toLowerCase()}.org
    """
    .stripIndent()
  }

  static public def rebuilderStepForWww = { host, product, webapp ->
    return """
      env
      \$HOME/bin/rebuilder-jenkins ${host}.${product.toLowerCase()}.org --webapp ${product}:${webapp}
    """
    .stripIndent()
  }


/** ******************************************************************************** 
TEST NG
******************************************************************************** **/

  static public def testngStepForIntegration = { host, product, webapp ->
    return {
      targets(['cleantestresults', 'cleaninstall', 'testbynames'])
      props('proj':'EuPathSiteCommon', 'comp':'Watar', 'targetDir':'\$WORKSPACE/test_home', 
        'projectsDir':'\$WORKSPACE', 'baseurl':"http://${host}.${product.toLowerCase()}.org", 
        'webappname':"${webapp}.integrate", 'testnames':'"Integration"')
      buildFile 'EuPathSiteCommon/Watar/build.xml'
    }
  }

  static public def testngStepForQa = { host, product, webapp ->
    return {
      targets(['cleantestresults', 'cleaninstall', 'testbynames'])
      props('proj':'EuPathSiteCommon', 'comp':'Watar', 'targetDir':'\$WORKSPACE/test_home', 
        'projectsDir':'\$WORKSPACE', 'baseurl':"http://${host}.${product.toLowerCase()}.org", 
        'webappname':"${webapp}", 'testnames':'"QA"')
      buildFile 'EuPathSiteCommon/Watar/build.xml'
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
          sendToRequester: false, 
          includeCulprits: false, 
          sendToRecipientList: true,
        )
        trigger(
          triggerName: 'Failure', 
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: true, 
          sendToRequester: true, 
          includeCulprits: true, 
          sendToRecipientList: true,
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
          sendToRequester: false, 
          includeCulprits: false, 
          sendToRecipientList: true,
        )
        trigger(
          triggerName: 'Failure', 
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: false, 
          sendToRequester: false, 
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
          sendToRequester: false, 
          includeCulprits: false, 
          sendToRecipientList: true,
        )
        trigger(
          triggerName: 'Failure', 
          subject: '$PROJECT_DEFAULT_SUBJECT',
          body: '$PROJECT_DEFAULT_CONTENT',
          sendToDevelopers: false, 
          sendToRequester: false, 
          includeCulprits: false, 
          sendToRecipientList: true,
        )
        configure { node ->
            node / contentType << 'default'
        }
    }
  } // wwwExtendedEmail


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

/** ******************************************************************************** 
JABBER CONTACTS
******************************************************************************** **/
  static public def jabberContactsStd = ['mheiges@apidb.org', 'caurreco@gmail.com']

  
/** ******************************************************************************** 
CONFIGURATIONS PER HOST

      label : 'aprium', // REQUIRED
      rebuilderStep : rebuilderStepForIntegration, // REQUIRED
      timeout : 20,  // OPTIONAL
      scmSchedule : scmScheduleAsap, // OPTIONAL
      quietPeriod : 180, // OPTIONAL
      testngStep : testngStepForIntegration, // OPTIONAL
      jabberContacts : jabberContactsStd, // OPTIONAL
      //logRotator(daysToKeepInt, numToKeepInt, artifactDaysToKeepInt, artifactNumToKeepInt)
      logRotator : [7, -1, -1, -1], // OPTIONAL
      extendedEmail : integrateExtendedEmail, // OPTIONAL
      jabberNotification: jabberNotificationIntegrate, // OPTIONAL

******************************************************************************** **/

  static public def hostSpecificConfig = [
    integrate : [
      label : 'aprium',
      timeout : 20,
      scmSchedule : scmScheduleAsap,
      quietPeriod : 180,
      rebuilderStep : rebuilderStepForIntegration,
      testngStep : testngStepForIntegration,
      jabberContacts : jabberContactsStd,
      //logRotator(daysToKeepInt, numToKeepInt, artifactDaysToKeepInt, artifactNumToKeepInt)
      logRotator : [7, -1, -1, -1],
      extendedEmail : integrateExtendedEmail,
      jabberNotification: jabberNotificationIntegrate,
    ],
    q1 : [
      label : 'olive',
      timeout : 30,
      scmSchedule : scmScheduleNightly,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      extendedEmail : qaExtendedEmail,
      jabberContacts: jabberContactsStd,
      jabberNotification: jabberNotificationWww,
    ],
    q2 : [
      label : 'oak',
      timeout : 30,
      scmSchedule : scmScheduleNightly,
      rebuilderStep: rebuilderStepForQa,
      testngStep: testngStepForQa,
      extendedEmail : qaExtendedEmail,
      jabberContacts: jabberContactsStd,
      jabberNotification: jabberNotificationWww,
    ],
    w1 : [
      label : 'olive',
      rebuilderStep: rebuilderStepForWww,
      logRotator : [-1, 10, -1, -1],
      extendedEmail : wwwExtendedEmail,
      jabberContacts : jabberContactsStd,
      jabberNotification: jabberNotificationWww,
    ],
    w2 : [
      label : 'oak',
      rebuilderStep: rebuilderStepForWww,
      logRotator : [-1, 10, -1, -1],
      extendedEmail : wwwExtendedEmail,
      jabberContacts : jabberContactsStd,
      jabberNotification: jabberNotificationWww,
    ],
  ]




  /** ******************************************************************************** 
    Default svn urls for jobs that do not have an existing SCM configuration.
  ******************************************************************************** **/
  static public def svnDefaultLocations = ([
    'WDK'                 :   'https://www.cbil.upenn.edu/svn/gus/WDK/trunk',
    'CBIL'                :   'https://www.cbil.upenn.edu/svn/gus/CBIL/trunk',
    'install'             :   'https://www.cbil.upenn.edu/svn/gus/install/trunk',
    'ReFlow'              :   'https://www.cbil.upenn.edu/svn/gus/ReFlow/trunk',
    'FgpUtil'             :   'https://www.cbil.upenn.edu/svn/gus/FgpUtil/trunk',
    'ApiCommonWebService' :   'https://www.cbil.upenn.edu/svn/apidb/ApiCommonWebService/trunk',
    'ApiCommonShared'     :   'https://www.cbil.upenn.edu/svn/apidb/ApiCommonShared/trunk',
    'WSF'                 :   'https://www.cbil.upenn.edu/svn/gus/WSF/trunk',
    'EuPathPresenters'    :   'https://www.cbil.upenn.edu/svn/apidb/EuPathPresenters/trunk',
    'GBrowse'             :   'https://www.cbil.upenn.edu/svn/apidb/GBrowse/trunk',
    'ApiCommonWebsite'    :   'https://www.cbil.upenn.edu/svn/apidb/ApiCommonWebsite/trunk',
    'EuPathSiteCommon'    :   'https://www.cbil.upenn.edu/svn/apidb/EuPathSiteCommon/trunk',
    'EuPathDatasets'      :   'https://www.cbil.upenn.edu/svn/apidb/EuPathDatasets/trunk',
    'EuPathWebSvcCommon'  :   'https://www.cbil.upenn.edu/svn/apidb/EuPathWebSvcCommon/trunk',
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
<font color='red'>This is project configuration is auto-generated by 
<a href="/${thisProject.url}">${thisProject.displayName}</a>. <br>
Manual configurations, other than SCM locations, may be lost.</font> <br>
(Generated by <a href="/${thisBuild.url}">${thisBuild.displayName}<a/>)
"""
  }



  /** ******************************************************************************** 
    Disable QA Jobs
  ******************************************************************************** **/
  def disableQABuilds(product) {
    {project -> project/publishers/'hudson.plugins.parameterizedtrigger.BuildTrigger' {
        'configs'  { 
          'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' {
            'configs' {
              'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters' {
                properties "JENKINS_JOBS=q1.${product.toLowerCase()}.org q2.${product.toLowerCase()}.org"
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

