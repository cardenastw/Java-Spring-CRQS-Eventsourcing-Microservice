package com.eventsourcing.es;

import java.util.List;

public interface EventBus {
    void publish(String partitionId, List<Event> events);
}