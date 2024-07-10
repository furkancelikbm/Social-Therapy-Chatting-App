package com.example.myprojectnew

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myprojectnew.databinding.ActivitySetupProfileBinding
import com.example.myprojectnew.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class SetupProfileActivity : AppCompatActivity() {

    var binding:ActivitySetupProfileBinding?=null
    var auth:FirebaseAuth? =null
    var database:FirebaseDatabase?=null
    var storage:FirebaseStorage?=null
    var selectedImage:Uri?=null
    var dialog:ProgressDialog?=null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog = ProgressDialog(this@SetupProfileActivity)

        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialog!!.setMessage("Updating Profile...")
        dialog!!.setCancelable(false)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()

        // Firebase Authentication ile kullanıcı oturumu var mı kontrolü
        val currentUser = auth?.currentUser

        // Firebase Database'den kullanıcı verilerini çekme
        val uid = currentUser?.uid
        if (uid != null) {
            database!!.reference
                .child("users")
                .child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Kullanıcı verileri bulunduğunda
                            val user = snapshot.getValue(User::class.java)
                            if (user != null && user.profileImage != null) {
                                // Profil fotoğrafı daha önceden yüklenmişse ve isim yazılmışsa
                                // MainActivity'e geçiş yap
                                val intent = Intent(this@SetupProfileActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // Profil fotoğrafı daha önceden yüklenmemişse veya isim yazılmamışsa
                                setupUI()
                            }
                        } else {
                            // Kullanıcı verileri bulunamadığında
                            setupUI()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Hata durumunda
                        setupUI()
                    }
                })
        } else {
            // Firebase Authentication ile kullanıcı oturumu yoksa
            setupUI()
        }
    }

    private fun setupUI() {
        binding!!.imageView1.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 45)
        }

        binding!!.ConinueBtn02.setOnClickListener {
            val name: String = binding!!.nameBox.text.toString()

            if (name.isEmpty() || selectedImage == null) {
                binding!!.nameBox.setError("Lütfen bir isim ve profil fotoğrafı ekleyin.")
            } else {
                dialog!!.show()
                val reference = storage!!.reference.child("Profile").child(auth!!.uid!!)
                reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnCompleteListener { uri ->
                            val imageUrl = uri.toString()
                            val uid = auth!!.uid
                            val phone = auth!!.currentUser!!.phoneNumber
                            val user = User(uid, name, phone, imageUrl)
                            database!!.reference
                                .child("users")
                                .child(uid!!)
                                .setValue(user)
                                .addOnCompleteListener {
                                    dialog!!.dismiss()
                                    val intent =
                                        Intent(this@SetupProfileActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                    } else {
                        dialog!!.dismiss()
                        // Handle error
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && data.data != null) {
            val uri = data.data
            val storage = FirebaseStorage.getInstance()
            val time = Date().time
            val reference = storage.reference
                .child("Profile")
                .child(time.toString())

            reference.putFile(uri!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reference.downloadUrl.addOnCompleteListener { uri ->
                        val filePath = uri.toString()
                        val obj = HashMap<String, Any>()
                        obj["image"] = filePath

                        val uid = FirebaseAuth.getInstance().uid
                        if (uid != null) {
                            val databaseRef = FirebaseDatabase.getInstance().reference
                            databaseRef.child("users")
                                .child(uid)
                                .updateChildren(obj)
                                .addOnSuccessListener {
                                    // Güncelleme başarılı olduğunda yapılacak işlemler
                                }
                                .addOnFailureListener {
                                    // Güncelleme başarısız olduğunda yapılacak işlemler
                                }
                        }
                    }
                } else {
                    // Dosyayı yüklerken bir hata oluştuğunda yapılacak işlemler
                }
            }

            binding!!.imageView1.setImageURI(data.data)
            selectedImage = data.data
        }
    }

}


