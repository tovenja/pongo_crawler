package pku.ss.crawler;

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

    private Logger logger = LoggerFactory.getLogger("PongoJob: " + Thread.currentThread().getName());

    private int pageNum;

    public PongoJob(int pageNum) {
        this.pageNum = pageNum;
    }

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


    @Override
    public void run() {
        Thread.currentThread().setName("Page:[" + pageNum + "]");
        String url = PONGO_URL + pageNum;
        String res = getHtml(url);
        Whitelist list = Whitelist.none().addTags("a", "div").addAttributes("a", "href").addAttributes("div", "class");
        String cleanRes = Jsoup.clean(res, list);
//        System.out.println(cleanRes);
        Document node = Jsoup.parse(cleanRes);
        Elements elements = node.select("div.position_list>div.dTit>a");
        for (Element ele : elements) {
            if (StringUtils.startsWith(ele.attr("href"), "/p/"))
                System.out.println(ele.attr("href"));
        }

    }

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
