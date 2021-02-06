package app.sato.ken.todoapp_with_fragment.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import app.sato.ken.todoapp_with_fragment.Functions.GetDateFunc
import app.sato.ken.todoapp_with_fragment.Functions.RealmFunc
import app.sato.ken.todoapp_with_fragment.R
import app.sato.ken.todoapp_with_fragment.data.Images
import app.sato.ken.todoapplication.Adapter.ListCustomAdapter
import app.sato.ken.todoapplication.Realm.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.calender.view.*
import kotlinx.android.synthetic.main.list_item.view.*


class TaskAdapter(

    private var ctx: Context,
    private var listener: OnItemClickListener,
    private var taskList: OrderedRealmCollection<Task>,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Task, TaskAdapter.ViewHolder>(taskList, autoUpdate) {
    private val realm: Realm by lazy { Realm.getDefaultInstance() }

    private val getDateFunc = GetDateFunc()
    private val realmFunc = RealmFunc()
    private val dialogC = BottomSheetDialog(ctx,R.style.AppTheme_ShareDialog)

    var t1 = ""
    var deta1 = ""
    var detaText = ""

    /**
     * アイテムクリックを受け取るリスナーを設定
     *
     */
    interface OnItemClickListener {
        /**detect clicks **/
        fun onItemClick(item: Task, position: Int, text: String)
        /**detect touch**/
        fun onItemTouch(text: String, date: String)
        /**get item size**/
        fun size(count: Int)
        /**detect check**/
       fun checked(text: String,fromPos: Int, position: Int)

    }

    override fun getItemCount(): Int {
        listener.size(taskList.size)
        Log.d("getItemcount", taskList.size.toString())
        return taskList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.checkBox.isChecked = taskList[position]!!.selected
        val task: Task = taskList[position] ?: return
        val date = getDateFunc.getToday()
        val date1 = getDateFunc.getTomorrow()
        val date2 = getDateFunc.getNextWeek()

        holder.container.tag = position
        holder.deta.tag = position
        holder.image.tag = position
        holder.image.tag as Int
        holder.deta.text = task.date

        holder.fromPos.text = task.fromPos.toString()
        holder.contentTextView.text = task.content

        holder.image.setOnClickListener {

            val viewP = LayoutInflater.from(ctx).inflate(R.layout.priority, null)

            val p1 = viewP.findViewById<Button>(R.id.p1)
            val p2 = viewP.findViewById<Button>(R.id.p2)
            val p3 = viewP.findViewById<Button>(R.id.p3)
            val p4 = viewP.findViewById<Button>(R.id.p4)

            val dialog = BottomSheetDialog(ctx,R.style.AppTheme_ShareDialog)
            dialog.setContentView(viewP)
            dialog.show()

            p1.setOnClickListener {

                Log.d("color","changed to blue")
                realm.executeTransaction {
                    /**
                     * 画像によって状態を変える
                     */
                    taskList[position].imageYellow = false
                    taskList[position].imageBlue = true
                    taskList[position].imageRed = false
                    taskList[position].imageNon = false
                }
                dialog.dismiss()
            }
            p2.setOnClickListener {
                Log.d("color","changed to yelow")
                realm.executeTransaction {
                    taskList[position].imageYellow = true
                    taskList[position].imageBlue = false
                    taskList[position].imageRed = false
                    taskList[position].imageNon = false
                }
                dialog.dismiss()
            }
            p3.setOnClickListener {
                Log.d("color","changed to red")
                realm.executeTransaction {
                    taskList[position].imageYellow = true
                    taskList[position].imageBlue = false
                    taskList[position].imageRed = true
                    taskList[position].imageNon = false
                }
                dialog.dismiss()
            }
            p4.setOnClickListener {
                holder.image.setImageResource(R.drawable.nomark)
                dialog.dismiss()
            }
        }
        holder.checkBox.setOnClickListener {
            //すぐに消えるとチェックボックスらしくないので
            //チェックした感じを出して消すために遅延させて実行する
            //これ実行しないと配列アクセスの問題で例外が発生する
            //adapterに通知してpositionを適正化する
            Handler().postDelayed({ notifyDataSetChanged() }, 500)
            //500msの遅延処理
            val tex = holder.contentTextView.text.toString()
            val d = holder.deta.text
            realmFunc.delete(position)

            listener.checked(tex, 1, position)
            Snackbar.make(holder.container,"項目を削除しました", Snackbar.LENGTH_SHORT).show()
        }
        holder.container.setOnClickListener {
            t1 = holder.contentTextView.text.toString()
            Log.d("position", position.toString())
            listener.onItemClick(task, position, holder.contentTextView.text.toString())

            val view = LayoutInflater.from(ctx).inflate(R.layout.bottom_sheet_layout, null)
            val e = view.findViewById<EditText>(R.id.editText)
            var picker0 = ""
            val d = view.findViewById<Button>(R.id.d)
            val p = view.findViewById<Button>(R.id.p)

            realm.executeTransaction {
                e.setText(t1)
                task.content = t1
            }
            p.setOnClickListener {
                val viewP = LayoutInflater.from(ctx).inflate(R.layout.priority, null)
                val p1 = viewP.findViewById<Button>(R.id.p1)
                val p2 = viewP.findViewById<Button>(R.id.p2)
                val p3 = viewP.findViewById<Button>(R.id.p3)
                val p4 = viewP.findViewById<Button>(R.id.p4)

                val dialog = BottomSheetDialog(ctx,R.style.AppTheme_ShareDialog)
                dialog.setContentView(viewP)
                dialog.show()

                p1.setOnClickListener {
                    p.text = "マーカー: 青"
                    Log.d("color","changed to blue")
                    realm.executeTransaction {
                        /**
                         * 画像によって状態を変える
                         */
                        taskList[position].imageYellow = false
                        taskList[position].imageBlue = true
                        taskList[position].imageRed = false
                        taskList[position].imageNon = false
                    }
                    dialog.dismiss()
                }
                p2.setOnClickListener {
                    p.text = "マーカー: 黄"
                    Log.d("color","changed to yelow")
                    realm.executeTransaction {
                        taskList[position].imageYellow = true
                        taskList[position].imageBlue = false
                        taskList[position].imageRed = false
                        taskList[position].imageNon = false
                    }
                    dialog.dismiss()
                }
                p3.setOnClickListener {
                    p.text = "マーカー: 赤"
                    Log.d("color","changed to red")
                    realm.executeTransaction {
                        taskList[position].imageYellow = true
                        taskList[position].imageBlue = false
                        taskList[position].imageRed = true
                        taskList[position].imageNon = false
                    }
                    dialog.dismiss()
                }
                p4.setOnClickListener {
                    p.text = "マーカー: なし"
                    holder.image.setImageResource(R.drawable.nomark)
                    dialog.dismiss()
                }
            }

            with(e) {
                realm.executeTransaction {
                    setText(t1)
                    task.content = t1
                }

                d.setOnClickListener {

                    /**
                     * 変数定義
                     */
                    val viewCalender = LayoutInflater.from(ctx).inflate(R.layout.calender, null)
                    val listView = viewCalender.findViewById<ListView>(R.id.Clist)
                    var picker = ""
                    val nweek = Images(R.drawable.nextweek, "来週")
                    val tomorrow = Images(R.drawable.tomorrow, "明日")
                    val today = Images(R.drawable.calender, "今日")
                    val mImagesList = arrayListOf(today, tomorrow, nweek)
                    val time = viewCalender.findViewById<Button>(R.id.time)



                    listView.adapter = ListCustomAdapter(ctx, mImagesList)

                    /**
                     * ビュー設定
                     */
                    dialogC.setContentView(viewCalender)

                    /**
                     * ダイヤログ表示
                     */
                    dialogC.show()

                    /**
                     * CarenderViewのリスナーを設定
                     */
                    viewCalender.calendarView2.setOnDateChangeListener{calendarView: CalendarView, year: Int, month: Int, dayOfMonth: Int ->
                        // monthは0起算のため+1します。
                        val displayMonth = month + 1

                        detaText = "$year/$displayMonth/$dayOfMonth"
                        dialogC.dismiss()

                        realm.executeTransaction {
                            task.date = "$detaText    $picker"
                        }

                        holder.deta.text = detaText
                        Log.d("detaText",holder.deta.text.toString())
                    }


                    /**
                     * ListViewのアイテムタッチリスナー
                     * @see R.id.editText
                     */
                    listView.setOnItemClickListener { _, _, i, _ ->
                        when (i) {
                            0 -> {
                                detaText = getDateFunc.getToday()
                                dialogC.dismiss()
                            }
                            1 -> {
                                detaText = getDateFunc.getTomorrow()
                                dialogC.dismiss()
                            }
                            2 -> {
                                detaText = getDateFunc.getNextWeek()
                                dialogC.dismiss()
                            }
                        }
                    }
                    dialogC.setOnDismissListener {

                        realm.executeTransaction {
                            when {
                                date == detaText -> {
                                    detaText = getDateFunc.getToday()
                                    task.date = getDateFunc.getToday()
                                    holder.deta.text = getDateFunc.getToday()
                                }
                                date1 == detaText -> {
                                    detaText = getDateFunc.getTomorrow()
                                    task.date = getDateFunc.getTomorrow()
                                    holder.deta.text = getDateFunc.getTomorrow()
                                }
                                date2 == detaText -> {
                                    detaText = getDateFunc.getNextWeek()
                                    task.date = getDateFunc.getNextWeek()
                                    holder.deta.text = getDateFunc.getNextWeek()
                                }else -> {
                                holder.deta.text = detaText
                            }
                            }
                        }
                        d.text = detaText
                    }
                }

                /**
                 * e ->
                 * @see R.id.editText
                 */
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        realm.executeTransaction {
                            task.content = text.toString()
                            holder.contentTextView.text = text
                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })
            }

            dialog(view)
        }
        holder.container.setOnTouchListener { _, _ ->
            listener.onItemTouch(
                holder.contentTextView.text.toString(),
                holder.deta.text.toString()
            )
            false
        }
        when {
            taskList[position].imageBlue -> {
                Log.d("color","setBlueImage")
                holder.image.setImageResource(R.drawable.bluemark)
            }
            taskList[position].imageRed -> {
                Log.d("color","setRedImage")
                holder.image.setImageResource(R.drawable.redmark)
            }
            taskList[position].imageYellow -> {
                Log.d("color","setYellowImage")
                holder.image.setImageResource(R.drawable.yellowmark)
            }
            taskList[position].imageNon -> {
                Log.d("color","setNonImage")
                holder.image.setImageResource(R.drawable.nomark)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item, viewGroup, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTextView: TextView = itemView.contentTextView
        val container: LinearLayout = itemView.container
        val deta: TextView = itemView.deta
        val image: ImageButton = itemView.marker
        val fromPos: TextView = itemView.fromPos
        var checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }

    fun dialog(view: View){
        val dialog = BottomSheetDialog(ctx,R.style.AppTheme_ShareDialog)
        dialog.setContentView(view)
        dialog.show()
    }
}


