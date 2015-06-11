package pku.ss.crawler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * PongoCrawler Tester.
 *
 * @author <yangliqun_sx>
 * @version 1.0
 * @since 06/10/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:crawler-root-context.xml"})
public class PongoCrawlerTest {
    @Autowired
    PongoCrawler pongoCrawler;
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
        pongoCrawler.crawler();
    }
} 
