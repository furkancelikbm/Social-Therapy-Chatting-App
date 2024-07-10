package com.example.myprojectnew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myprojectnew.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        // Kullanıcı girişi başarılı

                        val user = firebaseAuth.currentUser
                        if (user != null && user.isEmailVerified) {
                            // E-posta doğrulaması yapılmışsa MainActivity'e yönlendir
                            val intent = Intent(this, SetupProfileActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(
                                this,
                                "Giriş Başarılı",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // E-posta doğrulaması yapılmamışsa uyarı ver
                            Toast.makeText(
                                this,
                                "E-posta doğrulaması yapılmadı. Lütfen e-postanızı kontrol edin.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        // Kullanıcı girişi başarısız
                        Toast.makeText(this,"Email ya da şifre yanlış", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Boş alanlar izin verilmez!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val user = firebaseAuth.currentUser
        if (user != null && user.isEmailVerified) {
            // E-posta doğrulaması yapılmışsa MainActivity'e yönlendir
            val intent = Intent(this, SetupProfileActivity::class.java)
            startActivity(intent)
        }
    }
}