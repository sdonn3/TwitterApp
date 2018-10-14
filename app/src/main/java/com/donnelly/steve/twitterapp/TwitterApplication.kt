package com.donnelly.steve.twitterapp

import android.app.Application
import android.util.Log
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig



class TwitterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(TwitterAuthConfig("LIjtIdIAQ7RGkGVQyBiCFX0AF", "DboF5aX8q6QQe8JVEO8uXdRWV3rYc9ElZy776kkRQKn7UIVShW"))
                .debug(true)
                .build()
        Twitter.initialize(config)
    }
}