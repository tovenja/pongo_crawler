package pku.ss.crawler;

import com.google.common.base.Preconditions;
import com.google.common.hash.BloomFilter;
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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by _blank_ on 2015/6/10.
 */
public class PongoJob implements Runnable {

    private int pageNum;
    private BloomFilter<String> filter;

    private Logger logger = LoggerFactory.getLogger("PongoJob: pageNum[" + pageNum + "]");

    private static final String PONGO_URL = "http://job.csdn.net/Search/index?k=&t=1&f=";
    private static final String JOB_URL = "http://job.csdn.net";


    private static RequestConfig config = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
    private static CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build();
    ResponseHandler<? extends String> responseHandler = new ResponseHandler<String>() {
        @Override
        public String handleResponse(
                final HttpResponse response) throws IOException {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity == null ? null : EntityUtils.toString(entity);
            } else {
                throw new ClientProtocolException(" Unexpected response status: " + status);
            }
        }
    };


    public PongoJob(int pageNum, BloomFilter<String> filter) {
        this.pageNum = pageNum;
        this.filter = filter;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Page:[" + pageNum + "]");
        String url = PONGO_URL + pageNum;
        String res = getHtml(url);
        Whitelist list = Whitelist.none().addTags("a", "div").addAttributes("a", "href").addAttributes("div", "class");
        String cleanRes = Jsoup.clean(res, list);
        Document document = Jsoup.parse(cleanRes);
        Elements elements = document.select("div.position_list>div.dTit>a");
        for (Element ele : elements) {
            String uri = ele.attr("href");
            if (StringUtils.startsWith(uri, "/p/") && !alreadyCollected(uri)) {
                if (!collectJobDetail(uri)) {
                    addFailedJob(uri);
                    logger.info("Job [{}] collect failed...", uri);
                }
            }
        }
    }


    /**
     * 添加失败的任务到失败集合
     *
     * @param uri 失败URI
     */
    private synchronized void addFailedJob(String uri) {

    }

    /**
     * 判断是否抓取过，使用BloomFilter查询
     *
     * @param uri job详情页
     * @return 是否抓取过
     */
    private boolean alreadyCollected(String uri) {
        return filter.mightContain(uri);
    }


    /**
     * 获取job详细信息，首先获取html内容，然后解析，并存档
     *
     * @param uri 超链接
     */
    private boolean collectJobDetail(String uri) {
        String url = JOB_URL + uri;
        if (StringUtils.isBlank(url)) {
            logger.warn("job detail url is empty...");
            return false;
        }
        String jobHtml = getHtml(url);
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
            logger.info("Position:{}", positionEle.ownText());
            logger.info("WorkPlace:{}", workPlaceEle.ownText());
            logger.info("Company name:{}", companyEle.ownText());
            logger.info("Publish time:{}", publishTimeEle.ownText().trim());
            logger.info("Raw text:{}", descElements.first().text());
            System.out.println(positionEle.ownText());
            System.out.println(workPlaceEle.ownText());
            System.out.println(companyEle.ownText());
            System.out.println(publishTimeEle.ownText().trim());
            System.out.println(positionEle.ownText());
        } catch (Exception e) {
            logger.error("Job parse error...", e);
            return false;
        }
        filter.put(uri);
        return true;
    }

    /**
     * 使用HttpClient获取转义后的页面html内容，失败会重试2次
     *
     * @param url 超链接
     * @return html内容
     */
    private String getHtml(String url) {
        HttpGet get = new HttpGet(url);
        int retry = 3;
        String res = null;
        while (StringUtils.isBlank(res) && retry-- > 0) {
            try {
                res = client.execute(get, responseHandler);
            } catch (IOException e) {
                logger.error("..........IOException..........");
            }
            try {
                if (StringUtils.isBlank(res))
                    TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {

            }
        }
        return res;
    }
}
