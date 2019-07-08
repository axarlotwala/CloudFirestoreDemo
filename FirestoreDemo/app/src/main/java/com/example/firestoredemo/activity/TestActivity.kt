package com.example.firestoredemo.activity

import `in`.apparray.mylibrary.classes.SpinnerListener
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.firestoredemo.Package.Constant
import com.example.firestoredemo.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TestActivity : AppCompatActivity() {

    val fireDb = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        //array List

        //sItem.setItems(arrayListOf(HashMap("")))

        //SetClients()
        //RealTimeUpdate();
        SingleString();
        SingleHashmap();
        MultiSpinnerString();
        MultiSpinnerHashmap();

       // Toasty.error(this,"error toast",Toasty.LENGTH_SHORT,true).show()
       // Toasty.success(this,"Success !",Toasty.LENGTH_SHORT).show()
        var icon: Drawable? = resources.getDrawable(R.drawable.white_alert_24dp)
        Toasty.normal(this,"Normal icon icon",5000,icon,true).show()


        btn_progress.setOnClickListener { v: View? ->

            var sweetDialog : SweetAlertDialog = SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
            sweetDialog.progressHelper
            sweetDialog.setTitleText("Loading")
            sweetDialog.setCancelable(true)
            sweetDialog.show()
        }


        btn_msg.setOnClickListener { v: View? ->
            //Toast.makeText(this,"Ready To click",Toast.LENGTH_SHORT).show()
            var sweetMessage : SweetAlertDialog = SweetAlertDialog(this).setTitleText("Message Me")
            sweetMessage.show()

        }

        btn_title.setOnClickListener { v: View? ->

            var sweetTitle : SweetAlertDialog = SweetAlertDialog(this).setTitleText("Here is Message")
                    .setContentText("Its Ok ?")
                    sweetTitle.show()
        }

        btn_error.setOnClickListener { v: View? ->

            var sweetError : SweetAlertDialog = SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Something went Wrong")
            sweetError.show()
        }

        btn_warning.setOnClickListener { v: View? ->
            var sweetWarning : SweetAlertDialog = SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are You Sure ?")
                    .setContentText("want able To recoverd ?")
                    .setConfirmText("Yes Sure")
                    .setCancelText("Not sure")
             sweetWarning.show()       
        }
        
        btn_success.setOnClickListener { v: View? ->

            var sweetSuccess: SweetAlertDialog = SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("sweet !")
                    .setContentText("heres Custom Image")
            sweetSuccess.show()
        }

        btn_custom.setOnClickListener { v: View? ->

            var sweetCustom : SweetAlertDialog = SweetAlertDialog(this,SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Sweet Smily")
                    .setContentText("are You Smile or Not")
                    .setCustomImage(R.drawable.smily_yellow_24dp)
            sweetCustom.show()
        }


    }


    private fun SetClients() {

        fireDb.collection("Mfg").document("abc").collection("clients")
                .get()
                .addOnSuccessListener { result ->
                    val clientList = ArrayList<HashMap<String, Any>>()
                    for (document in result) {
                        clientList.add(document.data as HashMap<String, Any>)
                        Log.d("getData", "${document.id} => ${document.data}")

                    }
                    ssItem.setItems(clientList, "clientName")
                    //ssItem.setArrayItems(arrayListOf("Item 1", "Item 2"))
                }
                .addOnFailureListener { exception ->
                    Log.d("error", "Error getting documents: ", exception)
                }
    }

    private fun SingleString() {

        val arrList = arrayListOf<String>("One", "Two", "Three")
        ssItem.setArrayItems(arrList, "", SpinnerListener { items ->
            Toast.makeText(this, ssItem.text, Toast.LENGTH_LONG).show()
        })


        ssItem.getItem()
        ssItem.selectIndex(0)
        ssItem.selectItem("two")
        ssItem.selectValue("two")


    }

    private fun SingleHashmap() {
        val arrHList = arrayListOf<HashMap<String,Any>>()
        arrHList.add(hashMapOf("name" to "axar", "age" to "25", "address" to "surat", "mobileNo" to "1234567890"))
        arrHList.add(hashMapOf("name" to "new", "age" to "35", "address" to "adajan", "mobileNo" to "4567891230"))
        arrHList.add(hashMapOf("name" to "old", "age" to "20", "address" to "navsari", "mobileNo" to "01478520"))
        arrHList.add(hashMapOf("name" to "super", "age" to "75", "address" to "olpad", "mobileNo" to "89078945"))


        ssItem2.setItems(arrHList, "name,age","new, 35", SpinnerListener { items ->
            if (ssItem2.text == "") return@SpinnerListener
            val item = ssItem2.getItem()

            val name = item!!["name"]
            val address = item["address"]

            Toast.makeText(this, "$name, $address", Toast.LENGTH_LONG).show()
        })
    }

    private fun MultiSpinnerString() {

        var arrayList = arrayListOf<String>("two", "four", "five", "eight", "three")
        ssItem3.setArrayItems(arrayList, "", SpinnerListener { items ->
            Toast.makeText(this, ssItem3.text, Toast.LENGTH_SHORT).show()
        })
        ssItem3.getItem()
        ssItem3.setMultiSelect(true)
    }

    private fun MultiSpinnerHashmap() {

        var arrayList = arrayListOf<HashMap<String, Any>>()
        arrayList.add(hashMapOf("name" to "vijay", "age" to "25", "address" to "surat", "mobileNo" to "1234567890"))
        arrayList.add(hashMapOf("name" to "anand", "age" to "35", "address" to "adajan", "mobileNo" to "4567891230"))
        arrayList.add(hashMapOf("name" to "sharma", "age" to "20", "address" to "navsari", "mobileNo" to "01478520"))
        arrayList.add(hashMapOf("name" to "flupper", "age" to "75", "address" to "olpad", "mobileNo" to "89078945"))

        ssItem4.setItems(arrayList, "name,address", "", SpinnerListener { items ->
            if (ssItem4.text == "") return@SpinnerListener

            var items = ssItem4.getItems()
            var names = ""
            items.forEach { item: java.util.HashMap<String, Any> ->

                val name = item!!["name"]
                val address = item["address"]

                names += ", $name"
            }

             Toast.makeText(this,names,Toast.LENGTH_SHORT).show()

        })

        ssItem4.setMultiSelect(true)
    }



}
