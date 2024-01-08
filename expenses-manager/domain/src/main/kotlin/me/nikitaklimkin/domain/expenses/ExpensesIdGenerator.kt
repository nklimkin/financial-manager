package me.nikitaklimkin.domain.expenses

interface ExpensesIdGenerator {

    fun generate(): ExpensesId
}