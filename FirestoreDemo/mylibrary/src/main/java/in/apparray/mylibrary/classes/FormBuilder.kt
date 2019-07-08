package `in`.apparray.mylibrary.classes

import `in`.apparray.mylibrary.R
import `in`.apparray.mylibrary.utils.MyUtils
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import android.view.LayoutInflater
import android.widget.*
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.LinkedHashMap
import kotlin.collections.ArrayList

/**
 * Created by dariopellegrini on 19/06/2017.
 */

class FormBuilder(internal var context: Context, internal var linearLayout: LinearLayout) {

    var formMap: LinkedHashMap<String, FormElement>
    var viewMap: LinkedHashMap<String, View>

    internal var selectedEditText: EditText? = null
    internal lateinit var selectedFormElement: FormElement

    private val validation = AwesomeValidation(ValidationStyle.BASIC)

    private lateinit var calendar: Calendar

    private val datePickerListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val sdf = SimpleDateFormat(selectedFormElement.getDateFormat())

        if (selectedEditText != null) {
            selectedEditText!!.setText(sdf.format(calendar.time))
            selectedFormElement.setValue(sdf.format(calendar.time))
        }
    }

    private val timePickerListener = TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
        calendar.set(Calendar.HOUR, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)

        val sdf = SimpleDateFormat(selectedFormElement.getTimeFormat())

        if (selectedEditText != null) {
            selectedEditText!!.setText(sdf.format(calendar.time))
            selectedFormElement.setValue(sdf.format(calendar.time))
        }
    }

    init {
        calendar = Calendar.getInstance()
        formMap = LinkedHashMap()
        viewMap = LinkedHashMap()
    }

    fun build1(formObjects: List<FormObject>) {
        for (formObject in formObjects) {
            if (formObject is FormHeader) {
                addToLinearLayout(buildHeader(formObject), linearLayout, formObject.params)
            } else if (formObject is FormElement) {
                val tag = formObject.tagOrToString
                formMap[tag] = formObject
                addToLinearLayout(buildElement(formObject)!!, linearLayout, formObject.getParams())
            } else if (formObject is FormElementArray) {

            } else if (formObject is FormButton) {
                addToLinearLayout(buildButton(formObject), linearLayout, formObject.params)
            }
        }
    }

    fun build(formObjects: List<FormObject>, llLayout: LinearLayout = linearLayout) {
        for (formObject in formObjects) {
            if (formObject is FormHeader) {
                addToLinearLayout(buildHeader(formObject), llLayout, formObject.params)
            } else if (formObject is FormElement) {
                val tag = formObject.tagOrToString
                formMap[tag] = formObject
                addToLinearLayout(buildElement(formObject)!!, llLayout, formObject.getParams())
            } else if (formObject is FormElementArray) {
                //create new horizontal linearlayout
                //add controls to layout with build function call
                //add layout to main linearlayout

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
                )

                val layout2 = LinearLayout(context)
                layout2.layoutParams = layoutParams
                layout2.orientation = LinearLayout.HORIZONTAL

                val fObj = formObject.formElements
                build(fObj, layout2)
                addToLinearLayout(layout2, llLayout, layoutParams)

            } else if (formObject is FormButton) {
                addToLinearLayout(buildButton(formObject), llLayout, formObject.params)
            }
        }
    }

    // Builders
    private fun buildHeader(header: FormHeader): View {
        val headerTextView = TextView(context, null, android.R.attr.listSeparatorTextViewStyle)
        headerTextView.text = header.title
        return headerTextView
    }

    private fun buildElement(formElement: FormElement): View? {
        val type = formElement.getType()

        when (type) {
            FormElement.Type.TITLE -> {
                val textView = TextView(context)
                textView.text = formElement.getHint()
                textView.setTextAppearance(context, R.style.formTitle)
                viewMap[formElement.tagOrToString] = textView
                return textView
            }
            FormElement.Type.GROUPTITLE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.form_grouptitle, null)
                val textView: TextView = view.findViewById(R.id.textView)
                textView.text = formElement.getHint()
                viewMap[formElement.tagOrToString] = textView
                return textView
            }
            FormElement.Type.CHECKBOX -> {
                val view = LayoutInflater.from(context).inflate(R.layout.form_checkbox, null)
                val checkBox: CheckBox = view.findViewById(R.id.checkBox)
                checkBox.text = formElement.getHint()
                viewMap[formElement.tagOrToString] = checkBox

                try {
                    checkBox.isChecked = formElement.getValue() as Boolean
                } catch (e: Exception) {
                }
                return checkBox
            }
            FormElement.Type.SPINNER, FormElement.Type.SPINNER_MULTIPLE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.form_spinner, null)
                val selectSpinner: SelectSpinner = view.findViewById(R.id.selectSpinner)

                val multipleSelection = type == FormElement.Type.SPINNER_MULTIPLE
                //val selectSpinner = SelectSpinner(context)
                //val selectSpinner = SelectSpinner(ContextThemeWrapper(context, R.style.Base_Widget_AppCompat_Spinner_Underlined))

                selectSpinner.setTitle(formElement.getHint()!!, multipleSelection)
                val options = formElement.getSSOptions()

                val stringItem = options == null
                val selText = formElement.getSSSelText()
                if (stringItem) {
                    val options = formElement.getOptions()
                    selectSpinner.setArrayItems(options as ArrayList<String>, selText, formElement.getSpinnerListener())
                } else {
                    val nameKey = formElement.getSSDisplayClm()
                    selectSpinner.setItems(options!!, nameKey, selText, formElement.getSpinnerListener())
                }

                viewMap[formElement.tagOrToString] = selectSpinner

                if (formElement.getRequired()) MyUtils.SelectSpinnerValidation(validation, selectSpinner, context)
                return selectSpinner
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.form_edittext, null)
                val editText: EditText = view.findViewById(R.id.editText)

                editText.isEnabled = formElement.getEnabled()
                editText.hint = formElement.getHint()
                editText.setText(formElement.getValue())
                viewMap[formElement.tagOrToString] = editText

                if (formElement.getRequired()) {
                    when (type) {
                        FormElement.Type.SPINNER, FormElement.Type.SPINNER_MULTIPLE -> {
                        }
                        else -> validation.addValidation(editText, RegexTemplate.NOT_EMPTY, formElement.errorMessageOrDefault)
                    }
                }

                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

                    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                        formElement.setValue(charSequence.toString())
                    }

                    override fun afterTextChanged(editable: Editable) {}
                })
                when (type) {
                    FormElement.Type.TEXT -> {
                        //editText.inputType = InputType.TYPE_CLASS_TEXT
                        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                        return editText //return textInputLayout
                    }
                    FormElement.Type.TEXT_CAP -> {
                        //editText.inputType = InputType.TYPE_CLASS_TEXT
                        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                        return editText //return textInputLayout
                    }
                    FormElement.Type.TEXTVIEW -> {
                        editText.isFocusable = false
                        editText.isClickable = false
                        editText.gravity = Gravity.LEFT or Gravity.TOP
                        editText.inputType = InputType.TYPE_CLASS_TEXT
                        return editText
                    }
                    FormElement.Type.EMAIL -> {
                        editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        return editText //return textInputLayout
                    }
                    FormElement.Type.PHONE -> {
                        editText.inputType = InputType.TYPE_CLASS_PHONE
                        return editText //return textInputLayout
                    }
                    FormElement.Type.PASSWORD -> {
                        editText.inputType = InputType.TYPE_CLASS_TEXT
                        editText.transformationMethod = PasswordTransformationMethod.getInstance()
                        return editText //return textInputLayout
                    }
                    FormElement.Type.NUMBER, FormElement.Type.NUMBERTEXT -> {
                        editText.inputType = InputType.TYPE_CLASS_NUMBER
                        return editText //return textInputLayout
                    }
                    FormElement.Type.NUMBERDECIMAL -> {
                        editText.inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL
                        return editText //return textInputLayout
                    }
                    FormElement.Type.URL -> {
                        editText.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
                        return editText //return textInputLayout
                    }
                    FormElement.Type.ZIP -> {
                        editText.inputType = InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
                        return editText //return textInputLayout
                    }
                    FormElement.Type.DATE -> {
                        editText.isFocusable = false
                        editText.isClickable = true
                        editText.inputType = InputType.TYPE_CLASS_TEXT
                        editText.setOnClickListener {
                            selectedFormElement = formElement
                            pickDate(editText)
                        }
                        return editText //return textInputLayout
                    }
                    FormElement.Type.TIME -> {
                        editText.isFocusable = false
                        editText.isClickable = true
                        editText.inputType = InputType.TYPE_CLASS_TEXT
                        editText.setOnClickListener {
                            selectedFormElement = formElement
                            pickTime(editText)
                        }
                        return editText //return textInputLayout
                    }
                    FormElement.Type.SELECTION -> {
                        editText.isFocusable = false
                        editText.isClickable = true
                        editText.inputType = InputType.TYPE_CLASS_TEXT
                        editText.setOnClickListener { pickDialog(editText, formElement) }
                        return editText //return textInputLayout
                    }
                    FormElement.Type.MULTIPLE_SELECTION -> {
                        editText.isFocusable = false
                        editText.isClickable = true
                        editText.inputType = InputType.TYPE_CLASS_TEXT
                        editText.setOnClickListener { pickMultipleDialog(editText, formElement) }
                        return editText //return textInputLayout
                    }
                    else -> return null
                }
            }
        }
    }


    private fun buildButton(formButton: FormButton): View {
        val button = Button(context)
        button.text = formButton.title

        if (formButton.backgroundColor != null) {
            button.setBackgroundColor(formButton.backgroundColor!!)
        }

        if (formButton.getTextColor() != null) {
            button.setTextColor(formButton.getTextColor()!!)
        }

        if (formButton.runnable != null) {
            button.setOnClickListener { formButton.runnable.run() }
        }

        return button
    }

    private fun addToLinearLayout(view: View, linearLayout: LinearLayout, params: LinearLayout.LayoutParams?) {

        if (view.getParent() != null) (view.getParent() as ViewGroup).removeView(view) // <- fix

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        )

        layoutParams.setMargins(8, 8, 8, 8)
        view.layoutParams = params ?: layoutParams

        linearLayout.addView(view)
    }

    private fun addViewToView(parent: ViewGroup, child: View) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        child.layoutParams = layoutParams

        parent.addView(child)
    }

    // Date picker
    fun pickDate(et: EditText?) {
        if (et != null) {
            selectedEditText = et
            DatePickerDialog(
                context, datePickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // Time picker
    fun pickTime(et: EditText?) {
        if (et != null) {
            selectedEditText = et
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(context, timePickerListener, hour, minute, true)//Yes 24 hour time
            mTimePicker.show()
        }
    }

    // Choice
    private fun pickMultipleDialog(selectedEditText: EditText, selectedFormElement: FormElement) {
        val selectedElements = ArrayList(selectedFormElement.getOptionsSelected()!!)
        this.selectedEditText = selectedEditText
        val builder = AlertDialog.Builder(context)
        builder.setTitle("")

        builder.setMultiChoiceItems(
            selectedFormElement.getOptions()!!.toTypedArray<CharSequence>(), selectedFormElement.checkedValues
        ) { dialog, which, isChecked ->
            if (isChecked) {
                selectedElements.add(selectedFormElement.getOptions()!![which])
            } else {
                selectedElements.remove(selectedFormElement.getOptions()!![which])
            }
        }
        builder.setPositiveButton("OK") { dialogInterface, i ->
            selectedFormElement.setOptionsSelected(selectedElements)
            selectedEditText.setText(selectedFormElement.getOptionsSelected()!!.toString())
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.show()
    }

    private fun pickDialog(selectedEditText: EditText, selectedFormElement: FormElement) {
        val selectedElements = ArrayList(selectedFormElement.getOptionsSelected()!!)
        this.selectedEditText = selectedEditText
        val builder = AlertDialog.Builder(context)
        builder.setTitle("")

        builder.setSingleChoiceItems(
            selectedFormElement.getOptions()!!.toTypedArray<CharSequence>(), selectedFormElement.checkedValue, null
        )
        builder.setPositiveButton("OK") { dialogInterface, i ->
            dialogInterface.dismiss()
            val selectedPosition = (dialogInterface as AlertDialog).listView.checkedItemPosition
            selectedElements.add(selectedFormElement.getOptions()!![selectedPosition])
            selectedFormElement.setOptionsSelected(selectedElements)
            selectedEditText.setText(selectedFormElement.getOptionsSelected()!!.toString())
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.show()
    }

    // Validation
    fun validate(): Boolean {
        return validation.validate()
    }

    fun getFormData(): LinkedHashMap<String, Any> {
        val hashMap = LinkedHashMap<String, Any>()

        viewMap.forEach { entry ->
            try {
                val view = entry.value

                if (view.visibility == View.GONE) return@forEach
                try {
                    val v = view.parent as View
                    if (v.visibility == View.GONE) return@forEach
                } catch (e: Exception) {
                }


                val formElement = formMap.get(entry.key)!!
                val type = formElement.getType()

                when (view) {
                    is SelectSpinner -> {
                        val selectSpinner = view as SelectSpinner
                        hashMap[entry.key + "Text"] = selectSpinner.text
                        if (formElement.ssFlds == null) hashMap[entry.key] = selectSpinner.getValue()!!
                        else {
                            val data = selectSpinner.getValue()!! //as HashMap<String, Any>
                            when (data) {
                                is HashMap<*, *> -> {
                                    formElement.ssFlds!!.forEach { entry ->
                                        hashMap[entry.value] = MyUtils.hasMapString(data[entry.key])
                                    }
                                }
                                is ArrayList<*> -> {
                                    val valList = ArrayList<Any>()
                                    val valMap = HashMap<String, Any>()
                                    val fKey = formElement.ssFlds!!.keys.first()
                                    val fValue = formElement.ssFlds!!.get(fKey)//.equals("array")

                                    data.forEach { item: Any? ->
                                        try {
                                            val edata = item as HashMap<String, Any>
                                            when (fValue) {
                                                "array" -> {
                                                    valList.add(edata[fKey]!!)
                                                }
                                                "hashmap" -> {
                                                    valMap[MyUtils.hasMapString(edata[fKey])] = edata[fKey]!!
                                                }
                                                else -> {
                                                    val emap = HashMap<String, Any>()
                                                    formElement.ssFlds!!.forEach { entry ->
                                                        emap[entry.value] = MyUtils.hasMapString(edata[entry.key])
                                                    }
                                                    valList.add(emap)
                                                }
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                    if (fValue.equals("hashmap")) hashMap[entry.key] = valMap
                                    else hashMap[entry.key] = valList
                                }
                            }

                        }

                    }
                    is CheckBox -> {
                        hashMap[entry.key] = formElement.checkedValue
                    }
                    is TextInputEditText, is EditText -> {
                        when (type) {
                            FormElement.Type.NUMBERTEXT -> {
                                hashMap[entry.key] = MyUtils.hasMapString(formElement.getValue())
                            }
                            FormElement.Type.NUMBER -> {
                                hashMap[entry.key] = MyUtils.hasMapLong(formElement.getValue())
                            }
                            FormElement.Type.NUMBERDECIMAL -> {
                                hashMap[entry.key] = MyUtils.hasMapDouble(formElement.getValue())
                            }
                            FormElement.Type.DATE, FormElement.Type.TIME -> {
                                hashMap[entry.key] = MyUtils.hasMapString(formElement.getValue())
                                hashMap[entry.key + "Milis"] = MyUtils.hasMapLong(formElement.getValue())
                            }
                            else -> {
                                hashMap[entry.key] = MyUtils.hasMapString(formElement.getValue())
                            }
                        }
                    }
                    else -> hashMap[entry.key] = ""
                }

            } catch (e: Exception) {
                hashMap[entry.key] = "error"
            }
        }

        //extra keys and calculattion here but how?

        return hashMap
    }

    fun setFormData(item: HashMap<String, Any> = HashMap()) {
        viewMap.forEach { entry ->
            val view = entry.value
            var value: Any = ""
            if (item.containsKey("value")) value = item["value"]!! //MyUtils.hasMapString(item["value"])
            else if (item.containsKey(entry.key)) value = item[entry.key]!! // MyUtils.hasMapString(item[entry.key])

            when (view) {
                is CheckBox -> {
                    val checkBox = view as CheckBox
                    try {
                        checkBox.isChecked = value as Boolean
                    } catch (e: Exception) {
                    }
                    //editText.setText(MyUtils.hasMapString(value))
                }
                is EditText -> {
                    val editText = view as EditText
                    editText.setText(MyUtils.hasMapString(value))
                }
                is SelectSpinner -> {
                    val selectSpinner = view as SelectSpinner
                    val formElement = formMap.get(entry.key)!!

                    val valueClm = formElement.getSSValueClm()
                    if (item.containsKey(valueClm)) value = item[valueClm]!!

                    if (formElement.getType() == FormElement.Type.SPINNER_MULTIPLE) {
                        when (value) {
                            is HashMap<*, *> -> {
                                val selM = ArrayList<String>()
                                value.forEach { entry ->
                                    selM.add(MyUtils.hasMapString(entry.key))
                                }
                                selectSpinner.selectValue(selM, valueClm)
                            }
                            is ArrayList<*> -> {
                                val selM = ArrayList<String>()
                                value.forEach { any: Any? ->
                                    when (any) {
                                        is HashMap<*, *> -> {
                                            if (any.size == 1) selM.add(MyUtils.hasMapString(any.get(any.keys.first())))
                                            else selM.add(MyUtils.hasMapString(any[valueClm]))
                                        }
                                        else -> selM.add(MyUtils.hasMapString(any))
                                    }
                                }
                                selectSpinner.selectValue(selM, valueClm)
                            }
                            else -> {
                                selectSpinner.selectItem(MyUtils.hasMapString(value))
                            }
                        }
                    } else {
                        selectSpinner.selectValue(MyUtils.hasMapString(value), valueClm)
                    }

                }
                else -> {
                }
            }
        }
    }


    fun setFormData2(item: HashMap<String, Any> = HashMap()) {
        viewMap.forEach { entry ->
            val view = entry.value
            var value = ""
            if (item.containsKey("value")) value = MyUtils.hasMapString(item["value"])
            else value = MyUtils.hasMapString(item[entry.key])

            when (view) {
                is EditText -> {
                    val editText = view as EditText
                    editText.setText(value)
                }
                is SelectSpinner -> {
                    val selectSpinner = view as SelectSpinner
                    val formElement = formMap.get(entry.key)!!

                    val valueClm = formElement.getSSValueClm()

                    if (formElement.getType() == FormElement.Type.SPINNER_MULTIPLE) {


                        //selectItems
                    } else {
                        val value = MyUtils.hasMapString(item[valueClm])
                        selectSpinner.selectValue(value, valueClm)
                    }

                }
                else -> {
                }
            }
        }
    }

    fun setFormData1(item: HashMap<String, Any> = HashMap()) {
        viewMap.forEach { entry ->
            val view = entry.value
            var value = MyUtils.hasMapString(item[entry.key])

            when (view) {
                is EditText -> {
                    val editText = view as EditText
                    editText.setText(value)
                }
                is SelectSpinner -> {
                    value = MyUtils.hasMapString(item[entry.key + "Text"])
                    val selectSpinner = view as SelectSpinner
                    selectSpinner.selectItem(value)
                }
                else -> {
                }
            }
        }
    }
}
