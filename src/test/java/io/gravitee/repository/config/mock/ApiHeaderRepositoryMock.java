/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.repository.config.mock;

import io.gravitee.repository.management.api.ApiHeaderRepository;
import io.gravitee.repository.management.api.AuditRepository;
import io.gravitee.repository.management.api.search.AuditCriteria;
import io.gravitee.repository.management.model.ApiHeader;
import io.gravitee.repository.management.model.Audit;
import io.gravitee.repository.management.model.Plan;
import org.mockito.ArgumentMatcher;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ApiHeaderRepositoryMock extends AbstractRepositoryMock<ApiHeaderRepository> {

    public ApiHeaderRepositoryMock() {
        super(ApiHeaderRepository.class);
    }

    @Override
    void prepare(ApiHeaderRepository apiHeaderRepository) throws Exception {
        //shouldDelete
        when(apiHeaderRepository.findById("1"))
                .thenReturn(of(mock(ApiHeader.class)), empty());

        //shouldFindAll
        when(apiHeaderRepository.findAll())
                .thenReturn(new HashSet<>(Arrays.asList(mock(ApiHeader.class), mock(ApiHeader.class), mock(ApiHeader.class))));
        
      //shouldFindAllByEnvironment
        when(apiHeaderRepository.findAllByEnvironment(any()))
                .thenReturn(new HashSet<>(Arrays.asList(mock(ApiHeader.class), mock(ApiHeader.class))));

        //shouldUpdate
        ApiHeader up = new ApiHeader();
        up.setId("toUpdate");
        up.setEnvironment("new_DEFAULT");
        up.setName("newName");
        up.setValue("newValue");
        up.setOrder(123);
        up.setCreatedAt(new Date(1439027010882L));
        up.setCreatedAt(new Date(1439027010883L));
        when(apiHeaderRepository.findById("toUpdate")).thenReturn(of(up));
    }
}
