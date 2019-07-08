package `in`.apparray.biztra.classes

public interface MasterListener {
    fun onMasterEntry(keyDoc: ArrayList<HashMap<String, Any>>, entryKey: String, entryValue: String)
}