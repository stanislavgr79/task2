package com.exercises2.port;

import com.exercises2.storage.Goods;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Getter
@Setter
@ToString(of = {"nameOfShip", "capacityCargo"})
@EqualsAndHashCode(of = "nameOfShip")
public class Ship {

    private static Logger LOG = LoggerFactory.getLogger(Ship.class);

    private String nameOfShip;
    private boolean stateOfCargoFill;
    private int capacityCargo = 10;

    private List<Goods> goods = new CopyOnWriteArrayList<>();

    private Lock lock = new ReentrantLock();

    Ship(String nameOfShip) {
        this.nameOfShip = nameOfShip;
        stateOfCargoFill = new Random().nextBoolean();
        if(stateOfCargoFill) fillCargo();
    }

    private void fillCargo(){
        for (int x=0; x<capacityCargo; x++){
            goods.add(new Goods());
        }
    }


    void doWorkWithOtherShip(Ship other) {
        if (stateOfCargoFill) {
            unLoadingGoodsToOtherShip(other);
        } else {
            loadingGoodsFromOtherShip(other);
        }
    }

    private void loadingGoodsFromOtherShip(Ship from) {
        lock.lock();
        System.out.println(nameOfShip + " start loadingGoodsFromOtherShip=" + from.getNameOfShip() + " : my_cargo=" + this.getGoods().size() + ", from_cargo=" + from.getGoods().size());
        this.getGoods().addAll(from.goods);
        from.getGoods().clear();
        System.out.println(nameOfShip + " end loadingGoodsFromOtherShip=" + from.getNameOfShip() + " : my_cargo=" + this.getGoods().size() + ", from_cargo=" + from.getGoods().size());
        lock.unlock();
    }

    private void unLoadingGoodsToOtherShip(Ship to) {
        lock.lock();
        System.out.println(nameOfShip + " start UnLoadingToOtherShip=" + to.getNameOfShip() + " : my_cargo=" + this.getGoods().size() + ", to_cargo=" + to.getGoods().size());
            to.getGoods().addAll(this.goods);
            this.goods.clear();
        System.out.println(nameOfShip + " end UnLoadingToOtherShip=" + to.getNameOfShip() + " : my_cargo=" + this.getGoods().size() + ", to_cargo=" + to.getGoods().size());
        lock.unlock();
    }

}
