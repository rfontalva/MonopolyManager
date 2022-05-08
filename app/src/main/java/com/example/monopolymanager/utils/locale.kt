package com.example.monopolymanager.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*


fun switchLocal(context: Context, code: String, activity: Activity) {
    if (code.equals("", ignoreCase = true)) return
    val resources: Resources = context.getResources()
    val locale = Locale(code)
    Locale.setDefault(locale)
    val config = Configuration()
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.getDisplayMetrics())
    //restart base activity
    activity.finish()
    activity.startActivity(activity.intent)
}