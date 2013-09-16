public class Values {

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
      \$HOME/bin/rebuilder-jenkins ${host}.${product.toLowerCase()}.org --webapp ${product}:${product}
    """
    .stripIndent()
  }

/** ******************************************************************************** 
ANT
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
}