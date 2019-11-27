package com.exercises2.port;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Getter
@Setter
public class Port extends Thread{
    private static Logger LOG = LoggerFactory.getLogger(Port.class);

    private int numberOfShips;
    private List<Dock> docks = new ArrayList<>();
    private BlockingQueue<Ship> shipsInPort;

    public Port(int numberOfDocks, int numberOfShips) {
        this.numberOfShips = numberOfShips;
        this.shipsInPort = new ArrayBlockingQueue<>(numberOfShips, true);

        addShipsToPort();
        createDock(numberOfDocks);
    }

    private void createDock(int numberOfDocks){
        for (int i = 0; i < numberOfDocks; i++) {
            docks.add(new Dock(this, "Dock #" + i, shipsInPort));
        }
        LOG.info("size docks in port= " + docks.size());
    }

    private void addShipsToPort() {
        for (int i = 0; i < numberOfShips; i++) {
            try {
                shipsInPort.put(new Ship("Ship #" + i));
            } catch (InterruptedException e) {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
        }
        LOG.info("ships quantity came to port= " + shipsInPort.size());
    }

    Ship getFirstShipWithFreeCargoCapacityIfPossible(int needSize){
        for (Dock dock: docks){
            int currentCapacityCargoShipInDock;
            if(dock.getShip() != null && !dock.isDid_work()){
                currentCapacityCargoShipInDock = dock.getShip().getCapacityCargo();
                if(currentCapacityCargoShipInDock >= needSize && !dock.getShip().isStateOfCargoFill()){
                    return dock.getShip();
                }
            }

        }
        return null;
    }

    Ship getFirstShipWithCargoHaveNeedGoodsIfPossible(int needSize){
        for (Dock dock: docks){
            int currentCargoShipInDock;
            if(dock.getShip() != null && !dock.isDid_work()){
                currentCargoShipInDock = dock.getShip().getGoods().size();
                if(currentCargoShipInDock >= needSize ){
                    return dock.getShip();
                }
            }
        }
        return null;
    }

    void changeStatusDockAfterFreeShipUse(Ship ship){
        if(ship != null){
            for (Dock dock : docks) {
                if (dock.getShip().equals(ship)) {
                    dock.setDid_work(true);
                    LOG.info("ChangeStatusDockAfterFreeShipUse, dock=" + dock);
                    break;
                }
            }
        }
    }

    @Override
    public void run() {

        while (shipsInPort.size()>0) {
            startDocking();
        }
    }

    private void startDocking() {
        for (Dock dock : docks) {
            if (dock.getShip() == null) {
                try {
                    dock.docking(shipsInPort.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(dock).start();
            }
        }
    }

}
