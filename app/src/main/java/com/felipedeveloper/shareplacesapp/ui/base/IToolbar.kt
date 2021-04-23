package com.felipedeveloper.shareplacesapp.ui.base

import androidx.appcompat.widget.Toolbar

interface IToolbar {

    fun toolbarToLoad(toolbar: Toolbar)

    fun enableHomeDisplay(value: Boolean)

    fun setCustomBackArrow(resource: Int)

    fun toolbarTitle(title: String)
}