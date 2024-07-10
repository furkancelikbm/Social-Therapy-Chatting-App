package com.example.myprojectnew

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myprojectnew.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private lateinit var firebaseAuth:FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth=FirebaseAuth.getInstance()
        binding.textView.setOnClickListener{
            val intent= Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }


        // ...

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    if (email.endsWith("@ogr.bozok.edu.tr")) {
                        // Sadece @ogr.bozok.edu.tr uzantısına sahip e-posta adresleri kabul edilir
                        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener { signUpTask ->
                                if (signUpTask.isSuccessful) {
                                    // Kayıt başarılı
                                    firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Lütfen emailinizi doğrulayın",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                        val user = firebaseAuth.currentUser
                                        if (user != null && user.isEmailVerified) {
                                            // E-posta doğrulaması yapılmışsa MainActivity'e yönlendir
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }?.addOnFailureListener {
                                        Toast.makeText(
                                            this,
                                            it.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    // Kayıt başarısız
                                    Toast.makeText(
                                        this,
                                        "Kayıt başarısız. Lütfen tekrar deneyin.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Sadece @ogr.bozok.edu.tr uzantısına sahip e-posta adresleri kabul edilir.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }



// ...

    }
    private fun buildActionCodeSettings() {
        // [START auth_build_action_code_settings]
        val actionCodeSettings = actionCodeSettings {
            // URL you want to redirect back to. The domain (www.example.com) for this
            // URL must be whitelisted in the Firebase Console.
            url = "https://www.example.com/finishSignUp?cartId=1234"
            // This must be true
            handleCodeInApp = true
            setIOSBundleId("com.example.ios")
            setAndroidPackageName(
                "com.example.android",
                true, // installIfNotAvailable
                "12", // minimumVersion
            )
        }
        // [END auth_build_action_code_settings]
    }
}