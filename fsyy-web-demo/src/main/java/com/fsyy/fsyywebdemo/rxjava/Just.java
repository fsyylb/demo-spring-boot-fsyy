package com.fsyy.fsyywebdemo.rxjava;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class Just {
    public static void main(String[] args) {
        Observable.just("Hello World")
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println(throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("onComplete()");
                    }
                });

        System.out.println("----------------------------------");

        Observable.just("Hello World")
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println(throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("onComplete()");
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        System.out.println("subscribe");
                    }
                });

        System.out.println("***************************************");

        Observable.just("Hello World")
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        System.out.println("subscribe");
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        System.out.println(s);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete()");
                    }
                });


        System.out.println("！！！！！！！！！！！！！！！！！！！！！！！！！！！！");

        Observable.just("Hello")
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("doOnNext: " + s);
                    }
                })
                .doAfterNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("doAfterNext: " + s);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doOnComplete");
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        System.out.println("doOnSubscribe");
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doAfterTerminate");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doFinally");
                    }
                })
                .doOnEach(new Consumer<Notification<String>>() {
                    @Override
                    public void accept(Notification<String> stringNotification) throws Exception {
                        System.out.println("doOnEach: " + (stringNotification.isOnNext() ? "onNext" :
                                stringNotification.isOnComplete() ? "onComplete" : "onError"));
                    }
                })
                .doOnLifecycle(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        System.out.println("doOnLifecycle:" + disposable.isDisposed());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doOnLifecycle run");
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("收到消息：" + s);
                    }
                });
    }
}
