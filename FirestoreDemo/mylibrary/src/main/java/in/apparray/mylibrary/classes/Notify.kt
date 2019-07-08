package `in`.apparray.mylibrary.classes


class Notify {

    public lateinit var registrationTokens: List<String>
    //lateinit var message: String
    lateinit var data: HashMap<String, Any>
    lateinit var notification: HashMap<String, Any>

    constructor() {

    }

    constructor(registrationTokens: List<String>, title: String, body: String, data: HashMap<String, Any>? = null) {
        this.registrationTokens = registrationTokens

        notification = HashMap()
        notification.put("title", title)
        notification.put("body", body)

        if (data != null)
            this.data = data
        else
            this.data = HashMap()
    }

    constructor(title: String, body: String, data: HashMap<String, Any>? = null) {
        //this.registrationTokens = registrationTokens

        notification = HashMap()
        notification.put("title", title)
        notification.put("body", body)

        if (data != null)
            this.data = data
        else
            this.data = HashMap()
    }



}
