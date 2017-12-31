package de.aaronoe.seek.util


import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.subscribeDefault(onNext: (T) -> Unit, onError: (Throwable) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext, onError)
}

fun <T> Single<T>.subscribeDefault(onSuccess: (T) -> Unit, onError: (Throwable) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)
}

fun <T> Flowable<T>.subscribeDefault(onSuccess: (T) -> Unit, onError: (Throwable) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)
}

fun Completable.subscribeDefault(onComplete: () -> Unit, onError: (Throwable) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onComplete, onError)
}

fun <T> Observable<T>.subscribeDefault(onNext: (T) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext)
}

fun <T> Single<T>.subscribeDefault(onSuccess: (T) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess)
}

fun <T> Flowable<T>.subscribeDefault(onSuccess: (T) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess)
}

fun Completable.subscribeDefault(onComplete: () -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onComplete)
}

fun <T> Single<T>.subscribeComputation(onSuccess: (T) -> Unit): Disposable {
    return subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess)
}