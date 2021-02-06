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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.sato.ken.todoapp_with_fragment.Adapter.TaskAdapter
import app.sato.ken.todoapp_with_fragment.Adapter.TaskAdapter03
import app.sato.ken.todoapp_with_fragment.Functions.GetDateFunc
import app.sato.ken.todoapp_with_fragment.Functions.RealmFunc
import app.sato.ken.todoapp_with_fragment.Functions.RealmFunc03
import app.sato.ken.todoapp_with_fragment.R
import app.sato.ken.todoapplication.Realm.Task03
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_tab03.*

class Tab03Fragment: Fragment(){

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var itemTouchHelper : ItemTouchHelper
    private var myDataSet = mutableListOf<Task03>()
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val getDateFunc =
        GetDateFunc()
    val realmFunc = RealmFunc03()

    var getItemText = ""
    var itemTouchText = ""
    var swipedText = ""
    var dataText = ""
    var red = false
    var blue = false
    var yellow = false
    var non = true

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab03,container,false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val args = arguments?.getString("BUNDLE_KEY_TITLE")
        val args1 = arguments?.getString("BUNDLE_KEY_TITLE2")
        val args4 = arguments?.getString("BUNDLE_KEY_TITLE4")
        val args2 = arguments?.getInt("BUNDLE_KEY_INT")
        val args3 = arguments?.getInt("BUNDLE_KEY_INT2")
        val args5 = arguments?.getInt("BUNDLE_KEY_INT4")

        if (args4 != null && args5 != null){
            realmFunc.create(args4,
                checked = false,
                red = false,
                yellow = false,
                blue = false,
                non = true,
                fromPos = args5
            )
        }

        if (args != null && args2 != null){
            realmFunc.create(args,
                checked = false,
                red = false,
                yellow = false,
                blue = false,
                non = true,
                fromPos = args2
            )
            Log.d("args", args)
        }
        if (args1 != null && args3 != null){


            realmFunc.create(args1,
                checked = false,
                red = false,
                yellow = false,
                blue = false,
                non = true,
                fromPos = args3
            )
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomAppBar03 = view.findViewById<BottomAppBar>(R.id.bottomAppBar03)
        val content03 = view.findViewById<RecyclerView>(R.id.content03)

        /**
         * realmの情報がある配列
         */
        val taskList = realmFunc.readAll()
        viewAdapter = TaskAdapter03(
            requireContext(),
            object :
                TaskAdapter03.OnItemClickListener {
                override fun onItemClick(
                    item: Task03,
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
                    toolbar03.title = "完了したタスクが表示されます"
                    toolbar03.setTitleTextColor(
                        Color.rgb(
                            75,
                            75,
                            75
                        )
                    )
                }

                override fun checked(text: String, fromPos: Int) {
                    // Bundleインスタンスを作成
                    Log.d("frompos", fromPos.toString())
                    val bundle = Bundle()
                    bundle.putString("UNDO_TEXT", text)
                    bundle.putInt("UNDO_ID", fromPos)

                    Snackbar.make(
                        liner03,
                        "元に戻しました",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    when (fromPos) {
                        1 -> {

                            val fragment =
                                Tab01Fragment()
                            fragment.arguments = bundle
                            parentFragmentManager.beginTransaction()
                                .add(
                                    R.id.a,
                                    fragment
                                )
                                .commit()
                        }
                        2 -> {
                            val fragment =
                                Tab02Fragment()
                            fragment.arguments = bundle
                            parentFragmentManager.beginTransaction()
                                .add(
                                    R.id.a,
                                    fragment
                                )
                                .commit()
                        }
                    
                    }
                }
            }, taskList, true
        )

        /**
         * recyclerView枠線表示
         */

        /*val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(requireContext().getDrawable(R.drawable.divider)!!)
        recyclerView = view.findViewById(R.id.content)
        recyclerView.addItemDecoration(itemDecoration)*/


        itemTouchHelper = ItemTouchHelper(
            getRecyclerViewSimpleCallBack()
        )
        itemTouchHelper.attachToRecyclerView(content03)



        /**
         * 削除処理 -> すべてのタスクを削除する
         *すでに空の時は何もしない
         */
        bottomAppBar03.setOnMenuItemClickListener { it ->
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
                                            .where(Task03::class.java)
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

        /**
         * RecyclerViewのアダプター
         * クリックリスナー実装
         * @see TaskAdapter.OnItemClickListener
         * @see TaskAdapter.OnItemLongClickListener
         */

        content03.setHasFixedSize(true)
        content03.layoutManager =
            LinearLayoutManager(requireContext())
        content03.adapter = viewAdapter
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
                    bottomAppBar03,
                    "項目を削除しました",
                    Snackbar.LENGTH_SHORT
                ).setAction("元に戻す"){
                    realmFunc.create(itemTouchText,false,red,yellow, blue, non,3)
                }.show()
            }
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