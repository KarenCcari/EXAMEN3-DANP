package com.danp.lecturas_project.pager

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}