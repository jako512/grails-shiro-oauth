/*
 * Copyright 2012 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.shiro.oauth

import org.apache.shiro.authc.AuthenticationToken

/**
 * This is a Shiro authentication token for OAuth providers. It must be initialised
 * with a {@link Scribe https://github.com/fernandezpablo85/scribe-java} access token
 * from which dedicated provider tokens can extract extra information such as the
 * principal.
 */
abstract class OAuthToken implements AuthenticationToken {
    private scribeToken
    private Map tokenParams

    /**
     * Initialises the token from a Scribe access token.
     */
    OAuthToken(scribeToken) {
        this.scribeToken = scribeToken
        this.tokenParams = extractParameters(scribeToken.rawResponse)
    }

    /**
     * Returns the raw response from the OAuth provider as a string.
     */
    def getCredentials() {
        return scribeToken.rawResponse
    }

    /**
     * Returns the name of the OAuth provider for this token.
     */
    abstract String getProviderName()

    /**
     * Returns the parameters in the OAuth access token as a map.
     */
    protected final Map getParameters() { return Collections.unmodifiableMap(tokenParams) }

    private Map extractParameters(String data) {
        // Ideally we'd use collectEntries here, but it was only added in
        // Groovy 1.7.9 and so doesn't work with Grails 1.3.x.
        def result = [:]
        if (!data) return result

        def parameterStrings = data.split('&')
        for (param in parameterStrings) {
            def pair = param.split('=')
            result[pair[0]] = pair[1]
        }

        return result
    }
}
