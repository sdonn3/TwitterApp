package com.donnelly.steve.twitterapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.donnelly.steve.twitterapp.adapters.TimelineFragmentPagerAdapter
import com.donnelly.steve.twitterapp.adapters.TweetAdapter
import com.donnelly.steve.twitterapp.extensions.hide
import com.donnelly.steve.twitterapp.extensions.show
import com.jakewharton.rxbinding2.view.clicks
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_timeline.*
import java.util.concurrent.TimeUnit

class TimelineActivity : AppCompatActivity(), TweetAdapter.UserClickedListener {
    private val disposables by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        val session = TwitterCore.getInstance().sessionManager.activeSession
        setSupportActionBar(toolbar)
        supportActionBar?.title = "@" + session.userName

        viewpager.adapter = TimelineFragmentPagerAdapter(supportFragmentManager, this)
        tabLayout.setupWithViewPager(viewpager)
        tabLayout.getTabAt(0)?.select()

        disposables += fab
                .clicks()
                .throttleFirst(500L, TimeUnit.MILLISECONDS)
                .subscribe{
                    val intent = Intent(this, ComposeActivity::class.java)
                    startActivity(intent)
                }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_timeline, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.miUserProfile) {
            pbLoading.show()
            TwitterCore.getInstance().apiClient.accountService.verifyCredentials(
                    true,
                    false,
                    true
            ).enqueue(object : Callback<User>() {
                override fun success(result: Result<User>?) {
                    val user = result?.data
                    user?.let {
                        navigateToUser(it)
                    }
                    pbLoading.hide()
                }

                override fun failure(exception: TwitterException?) {
                    Toast.makeText(this@TimelineActivity, exception.toString(), Toast.LENGTH_LONG).show()
                    exception?.printStackTrace()
                    pbLoading.hide()
                }
            })
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun userClicked(user: User) {
        navigateToUser(user)
    }

    private fun navigateToUser(user: User) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(ProfileActivity.USER_ID, user.id)
        intent.putExtra(ProfileActivity.USER_BANNER_URL, user.profileBannerUrl)
        intent.putExtra(ProfileActivity.USER_PROFILE, user.profileImageUrl)
        intent.putExtra(ProfileActivity.USER_FOLLOWERS, user.followersCount)
        intent.putExtra(ProfileActivity.USER_FOLLOWNG, user.friendsCount)
        intent.putExtra(ProfileActivity.USER_NAME, user.name)
        intent.putExtra(ProfileActivity.USER_USERNAME, user.screenName)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    fun showLoading() {
        pbLoading.show()
    }

    fun hideLoading() {
        pbLoading.hide()
    }
}