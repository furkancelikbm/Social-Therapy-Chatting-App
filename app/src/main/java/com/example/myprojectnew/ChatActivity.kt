package com.example.myprojectnew

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myprojectnew.adapter.MessagesAdapter
import com.example.myprojectnew.databinding.ActivityChatBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Date
import android.os.Message as Message

class ChatActivity : AppCompatActivity() {
    var binding:ActivityChatBinding?=null
    var adapter:MessagesAdapter?=null
    var messages:ArrayList<com.example.myprojectnew.model.Message>?=null
    var senderRoom:String?=null
    var receiverRoom:String?=null
    var database:FirebaseDatabase?=null
    var storage:FirebaseStorage?=null
    var dialog:ProgressDialog?=null
    var senderUid:String?=null
    var receiverUid:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding=ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.toolbar)
        database=FirebaseDatabase.getInstance()
        storage=FirebaseStorage.getInstance()
        dialog= ProgressDialog(this@ChatActivity)
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)
        messages= ArrayList()
        val name = intent.getStringExtra("name")
        val profile = intent.getStringExtra("image")
        binding!!.name.text = name // Corrected line


        val userFolder = "Profile/${intent.getStringExtra("uid")}"
        // Firebase Storage referansını oluşturun
        val storageReference = FirebaseStorage.getInstance().getReference(userFolder)

        // Daha sonra bu referansı kullanarak işlemler yapabilirsiniz, örneğin URL almak:
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            // imageURL'i kullanarak işlemler yapabilirsiniz, örneğin Glide ile yükleme
            Glide.with(this@ChatActivity)
                .load(imageUrl)
                .placeholder(R.drawable.pngegg)
                .into(binding!!.profile01)
        }.addOnFailureListener { exception ->
            // Referansı almakta veya URL'yi oluşturmada bir hata oluştu
            Log.e(ContentValues.TAG, "Error getting image URL: ${exception.message}")
        }



        binding!!.imageView.setOnClickListener{finish()}
        receiverUid=intent.getStringExtra("uid")
        senderUid=FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence").child(receiverUid!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val status=snapshot.getValue(String::class.java)
                        if (status=="offline"){
                            binding!!.status.visibility= View.GONE
                        }
                        else{
                            binding!!.status.setText(status)
                            binding!!.status.visibility=View.VISIBLE
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        senderRoom=senderUid+receiverUid
        receiverRoom=receiverUid+senderUid
        val adapter= MessagesAdapter(this@ChatActivity,messages,senderRoom!!,receiverRoom!!)
        binding!!.recyclerView.adapter=adapter

        binding!!.recyclerView.layoutManager=LinearLayoutManager(this@ChatActivity)
        database!!.reference.child("chats")
            .child(senderRoom!!)
            .child("messages")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages!!.clear()
                    for (snapshot1 in snapshot.children){
                        val message:com.example.myprojectnew.model.Message?=snapshot1.getValue(com.example.myprojectnew.model.Message::class.java)
                        message!!.messageId=snapshot1.key
                        messages!!.add(message)
                    }

                    adapter!!.notifyDataSetChanged()
                    binding!!.recyclerView.postDelayed({
                        binding!!.recyclerView.scrollToPosition(messages!!.size - 1)
                    }, 100)
                }
                override fun onCancelled(error: DatabaseError) {}

            })

        binding!!.sendBtn.setOnClickListener {
            val messageTxt: String = binding!!.messageBox.text.toString()
            val date = Date()
            val message = com.example.myprojectnew.model.Message(messageTxt, senderUid, date.time)

            // Gönderenin mesajını gönder
            val senderRandomKey = database!!.reference.child("chats").child(senderRoom!!)
                .child("messages").push().key!!
            database!!.reference.child("chats").child(senderRoom!!)
                .child("messages").child(senderRandomKey).setValue(message)

            // Alıcının mesajını gönder
            val receiverRandomKey = database!!.reference.child("chats").child(receiverRoom!!)
                .child("messages").push().key!!
            database!!.reference.child("chats").child(receiverRoom!!)
                .child("messages").child(receiverRandomKey).setValue(message)

            // Son gönderilen mesajı güncelle
            val lastMsgObj = HashMap<String, Any>()
            lastMsgObj["lastMsg"] = message.message!!
            lastMsgObj["lastMsgTime"] = date.time
            database!!.reference.child("chats").child(senderRoom!!).updateChildren(lastMsgObj)
            database!!.reference.child("chats").child(receiverRoom!!).updateChildren(lastMsgObj)

            binding!!.messageBox.setText("")
        }

        binding!!.attachment.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 25)
        }
        val handler=Handler()
        binding!!.messageBox.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                database!!.reference.child("Presence")
                    .child(senderUid!!)
                    .setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping,1000)
            }
            var userStoppedTyping= Runnable {
                database!!.reference.child("Presence")
                    .child(senderUid!!)
                    .setValue("Online")
            }
        })
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25) {
            if (data != null && data.data != null) {
                val selectedImage = data.data
                val calendar = Calendar.getInstance()
                val reference = storage!!.reference.child("chats")
                    .child(calendar.timeInMillis.toString())

                dialog!!.show()
                reference.putFile(selectedImage!!)
                    .addOnCompleteListener { task ->
                        dialog!!.dismiss()
                        if (task.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                val filePath = uri.toString()
                                val date = Date()
                                val message =
                                    com.example.myprojectnew.model.Message("photo", senderUid, date.time)
                                message.imageUrl = filePath

                                // Gönderenin fotoğrafını gönder
                                val senderRandomKey =
                                    database!!.reference.child("chats").child(senderRoom!!)
                                        .child("messages").push().key!!
                                database!!.reference.child("chats").child(senderRoom!!)
                                    .child("messages").child(senderRandomKey).setValue(message)

                                // Alıcının fotoğrafını gönder
                                val receiverRandomKey =
                                    database!!.reference.child("chats").child(receiverRoom!!)
                                        .child("messages").push().key!!
                                database!!.reference.child("chats").child(receiverRoom!!)
                                    .child("messages").child(receiverRandomKey).setValue(message)

                                // Son gönderilen mesajı güncelle
                                val lastMsgObj = HashMap<String, Any>()
                                lastMsgObj["lastMsg"] = message.message!!
                                lastMsgObj["lastMsgTime"] = date.time
                                database!!.reference.child("chats").child(senderRoom!!)
                                    .updateChildren(lastMsgObj)
                                database!!.reference.child("chats").child(receiverRoom!!)
                                    .updateChildren(lastMsgObj)
                            }
                        }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentId=FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId=FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Offline")
    }
}