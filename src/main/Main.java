package main;

import main.httptaskserver.HttpTaskServer;
import main.kvserver.KVServer;


public class Main {

    public static void main(String[] args) {
        new KVServer().start();
        new HttpTaskServer().start();
    }

}
