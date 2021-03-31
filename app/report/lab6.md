# Цели

Получить практические навыки разработки многопоточных приложений:

1. Организация обработки длительных операций в background (worker) thread:
    - Запуск фоновой операции (coroutine/asynctask/thread)
    - Остановка фоновой операции (coroutine/asynctask/thread)
2. Публикация данных из background (worker) thread в main (ui) thread.

Освоить 3 основные группы API для разработки многопоточных приложений:

1. Kotlin Coroutines
2. AsyncTask
3. Java Threads

## Задача 1 - Альтернативные решения задачи "не секундомер" из Лаб. 2

Думаю, начнем с дефолтных тредов, так как нам нужно брать за основу код из второй лаюораторной работы,
а потом будем изменять его для альтеративных вариантов решения задачи

Ниже приведем листинг программы на тредах:

__Листинг 1.1 - Thread_1.kt__

    private const val key = "seconds"
    
    class Thread_1 : AppCompatActivity() {
        private var seconds = 0
        private var thread: Thread? = null
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (savedInstanceState != null) {
                seconds = savedInstanceState.getInt(key)
            }
            setContentView(R.layout.activity_main)
        }
    
        override fun onResume() {
            thread = Thread {
                try {
                    while (thread?.isInterrupted == false) {
                        Thread.sleep(1000)
                        secondsText.post {
                            secondsText.text = getString(R.string.seconds_thread, seconds++)
                        }
                        Log.i("Thread", "$seconds")
                    }
                } catch (e: InterruptedException) {
                    Log.i("Thread", "Thread is interrupted")
                }
            }
    
            thread?.start()
            super.onResume()
        }
    
        override fun onPause() {
            thread?.interrupt()
            super.onPause()
        }
    
        override fun onSaveInstanceState(outState: Bundle) {
            outState.putInt(key, seconds)
            super.onSaveInstanceState(outState)
        }
    
        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            savedInstanceState.getInt(key)
            super.onRestoreInstanceState(savedInstanceState)
        }
    }


Итак, что у нас поменялось по сравнению со 2 работой:

- Решил делать для `interrupt` треда в методах `onPause/onResume`, вместо `onStop/onStart` по рекомендации Андрея Николаевича
- В методе `onCreate()` мы будем восстанавливать значение секунд, если уже был хоть раз использован метод `onPause()`
- В методе `onPause` мы ставим `thread?.interrupt()` и в `onResume` мы добавляем проверку на этот же самый интеррапт, чтобы наш Тред перестал считать

В остальном, все осталось без изменений

Теперь перейдем к AsyncTask:

__Листинг 1.2 - Async_1.kt__

    private const val key = "seconds"
    
    @Suppress("DEPRECATION")
    class Async_1 : AppCompatActivity() {
        private var seconds = 0
        private var task: AsyncTasc? = null
    
    
        @SuppressLint("StaticFieldLeak")
        inner class AsyncTasc : AsyncTask<Unit, Unit, Unit>() {
    
            override fun onProgressUpdate(vararg values: Unit?) {
                super.onProgressUpdate(*values)
                secondsText.post {
                    secondsText.text = getString(R.string.seconds_task, seconds++)
                }
                Log.i("Task", "$seconds")
            }
    
            override fun doInBackground(vararg p0: Unit?) {
                while (!isCancelled) {
                    Thread.sleep(1000)
                    publishProgress()
                }
            }
        }
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (savedInstanceState != null) {
                seconds = savedInstanceState.getInt(key)
            }
            setContentView(R.layout.activity_main)
        }
    
        override fun onResume() {
            task = AsyncTasc()
            task?.execute()
            super.onResume()
        }
    
        override fun onPause() {
            task?.cancel(false)
            Log.i("Task", "Task is canceled")
            super.onPause()
        }
    
        override fun onSaveInstanceState(outState: Bundle) {
            outState.putInt(key, seconds)
            super.onSaveInstanceState(outState)
        }
    
        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            savedInstanceState.getInt(key)
            super.onRestoreInstanceState(savedInstanceState)
        }
    }

- Здесь мы внутри создаем ещё класс `AsyncTask` и делаем его `inner`, чтобы использовать `secondsText` и `seconds`
- Далее мы реализуем метод `doInBackground()`, в котором следует выполнять всякие тяжеловесные операции
- Метод `onProgressUpdate()` получает на вход промежуточные данные от `publishProgress()` (в нашем случае ничего) и обновляем текстовое поле.
- Ну и в методе `onResume()` создаем объект класса AsyncTask, после чего  вызываем на нем `execute()`, в методе `onPause` отменяем с помощью `cancel`

Ну и теперь, наконец, перейдем к корутинам!

__Листинг 1.3 - Coroutines_1.kt__

    private const val key = "seconds"
    
    class Coroutines_1 : AppCompatActivity() {
        private var seconds = 0
        private var job: Job? = null
    
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (savedInstanceState != null) {
                seconds = savedInstanceState.getInt(key)
            }
            setContentView(R.layout.activity_main)
        }
    
        override fun onResume() {
            job = lifecycleScope.launchWhenResumed {
                while (isActive) {
                    delay(1000)
                    secondsText.post {
                        secondsText.text = getString(R.string.seconds_cor, seconds++)
                    }
                    Log.i("Job", "$seconds")
                }
            }
            super.onResume()
        }
    
        override fun onPause() {
            job?.cancel()
            Log.i("Job", "Job is canceled")
            super.onPause()
        }
    
        override fun onSaveInstanceState(outState: Bundle) {
            outState.putInt(key, seconds)
            super.onSaveInstanceState(outState)
        }
    
        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            savedInstanceState.getInt(key)
            super.onRestoreInstanceState(savedInstanceState)
        }
    }

И перечислю совсем небольшие отличия:

- Здесь мы создаем интерфейс `Job` для работы с корутинами
- В методе `onResume()` мы запускаем нашу корутину с помощью метода `launchWhenResumed()`(очень удобная штука, кстати!)
И используем на остановку корутины `delay`, вместо `sleep` у тредов

В принципе, корутины для выполнения этой задачи мне показались наиболее удобными и понятными,
особенно, когда есть специальный конструктор, знающий за лайвсайклы!


## Задача 2 - Загрузка картинки в фоновом потоке (AsyncTask)

В этом задании по сути нужно было только разобраться с кодом, который нам уже был дан готовый.
Мы его перевели в Котлин и чуть подредачили:

__Листинг 2 - AsyncDownload.kt__

    private const val url = "https://pp.userapi.com/c628718/v628718010/15fe/7GxzcHAsxdA.jpg"
    
    @Suppress("DEPRECATION")
    class AsyncDownload : AppCompatActivity() {
    
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.download)
    
            button_download.setOnClickListener {
                DownloadImageTask().execute(url)
            }
        }
    
        @SuppressLint("StaticFieldLeak")
        inner class DownloadImageTask : AsyncTask<String?, Void?, Bitmap?>() {
    
            override fun doInBackground(vararg urls: String?): Bitmap? {
                val urldisplay: String? = urls[0]
                var mIcon11: Bitmap? = null
                try {
                    val input: InputStream = URL(urldisplay).openStream()
                    mIcon11 = BitmapFactory.decodeStream(input)
                } catch (e: Exception) {
                    Log.e("Error", e.message.toString())
                    e.printStackTrace()
                }
                return mIcon11
            }
    
            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)
                imageView_download.setImageBitmap(result)
            }
        }
    }

- При нажатии на кнопку мы запускам наш AsyncTask
- Что касаемо его самого, то в методе `doInBackground()` мы загружаем нашу картинку
- Метод `onPostExecute()` выполняется после `doInBackground()` и в нем мы загружаем нашу скачанную картинку в наш ImageView

## Задача 3 - Загрузка картинки в фоновом потоке (Kotlin Coroutines)

Теперь выполним нашу задачу, используя корутины:

__Листинг 3 - CoroutinesDownload.kt__

    private const val url = "https://pp.userapi.com/c628718/v628718010/15fe/7GxzcHAsxdA.jpg"
    
    @Suppress("BlockingMethodInNonBlockingContext")
    class CoroutinesDownload : AppCompatActivity() {
        private var icon: Bitmap? = null
    
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.download)
    
            button_download.setOnClickListener {
                lifecycleScope.launchWhenResumed {
                    withContext(Dispatchers.IO) {
                        try {
                            val input: InputStream = URL(url).openStream()
                            icon = BitmapFactory.decodeStream(input)
                        } catch (e: Exception) {
                            Log.e("Error", e.message.toString())
                            e.printStackTrace()
                        }
                    }
                    withContext(Dispatchers.Main) {
                        imageView_download.setImageBitmap(icon)
                    }
                }
            }
        }
    }

- Обозначим все в одном методе `onCreate`, энивей с `launchWhenResumed` наша корутина запустится только в состоянии `onResume`
- А внутри

Дальше мы последовательно делаем нужные нам задачи в двух `withContext()`

- Первое - мы с помощью `Dispatchers.IO` скачиваем картинку
- Второе - уже с помощью `Dispatchers.Main` мы запихиваем нашу картинку в ImageView

## Задача 4 - Использование сторонних библиотек (Picasso)

Картинку для этих заданий я выбрал только после того, как прочитал 4 задание и увидел здесь Picasso.
В момент мне вспомнилась одна из самых нелюбимых и неприятных мне картин Пабло Пикассо, поэтому я решил поделиться ей с вами :)

Представляем Вашему вниманию картину Пабло Пикассо "Сидящий на красной скамье Арлекин" !!!

![](/Users/joker/AndroidStudioProjects/lab6/app/Picasso.png)

__Листинг 4 - Task2_PicassoExample.kt__

    private const val url = "https://pp.userapi.com/c628718/v628718010/15fe/7GxzcHAsxdA.jpg"
    
    class PicassoExample : AppCompatActivity() {
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.download)
    
            button_download.setOnClickListener {
                Picasso.get().load(url).into(imageView_download)
            }
    
        }
    }

Загружать картинку с помощью готовой библиотеки оказалось очень даже удобно!
Метод `load()` загружает картинку, а с помощью `into()` мы кладем картинку в наш блок `imageView`



# Выводы

Было достаточно интересно поработать с этой лабораторной. Я прямо почувствовал какую-то магию,
когда мое приложение, которое я написал своими ру4ками, научилось что-то качать из интернета...
Я как будто наблюдал за его становлением в полезное для людей приложение...
И было очень интересно поработать с Корутинами(АсинкТаск уже устаревший, так что это была моя первая и последняя ним работа, видимо)
Меня как ребенка порадовал билдер, знающий за лайфсайклы... Мне в тот момент показалось, что за меня уже пол работы сделали, кайф...
Ну и прикольно было вспомнить про эту картинку...
Надеюсь, я её прикрепил к этому отчеты и вы её тоже видите!
В общем с просмотром лекций сидел где-то пару дней, но я не все время делал только лабы, офк
Думаю, часиков 10-15 на нее потратил где-то!