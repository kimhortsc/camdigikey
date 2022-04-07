package com.mk.camdigikey

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.mk.camdigikey.utils.NumericKeyBoardTransformationMethod
import com.mukesh.OtpView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // create instance of firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // we will use this to match the sent otp from firebase
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var etPhoneNumber: EditText
    private lateinit var btnSendPhoneOtp: Button
    private lateinit var tvBuildType: TextView
    private lateinit var otpViewPhoneOtp: OtpView
    private lateinit var etPinCode: TextInputEditText
    private lateinit var etRetypePinCode: TextInputEditText

    companion object {
        public val TAG = MainActivity::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_CamDigiKey)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init UI components and related event
        init()

        // init FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // handle sending phone number otp
        btnSendPhoneOtp.setOnClickListener{
            sendPhoneNumberOtp(it)
        }


        // verify phone number otp
        otpViewPhoneOtp.setOtpCompletionListener { otp ->
            val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId, otp)

            if(signInWithPhoneAuthCredential(credential)){
                Toast.makeText(this,"Valid OTP", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("MSg", "onVerificationFailed  $e")
            }

            // On code is sent by the firebase this method is called
            // in here we start a new activity where user can enter the OTP
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent: $verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }
        }
    }

    private fun init() {
        etPhoneNumber = findViewById(R.id.et_phone_number)
        etPhoneNumber.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        etPhoneNumber.transformationMethod = NumericKeyBoardTransformationMethod()

        etPinCode = findViewById(R.id.et_pin_code)
        etPinCode.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD

        etRetypePinCode = findViewById(R.id.et_retype_pin_code)
        etRetypePinCode.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD


        btnSendPhoneOtp = findViewById(R.id.btn_send_phone_otp)
        otpViewPhoneOtp = findViewById(R.id.otp_view_phone_otp)
        tvBuildType = findViewById(R.id.tv_build_type)

        tvBuildType.text = BuildConfig.BUILD_TYPE

        etPhoneNumber.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            // remove 0 from first digit
            override fun afterTextChanged(input: Editable?) {
                val phoneNumber = input ?: ""
                if(phoneNumber.startsWith("0")){
                    etPhoneNumber.setText(phoneNumber.substring(1))
                }
            }
        })
    }

    private fun sendPhoneNumberOtp(view: View) {
        var phoneNumber = etPhoneNumber.text.toString().trim()

        if (phoneNumber.isBlank()) {
            etPhoneNumber.error = "Enter Phone Number"
            return
        } else if (phoneNumber.length < 8){
            etPhoneNumber.error = "Invalid Phone Number"
            return
        }

        phoneNumber = "+855$phoneNumber"

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // verifies if the code matches sent by firebase
    // if success start the new activity in our case it is main Activity
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Boolean {
        return try{
            firebaseAuth.signInWithCredential(credential)
            true
        } catch (ex: Exception){
            false
        }
    }
}