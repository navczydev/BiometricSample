package com.example.biometricsample


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

/**
 * implemented new biometric api  from androidx
 * @author Nav Singh
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this
        val executor = Executors.newSingleThreadExecutor()
        val biometricPrompt =
            BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        // user clicked negative/cancel button
                    } else {
                        TODO("Called when an unrecoverable error has been encountered and the operation is complete.")
                    }
                }

                //on successful authentication use will lands to NextActivity
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    runOnUiThread {
                        this@MainActivity.startActivity(Intent(activity, NextActivity::class.java))
                        Toast.makeText(activity, "Authentication success", Toast.LENGTH_SHORT)
                            .show()

                    }
                }

                //if auth failed user will be notified by toast
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    runOnUiThread {
                        Toast.makeText(
                            activity,
                            "Authentication failed! Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

        //bio-metric prompt created with some props like title etc..
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authentication prompt!")
            /*Subtitle and description are optional parameters, so, you can skip those parameters.
            .setSubtitle("Set the subtitle to display.")
            .setDescription("Verification required")*/
            .setNegativeButtonText("Cancel")
            .setConfirmationRequired(true)
            .build()

        //initiate authentication button click
        authenticateButton.setOnClickListener {
            val biometricManager = BiometricManager.from(this)
            //check if bio-metric is available then proceed with it otherwise inform the user via toast
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    //make authentication calls.
                    biometricPrompt.authenticate(promptInfo)
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Toast.makeText(
                        activity,
                        "Make sure you have enrolled at least 1 finger print",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    //hw  unavailable
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    //no hw found
                }
            }

        }
    }
}