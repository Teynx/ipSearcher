package com.taehyunDev;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class bmcViewer {
    private static Set<Integer> ipServerList;

    private static Set<Integer> ipBmcList;

    private static long pastTime;

    private static final Integer MAX_RANGE = 255;

    public static final String IP_FORMAT = "192.168.0.";

    private static final Integer[] excludeList = new Integer[] { 1, 20, 200, 201 };

    public static Integer workerNum = 60;

    public static Integer taskNum;

    private static String padString(String input, int length) {
        if (input.length() >= length) {
            return input;
        }
        return input + " ".repeat(length - input.length());
    }

    public static synchronized void addIP(Integer backIP, Boolean isServer) {
        if (isServer) {
            ipServerList.add(backIP);
        } else {
            ipBmcList.add(backIP);
        }
    }

    public static synchronized void increaseTask() {
        taskNum ++;
        if (taskNum.equals(MAX_RANGE)) {
            notifyComplete();
        }
        displayProgressBar(taskNum);
    }

    public static void displayProgressBar(Integer taskNum) {
        int progressValue = (int)Math.ceil(taskNum / MAX_RANGE.doubleValue() * 100.0D);
        int progressKey = (int)Math.ceil(progressValue / 5.0D);
        String progressStat = "";
        int i;
        for (i = 0; i < progressKey; i++)
            progressStat = progressStat.concat(">");
        for (i = 0; i < 20 - progressKey; i++)
            progressStat = progressStat.concat(" ");
        System.out.print("\r[" + progressStat + "] " + progressValue + "%");
    }

    public static void notifyComplete() {
        long differ = System.currentTimeMillis() - pastTime;
        Objects.requireNonNull(ipServerList);
        Arrays.asList(excludeList).forEach(ipServerList::remove);
        Objects.requireNonNull(ipBmcList);
        Arrays.asList(excludeList).forEach(ipBmcList::remove);
        String elTime = padString(String.valueOf(differ / 1000.0D), 5);
        System.out.println("\n+-----------------------------------------------+\n|  BMC-SMI 2.0      Elapsed Time: " + elTime + "s        |\n|-----------------------------------------------|\n|-----------------------------------------------|\n|  Bmc:                                         |\n|   Ip                  Brand                   |\n|===============================================|");
        ipBmcList.stream().sorted().forEach(s -> System.out.println("|  " + padString(IP_FORMAT + s, 13) + "        " + padString(fileUtil.getInstance().getVendorForIp("192.168.0." + s), 10) + "              |"));
        System.out.println("|-----------------------------------------------|\n|  Server:                                      |\n|   Ip                  Brand                   |\n|===============================================|");
        ipServerList.stream().sorted().forEach(s -> System.out.println("|  " + padString(IP_FORMAT + s, 13) + "        " + padString(fileUtil.getInstance().getVendorForIp("192.168.0." + s), 10) + "              |"));
        System.out.println("+-----------------------------------------------+");
        System.exit(0);
    }

    public void searchLocal() {
        pastTime = System.currentTimeMillis();
        ipServerList = new HashSet<>();
        ipBmcList = new HashSet<>();
        threadHandler worker = threadHandler.getInstance();
        for (int i = 0; i <= MAX_RANGE; i++)
            worker.execute(i);
    }

    public static void main(String[] args) {
        if (args.length > 0 && Integer.parseInt(args[0]) < 256) {
            System.out.println("[Error] (1-256).");
            return;
        }
        try {
            fileUtil.getInstance().readMacVendorData();
        } catch (Exception e) {
            System.out.println("Error: Problem while loading Mac Data File.");
            System.exit(1);
        }
        workerNum = (Integer.parseInt(args[0]));
        taskNum = 0;
        new bmcViewer().searchLocal();
    }
}
