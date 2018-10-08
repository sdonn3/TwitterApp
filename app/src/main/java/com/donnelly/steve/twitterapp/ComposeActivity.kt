package com.donnelly.steve.twitterapp

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.donnelly.steve.twitterapp.glide.GlideApp
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_compose.*
import java.util.concurrent.TimeUnit

class ComposeActivity : AppCompatActivity() {

    private val disposables by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.backgroundAlternate)))
        val text: Spannable = SpannableString("Compose")
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        supportActionBar?.title = text

        disposables += ivClose.clicks().throttleFirst(500L, TimeUnit.MILLISECONDS).subscribe {
            finish()
        }

        disposables += etCompose
                .textChanges()
                .subscribe({
                    tvRemaaining.text = (140 - etCompose.text.length).toString()
                    btnTweet.isEnabled = etCompose.text.length <= 140
                }, {
                    Toast.makeText(this@ComposeActivity, it.toString(), Toast.LENGTH_LONG).show()
                    it.printStackTrace()
                })

        TwitterCore.getInstance().apiClient.accountService.verifyCredentials(
                true,
                false,
                true
        ).enqueue(object : Callback<User>() {
            override fun success(result: Result<User>?) {
                val user = result?.data
                user?.let {
                    tvName.text = it.name
                    tvUserName.text = getString(R.string.at_user, it.screenName)

                    GlideApp
                            .with(this@ComposeActivity)
                            .load(it.profileImageUrl)
                            .into(ivUserImage)
                }
            }

            override fun failure(exception: TwitterException?) {
                Toast.makeText(this@ComposeActivity, exception.toString(), Toast.LENGTH_LONG).show()
                exception?.printStackTrace()
            }
        })

        disposables +=
                btnTweet
                        .clicks()
                        .throttleFirst(500L, TimeUnit.MILLISECONDS)
                        .subscribe {
                            TwitterCore.getInstance().apiClient.statusesService.update(
                                    etCompose.text.toString(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null
                            ).enqueue(object : Callback<Tweet>() {
                                override fun success(result: Result<Tweet>?) {
                                    TimelineActivity.tweet = result?.data
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }

                                override fun failure(exception: TwitterException?) {
                                    Toast.makeText(this@ComposeActivity, exception.toString(), Toast.LENGTH_LONG).show()
                                    exception?.printStackTrace()
                                }
                            })
                        }

        val userId = TwitterCore.getInstance().sessionManager.activeSession.userId
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}