package com.dfdyz.void_power.utils;

public class SyncLocker<T> {
    T value;

    public SyncLocker(T init){
        value = init;
    }

    public void set(T value){
        synchronized (this){
            this.value = value;
        }
    }

    public T getThenSet(T value){
        synchronized (this){
            T org = this.value;
            this.value = value;
            return org;
        }
    }

    public T get(){
        return value;
    }

}
