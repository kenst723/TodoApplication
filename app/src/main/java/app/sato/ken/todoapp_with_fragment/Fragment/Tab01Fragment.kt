package app.sato.ken.todoapp_with_fragment.Fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.sato.ken.todoapp_with_fragment.Adapter.TaskAdapter
import app.sato.ken.todoapp_with_fragment.Functions.GetDateFunc
import app.sato.ken.todoapp_with_fragment.Functions.RealmFunc
import app.sato.ken.todoapp_with_fragment.R
import app.sato.ken.todoapp_with_fragment.data.Images
import app.sato.ken.todoapplication.Adapter.ListCustomAdapter
import app.sato.ken.todoapplication.Realm.Task
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import kotlinx.android.synthetic.main.calender.view.*
import kotlinx.android.synthetic.main.fragment_tab01.*
import java.util.*

class Tab01Fragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var itemTouchHelper : ItemTouchHelper
    private var shown = 0

    private var myDataSet = mutableListOf<Task>()
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }


    private val getDateFunc =
        GetDateFunc()
    val realmFunc =
        RealmFunc()


    var getItemText = ""
    var getCheckedText = ""
    var itemTouchText = ""
    var swipedText = ""
    var dataText = ""
    var red = false
    var blue = false
    var yellow = false
    var non = true
    var fromPos = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab01,container,false)
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        itemTouchHelper = ItemTouchHelper(getRecyclerViewSimpleCallBack())
        itemTouchHelper.attachToRecyclerView(content)
        val bottomAppBar = view.findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        val content = view.findViewById<RecyclerView>(R.id.content)
        val viewStub01 = view.findViewById<ViewStub>(R.id.viewStub01)
        val args1 = arguments?.getString("UNDO_TEXT")
        val args2 = arguments?.getInt("UNDO_ID")

        val taskList = realmFunc.readAll()

        viewAdapter = TaskAdapter(
            requireContext(),
            object :
                TaskAdapter.OnItemClickListener {
                override fun onItemClick(
                    item: Task,
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
                        viewStub01.visibility = View.VISIBLE
                    }else{
                        viewStub01.visibility = View.INVISIBLE
                    }
                    Log.d("shown","$shown")
                    shown = count
                    if (count == 0) {
                        toolbar.title = "タスクが登録されていません"
                        toolbar.setTitleTextColor(
                            Color.rgb(
                                75,
                                75,
                                75
                            )
                        )
                    } else {
                        toolbar.title = "未完了のタスクは$count 件です"
                        toolbar.setTitleTextColor(
                            Color.rgb(
                                75,
                                75,
                                75
                            )
                        )
                    }
                }

                override fun checked(text: String, fromPos: Int, position: Int) {
                    getCheckedText = text

                    Log.d("position", position.toString())
                    // Bundleインスタンスを作成
                    val bundle = Bundle()
                    bundle.putString("BUNDLE_KEY_TITLE", text)
                    bundle.putInt("BUNDLE_KEY_INT", fromPos)
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



        if (args1 != null && args2 != null && args2 == 1) {
            realmFunc.create(
                args1, false, "",
                red = false,
                yellow = false,
                blue = false,
                non = false,
                fromPos = args2
            )
        }

        bottomAppBar.setOnMenuItemClickListener { it ->
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
                            .setPositiveButton("YES") { _, _ ->
                                realm
                                    .executeTransaction {
                                        val task2 = it
                                            .where(Task::class.java)
                                            .findAll()
                                        task2.deleteAllFromRealm()
                                    }
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }
        fab.setOnClickListener {
            showAddBottomSheetDialog()
        }

        content.setHasFixedSize(true)
        content.layoutManager = LinearLayoutManager(requireContext())
        content.adapter = viewAdapter
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


                realmFunc.delete(p0.adapterPosition)
                Snackbar.make(
                    bottomAppBar,
                    "項目を削除しました",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    private fun showAddBottomSheetDialog(){

        blue = false
        red = false
        yellow = false
        non = true

        val view = layoutInflater.inflate(R.layout.add_dialog,null)
        val editText = view.findViewById<EditText>(
            R.id.editText
        )
        val dateButton = view.findViewById<Button>(
            R.id.today
        )
        val priorityButton = view.findViewById<Button>(
            R.id.priority
        )
        val dateTextView = view.findViewById<TextView>(
            R.id.dateTextView
        )
        val priorityTextView = view.findViewById<TextView>(
            R.id.priorityTextView
        )
        val addFab = view.findViewById<FloatingActionButton>(
            R.id.floatingActionButton
        )

        val picker0 = ""

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
        dateButton.setOnClickListener {
            /**
             * 変数定義
             */
            val viewC = layoutInflater.inflate(R.layout.calender, null)
            val listView = viewC.findViewById<ListView>(
                R.id.Clist
            )
            var picker = ""
            val time = viewC.findViewById<Button>(
                R.id.time
            )
            val nweek = Images(
                R.drawable.nextweek,
                "来週"
            )
            val tomorrow = Images(
                R.drawable.tomorrow,
                "明日"
            )
            val today = Images(
                R.drawable.calender,
                "今日"
            )
            val mImagesList = arrayListOf(today,tomorrow,nweek)


            listView.adapter =
                ListCustomAdapter(
                    requireContext(),
                    mImagesList
                )

            val dialog =
                BottomSheetDialog(
                    requireContext(),
                    R.style.AppTheme_ShareDialog
                )

            dialog.setContentView(viewC)
            dialog.show()


            viewC.calendarView2.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val displayMonth = month + 1
                dataText = "$year/$displayMonth/$dayOfMonth"
                dialog.dismiss()
            }


            /**
             * ListViewのアイテムタッチリスナー
             * @see R.id.editText
             */
            listView.setOnItemClickListener { _, _, i, _ ->
                when(i){
                    0 ->{
                        dataText =  getDateFunc.getToday()
                        dialog.dismiss()
                    }
                    1 ->{
                        dataText =  getDateFunc.getTomorrow()
                        dialog.dismiss()
                    }
                    2 ->{
                        dataText =  getDateFunc.getNextWeek()
                        dialog.dismiss()
                    }
                }
            }

            dialog.setOnDismissListener {
                dateTextView.text = dataText
                when (dateTextView.text) {
                    getDateFunc.getToday() -> {
                        dateTextView.text = getDateFunc.getToday()
                    }
                    getDateFunc.getTomorrow() -> {
                        dateTextView.text = getDateFunc.getTomorrow()
                    }
                    getDateFunc.getNextWeek() -> {
                        dateTextView.text = getDateFunc.getNextWeek()
                    }
                }
            }
        }
        addFab.setOnClickListener {
            if (editText.text.toString() != "" || editText.text.toString().isNotEmpty()){
                realmFunc.create(editText.text.toString(),
                    checked = false,
                    date = dateTextView.text.toString() +  "  " + picker0,
                    red = red,
                    yellow = yellow,
                    blue = blue,
                    non = non,
                    fromPos = fromPos)
            }else{
                Toast.makeText(
                    requireContext(),
                    "内容を入力してください",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        val dialog = BottomSheetDialog(
            requireContext(),
            R.style.AppTheme_ShareDialog
        )
        dialog.setContentView(view)
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}