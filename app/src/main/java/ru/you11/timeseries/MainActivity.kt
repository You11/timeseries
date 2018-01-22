package ru.you11.timeseries

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity() {

    private val signIn = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (FirebaseAuth.getInstance().currentUser != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, MainFragment(), "mainFragment")
                    .commit()
        } else {
            logIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == signIn) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show()
                restartActivity()
            } else {
                Toast.makeText(this, "Failed to logged in!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        //if user isn't logged in
        if (FirebaseAuth.getInstance().currentUser == null) {
            menuInflater.inflate(R.menu.menu_main, menu)
        } else {
            menuInflater.inflate(R.menu.menu_main_logged_in, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_main_login -> {
                logIn()
                return true
            }

            R.id.menu_main_logout -> {
                AuthUI.getInstance().signOut(this).addOnCompleteListener {
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    restartActivity()
                }
                return true
            }

            R.id.menu_main_user_profile -> {
                startActivity(Intent(this, UserProfileActivity()::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun logIn() {
        val providers = Arrays.asList(AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), signIn)
    }

    private fun restartActivity() {
        for (i in 0 until fragmentManager.backStackEntryCount) {
            fragmentManager.popBackStack()
        }
        //because fragment stays on screen otherwise
        val fragment = fragmentManager.findFragmentByTag("mainFragment")
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit()
        }
        recreate()
    }
}
