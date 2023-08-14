package com.taehyunDev;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class threadHandler {
    private static threadHandler instance = null;

    private static ExecutorService executorService = null;

    public static threadHandler getInstance() {
        if (instance == null)
            instance = new threadHandler();
        return instance;
    }

    public void execute(Integer backIP) {
        if (executorService == null)
            executorService = Executors.newFixedThreadPool(bmcViewer.workerNum);
        executorService.submit(new ipValidator(backIP));
    }
}
