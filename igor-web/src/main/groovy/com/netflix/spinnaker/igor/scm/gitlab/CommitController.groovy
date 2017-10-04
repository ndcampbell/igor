/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.igor.scm.gitlab

import com.netflix.spinnaker.igor.scm.AbstractCommitController
import com.netflix.spinnaker.igor.scm.gitlab.client.GitLabMaster
import com.netflix.spinnaker.igor.scm.gitlab.client.model.CompareCommitsResponse
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import retrofit.RetrofitError

@Slf4j
@RestController(value = "GitLabCommitController")
@ConditionalOnProperty('gitlab.baseUrl')
@RequestMapping("/gitlab")
class CommitController extends AbstractCommitController {
    @Autowired
    GitLabMaster master

    @RequestMapping(value = '/{projectKey}/{repositorySlug}/compareCommits')
    List compareCommits(@PathVariable(value = 'projectKey') String projectKey, @PathVariable(value='repositorySlug') repositorySlug, @RequestParam Map<String, String> requestParams) {
        super.compareCommits(projectKey, repositorySlug, requestParams)
        CompareCommitsResponse commitsResponse
        try {
            commitsResponse = master.gitlabClient.getCompareCommits(projectKey, repositorySlug, requestParams)
        } catch (RetrofitError e) {
            if(e.getKind() == RetrofitError.Kind.NETWORK) {
                throw new RuntimeException("Could not find the server ${gitlabMaster.baseUrl}")
            } else if(e.response.status == 404) {
                return getNotFoundCommitsResponse(projectKey, repositorySlug, requestParams.to, requestParams.from, gitlabMaster.baseUrl)
            }
        }

        List result = []
        //commitsResponse.values.each {
        //    result << [displayId: it?.displayId, id: it?.id, authorDisplayName: it?.author?.displayName,
        //               timestamp: it?.authorTimestamp, message : it?.message, commitUrl: it?.html_url]
       // }
        commitsResponse.values.each {
            result << ["test"]
        }
        return result
    }
}
