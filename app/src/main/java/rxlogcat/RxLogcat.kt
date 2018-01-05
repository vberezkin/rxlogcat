package rxlogcat

import io.reactivex.Observable
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by berezkin on 22/10/17.
 */
object RxLogcat {
    fun logcat() = Observable.create<String> { emitter ->
        val process = Runtime.getRuntime().exec("logcat")
        emitter.setCancellable { process.destroy() }
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        while (!emitter.isDisposed) try {
            reader.readLine()?.let { emitter.onNext(it) }
        } catch (e: IOException) {
            break
        }
    }
}
