public class Sites {

/**
*  Jobs will be created for all combinations of inclusiveHosts and inclusiveModels .
*
*  Additional sites can be added in customJobs() and set to null (to remove them from
*  the host/model combo) or set to an Map of values.
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
*      static public def inclusiveModels = [
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
*       model : "PlasmoDB", // REQUIRED
*       webapp : "plasmo", // REQUIRED
*       host : "a1", // REQUIRED
*       sld : "plasmodb", //REQUIRED
*       tld : "org", // REQUIRED
*       label : 'pine', // REQUIRED
*       scmSchedule : Values.scmScheduleNightly, // OPTIONAL
*       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
*       testngStep: Values.testngStepForIntegration, // OPTIONAL
*       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
*       jabberContacts: Values.jabberContactsIntegrate, // OPTIONAL
*       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
*   ],
*
**/

  // hosts that should be configured for all inclusiveModels
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
  ]

  // There must be configurations for each in Values.modelSpecificConfig
  static public def inclusiveModels = [
    'AmoebaDB',
    'ClinEpiDB',
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
  // list of jobs auto-generated from host + model lists
  static public def customJobs = [

/**    'a1.plasmodb.org' : [
  *     model : "PlasmoDB", // REQUIRED
  *     webapp : "plasmo.alpha", // REQUIRED
  *     host : "a1", // REQUIRED
  *     sld : "plasmodb", //REQUIRED
  *     tld : "org", // REQUIRED
  *     label : 'myrtle', // REQUIRED
  *     scmSchedule : Values.scmScheduleNightly, // OPTIONAL
  *     rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
  *     testngStep: Values.testngStepForQa, // OPTIONAL
  *     extendedEmail : Values.qaExtendedEmail, // OPTIONAL
  *     jabberContacts: Values.jabberContactsProduction, // OPTIONAL
  *     jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],
  **/

/** a2.plasmo, et al. now in inclusiveHosts for build-29 pre-release
  * 'a2.plasmodb.org' : [
  *    model : "PlasmoDB", // REQUIRED
  *    webapp : "plasmo.alpha", // REQUIRED
  *    host : "a2", // REQUIRED
  *    sld : "plasmodb", //REQUIRED
  *    tld : "org", // REQUIRED
  *    label : 'pine', // REQUIRED
  *    scmSchedule : Values.scmScheduleNightly, // OPTIONAL
  *    rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
  *    testngStep: Values.testngStepForQa, // OPTIONAL
  *    extendedEmail : Values.qaExtendedEmail, // OPTIONAL
  *    jabberContacts: Values.jabberContactsProduction, // OPTIONAL
  *    jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
  * ],
  **/
    'integrate.wdk.apidb.org' : [
       model : "TemplateDB", // REQUIRED
       webapp : "ROOT", // REQUIRED
       host : "integrate.wdk", // REQUIRED
       sld : "apidb", //REQUIRED
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
       model : "TemplateDB", // REQUIRED
       webapp : "templatesite.b20", // REQUIRED
       host : "qa.wdk", // REQUIRED
       sld : "apidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'pine', // REQUIRED
       scmSchedule : Values.scmScheduleNightly, // OPTIONAL
       checkoutRetryCount : 1,
       svnDefaultLocations : Values.svnWdkTemplateLocations,
       rebuilderStep: Values.rebuilderStepForWdkTemplate, // REQUIRED,
       // testngStep: Values.testngStepForIntegration, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ],

    'w2.restricted.clinepidb.org' : [
       model : "AllClinEpiDb", // REQUIRED
       webapp : "ce.restricted", // REQUIRED
       host : "w2.restricted", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'pine', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'w1.restricted.clinepidb.org' : [
       model : "AllClinEpiDb", // REQUIRED
       webapp : "ce.restricted", // REQUIRED
       host : "w1.restricted", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'myrtle', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'q2.restricted.clinepidb.org' : [
      /** https://redmine.apidb.org/issues/34369 **/
       model : "AllClinEpiDb", // REQUIRED
       webapp : "ce.restricted.qa", // REQUIRED
       host : "q2.restricted", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'pine', // REQUIRED
       timeout : 60, // OPTIONAL
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       scmSchedule : Values.scmScheduleNightly,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'q1.restricted.clinepidb.org' : [
      /** https://redmine.apidb.org/issues/34369 **/
       model : "AllClinEpiDb", // REQUIRED
       webapp : "ce.restricted.qa", // REQUIRED
       host : "q1.restricted", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'myrtle', // REQUIRED
       timeout : 60, // OPTIONAL
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       scmSchedule : Values.scmScheduleNightly,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],


    'w2.gates.clinepidb.org' : [
       model : "ClinEpiDb", // REQUIRED
       webapp : "ce.gates", // REQUIRED
       host : "w2.gates", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'pine', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'w1.gates.clinepidb.org' : [
       model : "ClinEpiDb", // REQUIRED
       webapp : "ce.gates", // REQUIRED
       host : "w1.gates", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'myrtle', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'q2.gates.clinepidb.org' : [
      /** https://redmine.apidb.org/issues/34369 **/
       model : "ClinEpiDb", // REQUIRED
       webapp : "ce.gates.q2", // REQUIRED
       host : "q2.gates", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'pine', // REQUIRED
       timeout : 60, // OPTIONAL
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'q1.gates.clinepidb.org' : [
      /** https://redmine.apidb.org/issues/34369 **/
       model : "ClinEpiDb", // REQUIRED
       webapp : "ce.gates.q1", // REQUIRED
       host : "q1.gates", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'myrtle', // REQUIRED
       timeout : 60, // OPTIONAL
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'q2.icemr.clinepidb.org' : [
      /** https://redmine.apidb.org/issues/34369 **/
       model : "ClinEpiDb", // REQUIRED
       webapp : "ce.icemr.q2", // REQUIRED
       host : "q2.icemr", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'pine', // REQUIRED
       timeout : 60, // OPTIONAL
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'q1.icemr.clinepidb.org' : [
      /** https://redmine.apidb.org/issues/34369 **/
       model : "ClinEpiDb", // REQUIRED
       webapp : "ce.icemr.q1", // REQUIRED
       host : "q1.icemr", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'myrtle', // REQUIRED
       timeout : 60, // OPTIONAL
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    'gems.clinepidb.org' : [
       model : "Gates", // REQUIRED
       webapp : "ce.gems", // REQUIRED
       host : "gems", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'pine', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

     'w2.icemr.clinepidb.org' : [
       model : "ClinEpiDb", // REQUIRED
       webapp : "ce.icemr", // REQUIRED
       host : "w2.icemr", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'pine', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

     'w1.icemr.clinepidb.org' : [
       model : "ClinEpiDb", // REQUIRED
       webapp : "ce.icemr", // REQUIRED
       host : "w1.icemr", // REQUIRED
       sld : "clinepidb", //REQUIRED
       tld : "org", // REQUIRED
       label : 'myrtle', // REQUIRED
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       checkoutRetryCount : 1,
       testngStep: Values.testngStepForQa, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsProduction, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

    // q1.clinepidb.org doesn't build nightly, because it is pointed to clin-dg readonly replicated db
      'q1.clinepidb.org' : [
         model : "ClinEpiDb", // REQUIRED
         webapp : "ce.qa", // REQUIRED
         host : "q1", // REQUIRED
         sld : "clinepidb", //REQUIRED
         tld : "org", // REQUIRED
         label : 'myrtle',
         timeout : 90,
         checkoutRetryCount : 1,
         rebuilderStep: Values.rebuilderStepForQa,
         testngStep: Values.testngStepForQa,
         extendedEmail : Values.qaExtendedEmail,
         jabberContacts: Values.jabberContactsProduction,
         jabberNotification: Values.jabberNotificationWww,
       ],

  ]

}

