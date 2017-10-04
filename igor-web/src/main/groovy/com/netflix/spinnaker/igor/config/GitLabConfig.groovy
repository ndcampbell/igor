/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.igor.config

import com.netflix.spinnaker.igor.scm.gitlab.client.GitLabClient
import com.netflix.spinnaker.igor.scm.gitlab.client.GitLabMaster
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit.Endpoints
import retrofit.RequestInterceptor
import retrofit.RestAdapter
import retrofit.client.OkClient
import retrofit.converter.JacksonConverter

import javax.validation.Valid

/**
 * Converts the list of GitLab Configuration properties a collection of clients to access the GitLab hosts
 */
@Configuration
@ConditionalOnProperty('gitlab.baseUrl')
@Slf4j
@CompileStatic
@EnableConfigurationProperties(GitLabProperties)
class GitLabConfig {

    @Bean
    GitLabMaster gitlabMasters(@Valid GitLabProperties gitlabProperties) {
        log.info "bootstrapping ${gitlabProperties.baseUrl} as gitlab"
        new GitLabMaster(gitlabClient: gitlabClient(gitlabProperties.baseUrl, gitlabProperties.accessToken), baseUrl: gitlabProperties.baseUrl)
    }

    GitLabClient gitlabClient(String address, String accessToken) {
        new RestAdapter.Builder()
            .setEndpoint(Endpoints.newFixedEndpoint(address))
            .setRequestInterceptor(new BasicAuthRequestInterceptor(accessToken))
            .setClient(new OkClient())
            .setConverter(new JacksonConverter())
            .build()
            .create(GitLabClient)

    }

    static class BasicAuthRequestInterceptor implements RequestInterceptor {

        private final String accessToken

        BasicAuthRequestInterceptor(String accessToken) {
            this.accessToken = accessToken
        }

        @Override
        void intercept(RequestInterceptor.RequestFacade request) {
            request.addQueryParam("access_token", accessToken)
        }
    }

}
