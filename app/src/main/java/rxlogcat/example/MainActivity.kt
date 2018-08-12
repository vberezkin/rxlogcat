package rxlogcat.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import rxlogcat.RxLogcat

class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv = itemView.findViewById<TextView>(R.id.tv)
}

class MainAdapter : RecyclerView.Adapter<MainViewHolder>() {
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.tv?.text = items[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MainViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    fun add(line: String) {
        items.add(line)
        notifyItemInserted(items.size - 1)
    }

    private val items = mutableListOf<String>()
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        subscr = RxLogcat.logcat().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { adapter.add(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscr.dispose()
    }

    private lateinit var subscr: Disposable
    private val adapter = MainAdapter()
    private lateinit var recyclerView: RecyclerView
}
