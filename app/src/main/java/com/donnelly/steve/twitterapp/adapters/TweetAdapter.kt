package com.donnelly.steve.twitterapp.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.donnelly.steve.twitterapp.R
import com.donnelly.steve.twitterapp.glide.GlideApp
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.item_tweet.view.*
import java.text.SimpleDateFormat
import java.util.*

class TweetAdapter : RecyclerView.Adapter<TweetAdapter.TweetViewHolder>() {

    var tweets : ArrayList<Tweet> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder =
            TweetViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_tweet, parent, false)
            )

    override fun getItemCount() =
            tweets.size

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) =
        holder.bind(tweets[position], position)

    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    inner class TweetViewHolder(tweetView: View) : RecyclerView.ViewHolder(tweetView) {
        private val todayDate : Date = Calendar.getInstance().time
        private val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy", Locale.getDefault())

        fun bind(tweet: Tweet, position: Int) {
            itemView.apply {
                if (position % 2 == 1) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundAlternate))
                }
                else {
                    setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                }
                GlideApp
                        .with(context)
                        .load(tweet.user?.profileImageUrl)
                        .into(ivTweet)

                tvName.text = tweet.user.name
                tvUser.text = context.getString(R.string.at_user, tweet.user.screenName)
                tvBody.text = tweet.text
                tvTime.text = dateStringToTime(tweet.createdAt)
            }
        }

        private fun dateStringToTime(date: String) : String {
            val incomingDate = dateFormat.parse(date)
            val difference = todayDate.time - incomingDate.time
            val years = difference / DateUtils.YEAR_IN_MILLIS
            if (years > 1) return "$years Years ago"
            if (years == 1L) return "1 Year ago"
            val months = difference / (DateUtils.DAY_IN_MILLIS * 30L)
            if (months > 1) return "$months Months ago"
            if (months == 1L) return "1 Month ago"
            val weeks = difference / (DateUtils.DAY_IN_MILLIS * 7L)
            if (weeks > 1) return "$weeks Weeks ago"
            if (weeks == 1L) return "1 Week ago"
            val days = difference / DateUtils.DAY_IN_MILLIS
            if (days > 1) return "$days Days ago"
            if (days == 1L) return "1 Day ago"
            val hours = difference / DateUtils.HOUR_IN_MILLIS
            if (hours > 1) return "$hours Hours ago"
            if (hours == 1L) return "1 Hour ago"
            val minutes = difference / DateUtils.MINUTE_IN_MILLIS
            if (minutes > 1) return "$minutes Minutes ago"
            if (minutes == 1L) return "1 Minute ago"
            val seconds = difference / DateUtils.SECOND_IN_MILLIS
            if (seconds > 1) return "$seconds Seconds ago"
            if (seconds == 1L) return "1 secondw ago"
            return ""
        }
    }

}