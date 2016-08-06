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
package io.gravitee.repository;

import io.gravitee.common.data.domain.Page;
import io.gravitee.repository.config.AbstractRepositoryTest;
import io.gravitee.repository.management.model.Event;
import io.gravitee.repository.management.model.EventType;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class EventRepositoryTest extends AbstractRepositoryTest {

    @Override
    protected String getTestCasesPath() {
        return "/data/event-tests/";
    }

    @Test
    public void createEventTest() throws Exception {
        Event event = new Event();
        event.setType(EventType.PUBLISH_API);
        event.setPayload("{}");
        event.setParentId(null);
        event.setCreatedAt(new Date());
        event.setUpdatedAt(event.getCreatedAt());

        Event eventCreated = eventRepository.create(event);

        assertEquals("Invalid saved event type.", EventType.PUBLISH_API, eventCreated.getType());
        assertEquals("Invalid saved event paylod.", "{}", eventCreated.getPayload());
    }

    @Test
    public void findByIdTest() throws Exception {
        Optional<Event> event = eventRepository.findById("event1");
        assertTrue("Event not found", event.isPresent());
        assertEquals(EventType.PUBLISH_API, event.get().getType());
    }

    @Test
    public void findByType() throws Exception {
        Set<Event> events = eventRepository.findByType(Arrays.asList(EventType.PUBLISH_API, EventType.UNPUBLISH_API));
        assertEquals(3, events.size());
        Optional<Event> event = events.stream().sorted((e1, e2) -> e2.getCreatedAt().compareTo(e1.getCreatedAt())).findFirst();
        assertTrue(event.isPresent());
        assertTrue("event3".equals(event.get().getId()));
    }

    @Test
    public void findByApi() throws Exception {
        String apiId = "api-1";
        Set<Event> events = eventRepository.findByProperty(Event.EventProperties.API_ID.getValue(), apiId);
        assertEquals(2, events.size());
        Optional<Event> event = events.stream().sorted((e1, e2) -> e1.getCreatedAt().compareTo(e2.getCreatedAt())).findFirst();
        assertTrue(event.isPresent());
        assertTrue("event1".equals(event.get().getId()));
    }

    @Test
    public void searchNoResults() {
        Map<String, Object> values = new HashMap<>();
        values.put("type", EventType.START_API.toString());

        Page<Event> eventPage = eventRepository.search(values, 1420070400000l, 1422748800000l, 0, 10);
        assertTrue(0l == eventPage.getTotalElements());
    }

    @Test
    public void searchBySingleEventType() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("type", EventType.START_API.toString());

        Page<Event> eventPage = eventRepository.search(values, 1451606400000l, 1470157767000l, 0, 10);
        assertTrue(2l == eventPage.getTotalElements());
        Optional<Event> event = eventPage.getContent().stream().sorted((e1, e2) -> e1.getCreatedAt().compareTo(e2.getCreatedAt())).findFirst();
        assertTrue("event5".equals(event.get().getId()));
    }

    @Test
    public void searchByMultipleEventType() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("type", Arrays.asList(EventType.START_API.toString(), EventType.STOP_API.toString()));

        Page<Event> eventPage = eventRepository.search(values, 1451606400000l, 1470157767000l, 0, 10);
        assertTrue(3l == eventPage.getTotalElements());
        Optional<Event> event = eventPage.getContent().stream().sorted((e1, e2) -> e1.getCreatedAt().compareTo(e2.getCreatedAt())).findFirst();
        assertTrue("event4".equals(event.get().getId()));
    }

    @Test
    public void searchByAPIId() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("properties.api_id", "api-1");

        Page<Event> eventPage = eventRepository.search(values, 1451606400000l, 1470157767000l, 0, 10);
        assertTrue(2l == eventPage.getTotalElements());
        Optional<Event> event = eventPage.getContent().stream().sorted((e1, e2) -> e1.getCreatedAt().compareTo(e2.getCreatedAt())).findFirst();
        assertTrue("event1".equals(event.get().getId()));
    }

    @Test
    public void searchByMixProperties() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("type", Arrays.asList(EventType.START_API.toString(), EventType.STOP_API.toString()));
        values.put("properties.api_id", "api-3");

        Page<Event> eventPage = eventRepository.search(values, 1451606400000l, 1470157767000l, 0, 10);
        assertTrue(1l == eventPage.getTotalElements());
        Optional<Event> event = eventPage.getContent().stream().sorted((e1, e2) -> e1.getCreatedAt().compareTo(e2.getCreatedAt())).findFirst();
        assertTrue("event4".equals(event.get().getId()));
    }
}
