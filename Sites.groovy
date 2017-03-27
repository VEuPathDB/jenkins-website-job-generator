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
*       jabberContacts: Values.jabberContactsIntegrate, // OPTIONAL
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
    //'a2',
    'q1',
    'q2',
    'w1',
    'w2',
    'r28',
  ]

  // There must be configurations for each in Values.productSpecificConfig
  static public def inclusiveProducts = [
    'AmoebaDB',
    'CryptoDB',
    'EuPathDB',
    'FungiDB',
    'GiardiaDB',
    'HostDB',
    'MicrobiomeDB',
    'MicrosporidiaDB',
    'PiroplasmaDB',
    'PlasmoDB',
    'SchistoDB',
    'ToxoDB',
    'TrichDB',
    'TriTrypDB',
  ]

  // Set jobName to null ( 'w1.hostdb.org' : null ) to remove from the
  // list of jobs auto-generated from host + product lists
  static public def customJobs = [

    'r28.hostdb.org': null,
    'r28.microbiomedb.org': null,

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
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],
/** a2.plasmo, et al. now in inclusiveHosts for build-29 pre-release
  * 'a2.plasmodb.org' : [
  *    product : "PlasmoDB", // REQUIRED
  *    webapp : "plasmo.alpha", // REQUIRED
  *    host : "a2", // REQUIRED
  *    tld : "org", // REQUIRED
  *    label : 'oak', // REQUIRED
  *    scmSchedule : Values.scmScheduleNightly, // OPTIONAL
  *    rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
  *    testngStep: Values.testngStepForQa, // OPTIONAL
  *    extendedEmail : Values.qaExtendedEmail, // OPTIONAL
  *    jabberContacts: Values.jabberContactsProduction, // OPTIONAL
  *    jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
  * ],
  **/
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
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
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
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
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
       jabberContacts: Values.jabberContactsIntegrate, // OPTIONAL
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
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
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
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
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
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
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
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
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
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
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
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
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
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

  ]

}

