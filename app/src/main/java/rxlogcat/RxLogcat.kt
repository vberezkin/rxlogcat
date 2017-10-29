package rxlogcat

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Created by berezkin on 22/10/17.
 */
object RxLogcat {
    fun logcat(): Observable<String> {
        val reader = Observable.create<BufferedReader> { emitter ->
            val process = Runtime.getRuntime().exec("logcat")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            emitter.onNext(reader)
            emitter.setCancellable { process.destroy() }
        }
        val interval = Observable.interval(0, 1, TimeUnit.SECONDS)
        val f = object : BiFunction<BufferedReader, Long, BufferedReader> {
            override fun apply(t1: BufferedReader, t2: Long): BufferedReader = t1
        }
        val pulse = Observable.combineLatest(reader, interval, f)
        fun lines(r: BufferedReader) = Observable.create<String> { emitter ->
            while (!emitter.isDisposed && r.ready()) {
                emitter.onNext(r.readLine())
            }
        }
        return pulse.flatMap { lines(it) }
    }
}
