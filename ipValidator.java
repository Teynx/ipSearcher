package com.taehyunDev;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ipValidator implements Runnable {
    private Integer backIP;

    public ipValidator(Integer backIP) {
        this.backIP = backIP;
    }

    private Boolean checkIpPort(String tempIP, Integer tempPort) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(tempIP, tempPort), 1500);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private int checkIP(String tempIP) {
        if (checkIpPort(tempIP, 443)) {
            return 2;
        }
        if (checkIpPort(tempIP, 22)) {
            return 1;
        }
        return 0;
    }

    public void run() {
        String ipFormat = "192.168.0.";
        int tempResult = checkIP(ipFormat + backIP);
        bmcViewer.increaseTask();
        if (tempResult == 2) {
            bmcViewer.addIP(this.backIP,false);
            return;
        }
        if (tempResult == 1) {
            bmcViewer.addIP(this.backIP, true);
        }
    }
}
