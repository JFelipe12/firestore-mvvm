package com.felipedeveloper.shareplacesapp.utilities

interface Mapper<I, O> {
    fun map(source: I): O
}