// output init hook stage
println '### JENKINS AUTOCONFIG: GITHUB OAUTH ###'

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
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse

import hudson.security.SecurityRealm
import org.jenkinsci.plugins.GithubSecurityRealm
import net.sf.json.JSONObject

Map getSecret(Map options) {
    String secretName = options.secretId
    Region region = Region.of(options.region)
    SecretsManagerClient client = SecretsManagerClient.builder()
        .region(region)
        .build()
    GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
        .secretId(secretName)
        .build()
    GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest)
    String secret = getSecretValueResponse.secretString()
    LoaderOptions loaderOptions = new LoaderOptions()
    def yaml = new Yaml(new SafeConstructor(loaderOptions))
    yaml.load(secret)
}
String ENVIRONMENT = System.env?.ENVIRONMENT
String HALF = System.env?.HALF

// list of github realm maps
github_realm = getSecret(region: 'us-east-1', secretId: "jenkins-ng/oauth/github")."${ENVIRONMENT}"."${HALF}".collectEntries()

/*
   Copyright (c) 2015-2020 Sam Gleske - https://github.com/samrocketman/jenkins-bootstrap-jervis

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   */

/*
   Configures GitHub as the security realm from the GitHub Authentication
   Plugin (github-oauth).

   github-oauth 0.29
 */

if(!binding.hasVariable('github_realm')) {
    github_realm = [:]
}

if(!(github_realm instanceof Map)) {
    throw new Exception('github_realm must be a Map.')
}

github_realm = github_realm as JSONObject

def setGithubOauth(Map github_realm){
    String githubWebUri = github_realm.optString('web_uri', GithubSecurityRealm.DEFAULT_WEB_URI)
    String githubApiUri = github_realm.optString('api_uri', GithubSecurityRealm.DEFAULT_API_URI)
    String oauthScopes = github_realm.optString('oauth_scopes', GithubSecurityRealm.DEFAULT_OAUTH_SCOPES)
    String clientID = github_realm.optString('client_id')
    String clientSecret = github_realm.optString('client_secret')
    if(!Jenkins.instance.isQuietingDown()) {
        if(clientID && clientSecret) {
            SecurityRealm updated_github_realm = new GithubSecurityRealm(githubWebUri, githubApiUri, clientID, clientSecret, oauthScopes)
            //check for equality, no need to modify the runtime if no settings changed
            if(!updated_github_realm.equals(Jenkins.instance.getSecurityRealm())) {
                Jenkins.instance.setSecurityRealm(updated_github_realm)
                Jenkins.instance.save()
                println 'Security realm configuration has changed.  Configured GitHub security realm.'
            } else {
                println 'Nothing changed.  GitHub security realm already configured.'
            }
        }
    } else {
        println 'Shutdown mode enabled.  Configure GitHub security realm SKIPPED.'
    }
}

setGithubOauth(github_realm)
