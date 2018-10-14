package com.donnelly.steve.twitterapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donnelly.steve.twitterapp.adapters.TweetAdapter
import com.donnelly.steve.twitterapp.extensions.hide
import com.donnelly.steve.twitterapp.extensions.show
import com.donnelly.steve.twitterapp.glide.GlideApp
import com.donnelly.steve.twitterapp.listeners.EndlessScrollListener
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    lateinit var adapter: TweetAdapter

    var userId: Long = 0
    var userScreenName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userProfileBannerUrl = intent?.extras?.getString(USER_BANNER_URL, "")
        val userProfileImage = intent?.extras?.getString(USER_PROFILE, "")
        val userFollowers = intent?.extras?.getInt(USER_FOLLOWERS)
        val userFollowing = intent?.extras?.getInt(USER_FOLLOWNG)
        val userName = intent?.extras?.getString(USER_NAME)
        userScreenName = intent?.extras?.getString(USER_USERNAME)

        GlideApp
                .with(this)
                .load(userProfileBannerUrl)
                .into(ivProfileBanner)

        GlideApp
                .with(this)
                .load(userProfileImage)
                .into(ivProfileImage)

        tvUser.text = userName
        tvUserName.text = "@$userScreenName"
        tvFollowers.text = (userFollowers.toString() + " followers")
        tvFollowing.text = "Following ${userFollowing.toString()}"

        adapter = TweetAdapter()
        rvTweets.setHasFixedSize(true)
        rvTweets.adapter = adapter
        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        rvTweets.addOnScrollListener(
                object : EndlessScrollListener(rvTweets.layoutManager as LinearLayoutManager) {
                    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                        loadItems(adapter.tweets.size - 1)
                    }
                }
        )

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            adapter.clear()
            loadItems(null)
        }
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)

        loadItems(null)
    }

    private fun loadItems(lastItemPosition: Int?) {
        pbLoading.show()
        var lastItem: Tweet? = null
        lastItemPosition?.let{
            lastItem = adapter.tweets[lastItemPosition]
        }

        TwitterCore.getInstance().apiClient.statusesService.userTimeline(
                userId,
                userScreenName,
                TWEETS_PER_PAGE,
                null,
                null,
                false,
                false,
                null,
                null).enqueue(object : Callback<List<Tweet>>() {
            override fun success(result: Result<List<Tweet>>?) {
                pbLoading.hide()
                result?.let {
                    lastItem?.run{
                        adapter.tweets.remove(lastItem as Tweet)
                    }
                    adapter.tweets.addAll(result.data)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun failure(exception: TwitterException?) {
                pbLoading.hide()
                Toast.makeText(this@ProfileActivity, exception.toString(), Toast.LENGTH_LONG).show()
                exception?.printStackTrace()
            }
        })
    }

    companion object {
        const val USER_ID = "UserId"
        const val USER_BANNER_URL = "UserBanner"
        const val USER_PROFILE = "UserProfile"
        const val USER_FOLLOWERS = "UserFollowers"
        const val USER_FOLLOWNG = "UserFollowing"
        const val USER_USERNAME = "UserUsername"
        const val USER_NAME = "UserName"
        private const val TWEETS_PER_PAGE = 30
    }
}