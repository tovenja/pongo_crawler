package pku.ss.crawler;

import com.google.common.base.Preconditions;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Sets;
import com.google.common.hash.BloomFilter;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Blank
 * Date: 2015/6/14
 * Time: 13:07
 */
public class ChinaHRJob implements Runnable {
    private int pageNum;
    private BloomFilter<byte[]> filter;
    private final ConcurrentHashMultiset<String> failedSet;
    private JobDao jobDao;
    private volatile int errorNum;
    private Logger logger = LoggerFactory.getLogger("ChinaHR:");

    private static final String CHINA_HR = "http://www.chinahr.com/so/0/0-0-0-0-0-0-0-1001_1002__1001_1003_1018__1001_1003_1021__1001_1003_1019__1001_1003_1040-1001_1001-0-0-0-0-0-0-0/p";

    public ChinaHRJob(int pageNum, BloomFilter<byte[]> filter, ConcurrentHashMultiset<String> failedSet, JobDao jobDao) {
        this.pageNum = pageNum;
        this.filter = filter;
        this.failedSet = failedSet;
        this.jobDao = jobDao;
    }

    @Override
    public void run() {
        checkErrorNum();
        Thread.currentThread().setName("Page:[" + pageNum + "]");
        String url = CHINA_HR + pageNum;
        String res = HtmlUtils.getHtml(url);
        Whitelist list = Whitelist.none().addTags("a").addAttributes("a", "href", "class");
        String cleanRes = Jsoup.clean(res, list);
        Document document = Jsoup.parse(cleanRes);
        Elements elements = document.select("a.js_detail");
        int size = 0;
        for (Element ele : elements) {
            checkErrorNum();
            String uri = ele.attr("href");
            if (StringUtils.startsWith(uri, "http://www.chinahr.com/job/")) {
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
        logger.info("Page[{}] has {} jobs", pageNum / 20, size);
    }

    private void checkErrorNum() {
        if (errorNum >= 10)
            Thread.currentThread().interrupt();
    }


    /**
     * Add failed job URI to FailedSet
     *
     * @param uri URI
     */
    private void addFailedJob(String uri) {
        logger.info("Job {} crawl failed, add to failed set...", uri);
        synchronized (failedSet) {
            errorNum++;
        }
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
     * @param url URI
     */
    private boolean collectJobDetail(String url) {
        if (StringUtils.isBlank(url)) {
            logger.warn("job detail url is empty...");
            return false;
        }
        String jobHtml = HtmlUtils.getHtml(url);
        try {
            Document document = Jsoup.parse(jobHtml);
            Element positionEle = document.select("div.fl.job_infoLeft > h1 > a").first();
            Element workPlaceEle = document.select("body > div.container_header > div.wrap > div.fl.left_size.box_borderGray > div.job_desc > p.infoMa > a").first();
            Element companyEle = document.select("body > div.container_header > div.wrap > div.fl.left_size.box_borderGray > div.job_info > div.fl.job_infoLeft > span.subC_name > a").first();
            Element publishTimeEle = document.select("body > div.container_header > div.wrap > div.fl.left_size.box_borderGray > div.job_info > div.fl.job_infoLeft > span.detail_C_Date.fl").first();
            Elements descElements = document.select("body > div.container_header > div.wrap > div.fl.left_size.box_borderGray > div.job_desc > p.detial_jobSec");
            Element needNumEle = document.select("body > div.container_header > div.wrap > div.fl.left_size.box_borderGray > div.job_info > div.fl.job_infoLeft > div > span").last();
            Preconditions.checkNotNull(needNumEle);
            Preconditions.checkNotNull(positionEle);
            Preconditions.checkNotNull(workPlaceEle);
            Preconditions.checkNotNull(companyEle);
            Preconditions.checkNotNull(publishTimeEle);
            Preconditions.checkNotNull(descElements);
            int needNum = NumberUtils.toInt(needNumEle.ownText().replaceAll("[^0-9]", ""), 0);
            String position = positionEle.ownText();
            String workPlace = workPlaceEle.ownText();
            String company = companyEle.ownText();
            String publishTime = "20" + StringUtils.substringAfter(publishTimeEle.ownText(), "20");
            String rawText = descElements.first().text();
            if (descElements.size() > 1)
                rawText += descElements.get(1).text();
            logger.info("Position:{}", position);
            logger.info("WorkPlace:{}", workPlace);
            logger.info("Company name:{}", company);
            logger.info("Publish time:{}", publishTime);
            logger.info("Need num:{}", needNum);
            logger.info("Raw text:{}", rawText);
            Set<String> skills;
            if (descElements.size() > 1) {
                skills = matchSkills(descElements.get(1).text());
            } else {
                skills = matchSkills(descElements.first().text());
            }
            assembleAndSaveJobDetail(position, workPlace, skills, company, publishTime, rawText, needNum, url);
            //sleep random seconds
            TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 4));
        } catch (Exception e) {
            logger.error("Job parse error...", e);
            return false;
        }
        filter.put(url.getBytes());
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

    private void assembleAndSaveJobDetail(String position, String workPlace, Set<String> skills, String company, String publishTime, String rawText, int needNum, String url) throws Exception {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(publishTime);
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
        Job job = new Job(company, position, skills, null, null, needNum, workPlace, date, rawText, url);
        int res = jobDao.saveJobInfo(job);
        if (res == 1) {
            logger.info("Save job to db success...");
        } else {
            logger.warn("Save job to db FAILED...");
        }
    }


}
