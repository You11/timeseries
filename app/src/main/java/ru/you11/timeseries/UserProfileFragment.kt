package ru.you11.timeseries

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_profile_fragment.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap



/**
 * Created by you11 on 21.01.2018.
 */
class UserProfileFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.user_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //circular avatars
        class CircularPicassoImage: Callback {
            override fun onSuccess() {
                val imageBitmap = (user_profile_photo.drawable as BitmapDrawable).bitmap
                val imageDrawable = RoundedBitmapDrawableFactory.create(resources, imageBitmap)
                imageDrawable.isCircular = true
                imageDrawable.cornerRadius = Math.max(imageBitmap.width, imageBitmap.height) / 2.0f
                user_profile_photo.setImageDrawable(imageDrawable)
            }

            override fun onError() {
                user_profile_photo.setImageResource(R.drawable.user)
            }
        }

        val user = FirebaseAuth.getInstance().currentUser ?: return
        user_profile_name.text = user.displayName
        user_profile_email.text = user.email
        if (user.photoUrl == null) {
            Picasso.with(activity)
                    .load("https://api.adorable.io/avatars/100/abott@adorable.png")
                    .into(user_profile_photo, CircularPicassoImage())
        } else {
            Picasso.with(activity)
                    .load(user.photoUrl)
                    .into(user_profile_photo, CircularPicassoImage())
        }
    }
}