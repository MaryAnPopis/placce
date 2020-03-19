package com.cenfotec.cf_places

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
    * LIFECYCLE SET UP
    * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bindView()
    }

    private fun bindView() {
        dashboard_home_button.setOnClickListener {

        }

        dashboard_search_button.setOnClickListener {

        }

        dashboard_events_button.setOnClickListener {

        }

        dashboard_profile_button.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
    * INTERFACE METHODS
    * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onBackPressed() {
        FacebookSdk.sdkInitialize(this.applicationContext)
        LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut()
        super.onBackPressed()
    }
}
