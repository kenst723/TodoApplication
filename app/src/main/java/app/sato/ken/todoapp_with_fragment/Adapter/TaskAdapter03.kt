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
import app.sato.ken.todoapp_with_fragment.Functions.RealmFunc03
import app.sato.ken.todoapp_with_fragment.R
import app.sato.ken.todoapp_with_fragment.data.Images
import app.sato.ken.todoapplication.Adapter.ListCustomAdapter
import app.sato.ken.todoapplication.Realm.Task03
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.calender.view.*
import kotlinx.android.synthetic.main.list_item.view.*


class TaskAdapter03(

    private var ctx: Context,
    private var listener: OnItemClickListener,
    private var taskList: OrderedRealmCollection<Task03>,
    autoUpdate: Boolean
) :
    RealmRecyclerViewAdapter<Task03, TaskAdapter03.ViewHolder>(taskList, autoUpdate) {
    private val realm: Realm by lazy { Realm.getDefaultInstance() }

    private val getDateFunc = GetDateFunc()
    private val realmFunc = RealmFunc03()
    private val dialogC = BottomSheetDialog(ctx,R.style.AppTheme_ShareDialog)

    var t1 = ""
    var deta1 = ""
    var detaText = ""

    /**
     * アイテムクリックを受け取るリスナーを設定
     */
    interface OnItemClickListener {
        fun onItemClick(item: Task03, position: Int, text: String)
        fun onItemTouch(text: String, date: String)
        fun size(count: Int)
        fun checked(text: String, fromPos: Int)
    }

    interface OnSizeCount{

    }

    override fun getItemCount(): Int {
        listener.size(taskList.size)
        return taskList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.checkBox.isChecked = taskList[position]!!.selected
        val task: Task03 = taskList[position] ?: return


        /**
         * checkbox, LinerLayoutの位置をtagとして設定
         */
        holder.checkBox.tag = position
        holder.container.tag = position
        holder.deta.tag = position
        holder.image.tag = position

        holder.checkBox.tag as Int
        holder.image.tag as Int
        /**
         * checkboxのクリックリスナー
         * ここでrelmのチェック情報を書き換える
         */
        holder.checkBox.setOnClickListener {
            //すぐに消えるとチェックボックスらしくないので
            //チェックした感じを出して消すために遅延させて実行する
            //これを実行しないと配列アクセスの問題で例外が発生する
            //adapterに通知してpositionを適正化する
            Handler().postDelayed({ notifyDataSetChanged() }, 500)
            //500msの遅延処理
            val tex = holder.contentTextView.text.toString()
            val fromPos = holder.fromPos.text
            realmFunc.delete(position)
            listener.checked(text = tex,
                            fromPos = Integer.parseInt(fromPos.toString()))
        }

        val date = getDateFunc.getToday()
        val date1 = getDateFunc.getTomorrow()
        val date2 = getDateFunc.getNextWeek()

        holder.container.setOnClickListener {
            t1 = holder.contentTextView.text.toString()
            listener.onItemClick(task, position, holder.contentTextView.text.toString())

            val view = LayoutInflater.from(ctx).inflate(R.layout.bottom_sheet_layout_today, null)
            val e = view.findViewById<EditText>(R.id.editText)
            val p = view.findViewById<Button>(R.id.p)

            realm.executeTransaction {
                e.setText(t1)
                task.content = t1
            }
            p.setOnClickListener {
                holder.image.setImageResource(R.drawable.nomark)
                val viewP = LayoutInflater.from(ctx).inflate(R.layout.priority, null)
                val p1 = viewP.findViewById<Button>(R.id.p1)
                val p2 = viewP.findViewById<Button>(R.id.p2)
                val p3 = viewP.findViewById<Button>(R.id.p3)
                val p4 = viewP.findViewById<Button>(R.id.p4)

                dialog(viewP)

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
                }
                p4.setOnClickListener {
                    p.text = "マーカー: なし"
                    holder.image.setImageResource(R.drawable.nomark)
                }
            }

            /**
             * e ->
             * @see R.id.editText
             */
            with(e) {
                realm.executeTransaction {
                    setText(t1)
                    task.content = t1
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

        /**
         * @see Task
         * booleanがtrue状態の時にマーク変更
         * ノーマーク状態の画像は常にtrueに設定
         * カラーされるときにノーマークの画像をfalseにする。
         */
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

        holder.container.setOnTouchListener { _, _ ->
            listener.onItemTouch(
                holder.contentTextView.text.toString(),
                holder.deta.text.toString()
            )
            false
        }


        holder.contentTextView.text = task.content
        holder.fromPos.text = task.fromPos.toString()


        Log.d("detaText",detaText)
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


