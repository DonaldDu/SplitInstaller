package com.donalddu.splitinstaller

import com.dhy.easyreflect.field
import java.lang.reflect.Field


internal fun Any.getFieldValue(name: String): Any? {
    return javaClass.field(name).get(this)
}

class FieldDelegate<T>(private val owner: Any, fieldName: String, secondaryFieldName: String? = null) {
    private val f: Field

    init {
        f = try {
            owner.javaClass.field(fieldName)
        } catch (e: Exception) {
            owner.javaClass.field(secondaryFieldName!!)
        }
    }

    @Suppress("UNCHECKED_CAST")
    var value: T?
        get() {
            return f.get(owner) as? T
        }
        set(value) {
            f.set(owner, value)
        }
}