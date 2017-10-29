# rxlogcat
An example how it is easy to read logcat using RxJava

## How it works
First, create a logcat reader that emits BufferedReader on subscribe

    val reader = Observable.create<BufferedReader> { emitter ->
            val process = Runtime.getRuntime().exec("logcat")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            emitter.onNext(reader)
            emitter.setCancellable { process.destroy() }
        }
        
Second, create interval source for pulling changes from logcat

    val interval = Observable.interval(0, 1, TimeUnit.SECONDS)
    
Third, combine this two sources

    val f = object : BiFunction<BufferedReader, Long, BufferedReader> {
            override fun apply(t1: BufferedReader, t2: Long): BufferedReader = t1
        }
    val pulse = Observable.combineLatest(reader, interval, f)
        
And in the end, flatMap this observer to parsed lines

    fun lines(r: BufferedReader) = Observable.create<String> { emitter ->
        while (!emitter.isDisposed && r.ready()) {
            emitter.onNext(r.readLine())
        }
    }
    return pulse.flatMap { lines(it) }

[Source code](app/src/main/java/rxlogcat/RxLogcat.kt)

[Example activity with RecyclerView Adapter](app/src/main/java/rxlogcat/example/MainActivity.kt)
