package app.sato.ken.todoapp_with_fragment.Fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.sato.ken.todoapp_with_fragment.Adapter.TaskAdapter
import app.sato.ken.todoapp_with_fragment.Adapter.TaskAdapter02
import app.sato.ken.todoapp_with_fragment.Functions.RealmFunc
import app.sato.ken.todoapp_with_fragment.Functions.RealmFunc02
import app.sato.ken.todoapp_with_fragment.R
import app.sato.ken.todoapplication.Realm.Task02
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_tab01.*
import kotlinx.android.synthetic.main.fragment_tab02.*

class Tab02Fragment: Fragment(){

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var itemTouchHelper : ItemTouchHelper
    private var myDataSet = mutableListOf<Task02>()
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val realmFunc = RealmFunc02()

    var getItemText = ""
    var itemTouchText = ""
    var swipedText = ""
    var dataText = ""
    var red = false
    var blue = false
    var yellow = false
    var non = true
    private var shown = 0

    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val args1 = arguments?.getString("UNDO_TEXT")
        val args2 = arguments?.getInt("UNDO_ID")


        if (args1 != null && args2 != null && args2 == 2) {
            realmFunc.create(
                args1, false,
                red = false,
                yellow = false,
                blue = false,
                non = false,
                fromPos = args2
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab02,container,false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomAppBar02 = view.findViewById<BottomAppBar>(R.id.bottomAppBar02)
        val fab02 = view.findViewById<FloatingActionButton>(R.id.fab02)
        val content02 = view.findViewById<RecyclerView>(R.id.content02)
        val taskList = realmFunc.readAll()
        itemTouchHelper = ItemTouchHelper(getRecyclerViewSimpleCallBack())

        val viewStub02 = view.findViewById<ViewStub>(R.id.viewStub02)
        itemTouchHelper.attachToRecyclerView(content02)
        viewAdapter = TaskAdapter02(
            requireContext(),
            object :
                TaskAdapter02.OnItemClickListener {
                override fun onItemClick(
                    item: Task02,
                    position: Int,
                    text: String
                ) {
                    Log.d("toast", "onClicked")
                    getItemText = text
                }

                override fun onItemTouch(text: String, date: String) {
                    Log.d("toast", "on")
                    itemTouchText = text
                    swipedText = date
                }

                override fun size(count: Int) {

                    if (shown == 0){
                        viewStub02.visibility = View.VISIBLE
                    }else{
                        viewStub02.visibility = View.INVISIBLE
                    }
                    shown = count
                    if (count == 0) {
                        toolbar02.title = "タスクが登録されていません"
                        toolbar02.setTitleTextColor(
                            Color.rgb(
                                75,
                                75,
                                75
                            )
                        )
                    } else {
                        toolbar02.title = "未完了のタスクは$count 件です"
                        toolbar02.setTitleTextColor(
                            Color.rgb(
                                75,
                                75,
                                75
                            )
                        )
                    }
                }

                override fun checked(text: String, fromPos: Int) {


                    Log.d("checked", "interface is runnning: $text")
                    // Bundleインスタンスを作成
                    val bundle = Bundle()
                    bundle.putString("BUNDLE_KEY_TITLE2", text)
                    bundle.putInt("BUNDLE_KEY_INT2", fromPos)
                    val fragment =
                        Tab03Fragment()
                    fragment.arguments = bundle
                    // 遷移処理
                    parentFragmentManager.beginTransaction()
                        .add(R.id.a, fragment)
                        .commit()
                }
            }, taskList, true
        )


        bottomAppBar02.setOnMenuItemClickListener { it ->
            when (it.itemId) {

                R.id.trash -> {
                    Log.d("ischecked", "クリック検知")
                    if (    myDataSet
                            .isNotEmpty()
                        || taskList
                            .isNotEmpty()
                    ) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("すべての項目を削除しますか?")
                            .setMessage("(この操作は取り消せません)")
                            .setPositiveButton("YES") { dialog, which ->
                                realm
                                    .executeTransaction {
                                        val task2 = it
                                            .where(Task02::class.java)
                                            .findAll()
                                        task2.deleteAllFromRealm()
                                    }
                            }
                            .setNegativeButton("No") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }
        fab02.setOnClickListener {
            showAddBottomSheetDialog()
        }
        content02.setHasFixedSize(true)
        content02.layoutManager = LinearLayoutManager(requireContext())
        content02.adapter = viewAdapter
    }

    private fun getRecyclerViewSimpleCallBack() =
        // 引数で、上下のドラッグ、および左方向のスワイプを有効にしている。
        object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {


            // ドラッグしたときに呼ばれる
            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            // スワイプしたとき
            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {

                /**
                 * @see RealmFunc.delete
                 * undo機能付きSnackBar(通知)
                 */
                realmFunc.delete(p0.adapterPosition)
                Snackbar.make(
                    bottomAppBar02,
                    "項目を削除しました",
                    Snackbar.LENGTH_SHORT
                ).setAction("元に戻す"){
                    realmFunc.create(itemTouchText,false,red,yellow, blue, non,2)
                }.show()
            }
        }

    private fun showAddBottomSheetDialog(){

        val view = layoutInflater.inflate(R.layout.add_dialog_today,null)
        val editText = view.findViewById<EditText>(
            R.id.editText
        )
        val priorityButton = view.findViewById<Button>(
            R.id.priority
        )
        val priorityTextView = view.findViewById<TextView>(
            R.id.priorityTextView
        )
        val addFab = view.findViewById<FloatingActionButton>(
            R.id.floatingActionButton
        )
        editText.requestFocus()


        blue = false
        red = false
        yellow = false
        non = true
        addFab.setOnClickListener {
            if (editText.text.toString() != "" || editText.text.toString().isNotEmpty()){
                realmFunc.create(editText.text.toString(),
                    checked = false,
                    red = red,
                    yellow = yellow,
                    blue = blue,
                    non = non,
                    fromPos = 2)
            }else{
                Toast.makeText(
                    requireContext(),
                    "内容を入力してください",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        priorityButton.setOnClickListener {
            val viewP = layoutInflater.inflate(R.layout.priority, null)
            val p1 = viewP.findViewById<Button>(R.id.p1)
            val p2 = viewP.findViewById<Button>(R.id.p2)
            val p3 = viewP.findViewById<Button>(R.id.p3)
            val p4 = viewP.findViewById<Button>(R.id.p4)
            val dialog =
                BottomSheetDialog(
                    requireContext(),
                    R.style.AppTheme_ShareDialog
                )



            p1.setOnClickListener {
                priorityTextView.text = "マーカー: 青"
                blue = true
                red = false
                yellow = false
                non = false
                dialog.dismiss()
            }
            p2.setOnClickListener {
                priorityTextView.text = "マーカー: 黄"
                yellow  = true
                red = false
                blue = false
                non = false
                dialog.dismiss()
            }
            p3.setOnClickListener {
                priorityTextView.text = "マーカー: 赤"
                red = true
                blue = false
                yellow = false
                non = false
                dialog.dismiss()
            }
            p4.setOnClickListener {
                priorityTextView.text = "マーカー: なし"
                dialog.dismiss()
                non = true
                yellow = false
                blue = false
                red = false
            }



            /**
             * ビュー設定
             */
            dialog.setContentView(viewP)

            /**
             * ダイヤログ表示
             */
            dialog.show()
        }

        val dialog = BottomSheetDialog(
            requireContext(),
            R.style.AppTheme_ShareDialog
        )
        dialog.setContentView(view)
        dialog.show()


    }

    /**
     * lifecycleの終了時
     * relmを終了する
     */
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}