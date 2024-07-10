package com.example.myprojectnew.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myprojectnew.ChatActivity
import com.example.myprojectnew.R
import com.example.myprojectnew.databinding.ItemProfileBinding
import com.example.myprojectnew.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage




class UserAdapter(var context: Context, var userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemProfileBinding = ItemProfileBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        FirebaseApp.initializeApp(context)
        val v = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false)
        return UserViewHolder(v)
    }



    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.username.text=user.name

        // Kullanıcının UID'sini alarak, ona özgü bir klasör oluşturun
        val userFolder = "Profile/${user.uid}"

        // Firebase Storage referansını oluşturun
        val storageReference = FirebaseStorage.getInstance().getReference(userFolder)

        // Daha sonra bu referansı kullanarak işlemler yapabilirsiniz, örneğin URL almak:
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            println("$uri")
            val imageUrl = uri.toString()
            println("$imageUrl")
            // imageURL'i kullanarak işlemler yapabilirsiniz, örneğin Glide ile yükleme
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.pngegg)
                .into(holder.binding.profile)
        }.addOnFailureListener { exception ->
            // Referansı almakta veya URL'yi oluşturmada bir hata oluştu
            Log.e(TAG, "Error getting image URL: ${exception.message}")
        }
        holder.itemView.setOnClickListener{
            val intent=Intent(context,ChatActivity::class.java)
            intent.putExtra("name",user.name)
            intent.putExtra("image",user.profileImage)
            intent.putExtra("uid",user.uid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = userList.size
}

