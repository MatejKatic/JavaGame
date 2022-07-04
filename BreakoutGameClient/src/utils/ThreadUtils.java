/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel.bele
 */
public class ThreadUtils {

    private ThreadUtils() {
    }

    public static void stopExecutor(ExecutorService executorService, int timeout, TimeUnit timeUnit) {
        try {
            executorService.shutdown();
            executorService.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        }
    }

}
