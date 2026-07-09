public class Hosts {

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
      b1: [
          label               : 'pineapple',
          folder              : 'site-builds/beta',
//          scmSchedule : Values.scmScheduleNightly,
//          ignorePostCommitHooks : true,
          rebuilderStep       : Values.rebuilderStepForBeta,
          checkoutRetryCount  : 1,
          logRotator          : [-1, 50, -1, -1],
          sitesearchStep      : Values.sitesearchStepForBeta,
          pipelineNotification: Values.pipelineNotificationEveryBuild,
          slackChannel        : "#alert-build-livesite",
          githubPush          : false,
//          authorization: Values.authorizationForQA
      ],
      b2: [
          label                : 'palm',
          folder               : 'site-builds/beta',
//          scmSchedule          : Values.scmScheduleNightly,
//          ignorePostCommitHooks: true,
          rebuilderStep        : Values.rebuilderStepForBeta,
          checkoutRetryCount   : 1,
          logRotator           : [-1, 50, -1, -1],
          sitesearchStep       : Values.sitesearchStepForBeta,
          pipelineNotification : Values.pipelineNotificationEveryBuild,
          slackChannel         : "#alert-build-livesite",
          githubPush           : false,
          authorization        : Values.authorizationForQA
      ],
//    integrate : [
//      label : 'pineapple',
//      folder: 'site-builds/integrate',
//      timeout : 30,
//      quietPeriod : 180,
//      checkoutRetryCount : 1,
//      rebuilderStep : Values.rebuilderStepForIntegration,
//      testngStep : Values.testngStepForIntegration,
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
//      scmSchedule : Values.scmScheduleNightly,
//      rebuilderStep: Values.rebuilderStepForQa,
//      ignorePostCommitHooks : 'true',
//      logRotator : [7, -1, -1, -1],
//      description : featureDescription(),
//      githubPush: false,
//    ],
//    q1 : [
//      label : 'watermelon',
//      folder: 'site-builds/qa',
//      timeout : 90,
//      scmSchedule : Values.scmScheduleNightly,
//      checkoutRetryCount : 1,
//      rebuilderStep: Values.rebuilderStepForQa,
//      testngStep: Values.testngStepForQa,
//      apitestStep: Values.apitestStepForQa,
//      cacheStep: Values.cacheStep,
//      sitesearchStep: Values.sitesearchStepForQa,
//      pipelineNotification: Values.pipelineNotificationChangeOnly,
//      slackChannel: "#alert-build-qa",
//      githubPush: false,
//    ],
//    q2 : [
//      label : 'fir',
//      folder: 'site-builds/qa',
//      timeout : 90,
//      scmSchedule : Values.scmScheduleNightly,
//      checkoutRetryCount : 1,
//      rebuilderStep: Values.rebuilderStepForQa,
//      testngStep: Values.testngStepForQa,
//      apitestStep: Values.apitestStepForQa,
//      cacheStep: Values.cacheStep,
//      sitesearchStep: Values.sitesearchStepForQa,
//      pipelineNotification: Values.pipelineNotificationChangeOnly,
//      slackChannel: "#alert-build-qa",
//      githubPush: false,
//    ],
//    w1 : [
//      label : 'watermelon',
//      folder: 'site-builds/prod',
//      rebuilderStep: Values.rebuilderStepForWww,
//      checkoutRetryCount : 1,
//      logRotator : [-1, 50, -1, -1],
//      sitesearchStep: Values.sitesearchStepForWww,
//      pipelineNotification: Values.pipelineNotificationEveryBuild,
//      slackChannel: "#alert-build-livesite",
//      githubPush: false,
//    ],
//    w2 : [
//      label : 'fir',
//      folder: 'site-builds/prod',
//      rebuilderStep: Values.rebuilderStepForWww,
//      checkoutRetryCount : 1,
//      logRotator : [-1, 50, -1, -1],
//      sitesearchStep: Values.sitesearchStepForWww,
//      pipelineNotification: Values.pipelineNotificationEveryBuild,
//      slackChannel: "#alert-build-livesite",
//      githubPush: false,
//    ],
  ]

} // Hosts class
