package `in`.apparray.mylibrary.classes

import `in`.apparray.biztra.classes.MasterListener
import `in`.apparray.mylibrary.R
import `in`.apparray.mylibrary.utils.MyUtils
import android.content.Context
import com.google.android.material.textfield.TextInputLayout
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.firebase.firestore.FirebaseFirestore
import dmax.dialog.SpotsDialog
import es.dmoral.toasty.Toasty


class MastersDialog {

    //private lateinit var materialDialog: MaterialDialog;
    //private var listener: MasterListener? = null

    lateinit var context: Context
    var listener: MasterListener? = null
    var ssSpinner: SelectSpinner? = null
    var itemList : ArrayList<HashMap<String, Any>>? = null

    constructor(context: Context, title: String, masterDocId: String, listener: MasterListener?) {
        this.context = context
        this.listener = listener

        if (MyUtils.mastersMap == null) {
            Toasty.error(context, "$title Masters not found.", Toast.LENGTH_LONG).show()
            return
        }

        //var addFlag = !AppUtils.mastersMap!!.containsKey(key)
        var keyDoc = MyUtils.docHasMap(masterDocId)
        var keyList = MyUtils.docHasMapList(masterDocId)

        masterDialog(title, masterDocId, keyDoc, keyList)

    }

    constructor(context: Context, title: String, masterDocId: String, keyList: ArrayList<HashMap<String, Any>>, listener: MasterListener?) {
        this.context = context
        this.listener = listener

        if (MyUtils.mastersMap == null) {
            Toasty.error(context, "$title Masters not found.", Toast.LENGTH_LONG).show()
            return
        }

        val keyDoc = HashMap<String, Any>()
        keyList.forEach { item: java.util.HashMap<String, Any>? ->
            keyDoc.put(MyUtils.hasMapString(item!!["key"]), item!!["value"]!!)
        }

        masterDialog(title, masterDocId, keyDoc, keyList)
    }

    constructor(context: Context, title: String, masterDocId: String, ssSpinner: SelectSpinner, listener: MasterListener?) {
        this.context = context
        this.listener = listener

        if (MyUtils.mastersMap == null) {
            Toasty.error(context, "$title Masters not found.", Toast.LENGTH_LONG).show()
            return
        }

        this.ssSpinner = ssSpinner

        itemList = ssSpinner.getList()

        val keyDoc = HashMap<String, Any>()
        itemList!!.forEach { item: java.util.HashMap<String, Any>? ->
            keyDoc.put(MyUtils.hasMapString(item!!["key"]), item!!["value"]!!)
        }

        masterDialog(title, masterDocId, keyDoc, itemList!!)
    }


    fun masterDialog(title: String, masterDocId: String, keyDoc: HashMap<String, Any>, keyList: ArrayList<HashMap<String, Any>>) {
        val builder = MaterialDialog.Builder(context)
                .customView(R.layout.dialog_masters, true).theme(Theme.LIGHT)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel).onNegative { dialog, which -> dialog.cancel() }
                .backgroundColorRes(R.color.material_grey50)
                .autoDismiss(false)
                .title(title)

        val materialDialog = builder.show()
        materialDialog.setCancelable(false)
        val view = materialDialog.getCustomView()

        //fill out selectSpinner
        val ssMKeys: SelectSpinner = view!!.findViewById(R.id.ssMKeys)
        val layout_et: TextInputLayout = view!!.findViewById(R.id.layout_et)
        val etKeyValue: EditText = view!!.findViewById(R.id.etKeyValue)
        val imgAdd: ImageView = view!!.findViewById(R.id.imgAdd)


        imgAdd.setOnClickListener { v ->
            layout_et.visibility = View.VISIBLE
            layout_et.hint = "Add New"
            etKeyValue.setTag("add")
            etKeyValue.setText("")
            etKeyValue.requestFocus()
        }

        ssMKeys.setHint("Modify $title")
        ssMKeys.setItems(keyList, "value", "", SpinnerListener { items ->
            if (ssMKeys.text.equals("")) return@SpinnerListener

            layout_et.visibility = View.VISIBLE
            val selItem = ssMKeys.getItem()



            layout_et.hint = "Edit: " + ssMKeys.text
            etKeyValue.setText(selItem!!["value"].toString())
            etKeyValue.setTag(selItem!!["key"].toString())
            etKeyValue.requestFocus()

        })


        var validation = AwesomeValidation(ValidationStyle.BASIC)
        validation.addValidation(etKeyValue, RegexTemplate.NOT_EMPTY, "Required")

        builder.onPositive { dialog, which ->

            if (!validation.validate()) return@onPositive
            val loaderDialog = SpotsDialog(context)
            loaderDialog.show()

            var iKey: String = etKeyValue.tag.toString()
            var iValue: String = MyUtils.titleCase(etKeyValue.text.toString().trim())
//            if (!iKey.equals("add"))
//                keyHasMap.put(iKey, iValue)
//            else
//                keyHasMap.put(iValue, iValue)

            if (iKey.equals("add"))
                iKey = iValue

            keyDoc.put(iKey, iValue)

            val db = FirebaseFirestore.getInstance()
            db.document("appMasters/$masterDocId").set(keyDoc).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (listener != null) {
                        val keyList = MyUtils.hasMapList(keyDoc)
                        listener!!.onMasterEntry(keyList, iKey, iValue)
                    }

                    if(ssSpinner != null){
                        val keyList = MyUtils.hasMapList(keyDoc)
                        ssSpinner!!.setItems(keyList, "value", iValue)
                    }

                    Toasty.success(context, "$title : Entry Successful.", Toast.LENGTH_LONG).show()
                    loaderDialog.dismiss()
                    dialog.cancel()
                } else {
                    Toasty.error(context, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                }
            }

        }
    }

}
