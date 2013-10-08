jenkins-website-job-generator
=============================

Generate Jenkins job configurations for EuPathDB core sites using the job-dsl plugin


Sites.groovy has two lists: inclusiveHosts for the hostnames you want to build (integrate, w1, q2, etc) and inclusiveProducts for the products (CryptoDB, AmoebaDB, etc). Jobs will be created for all combinations of Sites.inclusiveHosts and Sites.inclusiveProducts. That is,

    inclusiveHosts.each { host ->
      inclusiveProducts.each { product ->
        // create job for "${host}.${product}.org"
      }
    }

Define host-specific and product-specific values in Values.groovy .

Additional sites can be added in Sites.customJobs() and set to null (to remove them from the host/product combo) or set to a Map of values. In this case, we have to specify all the configuration values in Sites.groovy rather than rely on the host-specific and product-specific settings in Values.groovy.

      Sites.customJobs = [
        
        // do not generate a w1.hostdb.org job even though it was one of 
        // the host.product combinations
        'w1.hostdb.org' : null, 
                               
        // add this job that was not one of the host.product combinations
        'a1.plasmodb.org' : [
         product : "PlasmoDB", // REQUIRED
         webapp : "plasmo", // REQUIRED
         host : "a1", // REQUIRED
         label : 'oak', // REQUIRED
         scmSchedule : Values.scmScheduleNightly, // OPTIONAL
         rebuilderStep: Values.rebuilderStepForQa // REQUIRED,
         testngStep: Values.testngStepForIntegration, // OPTIONAL
         extendedEmail : Values.qaExtendedEmail, // OPTIONAL
         jabberContacts: Values.jabberContactsStd, // OPTIONAL
         jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
       ],
    ]
