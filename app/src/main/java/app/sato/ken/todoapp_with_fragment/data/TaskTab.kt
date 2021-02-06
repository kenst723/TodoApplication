package app.sato.ken.todoapplication.Realm


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class TaskTab(
    @PrimaryKey open var id: String = UUID.randomUUID().toString(),
    open var content: String = "",
    open var createdAt: String = ""
) : RealmObject()