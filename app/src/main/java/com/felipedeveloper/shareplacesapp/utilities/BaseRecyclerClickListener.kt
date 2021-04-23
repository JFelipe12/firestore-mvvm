package com.felipedeveloper.shareplacesapp.utilities

interface BaseRecyclerClickListener<T> {

    fun onClick(type: T, position: Int)

}