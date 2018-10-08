package com.donnelly.steve.twitterapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        btnLogin.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                Toast.makeText(this@MainActivity, getString(R.string.logging_you_in), Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, TimelineActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun failure(exception: TwitterException?) {
                Toast.makeText(this@MainActivity, exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        btnLogin.onActivityResult(requestCode, resultCode, data)
    }
}
