package ru.you11.timeseries

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by you11 on 21.01.2018.
 */
class UserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile_activity)

        fragmentManager.beginTransaction()
                .replace(R.id.user_profile_fragment_container, UserProfileFragment())
                .addToBackStack(null)
                .commit()
    }
}