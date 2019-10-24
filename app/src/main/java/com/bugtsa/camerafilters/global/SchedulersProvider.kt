package com.bugtsa.camerafilters.global

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.Executors
import io.reactivex.schedulers.Schedulers as RxSchedulers

object SchedulersProvider : SchedulersContract {

    override fun ui() = AndroidSchedulers.mainThread()
    override fun computation() = RxSchedulers.computation()
    override fun trampoline() = RxSchedulers.trampoline()
    override fun newThread() = RxSchedulers.newThread()
    override fun single() = RxSchedulers.single()
    override fun io() = RxSchedulers.io()
    override fun limited(limit: Int): Scheduler {
        val executor = Executors.newFixedThreadPool(limit)
        return RxSchedulers.from(executor)
    }

}

interface SchedulersContract {

    fun ui(): Scheduler
    fun computation(): Scheduler
    fun trampoline(): Scheduler
    fun single(): Scheduler
    fun newThread(): Scheduler
    fun io(): Scheduler
    fun limited(limit: Int): Scheduler
}