package com.exercises2.port;

import com.exercises2.storage.Storage;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

@Getter
@Setter
public class Dock implements Runnable {
    private static Logger LOG = LoggerFactory.getLogger(Dock.class);

    private String nameOfDock;

    private BlockingQueue<Ship> shipsInPort;

    private Ship ship;

    private Storage storage = Storage.getInstance();

    private Port port;

    private boolean did_work = false;

    private Lock lock = new ReentrantLock();

    public Dock(Port port, String nameOfDock, BlockingQueue<Ship> shipsInPort) {
        this.port = port;
        this.nameOfDock = nameOfDock;
        this.shipsInPort = shipsInPort;
    }

    void docking(Ship ship) {

        this.ship = ship;
        LOG.info("DOCKING = " + this + ", currentCargo_size " + ship.getGoods().size() + " sclad_total/current=" + storage.getDEFAULT_STORAGE_CAPACITY() + "/" + storage.getGoods().size() + "\n");
    }

    private void unDocking(Ship ship) {
        LOG.info("UN_DOCKING = " + this + ", currentCargo_size " + ship.getGoods().size() + " sclad_total/current=" + storage.getDEFAULT_STORAGE_CAPACITY() + "/" + storage.getGoods().size() + "\n");

        this.ship = null;
        this.setDid_work(false);
    }

    @Override
    public void run() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try_doWork();
        Thread.currentThread().interrupt();
    }



    private void try_doWork() {

        if (doWork()) {
            unDocking(ship);
        } else {
            LOG.info("GO_TO_WAIT ship_cargo=" + this + " current=" + this.ship.getGoods().size());
            int time = 0;

            while (time<1 && !did_work){

                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time++;
            }

            LOG.info("END_TO_WAIT=" + time + ", this=" + this);
            unDocking(ship);
        }
    }

    private boolean doWork() {
        if(work()) {
            this.setDid_work(true);
            return true;
        }
        return false;
    }


    private boolean work() {
        if (ship != null && ship.isStateOfCargoFill()) {
            LOG.info("START_WORK_SCLAD_SHIP_FILL: use_SPACE_sclad: " + this + ", sclad_current_size=" + storage.getGoods().size() + ", sclad_total=" + storage.getDEFAULT_STORAGE_CAPACITY());
            if (storage.getCurrentFreeCapacityInStorage() >= ship.getGoods().size()) {
                storage.moveGoodsFromShipToStorage(ship);
                LOG.info("END_WORK_SCLAD_SHIP_FILL: use_SPACE_sclad: " + this + ", sclad_current_size=" + storage.getGoods().size() + ", sclad_total=" + storage.getDEFAULT_STORAGE_CAPACITY());
                return true;
            } else {
                LOG.info("START_TRY_FIND_SHIP_+CARGO: sclad_DONT_HAVE_free_space: " + this + ", sclad_current_size=" + storage.getGoods().size() + ", sclad_total=" + storage.getDEFAULT_STORAGE_CAPACITY());
                Ship freeShip = port.getFirstShipWithFreeCargoCapacityIfPossible(ship.getGoods().size());
                if (freeShip != null) {
                    ship.doWorkWithOtherShip(freeShip);
                    port.changeStatusDockAfterFreeShipUse(freeShip);
                    LOG.info("END_TRY_FIND_SHIP Ship_+CARGO: ship_was_find" + this + " free_ship=" + freeShip + ", sclad_current_size=" + storage.getGoods().size() + ", sclad_total=" + storage.getDEFAULT_STORAGE_CAPACITY());
                    return true;
                } else {
                    return false;
                }
            }

        } else {
            assert ship != null;
            LOG.info("START SHIP DONT_FILL: in_dock=" + this + " size_ship=" + ship.getGoods().size() + " AND_Sclad_HAVE_CONTEINERS=" + storage.getGoods().size());
            if (storage.getGoods().size() >= ship.getCapacityCargo() && !ship.isStateOfCargoFill()) {
                storage.moveGoodsFromStorageToShip(ship);
                LOG.info("END SHIP DONT_FILL: in_dock=" + this + " size_ship=" + ship.getGoods().size() + " AND_Sclad_HAVE_CONTEINERS=" + storage.getGoods().size());
                return true;
            } else {
                LOG.info("START_TRY_FIND_SHIP_-CARGO: sclad_DONT_HAVE_free_space: " + this + ", sclad_current_size=" + storage.getGoods().size() + ", sclad_total=" + storage.getDEFAULT_STORAGE_CAPACITY());
                Ship shipWithCargoFill = port.getFirstShipWithCargoHaveNeedGoodsIfPossible(ship.getCapacityCargo());
                if (shipWithCargoFill != null) {
                    ship.doWorkWithOtherShip(shipWithCargoFill);
                    port.changeStatusDockAfterFreeShipUse(shipWithCargoFill);
                    LOG.info("END_TRY_FIND_SHIP_-CARGO: sclad_DONT_HAVE_free_space: " + this + ", sclad_current_size=" + storage.getGoods().size() + ", sclad_total=" + storage.getDEFAULT_STORAGE_CAPACITY());
                    return true;
                } else {
                    return false;
                }
            }
        }
    }


    @Override
    public String toString() {
        return nameOfDock + "_" + ship;
    }

}

