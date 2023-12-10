package me.nikitaklimkin.model

abstract class AggregateRoot<T>(id: T) : DomainEntity<T>(id) {
}