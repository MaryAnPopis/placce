package com.cenfotec.cf_places.network

import android.content.Context
import android.content.Intent
import com.cenfotec.cf_places.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleSignInHandler(context: Context) {

    private val googleSignInOptions: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val requestCode = 100

    val googleSignInClient: Intent

    init {
        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions).signInIntent
    }
}