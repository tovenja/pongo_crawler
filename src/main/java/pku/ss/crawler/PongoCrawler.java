package pku.ss.crawler;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by _blank_ on 2015/6/10.
 */
public class PongoCrawler {


    private static ExecutorService es = Executors.newFixedThreadPool(10);
    private static final BloomFilter<String> filter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 500);

    public static void crawler() {
        for (int i = 0; i < 100; i++) {
            PongoJob job = new PongoJob(i,filter);
            es.submit(job);
            break;
        }
        es.shutdown();

    }

}
