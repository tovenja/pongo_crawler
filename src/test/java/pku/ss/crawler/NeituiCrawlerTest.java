package pku.ss.crawler;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * NeituiCrawler Tester.
 *
 * @author <yangliqun_sx>
 * @version 1.0
 * @since 06/14/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:crawler-root-context.xml"})
public class NeituiCrawlerTest {
    @Autowired
    NeituiCrawler neituiCrawler;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: crawler()
     */
    @Test
    public void testCrawler() throws Exception {
        neituiCrawler.crawler();
    }

    @Test
    public void testColon() {
        String text = "地点市";
//        text = text.replaceAll("：",":");
        System.out.println(text);
        System.out.println(StringUtils.substringAfter(text, "："));
        System.out.println(StringUtils.length(text));
        System.out.println(StringUtils.substringBefore(text, "市"));
    }


} 
