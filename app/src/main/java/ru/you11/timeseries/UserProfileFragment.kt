package ru.you11.timeseries

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_profile_fragment.*
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.*


/**
 * Here shows user information. It is possible to change it through edit button in menu.
 */
class UserProfileFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_user_profile_edit -> {
                val user = FirebaseAuth.getInstance().currentUser ?: return super.onOptionsItemSelected(item)

                //show save button and edit texts, hide text views
                user_profile_name.visibility = TextView.GONE
                user_profile_email.visibility = TextView.GONE
                user_profile_edit_name.setText(user.displayName)
                user_profile_edit_email.setText(user.email)
                user_profile_edit_name.visibility = EditText.VISIBLE
                user_profile_edit_email.visibility = EditText.VISIBLE
                user_profile_save_button.visibility = Button.VISIBLE

                user_profile_save_button.setOnClickListener {

                    val nameInput = user_profile_edit_name.text.toString()
                    if (nameInput != user.displayName) {
                        val profileChangeRequest = UserProfileChangeRequest.Builder()
                                .setDisplayName(nameInput).build()

                        user.updateProfile(profileChangeRequest)
                                .addOnSuccessListener {
                                    Toast.makeText(activity, getString(R.string.username_changed_message), Toast.LENGTH_SHORT).show()
                                    user_profile_name.text = nameInput
                                }
                                .addOnFailureListener {
                                    Toast.makeText(activity, getString(R.string.error_with_localized_message) + it.localizedMessage, Toast.LENGTH_SHORT).show()
                                }
                    }

                    val emailInput = user_profile_edit_email.text.toString()
                    if (emailInput != user.email) {
                        val confirmationDialog = AlertDialog.Builder(activity)
                        confirmationDialog.setTitle(getString(R.string.email_change_dialog_title))
                                .setMessage(getString(R.string.email_change_dialog_message))
                                .setPositiveButton(getString(R.string.email_change_dialog_positive_btn), { dialog, which ->

                                    user.updateEmail(emailInput)
                                            .addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    Toast.makeText(activity, getString(R.string.email_changed_message), Toast.LENGTH_SHORT).show()
                                                    user_profile_email.text = emailInput
                                                }
                                            }
                                            .addOnFailureListener {
                                                if (it is FirebaseAuthRecentLoginRequiredException) {
                                                    val passwordInputView = EditText(activity)
                                                    passwordInputView.setPadding(20, 20, 20, 20)
                                                    passwordInputView.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                                                    val askPasswordDialog = AlertDialog.Builder(activity)
                                                    askPasswordDialog.setTitle(getString(R.string.ask_password_dialog_title))
                                                            .setMessage(getString(R.string.ask_password_dialog_message) + user.email)
                                                            .setView(passwordInputView)
                                                            .setPositiveButton(getString(R.string.ask_password_dialog_positive_btn), { dialog, which ->
                                                                val credential = EmailAuthProvider.getCredential(user.email!!, passwordInputView.text.toString())
                                                                user.reauthenticate(credential).addOnCompleteListener {
                                                                    user.updateEmail(emailInput)
                                                                            .addOnCompleteListener {
                                                                                Toast.makeText(activity, getString(R.string.email_changed_message), Toast.LENGTH_SHORT).show()
                                                                                user_profile_email.text = emailInput
                                                                            }
                                                                            .addOnFailureListener {
                                                                                Toast.makeText(activity, getString(R.string.error_with_localized_message), Toast.LENGTH_SHORT).show()
                                                                            }
                                                                }
                                                                .addOnFailureListener {
                                                                    Toast.makeText(activity, getString(R.string.error_with_localized_message) + it.localizedMessage, Toast.LENGTH_SHORT).show()
                                                                }
                                                            })
                                                            .setNegativeButton(getString(R.string.ask_password_dialog_negative_btn), { dialog, which ->
                                                                dialog.dismiss()
                                                            })
                                                    askPasswordDialog.show()
                                                } else
                                                    Toast.makeText(activity, getString(R.string.error_with_localized_message) + it.localizedMessage, Toast.LENGTH_SHORT).show()
                                            }
                                })
                                .setNegativeButton(getString(R.string.email_change_dialog_negative_btn), { dialog, which ->
                                    dialog.dismiss()
                                })
                        confirmationDialog.show()
                    }

                    //show text views, hide edit texts and save button
                    user_profile_edit_name.visibility = EditText.GONE
                    user_profile_edit_email.visibility = EditText.GONE
                    user_profile_save_button.visibility = Button.GONE
                    user_profile_name.visibility = TextView.VISIBLE
                    user_profile_email.visibility = TextView.VISIBLE
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}