package pku.ss.crawler;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by _blank_ on 2015/6/10.
 */
public class PongoCrawler {


    public static final String CRAWLED_DATA = "/data/pongo/crawled.dat";
    public static final String FAILED_DATA = "/data/pongo/failed.dat";
    private static ExecutorService es = Executors.newFixedThreadPool(10);
    static BloomFilter<byte[]> filter = BloomFilter.create(Funnels.byteArrayFunnel(), 500);
    private static Logger logger = LoggerFactory.getLogger(PongoJob.class);
    private static ConcurrentHashMultiset<String> failedSet = ConcurrentHashMultiset.create();

    public static void crawler() throws Exception {
        if (Files.exists(Paths.get(CRAWLED_DATA))) {
            logger.info("Crawled data file exist, reading from file...");
            filter = BloomFilter.readFrom(new FileInputStream(CRAWLED_DATA), Funnels.byteArrayFunnel());
        } else {
            com.google.common.io.Files.createParentDirs(new File(CRAWLED_DATA));
        }
        if (Files.exists(Paths.get(FAILED_DATA))) {
            logger.info("Failed set file exist, reading from file...");
            ObjectInputStream failedIn = new ObjectInputStream(new FileInputStream(FAILED_DATA));
            failedSet = (ConcurrentHashMultiset<String>) failedIn.readObject();
            failedIn.close();
        } else {
            com.google.common.io.Files.createParentDirs(new File(CRAWLED_DATA));
        }

        for (int i = 0; i < 100; i++) {
            PongoJob job = new PongoJob(i, filter, failedSet);
            es.submit(job);
            break;
        }
        es.shutdown();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(CRAWLED_DATA));
        while (!es.isTerminated())
            ;
        logger.info("Crawled data finished, writing out to file...");
        filter.writeTo(out);
        ObjectOutputStream failedOut = new ObjectOutputStream(new FileOutputStream(FAILED_DATA));
        failedOut.writeObject(failedSet);
        out.close();
        failedOut.close();
    }

}
