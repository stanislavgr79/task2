package com.exercises2.storage;

import com.exercises2.port.Ship;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
public class Storage {

    public final int DEFAULT_STORAGE_CAPACITY = 20;

    private Queue<Goods> goods;

    private static Lock lock = new ReentrantLock();

    private Storage(){
        goods = new LinkedBlockingQueue<>(DEFAULT_STORAGE_CAPACITY);
        fillStorage();
    }

    private static Storage storage = new Storage();

    private void fillStorage(){
        for (int x=0; x<10; x++){
            goods.add(new Goods());
        }
    }

    public static Storage getInstance(){
        return storage;
    }

    public int getCurrentFreeCapacityInStorage(){
        lock.lock();
        int result;
        result = getDEFAULT_STORAGE_CAPACITY()- goods.size();
        lock.unlock();
        return result;
    }

    public void moveGoodsFromShipToStorage(Ship ship){
        lock.lock();
        List<Goods> shipGoods = ship.getGoods();
        goods.addAll(shipGoods);
        ship.getGoods().clear();
        lock.unlock();
    }

    public void moveGoodsFromStorageToShip(Ship ship){
        lock.lock();
        int capacityCargo = ship.getCapacityCargo();
        for(int x=0; x<capacityCargo; x++){
            ship.getGoods().add(goods.poll());
        }
        lock.unlock();
    }
}
