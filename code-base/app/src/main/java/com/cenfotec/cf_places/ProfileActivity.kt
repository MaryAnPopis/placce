package com.cenfotec.cf_places

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cenfotec.cf_places.model.User
import com.cenfotec.cf_places.network.db.FirebaseDataManager
import com.cenfotec.cf_places.widget.UserSearchCard
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        bindView()
    }

    private fun bindView() {
        User.photoUrl?.let {
            Picasso.get().load(User.photoUrl).into(profile_user_photo)
        }

        profile_user_email.text = User.displayName

        val fbm = FirebaseDataManager()
//        fbm.getUsers("C")

        val users = ArrayList<Pair<String, String>>()

        fbm.getUsersFromDB()
            .addOnSuccessListener { documents ->
                                for (document in documents) {
                    val n = document.data["name"].toString()
                    if (document.id != User.email) {
                        users.add(Pair(n, document.data["imageUrl"].toString()))
                    }
                }
                Log.d("DEBUG/", users.toString())
            }
            .addOnFailureListener { e ->
                Log.w("DEBUG/", "Error adding document", e)
            }

        example_input.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                val value = s.toString()
                var filteredUsers = ""

                search_layout.removeAllViews()

                for (user in users) {
                    if (user.first.contains(value)) {
                        val card = UserSearchCard(this@ProfileActivity, null, 0
                            , user.first, "Not friended", user.second
                        )

                        search_layout.addView(
                            card
                        )


                        filteredUsers += (user.first + "\n")
                    }
                }

            }
        })

    }


}


