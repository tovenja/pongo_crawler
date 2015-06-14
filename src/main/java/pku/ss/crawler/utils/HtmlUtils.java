package pku.ss.crawler.utils;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Blank
 * Date: 2015/6/14
 * Time: 13:18
 */
public class HtmlUtils {
    private static Logger logger = LoggerFactory.getLogger(HtmlUtils.class);
    private static RequestConfig config = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
    private static CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build();
    static ResponseHandler<? extends String> responseHandler = new ResponseHandler<String>() {
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

    /**
     * Using HttpClient to get html content
     *
     * @param url URL
     * @return html content
     */
    public static String getHtml(String url) {
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
