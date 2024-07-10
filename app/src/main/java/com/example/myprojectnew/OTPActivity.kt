package com.example.myprojectnew

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.example.myprojectnew.databinding.ActivityOtpactivityBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    var binding: ActivityOtpactivityBinding?=null
    var verificationID:String?=null
    var auth:FirebaseAuth?=null
    var dialog:ProgressDialog?=null

    private fun formatPhoneNumber(phoneNumber: String?): String {
        // Özel formatlama kurallarını uygulayabilirsiniz
        // Örnek olarak, +905522983172 şeklinde bir formatı burada oluşturabilirsiniz
        return "+90" + phoneNumber?.substring(1) // Örneğin, sadece ilk karakteri çıkartarak +90 ekledik
    }



    override fun onCreate(savedInstanceState: Bundle?) {


        println("oncreat icindeyiz")
        super.onCreate(savedInstanceState)
        binding=ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialog= ProgressDialog(this@OTPActivity)
        dialog!!.setMessage("Sending OTP...")
        dialog!!.setCancelable(false)
        dialog!!.show()
        auth=FirebaseAuth.getInstance()
        supportActionBar?.hide()
        val phoneNumber = intent.getStringExtra("phoneNumber")
        // Telefon numarasını E.164 formatına çevirme
        // Kullanıcının girdiği telefon numarasını alın
        val userInputPhoneNumber = phoneNumber // Bu kısmı kullanıcının girdiği değerle değiştirin

        // Kullanıcının girişini kontrol et
        val formattedPhoneNumber = if (userInputPhoneNumber!!.startsWith("+")) {
            userInputPhoneNumber // Başında "+" işareti varsa, olduğu gibi bırak
        } else {
            "+$userInputPhoneNumber" // Başında "+" işareti yoksa, ekleyerek E.164 formatına çevir
        }

        // Şimdi formattedPhoneNumber değişkeni E.164 formatında bir telefon numarasını içerir
        println("Formatted Phone Number: $formattedPhoneNumber")


        binding!!.phoneLble.text = "Verify $formattedPhoneNumber"


        val options=PhoneAuthOptions.newBuilder(auth!!)
            .setPhoneNumber(formattedPhoneNumber!!)
            .setTimeout(120L,TimeUnit.SECONDS)
            .setActivity(this@OTPActivity)
            .setCallbacks(object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    TODO("Not yet implemented")
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    dialog?.dismiss()
                    Log.e("VerificationError", "Doğrulama Başarısız: ${p0.message}", p0)
                    Toast.makeText(this@OTPActivity, "Doğrulama Başarısız: ${p0.message}", Toast.LENGTH_SHORT).show()
                }



                override fun onCodeSent(
                    verifyId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verifyId, forceResendingToken)
                    Log.d("VerificationLog", "Verification ID received: $verifyId")
                    dialog!!.dismiss()
                    verificationID = verifyId
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    binding!!.otpView.requestFocus()
                }


            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        binding!!.otpView.setOtpCompletionListener { otp ->
            if (verificationID != null) {
                val credential = PhoneAuthProvider.getCredential(verificationID!!, otp)
                auth!!.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            println("successful icindeyiz")
                            val intent = Intent(this@OTPActivity, SetupProfileActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        } else {
                            Toast.makeText(this@OTPActivity, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // verificationID null ise işlem yapma, hatayı kontrol et
                Toast.makeText(this@OTPActivity, "Verification ID is null", Toast.LENGTH_SHORT).show()
            }
        }



    }
}