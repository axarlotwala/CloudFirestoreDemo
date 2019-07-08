package `in`.apparray.mylibrary.classes

import java.util.ArrayList

/**
 * Created by dariopellegrini on 19/06/2017.
 */

class FormElementArray : FormObject {
    lateinit var formElements: ArrayList<FormObject>

    constructor() {}

    constructor(formElements: ArrayList<FormObject>) {
        this.formElements = formElements
    }



}
