package com.example.tcpserver.model;

public enum CommandID {
    STOP(1),
    EXCHANGE(2),
    EXCHANGE_WAIT(3),
    DISCONNECT(4);
    public int val;

    CommandID(int val) {
        this.val = val;
    }

    public static CommandID commandById(int id) {
        switch (id) {
            case 1 -> {return STOP;}
            case 2 -> {return EXCHANGE;}
            case 3 -> {return EXCHANGE_WAIT;}
            case 4 -> {return DISCONNECT;}
            default -> {
                System.err.println("Wrong command id. Disconnects client.");
                return DISCONNECT;
            }
        }
    }
}
