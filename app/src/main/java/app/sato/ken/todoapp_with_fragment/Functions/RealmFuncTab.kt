package app.sato.ken.todoapp_with_fragment.Functions

import app.sato.ken.todoapplication.Realm.Task
import app.sato.ken.todoapplication.Realm.Task02
import app.sato.ken.todoapplication.Realm.TaskTab
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*

class RealmFuncTab {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    fun delete(pos: Int) {
        realm
            .executeTransaction {
                val task2 = it
                    .where(TaskTab::class.java)
                    .findAll()
                task2.deleteFromRealm(pos)
            }
    }


    /**
     * realmから読み出し
     */
    fun readAll(): RealmResults<TaskTab> {
        return realm.where(TaskTab::class.java).findAll().sort("createdAt", Sort.ASCENDING)
    }

    fun create(context: String){
       realm.executeTransaction {
           val task = it.createObject(TaskTab::class.java, UUID.randomUUID().toString())
           task.content = context
       }
    }

}