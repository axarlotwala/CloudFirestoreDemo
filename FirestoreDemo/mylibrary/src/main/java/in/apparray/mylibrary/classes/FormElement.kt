package `in`.apparray.mylibrary.classes

import android.widget.LinearLayout

import java.util.ArrayList
import java.util.HashSet

/**
 * Created by dariopellegrini on 19/06/2017.
 */

//https://github.com/dariopellegrini/FormBuilder

class FormElement : FormObject() {

    private var tag: String? = null
    private var type: Type? = null
    private var title: String? = null
    private var value: String? = null
    private var hint: String? = null
    private var options: List<String>? = null // list of options for single and multi picker
    private var optionsSelected: List<String>? = null // list of selected options for single and multi picker
    private var isRequired: Boolean = false
    private var isEnabled: Boolean = false
    private var dateFormat: String? = null
    private var timeFormat: String? = null
    private var dateTimeFormat: String? = null
    private var params: LinearLayout.LayoutParams? = null
    private var spinnerListener: SpinnerListener? = null

    private var ssOptions: List<HashMap<String, Any>>? = null // list of options for single and multi picker
    private var ssDisplayClm: String = ""
    private var ssValueClm = ""
    var ssFlds: HashMap<String, String>? = null
    private var ssSelText: String = ""

    //private FormValidation formValidation;
    private var errorMessage: String? = null

    val tagOrToString: String
        get() = if (tag == null) toString() else tag!!

    val checkedValues: BooleanArray
        get() {
            val booleans = BooleanArray(options!!.size)
            for (i in options!!.indices) {
                val element = options!![i]
                booleans[i] = optionsSelected!!.contains(element)
            }
            return booleans
        }

    val checkedValue: Int
        get() {
            if (optionsSelected!!.size > 0) {
                val element = optionsSelected!![0]
                if (options!!.contains(element)) {
                    return options!!.indexOf(element)
                }
            }
            return 0
        }

    val errorMessageOrDefault: String
        get() = if (errorMessage == null) "Error" else errorMessage!!

    enum class Type {
        TITLE, GROUPTITLE, TEXT, TEXT_CAP,  TEXTVIEW, EMAIL, PASSWORD, PHONE, NUMBER,NUMBERTEXT, NUMBERDECIMAL, URL, SPINNER, SPINNER_MULTIPLE, ZIP,
        SELECTION, MULTIPLE_SELECTION, DATE, TIME, CHECKBOX
    }

    init {
        isEnabled = true
        isRequired = false
        dateFormat = "dd-MM-yyyy"
        timeFormat = "HH:mm:ss"
        dateTimeFormat = "dd-MM-yyyy HH:mm:ss"
        options = ArrayList()
        optionsSelected = ArrayList()
        //ssOptions = ArrayList()
    }

    fun getTag(): String? {
        return tag
    }

    fun setTag(tag: String): FormElement {
        this.tag = tag
        return this
    }

    fun getType(): Type? {
        return type
    }

    fun setType(type: Type): FormElement {
        this.type = type
        return this
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String): FormElement {
        this.title = title
        return this
    }

    fun getValue(): String? {
        return value
    }

    fun setValue(value: String): FormElement {
        this.value = value
        return this
    }

    fun getHint(): String? {
        return hint
    }

    fun setHint(hint: String): FormElement {
        this.hint = hint
        return this
    }

    fun getOptions(): List<String>? {
        return options
    }

    fun setOptions(options: List<String>): FormElement {
        //        this.options = options;
        this.options = ArrayList(HashSet(options)) // Prevent duplicates (distinct)
        return this
    }

    fun getOptionsSelected(): List<String>? {
        return optionsSelected
    }

    fun setOptionsSelected(optionsSelected: List<String>): FormElement {
        this.optionsSelected = optionsSelected
        return this
    }

    fun getSSOptions(): List<HashMap<String, Any>>? {
        return ssOptions
    }

    fun getSSDisplayClm(): String {
        return ssDisplayClm
    }

    fun getSSValueClm(): String {
        return ssValueClm
    }

    fun getSSSelText(): String {
        return ssSelText
    }

    fun setSSOptions(options: List<HashMap<String, Any>>, dispalyClm: String = "key", valueClm: String = "", ssFlds: HashMap<String, String>? = null, selText: String = ""): FormElement {
        var valueClm = valueClm
        if (valueClm.equals("")) valueClm = dispalyClm
        this.ssOptions = options // ArrayList(HashSet(options)) // Prevent duplicates (distinct)
        this.ssDisplayClm = dispalyClm
        this.ssValueClm = valueClm
        this.ssSelText = selText
        this.ssFlds = ssFlds
        return this
    }

    fun getRequired(): Boolean {
        return isRequired
    }

    fun setRequired(required: Boolean, errorMessage: String = "Required"): FormElement {
        isRequired = required
        this.errorMessage = errorMessage
        return this
    }

    fun getEnabled(): Boolean {
        return isEnabled
    }

    fun setEnabled(enabled: Boolean): FormElement {
        isEnabled = enabled
        return this
    }

    fun getDateFormat(): String? {
        return dateFormat
    }

    fun setDateFormat(dateFormat: String): FormElement {
        this.dateFormat = dateFormat
        return this
    }

    fun getTimeFormat(): String? {
        return timeFormat
    }

    fun setTimeFormat(timeFormat: String): FormElement {
        this.timeFormat = timeFormat
        return this
    }

    fun getDateTimeFormat(): String? {
        return dateTimeFormat
    }

    fun setDateTimeFormat(dateTimeFormat: String): FormElement {
        this.dateTimeFormat = dateTimeFormat
        return this
    }

    fun getParams(): LinearLayout.LayoutParams? {
        return params
    }

    fun setParams(params: LinearLayout.LayoutParams): FormElement {
        this.params = params
        return this
    }

    //    public FormValidation getFormValidation() {
    //        return formValidation;
    //    }
    //
    //    public FormElement setFormValidation(FormValidation formValidation) {
    //        this.formValidation = formValidation;
    //        return this;
    //    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String): FormElement {
        this.errorMessage = errorMessage
        return this
    }

    fun setSpinnerListener(spinnerListener: SpinnerListener): FormElement {
        this.spinnerListener = spinnerListener
        return this
    }

    fun getSpinnerListener(): SpinnerListener? {
        return spinnerListener
    }


}
