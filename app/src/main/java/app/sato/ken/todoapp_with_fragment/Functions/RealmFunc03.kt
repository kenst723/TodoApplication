package app.sato.ken.todoapp_with_fragment.Functions

import app.sato.ken.todoapplication.Realm.Task
import app.sato.ken.todoapplication.Realm.Task02
import app.sato.ken.todoapplication.Realm.Task03
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*

class RealmFunc03 {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    fun delete(pos: Int) {
        realm
            .executeTransaction {
                val task03 = it
                    .where(Task03::class.java)
                    .findAll()
                task03.deleteFromRealm(pos)
            }
    }


    /**
     * realmから読み出し
     */
    fun readAll(): RealmResults<Task03> {
        return realm.where(Task03::class.java).findAll().sort("createdAt", Sort.ASCENDING)
    }

    /**
     * アイテムの作成とrealmへ登録
     */
    fun create(content: String, checked: Boolean,red: Boolean,yellow: Boolean, blue: Boolean, non: Boolean, fromPos: Int) {
        realm.executeTransaction {
            val task = it.createObject(Task03::class.java, UUID.randomUUID().toString())
            task.content = content
            task.selected = checked
            task.imageRed = red
            task.imageBlue = blue
            task.imageYellow = yellow
            task.imageNon = non
            task.fromPos = fromPos
        }
    }

}