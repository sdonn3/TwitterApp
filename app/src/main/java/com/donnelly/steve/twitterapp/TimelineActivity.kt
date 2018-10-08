package com.donnelly.steve.twitterapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donnelly.steve.twitterapp.adapters.TweetAdapter
import com.donnelly.steve.twitterapp.listeners.EndlessScrollListener
import com.jakewharton.rxbinding2.view.clicks
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_timeline.*
import java.util.concurrent.TimeUnit

class TimelineActivity : AppCompatActivity() {

    private val disposables by lazy { CompositeDisposable() }
    lateinit var adapter: TweetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        val session = TwitterCore.getInstance().sessionManager.activeSession
        supportActionBar?.title = "@" + session.userName
        adapter = TweetAdapter()
        rvTweets.setHasFixedSize(true)
        rvTweets.adapter = adapter
        rvTweets.layoutManager = LinearLayoutManager(this@TimelineActivity)
        rvTweets.addItemDecoration(DividerItemDecoration(this@TimelineActivity, DividerItemDecoration.HORIZONTAL))
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

        disposables += fab
                .clicks()
                .throttleFirst(500L, TimeUnit.MILLISECONDS)
                .subscribe{
                    val intent = Intent(this, ComposeActivity::class.java)
                    startActivityForResult(intent, COMPOSE_REQUEST)
                }

        loadItems(null)
    }

    private fun loadItems(lastItemPosition: Int?) {
        var lastItem: Tweet? = null
        lastItemPosition?.let{
            lastItem = adapter.tweets[lastItemPosition]
        }
        TwitterCore.getInstance().apiClient.statusesService.homeTimeline(
                TWEETS_PER_PAGE,
                null,
                lastItem?.id,
                null,
                null,
                false,
                false).enqueue(object : Callback<List<Tweet>>() {
            override fun success(result: Result<List<Tweet>>?) {
                result?.let {
                    lastItem?.run{
                        adapter.tweets.remove(lastItem as Tweet)
                    }
                    adapter.tweets.addAll(result.data)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun failure(exception: TwitterException?) {
                Toast.makeText(this@TimelineActivity, exception.toString(), Toast.LENGTH_LONG).show()
                exception?.printStackTrace()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == COMPOSE_REQUEST && resultCode == Activity.RESULT_OK) {
            tweet?.let{
                adapter.tweets.add(0, tweet as Tweet)
                adapter.notifyDataSetChanged()
                tweet = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    companion object {
        var tweet : Tweet? = null
        private const val COMPOSE_REQUEST = 111
        private const val TWEETS_PER_PAGE = 30
    }
}