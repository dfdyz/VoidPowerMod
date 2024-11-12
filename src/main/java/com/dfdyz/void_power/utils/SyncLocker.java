package com.dfdyz.void_power.utils;

public class SyncLocker<T> {
    T value;

    public SyncLocker(T init){
        value = init;
    }

    public synchronized void set(T value){
        this.value = value;
    }

    public T getThenSet(T value){
        T org = this.value;
        set(value);
        return org;
    }

    public T get(){
        return value;
    }

}
