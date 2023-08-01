package com.taehyunDev;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class bmcViewer {

    private static Set<Integer> ipServerList;
    private static Set<Integer> ipBmcList;
    private static long pastTime;
    private static final Integer MAX_RANGE = 255;
    public static final String IP_FORMAT = "192.168.0.";
    private static final Integer[] excludeList = {1, 20, 200, 201};
    public static Integer workerNum = 60;
    public static Integer taskNum;
    //{1, 3, 4, 11, 18, 20, 45, 121, 129, 144, 154, 160, 161, 172, 200, 201, 202};

    public synchronized static void addIP(Integer backIP, Boolean isServer){
        if(isServer){
            ipServerList.add(backIP);
        }else{
            ipBmcList.add(backIP);
        }
    }

    public synchronized static void increaseTask(){
        taskNum++;
        if(taskNum.equals(MAX_RANGE)){
            notifyComplete();
        }
        displayProgressBar(taskNum);
    }

    public static void displayProgressBar(Integer taskNum){
        int progressValue = (int) Math.ceil((taskNum / MAX_RANGE.doubleValue()) * 100d);
        int progressKey = (int) Math.ceil(progressValue / 5d);
        String progressStat = "";
        for(int i = 0; i < progressKey; i++){
            progressStat = progressStat.concat(">");
        }
        for(int i = 0; i < 20 - progressKey; i++){
            progressStat = progressStat.concat(" ");
        }
        System.out.print("\r["+progressStat+"] "+progressValue+"%");
    }


    public static void notifyComplete(){
        long differ = System.currentTimeMillis() - pastTime;

        Arrays.asList(excludeList).forEach(ipServerList::remove);
        Arrays.asList(excludeList).forEach(ipBmcList::remove);

        System.out.println("\n[Server]");
        ipServerList.stream().sorted().forEach(s -> System.out.println("  "+IP_FORMAT+s));
        System.out.println("\n[Bmc]");
        ipBmcList.stream().sorted().forEach(s -> System.out.println("  "+IP_FORMAT+s));
        System.out.println("\n총 소요시간: "+differ/1000+"s");
        System.exit(0);
    }

    public void searchLocal(){
        pastTime = System.currentTimeMillis();
        ipServerList = new HashSet<>();
        ipBmcList = new HashSet<>();
        threadHandler worker = threadHandler.getInstance();
        for(int i = 0; i <= MAX_RANGE; i++){
            worker.execute(i);
        }
    }

    public static void main(String[] args){


        if( (args.length > 0) && (Integer.parseInt(args[0]) < 256) ){
            System.out.println("[Error] 쓰레드 갯수를 다음 범위로 지정하십시오. (1-256).");
            return;
        }


        workerNum = Integer.parseInt(args[0]);


        taskNum = 0;
        new bmcViewer().searchLocal();
    }
}
