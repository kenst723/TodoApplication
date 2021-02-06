package app.sato.ken.todoapp_with_fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import app.sato.ken.todoapp_with_fragment.Fragment.Tab01Fragment
import app.sato.ken.todoapp_with_fragment.Fragment.Tab02Fragment
import app.sato.ken.todoapp_with_fragment.Fragment.Tab03Fragment
import app.sato.ken.todoapp_with_fragment.Functions.GetDateFunc
import app.sato.ken.todoapp_with_fragment.Functions.RealmFuncTab
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

lateinit var mTabLayout: TabLayout
lateinit var mPagerAdapter: SectionsPagerAdapter
lateinit var tabItem: ArrayList<String>

class MainActivity : AppCompatActivity() {

    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val  getDateFunc = GetDateFunc()
        val realmFunc = RealmFuncTab()

        mTabLayout = findViewById(R.id.tabs)
        mPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        tabItem = ArrayList()

        addTab("Todoリスト")
        addTab(getDateFunc.getTomorrowNotY() + "(今日)")
        addTab("完了済み")




        view_pager.adapter = mPagerAdapter
        tabs.setupWithViewPager(view_pager)

        var t = ""

    }
}

private fun addTab(title: String){
    mTabLayout.addTab(mTabLayout.newTab().setText(title))
    addTabPage(title)
}

private fun addTabPage(title: String){
    tabItem.add(title)
    mPagerAdapter.notifyDataSetChanged()
}

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                Tab01Fragment()
            }
            1 ->  {
                Tab02Fragment()
            }
            else -> {
                Tab03Fragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
     return tabItem[position]
    }

    override fun getCount(): Int {
        return tabItem.size
    }
}