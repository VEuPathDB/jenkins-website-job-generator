public class Sites {

/** 
  Jobs will be created for all combinations of inclusiveHosts and inclusiveProducts .

  Additional sites can be added in customJobs() and set to null (to remove them from
  the host/product combo) or set to an Map of values.
  
  You can decide which is less work: remove a few jobs from the combinatorially generated list
  or add jobs manually.
  
  For example, consider

      static public def inclusiveHosts = [ 
        'integrate', 'w1'
      ]
      
      static public def inclusiveProducts = [
        'HostDB',
      ]

  This will generate jobs for integrate.hostdb.org and w1.hostdb.org. Let's say w1.hostdb.org has
  not been released yet so we should not have a Jenkins job for it. We can undefine w1.hostdb.org
  from the combinatorial generation by setting it to null in customJobs
  
      static public def customJobs = [
        'w1.hostdb.org' : null,
      ]
      

  alpha sites tend to be for just one or two projects, so it may make sense to leave the a1 and
  a2 hosts off the inclusiveHosts list and configure custom jobs manually
  
      'a1.plasmodb.org' : [
       product : "PlasmoDB", // REQUIRED
       webapp : "plasmo", // REQUIRED
       host : "a1", // REQUIRED
       label : 'oak', // REQUIRED
       scmSchedule : Values.scmScheduleNightly, // OPTIONAL
       rebuilderStep: Values.rebuilderStepForQa, // REQUIRED,
       testngStep: Values.testngStepForIntegration, // OPTIONAL
       extendedEmail : Values.qaExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
   ],

**/
    
  // hosts that should be configured for all inclusiveProducts
  // There must be configurations for each in Values.hostSpecificConfig
  static public def inclusiveHosts = [ 
    'integrate',
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
    'GiardiaDB',
    'HostDB',
    'MicrosporidiaDB',
    'PiroplasmaDB',
    'PlasmoDB',
    'ToxoDB',
    'TrichDB',
    'TriTrypDB',
    'FungiDB',
  ]

  // Set jobName to null to remove from the list of jobs auto-generated from host + product lists
  static public def customJobs = [
    'w1.hostdb.org' : null,
    'w2.hostdb.org' : null,
    'integrate.wdk.apidb.org' : [
       product : "TemplateDB", // REQUIRED
       webapp : "ROOT", // REQUIRED
       host : "integrate.wdk", // REQUIRED
       label : 'aprium', // REQUIRED
       scmSchedule : Values.scmScheduleAsap, // OPTIONAL
       rebuilderStep: Values.rebuilderStepForWdkTemplate, // REQUIRED,
       #testngStep: Values.testngStepForIntegration, // OPTIONAL
       extendedEmail : Values.integrateExtendedEmail, // OPTIONAL
       jabberContacts: Values.jabberContactsStd, // OPTIONAL
       jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
    ]
  ]
  
  
}