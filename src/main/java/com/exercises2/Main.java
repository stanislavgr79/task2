package com.exercises2;


import com.exercises2.port.Port;

public class Main {

    public static void main(String[] args) {

        Port port = new Port(3, 20);
        port.start();
    }
}
