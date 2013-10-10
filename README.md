jenkins-website-job-generator
=============================

Generate Jenkins job configurations for EuPathDB core sites using the job-dsl plugin.


Sites.groovy has two lists: inclusiveHosts for the hostnames you want to build (integrate, w1, q2, etc) and inclusiveProducts for the products (CryptoDB, AmoebaDB, etc). Jobs will be created for all combinations of Sites.inclusiveHosts and Sites.inclusiveProducts. That is,

    inclusiveHosts.each { host ->
      inclusiveProducts.each { product ->
        // create job for "${host}.${product}.org"
      }
    }

Define host-specific and product-specific values in Values.groovy .

Subversion locations are important exception to using data from Values.groovy. If a job already exists and has svn locations defined, then that existing svn configuration is retained. If no existing locations are available then default ones are used. This allows the locations to be updated quickly through the Jenkins API without having to regenerate all configurations and it simplifies maintenance of Values.groovy.

Job disabled state is also retained. If an existing job was disabled when regenerating the configuration files, the new configuration will also be disabled.

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
         disabled: false // OPTIONAL, job should be disabled, default false
         scmSchedule : Values.scmScheduleNightly, // OPTIONAL
         rebuilderStep: Values.rebuilderStepForQa // REQUIRED,
         testngStep: Values.testngStepForIntegration, // OPTIONAL
         extendedEmail : Values.qaExtendedEmail, // OPTIONAL
         jabberContacts: Values.jabberContactsStd, // OPTIONAL
         jabberNotification: Values.jabberNotificationIntegrate,  // OPTIONAL
       ],
    ]

Jenkins DSL job configuration
----

Create a Freestyle job and add the Git repository for this project.

![scm conf](https://raw.github.com/EuPathDB/jenkins-website-job-generator/master/images/jenkins-scm-conf.jpg)

Add a Job DSL build step with `generate-website-jobs` as the DSL Script on the Filesystem. Leave `Ignore changes` unchecked - we want existing jobs to be overwritten. Ignore removed jobs.

![build step](https://raw.github.com/EuPathDB/jenkins-website-job-generator/master/images/jenkins-build-conf.jpg)
