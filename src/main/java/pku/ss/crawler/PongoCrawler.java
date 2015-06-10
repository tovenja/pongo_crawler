package pku.ss.crawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by _blank_ on 2015/6/10.
 */
public class PongoCrawler {


    private static ExecutorService es = Executors.newFixedThreadPool(10);


    public static void crawler() {
        for (int i = 0; i < 100; i++) {
            PongoJob job = new PongoJob(i);
            es.submit(job);
            break;
        }
        es.shutdown();

    }

}
