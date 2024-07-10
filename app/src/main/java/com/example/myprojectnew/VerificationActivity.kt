package com.example.myprojectnew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myprojectnew.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth

class VerificationActivity : AppCompatActivity() {

    var binding:ActivityVerificationBinding?=null
    var auth:FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVerificationBinding.inflate(layoutInflater)
        if (binding != null) {
            setContentView(binding!!.root)
        } else {
            // Hata durumunu ele alın
            println("hata y")
        }
        setContentView(binding!!.root)

        auth=FirebaseAuth.getInstance()
        if(auth!!.currentUser!=null){
            val intent=Intent(this@VerificationActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        supportActionBar?.hide()
        binding!!.editNumber.requestFocus()
        binding!!.ConinueBtn.setOnClickListener{
            val intent=Intent(this@VerificationActivity,OTPActivity::class.java)
            intent.putExtra("phoneNumber",binding!!.editNumber.text.toString())
            startActivity(intent)

        }
    }
}