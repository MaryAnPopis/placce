package com.cenfotec.cf_places

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cenfotec.cf_places.model.User
import com.cenfotec.cf_places.network.GoogleSignInHandler
import com.cenfotec.cf_places.network.db.FirebaseDataManager
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
    * LOCAL VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleHandler: GoogleSignInHandler
    private lateinit var facebookManager: CallbackManager

    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
    * LIFECYCLE SET UP
    * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFirebaseAuth()
        setGoogleSignInOptions()
        setFacebookSignInOptions()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
    * UTILS
    * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private fun setFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance()

        resetLoginManagers()
    }

    private fun resetLoginManagers() {
        FacebookSdk.sdkInitialize(this.applicationContext)
        LoginManager.getInstance().logOut()
        firebaseAuth.signOut()
    }

    private fun setGoogleSignInOptions() {
        googleHandler = GoogleSignInHandler(this)

        login_google_sign_in_button.setOnClickListener {
            startActivityForResult(
                googleHandler.googleSignInClient,
                googleHandler.requestCode
            )
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            setUserData(
                account?.email,
                account?.displayName,
                account?.photoUrl.toString(),
                GoogleAuthProvider.getCredential(account?.idToken, null)
            )
        } catch (e: ApiException) {
            Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setFacebookSignInOptions() {
        facebookManager = CallbackManager.Factory.create()

        // TODO: fix facebook hash bug... [Login works when Facebook app isn't installed]
        login_facebook_sign_in_button.setReadPermissions("email", "public_profile")
        login_facebook_sign_in_button.registerCallback(facebookManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookSignInResult(loginResult)
            }

            override fun onCancel() {
                Log.d("DEBUG/", "FACEBOOK LOGIN CANCELED")
            }

            override fun onError(error: FacebookException) {
                Log.d("DEBUG/", "FACEBOOK LOGIN ERROR")
            }
        })
    }

    private fun handleFacebookSignInResult(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
            setUserData(
                `object`.getString("email"),
                `object`.getString("name"),
                "https://graph.facebook.com/" + loginResult.accessToken.userId.toString() + "/picture?type=large",
                FacebookAuthProvider.getCredential(loginResult.accessToken.token)
            )
        }
        val parameters = Bundle()
        parameters.putString("fields", "id, name, email, gender, birthday")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun handleFirebaseLogin(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    goToDashboard()
                } else {
                    Toast.makeText(this, "Invalid account used", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun setUserData(email: String?, name: String?, imageUrl: String?, credential: AuthCredential) {
        val firebaseManager = FirebaseDataManager()

        User.email = email
        User.displayName = name
        User.photoUrl = Uri.parse(imageUrl)

        val user = hashMapOf(
            "name" to name,
            "imageUrl" to imageUrl
        )

        firebaseManager.setUserToDB(email, user)
            ?.addOnSuccessListener {
                handleFirebaseLogin(credential)
            }
            ?.addOnFailureListener {
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
            }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
    * INTERFACE METHODS
    * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == googleHandler.requestCode) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }
}
