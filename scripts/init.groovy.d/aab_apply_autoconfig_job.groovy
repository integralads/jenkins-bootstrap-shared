// output init hook stage
println '### JENKINS AUTOCONFIG: CREATING SELF-BOOTSTRAP JOB ###'

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
import groovy.xml.XMLParser
import groovy.xml.XMLSlurper
import org.jenkinsci.plugins.workflow.job.WorkflowJob

void addInitialBootstrapJob(){
    String jobXmlString = new File("${Jenkins.instance.rootDir}/init.groovy.d/__bootstrap_jenkins_autoconfig.xml").text
    def jobXmlStream = new StringBufferInputStream(jobXmlString)
    Jenkins.instance.createProjectFromXML("__bootstrap_jenkins_autoconfig", jobXmlStream)
}
addInitialBootstrapJob()

println "Jenkins initial bootstrap job created..."
