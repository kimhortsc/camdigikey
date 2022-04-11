package com.mk.camdigikey

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.mk.camdigikey.utils.LocaleUtils
import com.mukesh.OtpView
import java.util.concurrent.TimeUnit


class RegisterActivity : AppCompatActivity() {

    private var validPhoneNumber = false

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
    private lateinit var ivPhoneOtpValidation: ImageView
    private lateinit var etPinCode: TextInputEditText
    private lateinit var etRetypePinCode: TextInputEditText
    private lateinit var toolbar: Toolbar

    companion object {
        public val TAG = RegisterActivity::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_CamDigiKey)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
            signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(storedVerificationId, otp))
        }

        toolbar.setNavigationOnClickListener{ finish() }

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
        etPhoneNumber.inputType = InputType.TYPE_CLASS_NUMBER

        etPinCode = findViewById(R.id.et_pin_code)
        etPinCode.inputType = InputType.TYPE_CLASS_NUMBER

        etRetypePinCode = findViewById(R.id.et_retype_pin_code)
        etRetypePinCode.inputType = InputType.TYPE_CLASS_NUMBER

        btnSendPhoneOtp = findViewById(R.id.btn_send_phone_otp)
        otpViewPhoneOtp = findViewById(R.id.otp_view_phone_otp)
        tvBuildType = findViewById(R.id.tv_build_type)

        ivPhoneOtpValidation = findViewById(R.id.iv_phone_otp_validation)

        toolbar = findViewById(R.id.toolbar)

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

                validPhoneNumber = false
                ivPhoneOtpValidation.setImageResource(0)
            }
        })
    }

    private fun sendPhoneNumberOtp(view: View) {
        var phoneNumber = etPhoneNumber.text.toString().trim()

        if (phoneNumber.isBlank()) {
            etPhoneNumber.error = "Enter Phone Number"
            validPhoneNumber = false
            return
        } else if (phoneNumber.length < 8){
            etPhoneNumber.error = "Invalid Phone Number"
            validPhoneNumber = false
            return
        }

        validPhoneNumber = true

        if(validPhoneNumber){
            otpViewPhoneOtp.isEnabled = true
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
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    ivPhoneOtpValidation.setImageResource(R.drawable.ic_valid_otp)
                    otpViewPhoneOtp.isEnabled = false
                    etPhoneNumber.isFocusableInTouchMode = false

                } else {
                    ivPhoneOtpValidation.setImageResource(R.drawable.ic_invalid_otp)
                }
            }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.updateLocal(newBase))
    }
}

