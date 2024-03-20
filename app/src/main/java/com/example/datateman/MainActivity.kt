package com.example.datateman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.datateman.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.play.integrity.internal.s
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var auth: FirebaseAuth? = null
    private val RC_SIGN_IN = 1
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inisiasi ID Button
        binding.logout.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.showData.setOnClickListener(this)

        //mendapatkan instance firebase autentikasi
        auth = FirebaseAuth.getInstance()
    }

    private fun isEmpty(s: String): Boolean {
        return TextUtils.isEmpty(s)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.save -> {
                // Handle save button click

                //Mendapatkan UserID dari pengguna yang Terautentikasi
                val getUserID = auth!!.currentUser!!.uid

                //Mendapatkan Instance dari Database
                val database = FirebaseDatabase.getInstance()

                // Menyimpan Data yang diinputkan User kedalam Variable
                val getNama: String = binding.nama.text.toString()
                val getAlamat: String = binding.alamat.text.toString()
                val getNoHP: String = binding.noHp.text.toString()

                //Mendapatkan referensi dari Database
                val getReference: DatabaseReference
                getReference = database.reference

                //Mengecek apakah ada data yang kosong
                if (isEmpty(getNama) || isEmpty(getAlamat) || isEmpty(getNoHP)) {
                    //Jika ada, maka akan menampilkan pesan singkat seperti berikut ini
                    Toast.makeText(
                        this@MainActivity, "Data tidak boleh ada yang kosong",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //Jika tidak ada, maka menyimpan ke database sesuai ID masing-masing akun
                    getReference.child("Admin").child(getUserID).child("DataTeman").push()
                        .setValue(data_teman(getNama, getAlamat, getNoHP))
                        .addOnCompleteListener(this) {
                            //bagian ini terjadi ketika user berhasil menyimpan data
                            binding.nama.setText("")
                            binding.alamat.setText("")
                            binding.noHp.setText("")
                            Toast.makeText(this@MainActivity, "Data Tersimpan",
                                Toast.LENGTH_SHORT).show()
                        }
                }
            }
            R.id.logout -> {
                AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(task: Task<Void>) {
                            Toast.makeText(
                                this@MainActivity, "Logout Berhasil",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
            }
            R.id.show_data -> {
                // Handle show data button click
            }
        }
    }
}

