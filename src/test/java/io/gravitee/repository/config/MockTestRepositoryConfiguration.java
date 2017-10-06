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
package io.gravitee.repository.config;

import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.*;
import io.gravitee.repository.management.api.search.EventCriteria;
import io.gravitee.repository.management.api.search.builder.PageableBuilder;
import io.gravitee.repository.management.model.*;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.util.collections.Sets;
import org.springframework.context.annotation.Bean;

import java.util.*;

import static io.gravitee.repository.utils.DateUtils.parse;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.collections.Sets.newSet;

public class MockTestRepositoryConfiguration {

    @Bean
    public TestRepositoryInitializer testRepositoryInitializer() {
        return mock(TestRepositoryInitializer.class);
    }

    @Bean
    public ApiKeyRepository apiKeyRepository() throws Exception {
        final ApiKeyRepository apiKeyRepository = mock(ApiKeyRepository.class);

        final ApiKey apiKey = mock(ApiKey.class);
        when(apiKey.getKey()).thenReturn("apiKey");
        when(apiKey.getExpireAt()).thenReturn(parse("11/02/2016"));
        when(apiKey.getSubscription()).thenReturn("subscription1");
        when(apiKeyRepository.findById(anyString())).thenReturn(empty());
        when(apiKeyRepository.findById("d449098d-8c31-4275-ad59-8dd707865a33")).thenReturn(of(apiKey));
        when(apiKeyRepository.findById("apiKey")).thenReturn(of(apiKey));
        when(apiKeyRepository.findBySubscription("subscription1")).thenReturn(newSet(apiKey, mock(ApiKey.class)));

        when(apiKeyRepository.update(argThat(new ArgumentMatcher<ApiKey>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof ApiKey && ((ApiKey)o).getKey().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return apiKeyRepository;
    }

    @Bean
    public ApiRepository apiRepository() throws Exception {
        final ApiRepository apiRepository = mock(ApiRepository.class);

        final Api apiToDelete = mock(Api.class);
        when(apiToDelete.getId()).thenReturn("api-to-delete");

        final Api apiToUpdate = mock(Api.class);
        when(apiToUpdate.getId()).thenReturn("api-to-update");
        when(apiToUpdate.getName()).thenReturn("api-to-update");
        final Api apiUpdated = mock(Api.class);
        when(apiUpdated.getName()).thenReturn("New API name");
        when(apiUpdated.getDescription()).thenReturn("New description");
        when(apiUpdated.getViews()).thenReturn(Sets.newSet("view1", "view2"));
        when(apiUpdated.getDefinition()).thenReturn("New definition");
        when(apiUpdated.getDeployedAt()).thenReturn(parse("11/02/2016"));
        when(apiUpdated.getGroups()).thenReturn(Collections.singleton("New group"));
        when(apiUpdated.getLifecycleState()).thenReturn(LifecycleState.STARTED);
        when(apiUpdated.getPicture()).thenReturn("New picture");
        when(apiUpdated.getCreatedAt()).thenReturn(parse("11/02/2016"));
        when(apiUpdated.getUpdatedAt()).thenReturn(parse("13/11/2016"));
        when(apiUpdated.getVersion()).thenReturn("New version");
        when(apiUpdated.getVisibility()).thenReturn(Visibility.PRIVATE);

        when(apiRepository.findById("api-to-update")).thenReturn(of(apiToUpdate), of(apiUpdated));

        when(apiRepository.findById("api-to-delete")).thenReturn(of(apiToDelete), empty());

        when(apiRepository.findById("findByNameMissing")).thenReturn(empty());

        final Api newApi = mock(Api.class);
        when(newApi.getVersion()).thenReturn("1");
        when(newApi.getLifecycleState()).thenReturn(LifecycleState.STOPPED);
        when(newApi.getVisibility()).thenReturn(Visibility.PRIVATE);
        when(newApi.getDefinition()).thenReturn("{}");
        when(newApi.getCreatedAt()).thenReturn(parse("11/02/2016"));
        when(newApi.getUpdatedAt()).thenReturn(parse("12/02/2016"));
        when(apiRepository.findById("sample-new")).thenReturn(of(newApi));

        final Api groupedApi = mock(Api.class);
        when(groupedApi.getGroups()).thenReturn(Collections.singleton("api-group"));
        when(apiRepository.findById("grouped-api")).thenReturn(of(groupedApi));

        final Api apiToFindById = mock(Api.class);
        when(apiToFindById.getVersion()).thenReturn("1");
        when(apiToFindById.getName()).thenReturn("api-to-findById");
        when(apiToFindById.getLifecycleState()).thenReturn(LifecycleState.STOPPED);
        when(apiToFindById.getVisibility()).thenReturn(Visibility.PUBLIC);
        when(apiToFindById.getDefinition()).thenReturn("{}");
        when(apiToFindById.getCreatedAt()).thenReturn(parse("11/02/2016"));
        when(apiToFindById.getUpdatedAt()).thenReturn(parse("12/02/2016"));
        when(apiToFindById.getLabels()).thenReturn(Arrays.asList("label 1", "label 2"));
        when(apiRepository.findById("api-to-findById")).thenReturn(of(apiToFindById));

        when(apiRepository.findAll()).thenReturn(new HashSet<>(Arrays.asList(mock(Api.class), mock(Api.class), mock(Api.class), mock(Api.class))));

        when(apiRepository.findByIds(Arrays.asList("api-to-delete", "api-to-update", "unknown"))).
                thenReturn(new HashSet<>(Arrays.asList(apiToUpdate, apiToDelete)));

        when(apiRepository.update(argThat(new ArgumentMatcher<Api>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Api && ((Api)o).getId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return apiRepository;
    }

    @Bean
    public ApplicationRepository applicationRepository() throws Exception {
        final ApplicationRepository applicationRepository = mock(ApplicationRepository.class);
        final Application application = mock(Application.class);
        when(application.getId()).thenReturn("application-sample");
        when(applicationRepository.findById("application-sample")).thenReturn(of(application));

        final Set<Application> allApplications = newSet(
                application,
                mock(Application.class),
                mock(Application.class),
                mock(Application.class),
                mock(Application.class),
                mock(Application.class),
                mock(Application.class));
        when(applicationRepository.findAll()).thenReturn(allApplications);
        doAnswer(invocation -> allApplications.remove(application)).when(applicationRepository).delete("deleted-app");
        when(applicationRepository.findById("deleted-app")).thenReturn(empty());

        final Application newApplication = mock(Application.class);
        when(newApplication.getName()).thenReturn("created-app");
        when(newApplication.getDescription()).thenReturn("Application description");
        when(newApplication.getType()).thenReturn("type");
        when(newApplication.getStatus()).thenReturn(ApplicationStatus.ACTIVE);
        when(newApplication.getCreatedAt()).thenReturn(parse("11/02/2016"));
        when(newApplication.getUpdatedAt()).thenReturn(parse("12/02/2016"));

        when(applicationRepository.findById("created-app")).thenReturn(of(newApplication));

        final Application updatedApplication = mock(Application.class);
        when(updatedApplication.getId()).thenReturn("updated-app");
        when(updatedApplication.getName()).thenReturn("updated-app");
        when(updatedApplication.getDescription()).thenReturn("Updated description");
        when(updatedApplication.getType()).thenReturn("update-type");
        when(updatedApplication.getStatus()).thenReturn(ApplicationStatus.ARCHIVED);
        when(updatedApplication.getCreatedAt()).thenReturn(parse("11/02/2016"));
        when(updatedApplication.getUpdatedAt()).thenReturn(parse("22/02/2016"));

        when(applicationRepository.findById("updated-app")).thenReturn(of(updatedApplication));

        final Application groupedApplication1 = mock(Application.class);
        when(groupedApplication1.getId()).thenReturn("grouped-app1");
        when(groupedApplication1.getGroups()).thenReturn(Collections.singleton("application-group"));
        when(applicationRepository.findById("grouped-app1")).thenReturn(of(groupedApplication1));

        final Application groupedApplication2 = mock(Application.class);
        when(groupedApplication2.getId()).thenReturn("grouped-app2");
        when(groupedApplication2.getGroups()).thenReturn(Collections.singleton("application-group"));
        when(applicationRepository.findById("grouped-app2")).thenReturn(of(groupedApplication2));

        final Set<Application> allArchivedApplications = newSet(groupedApplication2);
        when(applicationRepository.findAll(ApplicationStatus.ARCHIVED)).thenReturn(allArchivedApplications);

        final Application searchedApp1 = mock(Application.class);
        final Application searchedApp2 = mock(Application.class);
        when(searchedApp1.getId()).thenReturn("searched-app1");
        when(searchedApp1.getName()).thenReturn("searched-app1");
        when(searchedApp2.getId()).thenReturn("searched-app2");
        when(searchedApp2.getName()).thenReturn("searched-app2");
        when(applicationRepository.findByName("searched-app1")).thenReturn(Collections.singleton(searchedApp1));
        when(applicationRepository.findByName("arched")).thenReturn(newSet(searchedApp1, searchedApp2));
        when(applicationRepository.findByName("aRcHEd")).thenReturn(newSet(searchedApp1, searchedApp2));

        when(applicationRepository.findByIds(asList("searched-app1", "searched-app2"))).thenReturn(newSet(searchedApp1, searchedApp2));
        when(applicationRepository.findByGroups(singletonList("application-group"))).thenReturn(newSet(groupedApplication1, groupedApplication2));
        when(applicationRepository.findByGroups(singletonList("application-group"), ApplicationStatus.ARCHIVED)).thenReturn(newSet(groupedApplication2));


        when(applicationRepository.findByIds(Arrays.asList("application-sample", "updated-app", "unknown"))).
                thenReturn(new HashSet<>(Arrays.asList(application, updatedApplication)));

        when(applicationRepository.update(argThat(new ArgumentMatcher<Application>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Application && ((Application)o).getId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return applicationRepository;
    }

    @Bean
    public EventRepository eventRepository() throws Exception {
        final EventRepository eventRepository = mock(EventRepository.class);

        final Event event1 = mock(Event.class);
        final Event event2 = mock(Event.class);
        final Event event3 = mock(Event.class);
        final Event event4 = mock(Event.class);
        final Event event5 = mock(Event.class);
        final Event event6 = mock(Event.class);
        final io.gravitee.common.data.domain.Page<Event> pageEvent = mock(io.gravitee.common.data.domain.Page.class);
        final io.gravitee.common.data.domain.Page<Event> pageEvent2 = mock(io.gravitee.common.data.domain.Page.class);
        final io.gravitee.common.data.domain.Page<Event> pageEvent3 = mock(io.gravitee.common.data.domain.Page.class);
        final io.gravitee.common.data.domain.Page<Event> pageEvent4 = mock(io.gravitee.common.data.domain.Page.class);
        final io.gravitee.common.data.domain.Page<Event> pageEvent5 = mock(io.gravitee.common.data.domain.Page.class);
        final io.gravitee.common.data.domain.Page<Event> pageEvent6 = mock(io.gravitee.common.data.domain.Page.class);
        final io.gravitee.common.data.domain.Page<Event> pageEvent7 = mock(io.gravitee.common.data.domain.Page.class);

        Map<String, String> eventProperties = new HashMap<>();
        eventProperties.put("api_id", "api-1");

        Map<String, String> eventProperties2 = new HashMap<>();
        eventProperties.put("api_id", "api-4");

        when(event1.getId()).thenReturn("event1");
        when(event1.getCreatedAt()).thenReturn(parse("11/02/2016"));
        when(event1.getType()).thenReturn(EventType.PUBLISH_API);
        when(event1.getPayload()).thenReturn("{}");
        when(event1.getProperties()).thenReturn(eventProperties);
        when(event2.getId()).thenReturn("event2");
        when(event2.getType()).thenReturn(EventType.UNPUBLISH_API);
        when(event2.getCreatedAt()).thenReturn(parse("12/02/2016"));
        when(event2.getProperties()).thenReturn(eventProperties);
        when(event3.getId()).thenReturn("event3");
        when(event3.getType()).thenReturn(EventType.PUBLISH_API);
        when(event3.getCreatedAt()).thenReturn(parse("13/02/2016"));
        when(event4.getId()).thenReturn("event4");
        when(event4.getType()).thenReturn(EventType.STOP_API);
        when(event4.getCreatedAt()).thenReturn(parse("14/02/2016"));
        when(event4.getProperties()).thenReturn(eventProperties2);
        when(event5.getId()).thenReturn("event5");
        when(event5.getType()).thenReturn(EventType.START_API);
        when(event5.getCreatedAt()).thenReturn(parse("15/02/2016"));
        when(event6.getId()).thenReturn("event6");
        when(event6.getType()).thenReturn(EventType.START_API);
        when(event6.getCreatedAt()).thenReturn(parse("16/02/2016"));

        when(eventRepository.findById("event1")).thenReturn(of(event1));

        when(eventRepository.findById("event5")).thenReturn(of(event5), empty());

        when(eventRepository.create(any(Event.class))).thenReturn(event1);

        when(pageEvent.getTotalElements()).thenReturn(2L);
        when(pageEvent.getContent()).thenReturn(asList(event6, event5));
        when(eventRepository.search(
                new EventCriteria.Builder().from(1451606400000L).to(1470157767000L).types(EventType.START_API).build(),
                new PageableBuilder().pageNumber(0).pageSize(10).build())).thenReturn(pageEvent);

        when(pageEvent2.getTotalElements()).thenReturn(3L);
        when(pageEvent2.getContent()).thenReturn(asList(event6, event5, event4));
        when(eventRepository.search(
                new EventCriteria.Builder().from(1451606400000L).to(1470157767000L).types(EventType.START_API, EventType.STOP_API).build(),
                new PageableBuilder().pageNumber(0).pageSize(10).build())).thenReturn(pageEvent2);

        when(pageEvent3.getTotalElements()).thenReturn(2L);
        when(pageEvent3.getContent()).thenReturn(asList(event2, event1));
        when(eventRepository.search(
                new EventCriteria.Builder()
                        .from(1451606400000L).to(1470157767000L)
                        .property(Event.EventProperties.API_ID.getValue(), "api-1")
                        .build(),
                new PageableBuilder().pageNumber(0).pageSize(10).build())).thenReturn(pageEvent3);

        when(pageEvent4.getTotalElements()).thenReturn(0L);
        when(pageEvent4.getContent()).thenReturn(Collections.emptyList());
        when(eventRepository.search(
                new EventCriteria.Builder().from(1420070400000L).to(1422748800000L).types(EventType.START_API).build(),
                new PageableBuilder().pageNumber(0).pageSize(10).build())).thenReturn(pageEvent4);

        when(pageEvent5.getTotalElements()).thenReturn(1L);
        when(pageEvent5.getContent()).thenReturn(singletonList(event4));
        when(eventRepository.search(
                new EventCriteria.Builder()
                        .from(1451606400000L).to(1470157767000L)
                        .property(Event.EventProperties.API_ID.getValue(), "api-3")
                        .types(EventType.START_API, EventType.STOP_API)
                        .build(),
                new PageableBuilder().pageNumber(0).pageSize(10).build())).thenReturn(pageEvent5);

        when(pageEvent6.getTotalElements()).thenReturn(2L);
        when(pageEvent6.getContent()).thenReturn(asList(event2, event1));
        when(eventRepository.search(
                new EventCriteria.Builder()
                .from(1451606400000L).to(1470157767000L)
                .property(Event.EventProperties.API_ID.getValue(), "api-1")
                .build(),
                null)).thenReturn(pageEvent6);

        when(pageEvent7.getTotalElements()).thenReturn(3L);
        when(pageEvent7.getContent()).thenReturn(asList(event4, event2, event1));
        when(eventRepository.search(
                new EventCriteria.Builder()
                        .from(1451606400000L).to(1470157767000L)
                        .property(Event.EventProperties.API_ID.getValue(), asList("api-1", "api-3"))
                        .build(),
                null)).thenReturn(pageEvent7);

        when(eventRepository.search(
                new EventCriteria.Builder()
                        .from(1451606400000L).to(1470157767000L)
                        .property(Event.EventProperties.API_ID.getValue(), asList("api-1", "api-3"))
                        .build())).thenReturn(asList(event4, event2, event1));

        when(eventRepository.search(
                new EventCriteria.Builder()
                        .property(Event.EventProperties.API_ID.getValue(), asList("api-1", "api-3"))
                        .build())).thenReturn(asList(event4, event2, event1));

        when(eventRepository.search(
                new EventCriteria.Builder().types(EventType.GATEWAY_STARTED).build(),
                new PageableBuilder().pageNumber(0).pageSize(10).build())).thenReturn(
                        new io.gravitee.common.data.domain.Page<>(Collections.emptyList(), 0, 0, 0));

        when(eventRepository.update(argThat(new ArgumentMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Event && ((Event)o).getId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return eventRepository;
    }

    @Bean
    public UserRepository userRepository() throws Exception {
        final UserRepository userRepository = mock(UserRepository.class);

        final User user = mock(User.class);
        when(user.getPassword()).thenReturn("New pwd");
        final User user4 = mock(User.class);
        when(userRepository.findAll()).thenReturn(newSet(user, mock(User.class), mock(User.class),
                mock(User.class), mock(User.class), mock(User.class)));
        when(userRepository.create(any(User.class))).thenReturn(user);
        when(userRepository.findByUsername("createuser1")).thenReturn(of(user));
        when(userRepository.findByUsername("user0")).thenReturn(of(user));
        when(userRepository.findByUsernames(asList("user0", "user4"))).thenReturn(new HashSet<>(asList(user,user4)));
        when(user.getUsername()).thenReturn("createuser1");
        when(user.getEmail()).thenReturn("createuser1@gravitee.io");

        when(userRepository.update(argThat(new ArgumentMatcher<User>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof User && ((User)o).getUsername().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return userRepository;
    }

    @Bean
    public ViewRepository viewRepository() throws Exception {
        final ViewRepository viewRepository = mock(ViewRepository.class);

        final View view = mock(View.class);
        when(view.getName()).thenReturn("View name");
        when(view.getDescription()).thenReturn("Description for the new view");

        final View view2 = mock(View.class);
        when(view2.getId()).thenReturn("view");
        when(view2.getName()).thenReturn("Products");

        final View view2Updated = mock(View.class);
        when(view2Updated.getName()).thenReturn("New product");
        when(view2Updated.getDescription()).thenReturn("New description");

        final Set<View> views = newSet(view, view2, mock(View.class));
        final Set<View> viewsAfterDelete = newSet(view, view2);
        final Set<View> viewsAfterAdd = newSet(view, view2, mock(View.class), mock(View.class));

        when(viewRepository.findAll()).thenReturn(views, viewsAfterAdd, views, viewsAfterDelete, views);

        when(viewRepository.create(any(View.class))).thenReturn(view);

        when(viewRepository.findById("new-view")).thenReturn(of(view));
        when(viewRepository.findById("unknown")).thenReturn(empty());
        when(viewRepository.findById("products")).thenReturn(of(view2), of(view2Updated));

        when(viewRepository.update(argThat(new ArgumentMatcher<View>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof View && ((View)o).getId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return viewRepository;
    }

    @Bean
    public TagRepository tagRepository() throws Exception {
        final TagRepository tagRepository = mock(TagRepository.class);

        final Tag tag = mock(Tag.class);
        when(tag.getName()).thenReturn("Tag name");
        when(tag.getDescription()).thenReturn("Description for the new tag");

        final Tag tag2 = mock(Tag.class);
        when(tag2.getId()).thenReturn("tag");
        when(tag2.getName()).thenReturn("Products");

        final Tag tag2Updated = mock(Tag.class);
        when(tag2Updated.getName()).thenReturn("New product");
        when(tag2Updated.getDescription()).thenReturn("New description");

        final Set<Tag> tags = newSet(tag, tag2, mock(Tag.class));
        final Set<Tag> tagsAfterDelete = newSet(tag, tag2);
        final Set<Tag> tagsAfterAdd = newSet(tag, tag2, mock(Tag.class), mock(Tag.class));

        when(tagRepository.findAll()).thenReturn(tags, tagsAfterAdd, tags, tagsAfterDelete, tags);

        when(tagRepository.create(any(Tag.class))).thenReturn(tag);

        when(tagRepository.findById("new-tag")).thenReturn(of(tag));
        when(tagRepository.findById("products")).thenReturn(of(tag2), of(tag2Updated));

        when(tagRepository.update(argThat(new ArgumentMatcher<Tag>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Tag && ((Tag)o).getId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return tagRepository;
    }


    @Bean
    public GroupRepository groupRepository() throws Exception {
        final GroupRepository groupRepository = mock(GroupRepository.class);

        final Group createGroup = new Group();
        createGroup.setId("1");
        createGroup.setAdministrators(Collections.emptyList());
        when(groupRepository.create(any())).thenReturn(createGroup);

        final Group group_application_1 = new Group();
        group_application_1.setId("group-application-1");
        group_application_1.setName("group-application-1");
        group_application_1.setAdministrators(asList("user1", "user2"));
        GroupEventRule eventRule1 = new GroupEventRule();
        eventRule1.setEvent(GroupEvent.API_CREATE);
        GroupEventRule eventRule2 = new GroupEventRule();
        eventRule2.setEvent(GroupEvent.APPLICATION_CREATE);
        group_application_1.setEventRules(Arrays.asList(eventRule1, eventRule2));
        final Group group_api_to_delete = new Group();
        group_api_to_delete.setId("group-api-to-delete");
        group_api_to_delete.setName("group-api-to-delete");
        group_api_to_delete.setAdministrators(Collections.emptyList());
        final Group group_updated = new Group();
        group_updated.setId("group-application-1");
        group_updated.setName("Modified Name");
        group_updated.setUpdatedAt(new Date(0));
        when(groupRepository.findAll()).thenReturn(newSet(group_application_1, group_api_to_delete));
        when(groupRepository.findById("group-application-1")).thenReturn(of(group_application_1));
        when(groupRepository.findById("unknown")).thenReturn(empty());
        when(groupRepository.findById("group-api-to-delete")).thenReturn(empty());
        when(groupRepository.update(argThat(new ArgumentMatcher<Group>() {
            @Override
            public boolean matches(Object o) {
                return o != null && o instanceof Group && ((Group)o).getId().equals("unknown");
            }
        }))).thenThrow(new TechnicalException());

        when(groupRepository.update(argThat(new ArgumentMatcher<Group>() {
            @Override
            public boolean matches(Object o) {
                return o != null && o instanceof Group && ((Group)o).getId().equals("group-application-1");
            }
        }))).thenReturn(group_updated);

        when(groupRepository.findByIds(new HashSet<>(Arrays.asList("group-application-1", "group-api-to-delete", "unknown")))).
                thenReturn(new HashSet<>(Arrays.asList(group_application_1, group_api_to_delete)));

        when(groupRepository.update(argThat(new ArgumentMatcher<Group>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Group && ((Group)o).getId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return groupRepository;
    }

    @Bean
    public PlanRepository planRepository() throws Exception {
        final PlanRepository planRepository = mock(PlanRepository.class);

        final Plan plan = mock(Plan.class);
        when(plan.getName()).thenReturn("Plan name");
        when(plan.getDescription()).thenReturn("Description for the new plan");
        when(plan.getValidation()).thenReturn(Plan.PlanValidationType.AUTO);
        when(plan.getType()).thenReturn(Plan.PlanType.API);
        when(plan.getApis()).thenReturn(Collections.singleton("my-api"));
        when(plan.getCreatedAt()).thenReturn(parse("11/02/2016"));
        when(plan.getUpdatedAt()).thenReturn(parse("12/02/2016"));
        when(plan.getPublishedAt()).thenReturn(parse("13/02/2016"));
        when(plan.getClosedAt()).thenReturn(parse("14/02/2016"));
        when(plan.getStatus()).thenReturn(Plan.Status.STAGING);
        when(plan.getSecurity()).thenReturn(Plan.PlanSecurityType.KEY_LESS);

        final Plan plan2 = mock(Plan.class);
        when(plan2.getId()).thenReturn("my-plan");
        when(plan2.getName()).thenReturn("Free plan");
        when(plan2.getDescription()).thenReturn("Description of the free plan");
        when(plan2.getApis()).thenReturn(singleton("api1"));
        when(plan2.getSecurity()).thenReturn(Plan.PlanSecurityType.API_KEY);
        when(plan2.getValidation()).thenReturn(Plan.PlanValidationType.AUTO);
        when(plan2.getType()).thenReturn(Plan.PlanType.API);
        when(plan2.getStatus()).thenReturn(Plan.Status.PUBLISHED);
        when(plan2.getOrder()).thenReturn(2);
        when(plan2.getCreatedAt()).thenReturn(new Date(1506964899000L));
        when(plan2.getUpdatedAt()).thenReturn(new Date(1507032062000L));
        when(plan2.getPublishedAt()).thenReturn(new Date(1506878460000L));
        when(plan2.getClosedAt()).thenReturn(new Date(1507611600000L));
        when(plan2.getCharacteristics()).thenReturn(Arrays.asList("charac 1", "charac 2"));
        when(plan2.getExcludedGroups()).thenReturn(singletonList("grp1"));

        final Plan updatedPlan = mock(Plan.class);
        when(updatedPlan.getId()).thenReturn("updated-plan");
        when(updatedPlan.getName()).thenReturn("New plan");

        when(planRepository.create(any(Plan.class))).thenReturn(plan);

        when(planRepository.findById("new-plan")).thenReturn(of(plan));
        when(planRepository.findById("my-plan")).thenReturn(of(plan2));
        when(planRepository.findById("updated-plan")).thenReturn(of(updatedPlan));

        when(planRepository.findById("stores")).thenReturn(Optional.empty());

        when(planRepository.findByApi("api1")).thenReturn(
                new HashSet<>(asList(plan, plan2)));

        when(planRepository.findById("unknown")).thenReturn(empty());

        when(planRepository.update(argThat(new ArgumentMatcher<Plan>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Plan && ((Plan)o).getId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return planRepository;
    }

    @Bean
    public MembershipRepository membershipRepository() throws Exception {
        final MembershipRepository repo = mock(MembershipRepository.class);

        Map<Integer, String> API_OWNER_ROLE_MAP = Collections.singletonMap(RoleScope.API.getId(), "OWNER");
        Membership m1 = mock(Membership.class);
        when(m1.getUserId()).thenReturn("user1");
        when(m1.getReferenceType()).thenReturn(MembershipReferenceType.API);
        when(m1.getRoles()).thenReturn(API_OWNER_ROLE_MAP);
        when(m1.getReferenceId()).thenReturn("api1");
        Membership m2 = new Membership("user2", "api2", MembershipReferenceType.API);
        m2.setRoles(API_OWNER_ROLE_MAP);
        Membership m3 = new Membership("user3", "api3", MembershipReferenceType.API);
        m3.setRoles(API_OWNER_ROLE_MAP);
        Membership m4 = new Membership("userToDelete", "app1", MembershipReferenceType.APPLICATION);
        m4.setCreatedAt(new Date(0));

        when(repo.findById("user1", MembershipReferenceType.API, "api1"))
                .thenReturn(of(m1));
        when(repo.findById(null, MembershipReferenceType.API, "api1"))
                .thenReturn(empty());
        when(repo.findById("userToDelete", MembershipReferenceType.APPLICATION, "app1"))
                .thenReturn(empty());
        when(repo.findByReferenceAndRole(eq(MembershipReferenceType.API), eq("api1"), eq(RoleScope.API), any()))
                .thenReturn(Collections.singleton(m1));
        when(repo.findByReferenceAndRole(eq(MembershipReferenceType.API), eq("api1"), eq(null), any()))
                .thenReturn(Collections.singleton(m1));
        when(repo.findByUserAndReferenceType("user1", MembershipReferenceType.API))
                .thenReturn(Collections.singleton(m1));
        when(repo.findByUserAndReferenceTypeAndRole("user1", MembershipReferenceType.API, RoleScope.API, "OWNER"))
                .thenReturn(Collections.singleton(m1));
        when(repo.findByReferencesAndRole(MembershipReferenceType.API, asList("api2", "api3"), null, null))
                .thenReturn(new HashSet<>(asList(m2, m3)));
        when(repo.findByReferencesAndRole(MembershipReferenceType.API, asList("api2", "api3"), RoleScope.API, "OWNER"))
                .thenReturn(new HashSet<>(singletonList(m2)));
        when(repo.update(any())).thenReturn(m4);

        Membership api1_findByIds = mock(Membership.class);
        when(api1_findByIds.getReferenceId()).thenReturn("api1_findByIds");
        Membership api2_findByIds = mock(Membership.class);
        when(api2_findByIds.getReferenceId()).thenReturn("api2_findByIds");
        when(repo.findByIds(
                "user_findByIds",
                MembershipReferenceType.API,
                new HashSet<>(Arrays.asList("api1_findByIds", "api2_findByIds", "unknown")))).
                thenReturn(new HashSet<>(Arrays.asList(api1_findByIds, api2_findByIds)));

        when(repo.update(argThat(new ArgumentMatcher<Membership>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Membership && ((Membership)o).getReferenceId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return repo;
    }

    @Bean
    public PageRepository pageRepository() throws Exception {
        final PageRepository pageRepository = mock(PageRepository.class);

        Page findApiPage = mock(Page.class);
        when(findApiPage.getId()).thenReturn("FindApiPage");
        when(findApiPage.getName()).thenReturn("Find apiPage by apiId or Id");
        when(findApiPage.getContent()).thenReturn("Content of the page");
        when(findApiPage.getApi()).thenReturn("my-api");
        when(findApiPage.getType()).thenReturn(PageType.MARKDOWN);
        when(findApiPage.getLastContributor()).thenReturn("john_doe");
        when(findApiPage.getOrder()).thenReturn(2);
        when(findApiPage.isPublished()).thenReturn(true);
        PageSource pageSource = new PageSource();
        pageSource.setType("sourceType");
        pageSource.setConfiguration("sourceConfiguration");
        when(findApiPage.getSource()).thenReturn(pageSource);
        PageConfiguration pageConfiguration = new PageConfiguration();
        pageConfiguration.setTryIt(true);
        pageConfiguration.setTryItURL("http://company.com");
        when(findApiPage.getConfiguration()).thenReturn(pageConfiguration);
        when(findApiPage.isHomepage()).thenReturn(true);
        when(findApiPage.getExcludedGroups()).thenReturn(Arrays.asList("grp1", "grp2"));
        when(findApiPage.getCreatedAt()).thenReturn(new Date(1439022010883L));
        when(findApiPage.getUpdatedAt()).thenReturn(new Date(1119022010883L));

        // shouldFindApiPageByApiId
        when(pageRepository.findApiPageByApiId("my-api")).thenReturn(newSet(findApiPage));

        // shouldFindApiPageById
        when(pageRepository.findById("FindApiPage")).thenReturn(of(findApiPage));

        // shouldCreateApiPage
        final Page createPage = mock(Page.class);
        when(createPage.getName()).thenReturn("Page name");
        when(createPage.getContent()).thenReturn("Page content");
        when(createPage.getOrder()).thenReturn(3);
        when(createPage.getType()).thenReturn(PageType.MARKDOWN);
        when(createPage.isHomepage()).thenReturn(true);
        when(pageRepository.findById("new-page")).thenReturn(empty(), of(createPage));

        // shouldCreatePortalPage
        final Page createPortalPage = mock(Page.class);
        when(createPortalPage.getName()).thenReturn("Page name");
        when(createPortalPage.getContent()).thenReturn("Page content");
        when(createPortalPage.getOrder()).thenReturn(3);
        when(createPortalPage.getType()).thenReturn(PageType.MARKDOWN);
        when(createPortalPage.isHomepage()).thenReturn(false);
        when(pageRepository.findById("new-portal-page")).thenReturn(empty(), of(createPortalPage));

        // shouldDelete
        when(pageRepository.findById("page-to-be-deleted")).thenReturn(of(mock(Page.class)), empty());

        // should Update
        Page updatePageBefore = mock(Page.class);
        when(updatePageBefore.getId()).thenReturn("updatePage");
        when(updatePageBefore.getName()).thenReturn("Update Page");
        when(updatePageBefore.getContent()).thenReturn("Content of the update page");
        Page updatePageAfter = mock(Page.class);
        when(updatePageAfter.getId()).thenReturn("updatePage");
        when(updatePageAfter.getName()).thenReturn("New name");
        when(updatePageAfter.getContent()).thenReturn("New content");
        when(pageRepository.findById("updatePage")).thenReturn(of(updatePageBefore), of(updatePageAfter));

        when(pageRepository.update(argThat(new ArgumentMatcher<Page>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Page && ((Page)o).getId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        //Find api pages
        final Page homepage = mock(Page.class);
        when(homepage.getId()).thenReturn("home");
        when(pageRepository.findApiPageByApiIdAndHomepage("my-api-2", true)).thenReturn(Collections.singleton(homepage));
        when(pageRepository.findApiPageByApiIdAndHomepage("my-api-2", false)).thenReturn(newSet(mock(Page.class), mock(Page.class)));

        //Find portal pages
        final Page portalHomepage = mock(Page.class);
        when(portalHomepage.getId()).thenReturn("FindPortalPage-homepage");
        final Page portalNotHomepage = mock(Page.class);
        when(portalNotHomepage.getId()).thenReturn("FindPortalPage-nothomepage");
        when(pageRepository.findPortalPages()).thenReturn(newSet(portalHomepage, portalNotHomepage));
        when(pageRepository.findPortalPageByHomepage(true)).thenReturn(newSet(portalHomepage));
        when(pageRepository.findPortalPageByHomepage(false)).thenReturn(newSet(portalNotHomepage));

        // Find max api page order
        when(pageRepository.findMaxApiPageOrderByApiId("my-api-2")).thenReturn(2);
        when(pageRepository.findMaxApiPageOrderByApiId("unknown api id")).thenReturn(0);
        when(pageRepository.findMaxPortalPageOrder()).thenReturn(20);



        return pageRepository;
    }

    @Bean
    public SubscriptionRepository subscriptionRepository() throws Exception {
        final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);

        final Subscription sub1 = new Subscription();
        sub1.setId("sub1");
        sub1.setApplication("app1");
        sub1.setPlan("plan1");
        sub1.setUpdatedAt(new Date(0));

        when(subscriptionRepository.findByPlan("plan1")).thenReturn(newSet(sub1));
        when(subscriptionRepository.findByPlan("unknown-plan")).thenReturn(Collections.emptySet());
        when(subscriptionRepository.findByApplication("app1")).thenReturn(newSet(sub1));
        when(subscriptionRepository.findByApplication("unknown-app")).thenReturn(Collections.emptySet());
        when(subscriptionRepository.findById("sub1")).thenReturn(of(sub1));
        when(subscriptionRepository.findById("unknown-sub")).thenReturn(empty());
        when(subscriptionRepository.findById("sub2")).thenReturn(empty());
        when(subscriptionRepository.update(sub1)).thenReturn(sub1);

        when(subscriptionRepository.update(argThat(new ArgumentMatcher<Subscription>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Subscription && ((Subscription)o).getId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());
        return subscriptionRepository;
    }

    @Bean
    public TenantRepository tenantRepository() throws Exception {
        final TenantRepository tenantRepository = mock(TenantRepository.class);

        final Tenant tenant = mock(Tenant.class);
        when(tenant.getName()).thenReturn("Tenant name");
        when(tenant.getDescription()).thenReturn("Description for the new tenant");

        final Tenant tenant2 = mock(Tenant.class);
        when(tenant2.getId()).thenReturn("tenant");
        when(tenant2.getName()).thenReturn("Asia");

        final Tenant tenant2Updated = mock(Tenant.class);
        when(tenant2Updated.getName()).thenReturn("New tenant");
        when(tenant2Updated.getDescription()).thenReturn("New description");

        final Set<Tenant> tenants = newSet(tenant, tenant2, mock(Tenant.class));
        final Set<Tenant> tenantsAfterDelete = newSet(tenant, tenant2);
        final Set<Tenant> tenantsAfterAdd = newSet(tenant, tenant2, mock(Tenant.class), mock(Tenant.class));

        when(tenantRepository.findAll()).thenReturn(tenants, tenantsAfterAdd, tenants, tenantsAfterDelete, tenants);

        when(tenantRepository.create(any(Tenant.class))).thenReturn(tenant);

        when(tenantRepository.findById("new-tenant")).thenReturn(of(tenant));
        when(tenantRepository.findById("asia")).thenReturn(of(tenant2), of(tenant2Updated));

        when(tenantRepository.update(argThat(new ArgumentMatcher<Tenant>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Tenant && ((Tenant)o).getId().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return tenantRepository;
    }

    @Bean
    public MetadataRepository metadataRepository() throws Exception {
        final MetadataRepository metadataRepository = mock(MetadataRepository.class);

        final Metadata booleanMetadata = mock(Metadata.class);
        when(booleanMetadata.getKey()).thenReturn("boolean");
        when(booleanMetadata.getName()).thenReturn("Boolean");

        final Metadata stringMetadata = mock(Metadata.class);
        when(stringMetadata.getName()).thenReturn("Metadata name");
        when(stringMetadata.getFormat()).thenReturn(MetadataFormat.STRING);
        when(stringMetadata.getValue()).thenReturn("String");
        when(stringMetadata.getKey()).thenReturn("key");
        when(stringMetadata.getReferenceId()).thenReturn("apiId");
        when(stringMetadata.getReferenceType()).thenReturn(MetadataReferenceType.API);

        final Metadata metadata2Updated = mock(Metadata.class);
        when(metadata2Updated.getName()).thenReturn("New metadata");
        when(metadata2Updated.getValue()).thenReturn("New value");
        when(metadata2Updated.getFormat()).thenReturn(MetadataFormat.URL);
        when(metadata2Updated.getReferenceType()).thenReturn(MetadataReferenceType.APPLICATION);

        final List<Metadata> metadataList = asList(booleanMetadata, stringMetadata, mock(Metadata.class));
        final List<Metadata> metadataListAfterAdd = asList(booleanMetadata, stringMetadata, mock(Metadata.class), mock(Metadata.class));
        final List<Metadata> metadataListAfterDelete = asList(booleanMetadata, stringMetadata);


        when(metadataRepository.findByReferenceType(MetadataReferenceType.DEFAULT)).thenReturn(metadataList, metadataListAfterAdd, metadataList, metadataList);
        when(metadataRepository.findByReferenceTypeAndReferenceId(MetadataReferenceType.APPLICATION, "applicationId")).thenReturn(metadataList, metadataListAfterDelete);
        when(metadataRepository.findByReferenceTypeAndReferenceId(MetadataReferenceType.API, "apiId")).thenReturn(singletonList(stringMetadata));
        when(metadataRepository.findByReferenceType(MetadataReferenceType.APPLICATION)).thenReturn(singletonList(metadata2Updated));
        when(metadataRepository.findByKeyAndReferenceType("string", MetadataReferenceType.API)).thenReturn(singletonList(stringMetadata));

        when(metadataRepository.create(any(Metadata.class))).thenReturn(booleanMetadata);

        when(metadataRepository.findById("new-metadata", "_", MetadataReferenceType.DEFAULT)).thenReturn(of(stringMetadata));
        when(metadataRepository.findById("boolean", "_", MetadataReferenceType.DEFAULT)).thenReturn(of(booleanMetadata), of(metadata2Updated));

        when(metadataRepository.update(argThat(new ArgumentMatcher<Metadata>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Metadata && ((Metadata)o).getKey().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return metadataRepository;
    }

    @Bean
    public RoleRepository roleRepository() throws Exception {
        final RoleRepository roleRepository = mock(RoleRepository.class);

        final Role toCreate = mock(Role.class);
        when(toCreate.getName()).thenReturn("to create");
        when(toCreate.getScope()).thenReturn(RoleScope.API);
        when(toCreate.getPermissions()).thenReturn(new int[]{3});

        final Role toDelete = mock(Role.class);
        when(toDelete.getName()).thenReturn("to delete");
        when(toDelete.getScope()).thenReturn(RoleScope.MANAGEMENT);
        when(toDelete.getPermissions()).thenReturn(new int[]{1,2,3});

        final Role toUpdate = mock(Role.class);
        when(toUpdate.getName()).thenReturn("to update");
        when(toUpdate.getDescription()).thenReturn("new description");
        when(toUpdate.getScope()).thenReturn(RoleScope.MANAGEMENT);
        when(toUpdate.isDefaultRole()).thenReturn(true);
        when(toUpdate.getPermissions()).thenReturn(new int[]{4,5});

        final Role findByScope1 = mock(Role.class);
        when(findByScope1.getName()).thenReturn("find by scope 1");
        when(findByScope1.getDescription()).thenReturn("role description");
        when(findByScope1.getScope()).thenReturn(RoleScope.PORTAL);
        when(findByScope1.isDefaultRole()).thenReturn(true);
        when(findByScope1.isSystem()).thenReturn(true);
        when(findByScope1.getPermissions()).thenReturn(new int[]{1});

        final Role findByScope2 = mock(Role.class);
        when(findByScope2.getName()).thenReturn("find by scope 2");
        when(findByScope2.getScope()).thenReturn(RoleScope.PORTAL);
        when(findByScope2.isDefaultRole()).thenReturn(false);
        when(findByScope2.getPermissions()).thenReturn(new int[]{1});

        when(roleRepository.findById(findByScope1.getScope(), findByScope1.getName())).thenReturn(of(findByScope1));
        when(roleRepository.findById(toUpdate.getScope(), toUpdate.getName())).thenReturn(of(toUpdate));
        when(roleRepository.findById(toCreate.getScope(), toCreate.getName())).thenReturn(empty(),of(findByScope1));
        when(roleRepository.findById(toDelete.getScope(), toDelete.getName())).thenReturn(of(findByScope1), empty());
        when(roleRepository.findById(findByScope2.getScope(), findByScope2.getName())).thenReturn(of(findByScope2));
        when(roleRepository.create(any(Role.class))).thenReturn(toCreate);
        when(roleRepository.findAll()).thenReturn(newSet(toDelete,toUpdate, findByScope1, findByScope2));
        when(roleRepository.findByScope(RoleScope.PORTAL)).thenReturn(newSet(findByScope1, findByScope2));
        when(roleRepository.update(any())).thenReturn(toUpdate);

        when(roleRepository.update(argThat(new ArgumentMatcher<Role>() {
            @Override
            public boolean matches(Object o) {
                return o == null || (o instanceof Role && ((Role)o).getName().equals("unknown"));
            }
        }))).thenThrow(new IllegalStateException());

        return roleRepository;
    }
}
