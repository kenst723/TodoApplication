package app.sato.ken.todoapplication.Realm


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class Task(
    @PrimaryKey open var id: String = UUID.randomUUID().toString(),
    open var content: String = "",
    open var imageRed: Boolean = false,
    open var imageBlue: Boolean = false,
    open var imageYellow: Boolean = false,
    open var imageNon: Boolean = true,
    open var date: String = "なし",
    open var selected: Boolean = false,
    open var fromPos: Int = 1,
    open var createdAt: String = ""
) : RealmObject()