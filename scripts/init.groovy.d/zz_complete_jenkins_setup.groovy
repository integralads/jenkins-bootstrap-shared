import jenkins.model.Jenkins

/**
  * Function to check if Jenkins should not configure itself and skip.
  */
Boolean skipConfiguringJenkins() {
    !(System.env?.environment in ['dev', 'staging']) ||
        (new File("${Jenkins.instance.rootDir}/autoConfigComplete").exists())
}

void setConfigurationComplete() {
  if(skipConfiguringJenkins()) {
      println 'Skipping Jenkins auto-configuration'
      return
  }
  new File("${Jenkins.instance.rootDir}/autoConfigComplete").withWriter { w -> w << '' }
  println 'Jenkins autoconfiguration complete, writing flag file...'
}

setConfigurationComplete()
