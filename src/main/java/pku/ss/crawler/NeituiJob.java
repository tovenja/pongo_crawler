package pku.ss.crawler;

import com.google.common.base.Preconditions;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Sets;
import com.google.common.hash.BloomFilter;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
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
 * Time: 14:51
 */
public class NeituiJob implements Runnable {
    private int pageNum;
    private BloomFilter<byte[]> filter;
    private final ConcurrentHashMultiset<String> failedSet;
    private JobDao jobDao;
    private Logger logger = LoggerFactory.getLogger("Neitui:");

    private static final String NEITUI = "http://www.neitui.me/neitui/type=all&page=PN.html";
    private static final String BASE = "http://www.neitui.me";

    public NeituiJob(int pageNum, BloomFilter<byte[]> filter, ConcurrentHashMultiset<String> failedSet, JobDao jobDao) {
        this.pageNum = pageNum;
        this.filter = filter;
        this.failedSet = failedSet;
        this.jobDao = jobDao;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Page:[" + pageNum + "]");
        String url = StringUtils.replace(NEITUI, "PN", pageNum + "");
        String res = HtmlUtils.getHtml(url);
        Whitelist list = Whitelist.none().addTags("a").addAttributes("a", "href");
        String cleanRes = Jsoup.clean(res, list);
        Document document = Jsoup.parse(cleanRes);
        Elements elements = document.select("a");
        int size = 0;
        for (Element ele : elements) {
            String uri = ele.attr("href");
            if (StringUtils.startsWith(uri, "/?name=job&handle=detail")) {
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


    /**
     * Add failed job URI to FailedSet
     *
     * @param uri URI
     */
    private void addFailedJob(String uri) {
        logger.info("Job {} crawl failed, add to failed set...", uri);
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
        if (StringUtils.isBlank(uri)) {
            logger.warn("job detail url is empty...");
            return false;
        }
        String url = BASE + uri;
        String jobHtml = HtmlUtils.getHtml(url);
        try {
            Document document = Jsoup.parse(jobHtml);
            Element positionEle = document.select("#detail > div > ul > li > div.cont > div:nth-child(2) > strong").first();
            Element workPlaceEle = document.select("#detail > div > ul > li > div.cont > div.jobtitle > span.jobtitle-r").first();
            Element companyEle = document.select("#detail > div > ul > li > div.cont > div.jobtitle > span.jobtitle-l").first();
            Element publishTimeEle = document.select("#detail > div > ul > li > div.cont > div:nth-child(1)").first();
            Element descElement = document.select("#detail > div > ul > li > div.cont > div.jobdetail.nooverflow").first();
            Preconditions.checkNotNull(positionEle);
            Preconditions.checkNotNull(workPlaceEle);
            Preconditions.checkNotNull(companyEle);
            Preconditions.checkNotNull(publishTimeEle);
            Preconditions.checkNotNull(descElement);
            int needNum = 0;
            String position = positionEle.ownText();
            String workPlace = StringUtils.substringAfter(workPlaceEle.ownText(), "：");
            workPlace = StringUtils.substringBefore(workPlace, "市");
            if (StringUtils.length(workPlace) > 6)
                workPlace = StringUtils.substring(workPlace, 0, 2);
            String company = StringUtils.substringAfter(companyEle.ownText(), "：");
            String publishTime = publishTimeEle.outerHtml();
            publishTime = publishTime.replaceAll("[^0-9]", "");
            publishTime = "2015" + StringUtils.substring(publishTime, publishTime.length() - 4, publishTime.length());
            String rawText = descElement.text();
            logger.info("Position:{}", position);
            logger.info("WorkPlace:{}", workPlace);
            logger.info("Company name:{}", company);
            logger.info("Publish time:{}", publishTime);
            logger.info("Need num:{}", needNum);
            logger.info("Raw text:{}", rawText);
            Set<String> skills;
            int start = StringUtils.indexOf(rawText, "要求");
            int zStart = StringUtils.indexOf(rawText, "资格");
            if (start != -1) {
                skills = matchSkills(rawText.substring(start));
            } else if (zStart != -1) {
                skills = matchSkills(rawText.substring(zStart));
            } else {
                skills = matchSkills(rawText);
            }
            assembleAndSaveJobDetail(position, workPlace, skills, company, publishTime, rawText, needNum, url);
            //sleep random seconds
            TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 4));
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

    private void assembleAndSaveJobDetail(String position, String workPlace, Set<String> skills, String company, String publishTime, String rawText, int needNum, String url) throws Exception {
        Date date;
        try {
            date = new SimpleDateFormat("yyyyMMdd").parse(publishTime);
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
