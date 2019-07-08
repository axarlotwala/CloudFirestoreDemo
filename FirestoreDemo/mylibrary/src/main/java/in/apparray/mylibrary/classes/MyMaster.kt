package `in`.apparray.mylibrary.classes

import `in`.apparray.mylibrary.R
import `in`.apparray.mylibrary.utils.MyUtils
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dmax.dialog.SpotsDialog
import es.dmoral.toasty.Toasty
import hk.ids.gws.android.sclick.SClick

class MyMaster : LinearLayout, View.OnClickListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var loaderDialog: SpotsDialog

    lateinit var formBuilder: FormBuilder
    private lateinit var selectSpinner: SelectSpinner
    private lateinit var linearLayout: LinearLayout
    private lateinit var btnSave: MaterialFancyBtn

    private lateinit var itemList: ArrayList<HashMap<String, Any>>
    private lateinit var preDocMap: HashMap<String, Any>

    private var title = ""
    private var path = ""
    private var nameKey = ""
    private var isDocPath = false
    private var docExists = true
    private var docId = ""


    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        li.inflate(R.layout.my_master, this, true)

        loaderDialog = SpotsDialog(context)
        db = FirebaseFirestore.getInstance()

        linearLayout = findViewById(R.id.linearLayout)
        selectSpinner = findViewById(R.id.selectSpinner)
        btnSave = findViewById(R.id.btnSave)

        selectSpinner.setArrayItems(arrayListOf(), "")
        selectSpinner.isEnabled = false

        btnSave.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSave -> saveMaster()
        }

    }


    fun initMaster(
            title: String, path: String,
            formObjects: ArrayList<FormObject>,
            itemList: ArrayList<HashMap<String, Any>>,
            nameKey: String, isDocPath: Boolean = true, docExists: Boolean = true
    ) {
        this.title = title
        this.path = path
        this.isDocPath = isDocPath
        this.docExists = docExists
        this.itemList = itemList
        this.nameKey = nameKey

        if (linearLayout.childCount > 0)
            linearLayout.removeAllViews()

        formBuilder = FormBuilder(context, linearLayout)
        formBuilder.build(formObjects)

        selectSpinner.setHint("Manage $title")
        setSpinner()
        selectSpinner.addNewListener(OnClickListener { v ->
            fillItem()
        })
        linearLayout.visibility = View.GONE
        btnSave.visibility = View.GONE

    }

    private fun setSpinner() {



        selectSpinner.isEnabled = true
        selectSpinner.setItems(itemList, nameKey, "", SpinnerListener { items ->
            //fill out data for selected item to modify
            if (selectSpinner.text.equals("")) return@SpinnerListener
            fillItem(selectSpinner.getItem()!!, addFlag = false)
        })
    }

    fun initMaster(
            title: String,
            path: String,
            fldTitle: String,
            itemList: ArrayList<HashMap<String, Any>>,
            nameKey: String = "value",
            isDocPath: Boolean = true
    ) {
        val formObjects = ArrayList<FormObject>()
        formObjects.add(FormElement().setTag("value").setHint(fldTitle).setType(FormElement.Type.TEXT).setRequired(true))

        initMaster(title, path, formObjects, itemList, nameKey, isDocPath, docExists)
    }

    fun initMaster(
            title: String,
            path: String,
            fldTitle: String,
            nameKey: String = "value",
            isDocPath: Boolean = true
    ) {
        val formObjects = ArrayList<FormObject>()
        formObjects.add(FormElement().setTag("value").setHint(fldTitle).setType(FormElement.Type.TEXT).setRequired(true))

        initMaster(title, path, formObjects, nameKey, isDocPath)
    }

    fun initMaster(
            title: String,
            path: String,
            formObjects: ArrayList<FormObject>,
            nameKey: String = "value",
            isDocPath: Boolean = true
    ) {
        var eRef: Task<out Any>
        if (isDocPath)
            eRef = db.document("$path").get()
        else
            eRef = db.collection("$path").get()

        initMaster(title, path, formObjects, nameKey, isDocPath, eRef)

//        db.document(path).get().addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                loaderDialog.dismiss()
//                Toasty.error(context, "Error. ${task.exception!!.message.toString()}").show()
//                return@addOnCompleteListener
//            }
//
//            var itemList = ArrayList<HashMap<String, Any>>()
//            val docExists = task.result!!.exists()
//            if (docExists) {
//                val data = task.result!!.data
//                itemList = MyUtils.hasMapList(data)
//            }
//            initMaster(title, path, formObjects, itemList, nameKey, isDocPath, docExists)
//        }
    }

    fun initMaster(
            title: String,
            path: String,
            formObjects: ArrayList<FormObject>,
            nameKey: String = "value",
            isDocPath: Boolean = false,
            eRef: Task<out Any>
    ) {
        linearLayout.visibility = View.GONE
        eRef.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                loaderDialog.dismiss()
                Toasty.error(context, "Error. ${task.exception!!.message.toString()}").show()
                return@addOnCompleteListener
            }

            var itemList = ArrayList<HashMap<String, Any>>()
            var docExists = true
            if (isDocPath) {
                val result = task.result as DocumentSnapshot
                docExists = result.exists()
                if (docExists) {
                    val data = result.data
                    if (formObjects.size == 1)
                        itemList = MyUtils.hasMapList(data)
                    else { //to store named key in hashtable fro display properly in spinner
                        data!!.forEach { entry ->
                            val edata =entry.value as HashMap<String, Any>
                            edata["key"] = entry.key
                            itemList.add(edata)
                        }
                    }
                }
            } else {
                val result = task.result as QuerySnapshot
                result.documents.forEach { documentSnapshot: DocumentSnapshot? ->
                    val data = documentSnapshot!!.data
                    data!!["docId"] = documentSnapshot.id
                    itemList.add(data as HashMap<String, Any>)
                }

            }
            initMaster(title, path, formObjects, itemList, nameKey, isDocPath, docExists)
        }
    }

    fun fillItem(item: HashMap<String, Any> = HashMap(), docId: String = "", addFlag: Boolean = true) {
        preDocMap = item
        if (!docId.equals(""))
            this.docId = docId
        else
            if (isDocPath)
                this.docId = MyUtils.hasMapString(item["key"])
            else
                this.docId = MyUtils.hasMapString(item["docId"])

        if (addFlag)
            btnSave.setText("Add")
        else
            btnSave.setText("Update")

        linearLayout.visibility = View.VISIBLE
        btnSave.visibility = View.VISIBLE

        var items = item
        if (formBuilder.viewMap.size > 1) {
            try {
                items = item["value"] as HashMap<String, Any>
            } catch (e: Exception) {
            }
        }

        formBuilder.setFormData(items)

//        formBuilder.viewMap.forEach { entry ->
//            val view = entry.value
//            val value = MyUtils.hasMapString(items[entry.key])
//
//            when (view) {
//                is EditText -> {
//                    val editText = view as EditText
//                    editText.setText(value)
//                }
//                is SelectSpinner -> {
//                    val formElement = formBuilder.formMap.get(entry.key)!!
//                    val valueClm = formElement.getSSValueClm()
//                    val value = MyUtils.hasMapString(items[valueClm])
//
//                    val selectSpinner = view as SelectSpinner
//                    selectSpinner.selectValue(value, valueClm)
//                }
//                else -> {
//                }
//            }
//        }

        if (item.size <= 0) selectSpinner.selectItem("")

    }

    private fun saveMaster() {
        if (!SClick.check(SClick.BUTTON_CLICK)) return
        if (!formBuilder.validate()) return
        val mastDoc = formBuilder.getFormData()

        var uptDoc = mastDoc
        var dRef: Task<Void>
        if (isDocPath) { //add / update doc
            //check if doc is exist logic pending here...
            if (docId.equals("")) {
                //set docId of first item
                val key = formBuilder.formMap.keys.first()
                docId = MyUtils.hasMapString(mastDoc[key])
            }
            if (mastDoc.size == 1) {
                val key = formBuilder.formMap.keys.first()
                if (docExists) dRef = db.document("$path").update(docId, mastDoc[key])
                else dRef = db.document("$path").set(hashMapOf(docId to mastDoc[key]))
                uptDoc = linkedMapOf("key" to docId, "value" to mastDoc[key]!!)
            } else {
                uptDoc = linkedMapOf(docId to mastDoc)
                if (docExists) dRef = db.document("$path").update(uptDoc)
                else dRef = db.document("$path").set(uptDoc)

                uptDoc = mastDoc
                uptDoc["key"] = docId
                //uptDoc = linkedMapOf("key" to docId, "value" to mastDoc)
            }


        } else { //add / update collection
            if (docId.equals("")) { //add Doc or key
                dRef = db.collection("$path").document().set(mastDoc)
            } else {
                dRef = db.document("$path/$docId").update(mastDoc)
            }
        }

        loaderDialog.show()
        dRef.addOnCompleteListener { task ->
            loaderDialog.dismiss()
            if (!task.isSuccessful) {
                Toasty.error(context, "Error. ${task.exception!!.message.toString()}").show()
                return@addOnCompleteListener
            }

            Toasty.success(context, "$title Updated").show()
            itemList.remove(preDocMap)
            itemList.add(uptDoc)
            docExists = true
            setSpinner()
            fillItem()

            //also add to parent masters if dialoge is invoked from here.....

        }

    }

}