/**
  * Check if Jenkins should not configure itself and skip.
  */
Boolean skipConfiguringJenkins() {
    !(System.env?.environment in ['dev', 'staging']) ||
        (new File("${Jenkins.instance.rootDir}/autoConfigComplete").exists())
}

if(skipConfiguringJenkins()) {
    println 'Skipping Jenkins auto-configuration'
    return
}

/**
  * From this point onward Jenkins should configure itself.
  */
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

void setConfigurationComplete() {
    new File("${Jenkins.instance.rootDir}/autoConfigComplete").withWriter { w -> w << '' }
}

println 'We are now configuring Jenkins'
setConfigurationComplete()