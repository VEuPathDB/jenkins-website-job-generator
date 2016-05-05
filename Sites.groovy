public class Sites {

/** 
*  Jobs will be created for all combinations of inclusiveHosts and inclusiveProducts .
*
*  Additional sites can be added in customJobs() and set to null (to remove them from
*  the host/product combo) or set to an Map of values.
*  
*  You can decide which is less work: remove a few jobs from the combinatorially generated list
*  or add jobs manually.
*  
*  For example, consider
*
*      static public def inclusiveHosts = [ 
*        'integrate', 'w1'
*      ]
*      
*      static public def inclusiveProducts = [
*        'HostDB',
*      ]
*
*  This will generate jobs for integrate.hostdb.org and w1.hostdb.org. Let's say w1.hostdb.org has
*  not been released yet so we should not have a Jenkins job for it. We can undefine w1.hostdb.org
*  from the combinatorial generation by setting it to null in customJobs
*  
*      static public def customJobs = [
*        'w1.hostdb.org' : null,
*      ]
*      
*
*  alpha sites tend to be for just one or two projects, so it may make sense to leave the a1 and
*  a2 hosts off the inclusiveHosts list and configure custom jobs manually
*  
*      'a1.plasmodb.org' : [
*       product : "PlasmoDB", // REQUIRED
*       webapp : "plasmo", // REQUIRED
*       host : "a1", // REQUIRED
*       tld : "org", // REQUIRED
*       label : 'oak', // REQUIRED
*       scmSchedule : Values.scmScheduleNightly, // OPTIONAL
*       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
*       testngStep: Values.testngStepForIntegration, // OPTIONAL
*       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
*       jabberContacts: Values.jabberContactsStd, // OPTIONAL
*       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
*   ],
*
**/
    
  // hosts that should be configured for all inclusiveProducts
  // There must be configurations for each in Values.hostSpecificConfig
  static public def inclusiveHosts = [
    'feature',
    'integrate',
    'maint',
    'q1',
    'q2',
    'w1',
    'w2',
  ]
  
  // There must be configurations for each in Values.productSpecificConfig
  static public def inclusiveProducts = [
    'AmoebaDB',
    'CryptoDB',
    'EuPathDB',
    'FungiDB',
    'GiardiaDB',
    'HostDB',
    'MicrosporidiaDB',
    'PiroplasmaDB',
    'PlasmoDB',
    'SchistoDB',
    'ToxoDB',
    'TrichDB',
    'TriTrypDB',
  ]

  // Set jobName to null to remove from the list of jobs auto-generated from host + product lists
  static public def customJobs = [
    'w1.hostdb.org' : null,
    'w2.hostdb.org' : null,

    'a1.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo.alpha", // REQUIRED
       host : "a1", // REQUIRED
       tld : "org", // REQUIRED
       label : 'myrtle', // REQUIRED
       scmSchedule : Values.scmScheduleNightly, // OPTIONAL
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],
    'a2.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo.alpha", // REQUIRED
       host : "a2", // REQUIRED
       tld : "org", // REQUIRED
       label : 'oak', // REQUIRED
       scmSchedule : Values.scmScheduleNightly, // OPTIONAL
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],

    'cdi.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo.cdi", // REQUIRED
       host : "cdi", // REQUIRED
       tld : "org", // REQUIRED
       label : 'oak', // REQUIRED
       checkoutRetryCount : 1,
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],
    'qa.cdi.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo.cdiqa", // REQUIRED
       host : "qa.cdi", // REQUIRED
       tld : "org", // REQUIRED
       label : 'oak', // REQUIRED
       checkoutRetryCount : 1,
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'integrate.wdk.apidb.org' : [
       product : "TemplateDB", // REQUIRED
       webapp : "ROOT", // REQUIRED
       host : "integrate.wdk", // REQUIRED
       tld : "org", // REQUIRED
       label : 'santol', // REQUIRED
       scmSchedule : Values.scmScheduleAsap, // OPTIONAL
       checkoutRetryCount : 1,
       svnDefaultLocations : Values.svnWdkTemplateLocations,
       rebuilderStep: Values.rebuilderStepForWdkTemplate, // REQUIRED,
       // testngStep: Values.testngStepForIntegration, // OPTIONAL
       extendedEmail : Values.integrateExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],

    'qa.wdk.apidb.org' : [
       product : "TemplateDB", // REQUIRED
       webapp : "templatesite.b20", // REQUIRED
       host : "qa.wdk", // REQUIRED
       tld : "org", // REQUIRED
       label : 'oak', // REQUIRED
       scmSchedule : Values.scmScheduleNightly, // OPTIONAL
       checkoutRetryCount : 1,
       svnDefaultLocations : Values.svnWdkTemplateLocations,
       rebuilderStep: Values.rebuilderStepForWdkTemplate, // REQUIRED,
       // testngStep: Values.testngStepForIntegration, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],

    'icemr.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo.icemr", // REQUIRED
       host : "icemr", // REQUIRED
       tld : "org", // REQUIRED
       label : 'oak', // REQUIRED
       scmSchedule : Values.scmScheduleNightly, // OPTIONAL
       checkoutRetryCount : 1,
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'pr1.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo.prism", // REQUIRED
       host : "pr1", // REQUIRED
       tld : "org", // REQUIRED
       label : 'myrtle', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'pr2.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo.prism", // REQUIRED
       host : "pr2", // REQUIRED
       tld : "org", // REQUIRED
       label : 'oak', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'amazonia.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo.amazonia", // REQUIRED
       host : "amazonia", // REQUIRED
       tld : "org", // REQUIRED
       label : 'oak', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'q1.prism.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo.prismqa", // REQUIRED
       host : "q1.prism", // REQUIRED
       tld : "org", // REQUIRED
       label : 'myrtle', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'q2.prism.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo.prismqa", // REQUIRED
       host : "q2.prism", // REQUIRED
       tld : "org", // REQUIRED
       label : 'oak', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'galaxy.cryptodb.org' : [
       product : "CryptoDB", // REQUIRED
       webapp : "cryptodb.galaxy", // REQUIRED
       host : "galaxy", // REQUIRED
       tld : "org", // REQUIRED
       label : 'luffa', // REQUIRED
       scmSchedule : Values.scmScheduleNightly, // OPTIONAL
       checkoutRetryCount : 1,
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'b1.hostdb.org' : [
        product : "HostDB", // REQUIRED
        webapp : "hostdb", // REQUIRED
        host : "b1", // REQUIRED
        tld : "org", // REQUIRED
        label : 'myrtle', // REQUIRED
        checkoutRetryCount : 1,
        rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
        extendedEmail : Values.wwwExtendedEmail, // OPTIONAL
        jabberContacts: Values.jabberContactsStd, // OPTIONAL
        jabberNotification: Values.jabberNotificationWww,  // OPTIONAL
    ],

    'b2.hostdb.org' : [
        product : "HostDB", // REQUIRED
        webapp : "hostdb", // REQUIRED
        host : "b2", // REQUIRED
        tld : "org", // REQUIRED
        label : 'oak', // REQUIRED
        checkoutRetryCount : 1,
        rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
        extendedEmail : Values.wwwExtendedEmail, // OPTIONAL
        jabberContacts: Values.jabberContactsStd, // OPTIONAL
        jabberNotification: Values.jabberNotificationWww,  // OPTIONAL
    ],

    /** https://redmine.apidb.org/issues/21007 */
    'integrate.gus4.plasmodb.org' : [
       product : "PlasmoDB",
       webapp : "plasmo.gus4",
       host : "integrate.gus4",
       tld : "org",
       label : 'santol',
       timeout : 30,
       scmSchedule : Values.scmScheduleYearly,
       checkoutRetryCount : 1,
       ignorePostCommitHooks : 'false',
       quietPeriod : 180,
       rebuilderStep: Values.rebuilderStepForIntegration, // REQUIRED,
       testngStep : Values.testngStepForIntegration,
       jabberContacts : Values.jabberContactsStd,
       logRotator : [7, -1, -1, -1],
       extendedEmail : Values.integrateExtendedEmail, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],

    /** https://redmine.apidb.org/issues/21007 */
    'q1.gus4.plasmodb.org' : [
       product : "PlasmoDB",
       webapp : "plasmo.gus4.q1",
       host : "q1.gus4",
       tld : "org",
       label : 'myrtle',
       timeout : 30,
       scmSchedule : Values.scmScheduleNightly,
       checkoutRetryCount : 1,
       quietPeriod : 180,
       testngStep: Values.testngStepForQa,
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       jabberContacts : Values.jabberContactsStd,
       extendedEmail : Values.integrateExtendedEmail, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],
    'q2.gus4.plasmodb.org' : [
       product : "PlasmoDB",
       webapp : "plasmo.gus4.q2",
       host : "q2.gus4",
       tld : "org",
       label : 'oak',
       timeout : 30,
       scmSchedule : Values.scmScheduleNightly,
       checkoutRetryCount : 1,
       quietPeriod : 180,
       testngStep: Values.testngStepForQa,
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       jabberContacts : Values.jabberContactsStd,
       extendedEmail : Values.integrateExtendedEmail, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],

    /** https://redmine.apidb.org/issues/22224 */
    'integrate.gus4.toxodb.org' : [
       product : "ToxoDB",
       webapp : "toxo.gus4",
       host : "integrate.gus4",
       tld : "org",
       label : 'santol',
       timeout : 30,
       scmSchedule : Values.scmScheduleYearly,
       checkoutRetryCount : 1,
       ignorePostCommitHooks : 'false',
       quietPeriod : 180,
       rebuilderStep: Values.rebuilderStepForIntegration, // REQUIRED,
       testngStep : Values.testngStepForIntegration,
       jabberContacts : Values.jabberContactsStd,
       logRotator : [7, -1, -1, -1],
       extendedEmail : Values.integrateExtendedEmail, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],

    /** https://redmine.apidb.org/issues/22224 */
    'q1.gus4.toxodb.org' : [
       product : "ToxoDB",
       webapp : "toxo.gus4.q1",
       host : "q1.gus4",
       tld : "org",
       label : 'myrtle',
       timeout : 30,
       scmSchedule : Values.scmScheduleNightly,
       checkoutRetryCount : 1,
       quietPeriod : 180,
       testngStep: Values.testngStepForQa,
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       jabberContacts : Values.jabberContactsStd,
       extendedEmail : Values.integrateExtendedEmail, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],
    'q2.gus4.toxodb.org' : [
       product : "ToxoDB",
       webapp : "toxo.gus4.q2",
       host : "q2.gus4",
       tld : "org",
       label : 'oak',
       timeout : 30,
       scmSchedule : Values.scmScheduleNightly,
       checkoutRetryCount : 1,
       quietPeriod : 180,
       testngStep: Values.testngStepForQa,
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       jabberContacts : Values.jabberContactsStd,
       extendedEmail : Values.integrateExtendedEmail, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],

  ]

}

