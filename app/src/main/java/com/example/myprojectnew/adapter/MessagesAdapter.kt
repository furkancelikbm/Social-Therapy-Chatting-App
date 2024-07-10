package com.example.myprojectnew.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myprojectnew.R
import com.example.myprojectnew.databinding.DeleteLayoutBinding
import com.example.myprojectnew.databinding.ReceiveMsgBinding
import com.example.myprojectnew.databinding.SendMsgBinding
import com.example.myprojectnew.model.Message
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessagesAdapter(
    var context: Context,
    messages: ArrayList<Message>?,
    senderRoom: String,
    receiverRoom: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    lateinit var messages: ArrayList<Message>
    val ITEM_SENT = 1
    val ITEM_RECEIVE = 2
    var senderRoom: String? = null
    var receiverRoom: String? = null

    class SentMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: SendMsgBinding = SendMsgBinding.bind(itemView)
    }

    class ReceiveMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ReceiveMsgBinding = ReceiveMsgBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        FirebaseApp.initializeApp(context)

        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(context).inflate(R.layout.send_msg, parent, false)
            SentMsgHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.receive_msg, parent, false)
            ReceiveMsgHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messages = messages[position]
        return if (FirebaseAuth.getInstance().uid == messages.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder is SentMsgHolder) {
            if (message.message.equals("photo")) {
                holder.binding.image.visibility = View.VISIBLE
                holder.binding.message.visibility = View.GONE
                holder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.image)
            } else {
                holder.binding.message.text = message.message
                holder.binding.image.visibility = View.GONE
                holder.binding.message.visibility = View.VISIBLE
                holder.binding.mLinear.visibility = View.VISIBLE
            }

            holder.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()
                binding.everyone.setOnClickListener {

                    val NewMessage="Bu mesaj silindi"
                    message.messageId?.let { senderMessageId ->
                        // Update sender's message content
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom!!)
                            .child("messages")
                            .child(senderMessageId)
                            .child("message")
                            .setValue(NewMessage)
                        println(" gönderici silinen mesajıd = ${message.messageId}")
                    }
                    dialog.dismiss()

                    // Veritabanı işlemlerinden önce boş değerleri kontrol edin


                }


                binding.delete.setOnClickListener {
                    message.messageId?.let { messageId ->
                        // Set the message content to null in senderRoom
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom!!)
                            .child("messages")
                            .child(messageId)
                            .child("message")
                            .setValue(null)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    notifyDataSetChanged()
                                    dialog.dismiss()
                                } else {
                                    // Handle the error
                                }
                            }
                    }
                }


                binding.cancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                false
            }
        } else if (holder is ReceiveMsgHolder) {
            println("holder is ReceiveMsgHolder içindeyiz")
            if (message.message.equals("photo")) {
                holder.binding.image.visibility = View.VISIBLE
                holder.binding.message.visibility = View.GONE
                holder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.image)
            } else {
                holder.binding.message.text = message.message
                holder.binding.image.visibility = View.GONE
                holder.binding.message.visibility = View.VISIBLE
                holder.binding.mLinear.visibility = View.VISIBLE
            }

            holder.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()

                binding.everyone.setOnClickListener {
                    val newMessage= "Bu Mesaj silindi"

                    message.messageId?.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiverRoom!!)
                            .child("message")
                            .child(it1).setValue(newMessage)
                    }
                    dialog.dismiss()
                }
                binding.delete.setOnClickListener {
                    message.messageId?.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom!!)
                            .child("message")
                            .child(it1).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.cancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                false
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    init {
        if (messages != null) {
            this.messages = messages
        }
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }
}
