package pku.ss.crawler;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ChinaHRCrawler Tester.
 *
 * @author <yangliqun_sx>
 * @version 1.0
 * @since 06/14/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:crawler-root-context.xml"})
public class ChinaHRCrawlerTest {

    @Autowired
    ChinaHRCrawler chinaHRCrawler;

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
        chinaHRCrawler.crawler();
    }

    @Test
    public void testDateFormat() throws Exception {
        Date date;
        String t = "刷新日期：2015-06-14";
        String tm = "20" + StringUtils.substringAfter(t, "20");
        System.out.println(tm);
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(tm);
        } catch (ParseException e) {
            System.out.println("Parse error");
            date = Calendar.getInstance().getTime();
        }
        System.out.println(date);
    }


    @Test
    public void testNeedNum() {
        String num = "招聘人数：2人";
        String res = StringUtils.substringBetween(num, "：", "人");
        System.out.println(res);
        String reg = "[^0-9]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(num);
        System.out.println(num.replaceAll("[^0-9]", ""));
    }

    @Test
    public void testSet() {
        Set<String> set = Sets.newHashSet();
        set.add("kkk");
        System.out.println(set);
    }


} 
