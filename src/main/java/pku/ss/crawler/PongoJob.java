package pku.ss.crawler;

import com.google.common.base.Preconditions;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Sets;
import com.google.common.hash.BloomFilter;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pku.ss.crawler.dao.JobDao;
import pku.ss.crawler.model.Job;
import pku.ss.crawler.utils.HtmlUtils;
import pku.ss.crawler.utils.SkillMatchUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by _blank_ on 2015/6/10.
 */
public class PongoJob implements Runnable {

    private int pageNum;
    private BloomFilter<byte[]> filter;
    private final ConcurrentHashMultiset<String> failedSet;
    private JobDao jobDao;
    private volatile AtomicInteger errorNum;
    private Logger logger = LoggerFactory.getLogger("PongoJob: pageNum[" + pageNum + "]");

    private static final String PONGO_URL = "http://job.csdn.net/Search/index?k=&t=1&f=";
    private static final String JOB_URL = "http://job.csdn.net";

    public PongoJob(int pageNum, BloomFilter<byte[]> filter, ConcurrentHashMultiset<String> failedSet, JobDao jobDao, AtomicInteger errorNum) {
        this.pageNum = pageNum;
        this.filter = filter;
        this.failedSet = failedSet;
        this.jobDao = jobDao;
        this.errorNum = errorNum;
    }

    @Override
    public void run() {
        checkErrorNum();
        Thread.currentThread().setName("Page:[" + pageNum + "]");
        String url = PONGO_URL + pageNum;
        String res = HtmlUtils.getHtml(url);
        Whitelist list = Whitelist.none().addTags("a", "div").addAttributes("a", "href").addAttributes("div", "class");
        String cleanRes = Jsoup.clean(res, list);
        Document document = Jsoup.parse(cleanRes);
        Elements elements = document.select("div.position_list>div.dTit>a");
        int size = 0;
        for (Element ele : elements) {
            checkErrorNum();
            String uri = ele.attr("href");
            if (StringUtils.startsWith(uri, "/p/")) {
                size++;
                if (alreadyCollected(uri)) {
                    continue;
                }
                if (!collectJobDetail(uri)) {
                    addFailedJob(uri);
                    logger.info("Job [{}] collect failed...", uri);
                }
            }
        }
        logger.info("Page[{}] has {} jobs", pageNum, size);
    }

    private void checkErrorNum() {
        if (errorNum.get() == 10) {
            logger.error("Error number eq 10, interrupt thread...");
        }
        if (errorNum.get() >= 10)
            Thread.currentThread().interrupt();
    }


    /**
     * Add failed job URI to FailedSet
     *
     * @param uri URI
     */
    private void addFailedJob(String uri) {
        logger.info("Job {} crawl failed, add to failed set...", uri);
        errorNum.getAndIncrement();
        failedSet.add(uri);
    }

    /**
     * To detect if uri already collected
     *
     * @param uri job URI
     * @return Boolean
     */
    private boolean alreadyCollected(String uri) {
        boolean res = filter.mightContain(uri.getBytes());
        if (res) {
            logger.info("Job {} already collected...", uri);
        }
        return res;
    }


    /**
     * collect job detail info
     *
     * @param uri URI
     */
    private boolean collectJobDetail(String uri) {
        String url = JOB_URL + uri;
        if (StringUtils.isBlank(url)) {
            logger.warn("job detail url is empty...");
            return false;
        }
        String jobHtml = HtmlUtils.getHtml(url);
        try {
            Document document = Jsoup.parse(jobHtml);
            Element positionEle = document.select("h2.highlight").first();
            Element workPlaceEle = document.select("ul.left-top>li").get(2);
            Element companyEle = document.select("dl.top>dd>h4>a").first();
            Element publishTimeEle = document.select("div.time>span").first();
            Elements descElements = document.select("div.myj-details-descrip");
            Preconditions.checkNotNull(positionEle);
            Preconditions.checkNotNull(workPlaceEle);
            Preconditions.checkNotNull(companyEle);
            Preconditions.checkNotNull(publishTimeEle);
            Preconditions.checkNotNull(descElements);
            String position = positionEle.ownText();
            String workPlace = workPlaceEle.ownText();
            String company = companyEle.ownText();
            String publishTime = StringUtils.split(publishTimeEle.ownText().trim(), " ")[0];
            String rawText = descElements.first().text() + descElements.get(1).text();
            logger.info("Position:{}", position);
            logger.info("WorkPlace:{}", workPlace);
            logger.info("Company name:{}", company);
            logger.info("Publish time:{}", publishTime);
            logger.info("Raw text:{}", rawText);
            Set<String> skills = matchSkills(descElements.get(1).text());
            assembleAndSaveJobDetail(position, workPlace, skills, company, publishTime, rawText, url);
            //sleep random seconds
            TimeUnit.SECONDS.sleep(RandomUtils.nextInt(3, 6));
        } catch (Exception e) {
            logger.error("Job parse error...", e);
            return false;
        }
        filter.put(uri.getBytes());
        return true;
    }

    private Set<String> matchSkills(String desc) {
        Set<String> res = Sets.newHashSet();
        if (StringUtils.isBlank(desc)) {
            return res;
        }
        Set<String> skills = Sets.newHashSet(SkillMatchUtils.getKeyWords(desc));
        logger.info("Get skills:{}", skills);
        return skills;
    }

    private void assembleAndSaveJobDetail(String position, String workPlace, Set<String> skills, String company, String publishTime, String rawText, String url) throws Exception {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy/MM/dd").parse(publishTime);
        } catch (ParseException e) {
            logger.error("Date parse error, default today...");
            date = Calendar.getInstance().getTime();
        }
        Preconditions.checkNotNull(position);
        Preconditions.checkNotNull(workPlace);
        Preconditions.checkNotNull(skills);
        Preconditions.checkNotNull(company);
        Preconditions.checkNotNull(date);
        Preconditions.checkNotNull(rawText);
        Preconditions.checkNotNull(url);
        Job job = new Job(company, position, skills, null, null, 0, workPlace, date, rawText, url);
        int res = jobDao.saveJobInfo(job);
        if (res == 1) {
            logger.info("Save job to db success...");
        } else {
            logger.warn("Save job to db FAILED...");
        }
    }
}
