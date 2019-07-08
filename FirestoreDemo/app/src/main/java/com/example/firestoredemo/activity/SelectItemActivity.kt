package com.example.firestoredemo.activity

import `in`.apparray.mylibrary.classes.FormBuilder
import `in`.apparray.mylibrary.classes.FormElement
import `in`.apparray.mylibrary.classes.FormObject
import `in`.apparray.mylibrary.classes.SpinnerListener
import `in`.apparray.mylibrary.utils.MyUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firestoredemo.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_select_item.*
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SelectItemActivity : AppCompatActivity() {

    lateinit var firedb: FirebaseFirestore
    lateinit var formBuilder: FormBuilder
    lateinit var detaildata: HashMap<String, Any>

    lateinit var contractId: String
    lateinit var contractNo: String
    lateinit var kanbandate: String
    lateinit var partDesc: String
    lateinit var quantity: String
    lateinit var stdWeight: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_item)

        firedb = FirebaseFirestore.getInstance()

        getContracts()
        getParts()
        formBuild()

    }

    fun getContracts() {

        firedb.collection("Mfg/abc/contarctsBook")
                .get()
                .addOnSuccessListener { result ->
                    val contractlist = arrayListOf<HashMap<String, Any>>()
                    if (result != null) {
                        //Log.d("GetValue","Data : "+)
                        for (doc in result) {
                            detaildata = doc.data as HashMap<String, Any>
                            contractlist.add(detaildata)
                            // detaildata.get("contractId")
                            //Log.d("conId",":"+conId)
                        }
                        ssClients.setItems(contractlist, "contractName", "", SpinnerListener { items ->
                            if (ssClients.text == "") return@SpinnerListener
                        })
                        fillData(detaildata)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
    }

    fun getParts() {

        firedb.collection("Mfg/abc/parts")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val partList = arrayListOf<HashMap<String, Any>>()
                    for (doc in querySnapshot) {
                        partList.add(doc.data as HashMap<String, Any>)
                    }
                    ssParts.setItems(partList, "partName", "", SpinnerListener { items ->
                        if (ssParts.text == "") return@SpinnerListener

                        var parts = ssParts.getItem()
                    })
                }
                .addOnFailureListener { exception ->
                    Log.d("errorfind", exception.toString())
                }
    }

    fun formBuild() {

        var formObject = ArrayList<FormObject>()

        formObject.add(FormElement().setTag("contractNo").setHint("contract No").setType(FormElement.Type.NUMBER).setRequired(true))
        formObject.add(FormElement().setTag("kanbanDate").setHint("KanbanDate").setType(FormElement.Type.DATE).setRequired(true))
        formObject.add(FormElement().setTag("partdesc").setHint("part Description").setType(FormElement.Type.TEXT).setRequired(true))
        formObject.add(FormElement().setTag("quantity").setHint("Quantity").setType(FormElement.Type.NUMBER).setRequired(true))
        formObject.add(FormElement().setTag("stdWeight").setHint("standard Weight").setType(FormElement.Type.NUMBER).setRequired(true))

        formBuilder = FormBuilder(this, linearSelect)
        formBuilder.build(formObject)
    }

    private fun fillData(data: HashMap<String, Any> = HashMap()) {

        formBuilder.setFormData(data)
        contractId = MyUtils.hasMapString(data["ContarctId"])
        contractNo = MyUtils.hasMapString(data["contractNo"])
        kanbandate = MyUtils.hasMapString(data["kanbanDate"])
        quantity = MyUtils.hasMapString(data["quantity"])
        stdWeight = MyUtils.hasMapString(data["stdWeight"])
        Log.d("GetData", "Data" + data)
    }
}
