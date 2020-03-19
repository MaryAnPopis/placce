package com.cenfotec.cf_places.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.cenfotec.cf_places.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.widget_user_search_card.view.*

class UserSearchCard constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    name: String,
    status: String,
    imageUrl: String
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_user_search_card, this, true)

        user_search_profile_name.text = name
        user_search_invitation_status.text = status

        Picasso.get().load(imageUrl).into(user_search_profile_pic)
    }



}