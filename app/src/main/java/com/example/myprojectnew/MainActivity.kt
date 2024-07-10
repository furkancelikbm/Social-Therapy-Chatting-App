package com.example.myprojectnew

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.myprojectnew.adapter.UserAdapter
import com.example.myprojectnew.databinding.ActivityMainBinding
import com.example.myprojectnew.databinding.NavHeaderBinding
import com.example.myprojectnew.model.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.initialize
import com.google.firebase.storage.FirebaseStorage
import org.checkerframework.common.returnsreceiver.qual.This



var binding:ActivityMainBinding?=null
 var database:FirebaseDatabase?=null
 var users:ArrayList<User>?=null
var dialog:ProgressDialog?=null
var usersAdapter:UserAdapter?=null
var user: User?=null
var navHeaderBinding:NavHeaderBinding?=null



class MainActivity : AppCompatActivity() {

    lateinit var toggle:ActionBarDrawerToggle //menü için toggle
    private lateinit var drawerLayout: DrawerLayout

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


         Firebase.initialize(context = this)
         Firebase.appCheck.installAppCheckProviderFactory(
             PlayIntegrityAppCheckProviderFactory.getInstance(),
         )

         binding = ActivityMainBinding.inflate(layoutInflater)
         navHeaderBinding = NavHeaderBinding.inflate(layoutInflater) // NavHeaderBinding'ı başlat
         setContentView(binding!!.root)
         // drawerLayout'ı başlat
         val navHeader = binding!!.navView.getHeaderView(0)
         navHeaderBinding = NavHeaderBinding.bind(navHeader)//eğer baska xml'i alıyorsan kesinlikle olması gereken bu ve yukardaki



         // Çıkış butonuna tıklama olayını ekleme
         val aibutton: ImageView = binding!!.aiButton
         aibutton.setOnClickListener {
             Toast.makeText(this, "Yapay Zeka şuan bakımda", Toast.LENGTH_SHORT).show()

         }



         dialog= ProgressDialog(this@MainActivity)
         dialog!!.setMessage("Uploading Image..")
         dialog!!.setCancelable(false)
         database=FirebaseDatabase.getInstance()
         users= ArrayList<User>()
         usersAdapter=UserAdapter(this@MainActivity,users!!)

         val layoutManager=GridLayoutManager(this@MainActivity,2)
         binding!!.mRec1.layoutManager=layoutManager
         database!!.reference.child("users")
             .child(FirebaseAuth.getInstance().uid!!)
             .addValueEventListener(object :ValueEventListener{
                 override fun onDataChange(snapshot: DataSnapshot) {
                     user=snapshot.getValue(User::class.java)
                 }

                 override fun onCancelled(error: DatabaseError) {}
             })
         binding!!.mRec1.adapter= usersAdapter
         database!!.reference.child("users").addValueEventListener(object :ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 users!!.clear()
                 for(snapshot1 in snapshot.children){
                     val user:User?=snapshot1.getValue(User::class.java)
                     if (!user!!.uid.equals(FirebaseAuth.getInstance().uid)) users!!.add(user)
                 }
                 usersAdapter!!.notifyDataSetChanged()
             }

             override fun onCancelled(error: DatabaseError) {}
         })

         //menü ayarları
         val navView: NavigationView = binding!!.navView
         binding!!.menuButton.setOnClickListener {

             // Kullanıcının mevcut oturumunu al
             val currentUser = FirebaseAuth.getInstance().currentUser

             // activitydeki verileri nav_holder aktarma
             if (user != null) {
                 navHeaderBinding!!.name.text = user!!.name
             } else {
                 // user is null, handle this case appropriately
             }

             var userEmail = currentUser?.email
             if (userEmail != null) {
                 navHeaderBinding!!.email.text = userEmail
             } else {
                 // userEmail is null, handle this case appropriately
             }

             val userFolder = "Profile/${intent.getStringExtra("uid")}"

             // Firebase Storage referansını oluşturun
             val storageReference = FirebaseStorage.getInstance().getReference(userFolder)

             // Daha sonra bu referansı kullanarak işlemler yapabilirsiniz, örneğin URL almak:
             storageReference.downloadUrl.addOnFailureListener { exception ->
                 // Profil resmi bulunmuyorsa durumu işle
                 Log.e(ContentValues.TAG, "Resim URL'sini alma hatası: ${exception.message}")

                 // Varsayılan bir resim göster veya kullanıcıya bir hata mesajı göster
                 Glide.with(this@MainActivity)
                     .load(R.drawable.pngegg)
                     .into(navHeaderBinding!!.profileImage)
             }

             val navView: NavigationView = binding!!.navView
             if (navView.visibility == View.VISIBLE) {
                 navView.visibility = View.INVISIBLE
             } else {
                 navView.visibility = View.VISIBLE
             }
         }

         val contentLayout: LinearLayout = findViewById(R.id.content_layout)
         val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

         contentLayout.setOnClickListener {
             binding!!.navView.visibility=View.GONE
         }





         supportActionBar?.setDisplayHomeAsUpEnabled(true)
         navView.setNavigationItemSelectedListener {
                    when(it.itemId){
                        R.id.nav_home-> Toast.makeText(applicationContext,"Anasayfaya Tıklandı",Toast.LENGTH_SHORT).show()
                        R.id.nav_message-> Toast.makeText(applicationContext,"Anasayfaya Tıklandı",Toast.LENGTH_SHORT).show()
                        R.id.nav_sync-> Toast.makeText(applicationContext,"Anasayfaya Tıklandı",Toast.LENGTH_SHORT).show()
                        R.id.nav_trash-> Toast.makeText(applicationContext,"Anasayfaya Tıklandı",Toast.LENGTH_SHORT).show()
                        R.id.nav_settings-> Toast.makeText(applicationContext,"Anasayfaya Tıklandı",Toast.LENGTH_SHORT).show()


                        R.id.nav_logout-> // AlertDialog oluştur
                            AlertDialog.Builder(this)
                                .setTitle("Çıkış")
                                .setMessage("Çıkış yapmak istediğinize emin misiniz?")
                                .setPositiveButton("Evet") { dialog, which ->
                                    // Kullanıcı eveti seçtiğinde çıkış işlemini burada gerçekleştirin
                                    FirebaseAuth.getInstance().signOut()

                                    // Kullanıcı çıkış yaptıktan sonra istediğiniz başka bir aktiviteye yönlendirebilirsiniz.
                                    val intent = Intent(this, SignInActivity::class.java)
                                    startActivity(intent)
                                    finish()  // Mevcut aktiviteyi kapatmak istiyorsanız
                                    Toast.makeText(this, "Çıkış başarılı", Toast.LENGTH_SHORT).show()
                                }
                                .setNegativeButton("Hayır", null)  // Hayır seçeneği için herhangi bir işlem yapma
                                .show()

                        R.id.nav_share-> Toast.makeText(applicationContext,"Anasayfaya Tıklandı",Toast.LENGTH_SHORT).show()
                        R.id.nav_rate_us-> Toast.makeText(applicationContext,"Anasayfaya Tıklandı",Toast.LENGTH_SHORT).show()

                    }
                    true
                }


     }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState)
        // ActionBarDrawerToggle'ın durumunu senkronize et
        toggle.syncState()
    }

    override fun onResume() {
        super.onResume()
        val currentId=FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()

        val currentId = FirebaseAuth.getInstance().uid
        if (currentId != null) {
            database!!.reference.child("presence")
                .child(currentId).setValue("Offline")
        }
    }


}