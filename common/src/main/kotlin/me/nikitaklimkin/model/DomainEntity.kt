package me.nikitaklimkin.model

abstract class DomainEntity<T> constructor(id: T) {

    private var events = mutableListOf<DomainEvent>();

    protected fun addEvent(event: DomainEvent) {
        events.add(event)
    }

    protected fun popEvents(): List<DomainEvent> {
        val readOnlyEvents = events.toList()
        events = mutableListOf()
        return readOnlyEvents
    }
}