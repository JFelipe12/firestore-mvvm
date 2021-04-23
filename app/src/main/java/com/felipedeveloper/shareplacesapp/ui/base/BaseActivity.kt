package com.felipedeveloper.shareplacesapp.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem

open class BaseActivity : AppCompatActivity(), IToolbar {

    override fun toolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    private var _toolbar: Toolbar? = null

    override fun enableHomeDisplay(value: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(value)
    }

    override fun toolbarToLoad(toolbar: Toolbar) {
        _toolbar = toolbar
        _toolbar?.let { setSupportActionBar(_toolbar) }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun setCustomBackArrow(resource: Int) {
        supportActionBar?.setHomeAsUpIndicator(resource)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}