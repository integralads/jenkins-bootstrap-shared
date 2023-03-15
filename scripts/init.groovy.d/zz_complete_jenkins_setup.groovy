/**
  * Check if Jenkins should not configure itself and skip.
  */

Boolean skipConfiguringJenkins() {
    !(System.env?.ENVIRONMENT in ['dev', 'staging']) ||
        (new File("${Jenkins.instance.rootDir}/autoConfigComplete").exists())
}
if(skipConfiguringJenkins()) {
    println 'Skipping Jenkins auto-configuration'
    return
}

/**
  * From this point onward Jenkins should configure itself.
  */
import jenkins.model.Jenkins

void setConfigurationComplete() {
  new File("${Jenkins.instance.rootDir}/autoConfigComplete").withWriter { w -> w << '' }
  println 'Jenkins autoconfiguration complete, writing flag file...'
}

setConfigurationComplete()
