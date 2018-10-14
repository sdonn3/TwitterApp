package com.donnelly.steve.twitterapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.donnelly.steve.twitterapp.adapters.TweetAdapter
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.fragment_home.*

class MentionsFragment : Fragment() {

    lateinit var contentView: View
    lateinit var adapter: TweetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TweetAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = inflater.inflate(R.layout.fragment_mentions, container, false)
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvTweets.setHasFixedSize(true)
        rvTweets.adapter = adapter
        rvTweets.layoutManager = LinearLayoutManager(context)
        rvTweets.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))

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
        (activity as TimelineActivity).showLoading()
        var lastItem: Tweet? = null
        lastItemPosition?.let{
            lastItem = adapter.tweets[lastItemPosition]
        }
        TwitterCore.getInstance()
                .apiClient
                .statusesService
                .mentionsTimeline(
                        TWEETS_PER_PAGE,
                        lastItem?.id,
                        null,
                        null,
                        null,
                        null
                ).enqueue(object : Callback<List<Tweet>>() {
            override fun success(result: Result<List<Tweet>>?) {
                (activity as TimelineActivity).hideLoading()
                result?.let {
                    lastItem?.run{
                        adapter.tweets.remove(lastItem as Tweet)
                    }
                    adapter.tweets.addAll(result.data)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun failure(exception: TwitterException?) {
                Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show()
                exception?.printStackTrace()
                (activity as TimelineActivity).hideLoading()
            }
        })
    }

    companion object {
        private const val TWEETS_PER_PAGE = 30

        fun newInstance() : MentionsFragment {
            return MentionsFragment()
        }
    }
}