package `in`.apparray.aceta

import `in`.apparray.mylibrary.classes.SelectSpinner
import `in`.apparray.mylibrary.classes.SpinnerListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_company.*
import `in`.apparray.mylibrary.classes.FormElement
import `in`.apparray.mylibrary.classes.FormObject
import `in`.apparray.mylibrary.classes.FormBuilder
import `in`.apparray.mylibrary.classes.FormElementArray
import `in`.apparray.mylibrary.utils.MyUtils
import com.google.android.material.textfield.TextInputLayout
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import `in`.apparray.utis.classes.HashMapComparator
import android.content.Intent
import android.widget.LinearLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.firestoredemo.R
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.*
import es.dmoral.toasty.Toasty
import hk.ids.gws.android.sclick.SClick
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CompanyActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var formBuilder: FormBuilder
    private lateinit var db: FirebaseFirestore
    private lateinit var loaderDialog: SweetAlertDialog
    private var companyDocId = ""

    private lateinit var ssType: SelectSpinner
    private lateinit var sCompany: SelectSpinner
    private lateinit var tvATitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company)


        loaderDialog = SweetAlertDialog(this)
        loaderDialog =  SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
        loaderDialog.progressHelper
        loaderDialog.setTitleText("Loading")
        loaderDialog.setCancelable(true)
        loaderDialog.show()


        db = FirebaseFirestore.getInstance()

        btnAdd.setOnClickListener(this)
        /*btnSettings.setOnClickListener(this)
        btnSign.setOnClickListener(this)
        btnReset.setOnClickListener(this)*/
        btnSave.setOnClickListener(this)

        val companyList = arrayListOf<HashMap<String,Any>>()
        Collections.sort(companyList, HashMapComparator("name"))
        ssCompany.setItems(companyList, "name", "", SpinnerListener { items ->
            if (ssCompany.text.equals("")) return@SpinnerListener

            val item = ssCompany.getItem()
            val companyId = MyUtils.hasMapString(item!!["docId"])
            loaderDialog.show()
            db.document("").get().addOnCompleteListener { task ->
                loaderDialog.hide()
                if (!task.isSuccessful) {
                    Toasty.error(this, "Error. ${task.exception!!.message.toString()}").show()
                    return@addOnCompleteListener
                }

                val data = task.result!!.data as HashMap<String, Any>
                data["docId"] = task.result!!.id
                fillItem(data)
            }
        })
        ssCompany.addNewListener(View.OnClickListener { v ->
            //fillItem()
        })

        //if (companyList.size == 1) ssCompany.selectIndex(0)
        //ssCompany.isEnabled = false

        val onlyCompanyList = ArrayList<HashMap<String, Any>>()
        companyList.forEach { item: java.util.HashMap<String, Any> ->
            if (MyUtils.hasMapString(item["type"]).equals("company", true)) {
                item["companyId"] = item["docId"]!!
                onlyCompanyList.add(item)
            }

          //companyList.forEach()
            /*for (i in 0 until array.size()) {

            }*/
        }

        val formObjects = ArrayList<FormObject>()
       // formObjects.add(FormElement().setTag("aTitle").setHint("Add Company / Location").setType(FormElement.Type.TITLE))
        formObjects.add(FormElement().setTag("type").setHint("Type *").setType(FormElement.Type.SPINNER).setOptions(arrayListOf("contractName", "clientName")).setRequired(true))
        formObjects.add(FormElement().setTag("companyId").setHint("Company *").setType(FormElement.Type.SPINNER).setSSOptions(onlyCompanyList, "name", "companyId", hashMapOf("docId" to "companyId")).setRequired(true))

        formObjects.add(FormElement().setTag("name").setHint("Company / Location Name *").setType(FormElement.Type.TEXT_CAP).setRequired(true))
        //formObjects.add(FormElement().setTag("operator").setHint("Operator").setType(FormElement.Type.SPINNER).setOptions(arrayListOf("Jio", "Idea-Vodafone", "Airtel", "BSNL")).setRequired(true))

        var fObj = ArrayList<FormObject>()
        fObj.add(FormElement().setTag("firstName").setHint("Name").setType(FormElement.Type.TEXT_CAP))
        fObj.add(FormElement().setTag("surname").setHint("Surname").setType(FormElement.Type.TEXT_CAP))
        formObjects.add(FormElementArray(fObj))

        fObj = ArrayList()
        fObj.add(FormElement().setTag("mobileNo").setHint("Mobile No").setType(FormElement.Type.NUMBERTEXT))
        fObj.add(FormElement().setTag("email").setHint("Email").setType(FormElement.Type.EMAIL))
        formObjects.add(FormElementArray(fObj))

        formObjects.add(FormElement().setTag("address").setHint("Address").setType(FormElement.Type.TEXT))
        fObj = ArrayList()
       // fObj.add(FormElement().setTag("state").setHint("State *").setType(FormElement.Type.SPINNER).setSSOptions(AppUtils.mStateList, "key", "", hashMapOf("key" to "state", "value" to "stateCode")).setRequired(true))
        fObj.add(FormElement().setTag("city").setHint("City").setType(FormElement.Type.TEXT))
        formObjects.add(FormElementArray(fObj))

        formObjects.add(FormElement().setTag("gt").setHint("GST Details").setType(FormElement.Type.GROUPTITLE))
        formObjects.add(FormElement().setTag("gstType").setHint("GST Status").setType(FormElement.Type.SPINNER).setOptions(arrayListOf("Unregistered", "Registered - Regular", "Registered - Composite")))

        fObj = ArrayList()
        fObj.add(FormElement().setTag("gstinNo").setHint("GSTIN No").setType(FormElement.Type.TEXT_CAP))
        fObj.add(FormElement().setTag("panNo").setHint("PAN No").setType(FormElement.Type.TEXT_CAP))
        formObjects.add(FormElementArray(fObj))

        formObjects.add(FormElement().setTag("gto").setHint("Other").setType(FormElement.Type.GROUPTITLE))
        formObjects.add(FormElement().setTag("terms").setHint("Invoice Terms").setType(FormElement.Type.TEXT))

        formBuilder = FormBuilder(this, linearLayout)
        formBuilder.build(formObjects)
        tvATitle = formBuilder.viewMap["aTitle"] as TextView

        //type,company show hide based on location

        ssType = formBuilder.viewMap["type"] as SelectSpinner
        sCompany = formBuilder.viewMap["companyId"] as SelectSpinner
        ssType.selectIndex(0)
        sCompany.visibility = View.GONE

        ssType.setSpinnerListener(SpinnerListener { items ->
            if (ssType.text.equals("")) return@SpinnerListener
            when (ssType.text) {
                "Location" -> sCompany.visibility = View.VISIBLE
                else -> sCompany.visibility = View.GONE
            }
        })

        sCompany.visibility = View.GONE
        linearLayoutForm.visibility = View.GONE

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnAdd -> fillItem()
          //  R.id.btnSign -> { }
          //  R.id.btnSettings -> openSrNoDialog()
          //  R.id.btnReset -> resetDialog()
            R.id.btnSave -> {
                if (!SClick.check(SClick.BUTTON_CLICK)) return
                if (!formBuilder.validate()) return

                val cDoc = formBuilder.getFormData()


//                val builder = MaterialDialog.Builder(this).title("Save data?").positiveText(android.R.string.ok).negativeText(android.R.string.cancel).onNegative { dialog, which -> dialog.cancel() }
//
//                val dialog = builder.build()
//                dialog.show()
//
//                builder.onPositive { dialog, which ->
//                    dialog.dismiss()
//                    saveCompany()
//                }
            }
            else -> {
            }
        }

    }

    override fun onBackPressed() {
        if (linearLayoutBtn.visibility == View.GONE) {
            ssCompany.selectValue("")
            linearLayoutBtn.visibility = View.VISIBLE
            linearLayoutForm.visibility = View.GONE
            return
        }
        super.onBackPressed()
    }

   /* private fun openSrNoDialog() {

        var builder = MaterialDialog.Builder(this).customView(R.layout.dialog_srno, true).positiveText(android.R.string.ok).negativeText(android.R.string.cancel).onNegative { dialog, which -> dialog.cancel() }.autoDismiss(false).title("Manage Sr#")

        val materialDialog = builder.show()
        materialDialog.setCancelable(false)

        val view = materialDialog.getCustomView()

        val ssFinYear: SelectSpinner = view!!.findViewById(R.id.ssFinYear)
        val ssInvType: SelectSpinner = view!!.findViewById(R.id.ssInvType)
        val tvInvNo: TextInputEditText = view!!.findViewById(R.id.tvInvNo)
        val tvPrefix: TextInputEditText = view!!.findViewById(R.id.tvPrefix)
        val tvSuffix: TextInputEditText = view!!.findViewById(R.id.tvSuffix)

        val tlInvNo: TextInputLayout = view!!.findViewById(R.id.tlInvNo)

        val llForm: LinearLayout = view!!.findViewById(R.id.llForm)

        var srNoKey = ""
        var srNoDocId = ""

        val validation = AwesomeValidation(ValidationStyle.BASIC)
        validation.addValidation(tvInvNo, RegexTemplate.NOT_EMPTY, "Required")


        db.collection("${AppUtils.mPath}/srNos").get().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                loaderDialog.dismiss()
                Toasty.error(this, "Error. ${task.exception!!.message.toString()}").show()
                return@addOnCompleteListener
            }

            val srNoList = ArrayList<HashMap<String, Any>>()
            if (!task.result!!.isEmpty) task.result!!.documents.forEach { doc: DocumentSnapshot? ->
                val data = doc!!.data
                data!!["docId"] = doc.id

                if (!doc.id.startsWith(AppUtils.mCompanyId)) return@forEach

                data["finYear"] = doc.id.replace("${AppUtils.mCompanyId}-", "")
                srNoList.add(data as HashMap<String, Any>)
            }

            if(srNoList.size <= 0){
                val fYear = MyFuns.finYear(MyUtils.curMomentLong())
                //srNoDocId = "${AppUtils.mCompanyId}-$fYear"
                srNoList.add(hashMapOf("finYear" to fYear, "docId" to "${AppUtils.mCompanyId}-$fYear"))
            }

            ssInvType.visibility = View.GONE
            llForm.visibility = View.GONE


            ssFinYear.setItems(srNoList, "finYear", "", SpinnerListener { items ->

                if (ssFinYear.text.equals("")) return@SpinnerListener

                val item = ssFinYear.getItem()
                val invList = arrayListOf<String>("Sales", "Purchase", "Transfer", "Return", "Bank-Cash", "Payment Out", "Payment In", "Estimate")
//                item!!.forEach { entry ->
//                    if(!entry.key.endsWith("srNo")) return@forEach
//                    invList.add(entry.key)
//                }

                srNoDocId = MyUtils.hasMapString(item!!["docId"])
                ssInvType.visibility = View.VISIBLE
                llForm.visibility = View.GONE

                ssInvType.setArrayItems(invList, "", SpinnerListener { items ->

                    srNoKey = ssInvType.text
                    if (srNoKey.equals("")) return@SpinnerListener


                    //srNoKey = invType.replace("-srNo", "")

                    tvInvNo.setText(MyUtils.hasMapString(item!!["$srNoKey-srNo"]))
                    tvPrefix.setText(MyUtils.hasMapString(item["$srNoKey-prefix"]))
                    tvSuffix.setText(MyUtils.hasMapString(item["$srNoKey-suffix"]))

                    tlInvNo.hint = "Sr # ${tvInvNo.text}" //tvInvNo.text
//                    tlPrefix.hint = "Start With ${tvPrefix.text}"
//                    tlSuffix.hint = "End With ${tvSuffix.text}"

                    llForm.visibility = View.VISIBLE
                })

            })

        }

        builder.onPositive { dialog, which ->
            if (!SClick.check(SClick.BUTTON_CLICK)) return@onPositive
            if (!validation.validate()) return@onPositive

            val keyMap = HashMap<String, Any>()
            keyMap["$srNoKey-srNo"] = MyUtils.hasMapInt(tvInvNo.text)
            keyMap["$srNoKey-prefix"] = tvPrefix.text.toString()
            keyMap["$srNoKey-suffix"] = tvSuffix.text.toString()


            db.document("${AppUtils.mPath}/srNos/$srNoDocId").set(keyMap, SetOptions.merge()).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                	loaderDialog.dismiss()
                	Toasty.error(this, "Error. ${task.exception!!.message.toString()}").show()
                	return@addOnCompleteListener
                }

                Toasty.success(this, "$srNoKey Sr# updated.").show()
                
                dialog.dismiss()
                
            }


        }

    }

    private fun resetDialog() {

        val name = ssCompany.text
        val builder = MaterialDialog.Builder(this).customView(R.layout.dialog_input, true).positiveText(android.R.string.ok).negativeText(android.R.string.cancel).onNegative { dialog, which ->
            dialog.cancel()
        }.autoDismiss(false).title(name)

        val materialDialog = builder.show()
        materialDialog.setCancelable(false)

        val view = materialDialog.getCustomView()

        val textView: TextView = view!!.findViewById(R.id.textView)
        val tvInput: EditText = view!!.findViewById(R.id.tvInput)
        val tlInput: TextInputLayout = view!!.findViewById(R.id.tlInput)

        val inputValidation = AwesomeValidation(ValidationStyle.BASIC)
        inputValidation.addValidation(tvInput, RegexTemplate.NOT_EMPTY, "Required")

        tvInput.setText("")
        tlInput.hint = "To Remove Invoices type RESET"

        textView.text = "Remove Invoices and Reset $name data?"
        textView.visibility = View.VISIBLE

        builder.onPositive { dialog, which ->
            if (!tvInput.text.toString().equals("reset", true)) {
                Toasty.info(this, "Type Reset to continue.").show()
                return@onPositive
            }

            resetCompany(ssCompany.getItem()!!)
            dialog.dismiss()
        }

    }
*/
    private var batchItemCount = 0
    /*private fun removeDoc(task: Task<QuerySnapshot>, batches: ArrayList<WriteBatch>, collName: String) {
        var batch = batches.last()
        task.result!!.documents.forEach { doc: DocumentSnapshot? ->
            batch.delete(db.document("${AppUtils.mPath}/$collName/" + doc!!.id))
            if (batchItemCount >= 499) {
                batches.add(db.batch())
                batchItemCount = 0
                batch = batches.last()
            } else batchItemCount += 1
        }
    }*/

   /* private fun resetCompany(item: java.util.HashMap<String, Any>) {

        val companyId = MyUtils.hasMapString(item["docId"])
        val name = MyUtils.hasMapString(item["name"])

        val tRef = db.collection("${AppUtils.mPath}/transactions-$companyId").get()
        val sRef = db.collection("${AppUtils.mPath}/summaryColl").whereEqualTo("companyId", companyId).get()
        val pRef = db.collection("${AppUtils.mPath}/pendingColl").whereEqualTo("companyId", companyId).get()
        val iHRef = db.collection("${AppUtils.mPath}/itemsHed").whereEqualTo("companyId", companyId).get()
        val iLRef = db.collection("${AppUtils.mPath}/itemsLin").whereEqualTo("companyId", companyId).get()
        val dRef = db.collection("${AppUtils.mPath}/salesColl").whereEqualTo("companyId", companyId).get()
        val srRef = db.collection("${AppUtils.mPath}/srNos").get()
//        val iSRef = db.collection("${AppUtils.mPath}/itemsSummary").whereEqualTo("companyId", companyId).get()
//        val iMRef = db.collection("${AppUtils.mPath}/imeiSummary").get()

        val arrTask = ArrayList<Task<out Any>>()
        arrTask.add(tRef)
        arrTask.add(sRef)
        arrTask.add(pRef)
        arrTask.add(iHRef)
        arrTask.add(iLRef)
        arrTask.add(dRef)
        arrTask.add(srRef)
//        arrTask.add(iSRef)
//        arrTask.add(iMRef)

        loaderDialog.show()
        Tasks.whenAll(arrTask).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                loaderDialog.dismiss()
                Toasty.error(this, "Error. ${task.exception!!.message.toString()}").show()
                return@addOnCompleteListener
            }

            val batches = ArrayList<WriteBatch>()
            batches.add(db.batch())
            batchItemCount = 2

            var batch = batches.last()
            //batch.delete(db.document("${AppUtils.mPath}/srNos/$companyId"))
            batch.set(db.document("${AppUtils.mPath}/itemsSum/imeiNos"), HashMap<String, Any>())

            removeDoc(tRef, batches, "transactions-$companyId")
            removeDoc(sRef, batches, "summaryColl")
            removeDoc(pRef, batches, "pendingColl")
            removeDoc(iHRef, batches, "itemsHed")
            removeDoc(iLRef, batches, "itemsLin")
            removeDoc(dRef, batches, "salesColl")

            srRef.result!!.documents.forEach { doc: DocumentSnapshot? ->
                var docId = doc!!.id
                if (!docId.startsWith("$companyId-")) return@forEach
                batch.delete(db.document("${AppUtils.mPath}/srNos/" + doc!!.id))
                if (batchItemCount >= 499) {
                    batches.add(db.batch())
                    batchItemCount = 0
                    batch = batches.last()
                } else batchItemCount += 1
            }
//            removeDoc(iSRef, batches, "itemsSummary")
//            removeDoc(iMRef, batches, "imeiSummary")

            batches.forEach { batch: WriteBatch ->
                batch.commit().addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        loaderDialog.dismiss()
                        Toasty.error(this, "Error. ${task.exception!!.message.toString()}").show()
                        return@addOnCompleteListener
                    }
                }
            }


            loaderDialog.dismiss()

            Toasty.success(this, "$name, Reset Complete. Restart App").show()
            finish()

        }


    }*/


    private fun fillItem(item: HashMap<String, Any> = HashMap()) {

        val name = MyUtils.hasMapString(item["name"])
        if (name == "") tvATitle.text = "Add New ${ssType.text}"
        else tvATitle.text = "Edit $name"


        companyDocId = MyUtils.hasMapString(item["docId"])
        formBuilder.setFormData(item)
        linearLayoutForm.visibility = View.VISIBLE
        linearLayoutBtn.visibility = View.GONE
        //if (item.size > 0) linearLayoutOther.visibility = View.VISIBLE
        //else ssType.selectIndex(0)

    }

   /* private fun saveCompany() {
        val cDoc = formBuilder.getFormData()
        val name = MyUtils.hasMapString(cDoc["name"]).toUpperCase()
        cDoc["name"] = name

        //if (!MyFuns.checkDuplicateName(this, AppUtils.mCompanyMap, companyDocId, name)) return

//        if(!MyUtils.isValidName(name)){
//            Toasty.error(this, "Invalid Name. Valid characters are A-Z a-z 0-9 . & _ -").show()
//            return
//        }

        var gstType = MyUtils.hasMapString(cDoc["gstType"])
        val gstinNo = MyUtils.hasMapString(cDoc["gstinNo"])
        if (gstType == "") gstType = "Unregistered"

        if(gstType.equals("Unregistered", true) && gstinNo != ""){
            Toasty.error(this, "GSTIN No specified for unregistered Company / location.").show()
            return
        }
        else if(!gstType.equals("Unregistered") && gstinNo == ""){
            Toasty.error(this, "GSTIN No Required.").show()
            return
        }

        val cMastDoc = HashMap<String, Any>()
        val cType = ssType.text
        cMastDoc["type"] = cType
        cMastDoc["name"] = cDoc["name"]!!

        if (cType.equals("Location")) {
            val company = sCompany.getItem()
            cMastDoc["companyId"] = company!!["docId"]!!
            cMastDoc["companyName"] = company!!["name"]!!
            cMastDoc["cName"] = company!!["name"]!!
        }

        val batch = db.batch()
        if (companyDocId.equals("")) companyDocId = db.collection("${AppUtils.mPath}/company").document().id

       // batch.set(db.document("${AppUtils.mPath}/company/$companyDocId"), cDoc)
       // batch.update(db.document("${AppUtils.mPath}/masters/company"), companyDocId, cMastDoc)

        loaderDialog.show()
        batch.commit().addOnCompleteListener { task ->
            loaderDialog.dismiss()
            if (!task.isSuccessful) {
                Toasty.error(this, "Error. ${task.exception!!.message.toString()}").show()
                return@addOnCompleteListener
            }

            //add to mcompanyMap
            //toast and exit from here
           // AppUtils.mCompanyMap[companyDocId] = cMastDoc
            val companyName = MyUtils.hasMapString(cDoc["name"])
            Toasty.success(this, "$companyName, Entry Successful.").show()

            val intent = Intent()
            setResult(CommonStatusCodes.SUCCESS, intent)
            finish()

        }

    }*/

}
