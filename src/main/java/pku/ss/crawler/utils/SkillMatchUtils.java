package pku.ss.crawler.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by _blank_ on 2015/6/11.
 */
public class SkillMatchUtils {

    private static Logger logger = LoggerFactory.getLogger(SkillMatchUtils.class);

    public static List<String> getKeyWords(String text) {
        text = StringUtils.replace(text, "\t", "");
        text = StringUtils.replace(text, "\n", "");
        configDict();
        List<Term> words = HanLP.segment(text);
        /*Segment nShortSegment = new NShortSegment().enableCustomDictionary(true).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        List<Term> words = nShortSegment.seg(text);*/
        logger.info("Get keywords :[{}]", words);
        Map<String, String> res = Maps.newHashMap();
        for (Term term : words) {
            if ((term.nature.equals(Nature.nx) || term.nature.equals(Nature.gi)) && StringUtils.isNotBlank(term.word)) {
                res.put(StringUtils.lowerCase(term.word), term.word);
            }
        }
//        System.out.println(res.values());
        return Lists.newArrayList(res.values());
    }

    private static void configDict() {
        CustomDictionary.add("J2EE", "gi 1");
        CustomDictionary.add("j2ee", "gi 1");
        CustomDictionary.add("C++", "gi 1");
        CustomDictionary.add("c++", "gi 1");
        CustomDictionary.add("db2", "gi 1");
        CustomDictionary.add("DB2", "gi 1");
        CustomDictionary.add("分布式系统", "gi 1");
        CustomDictionary.add("x86", "gi 1");
        CustomDictionary.add("X86", "gi 1");
        CustomDictionary.add("Sql Server", "gi 1");
        CustomDictionary.add("SQL Server", "gi 1");
        CustomDictionary.add("SQLServer", "gi 1");
        CustomDictionary.add("SQLSERVER", "gi 1");
        CustomDictionary.add("SQL SERVER", "gi 1");
        CustomDictionary.add("TCP/IP", "gi 1");
        CustomDictionary.add("tcp/ip", "gi 1");
        CustomDictionary.add("Tcp/Ip", "gi 1");
        CustomDictionary.add("web service", "gi 1");
        CustomDictionary.add("Web Service", "gi 1");
        CustomDictionary.add("WebService", "gi 1");
        CustomDictionary.add("WEB SERVICE", "gi 1");
        CustomDictionary.add("Mac OS X", "n 1");
        CustomDictionary.add(".net", "gi 1");
        CustomDictionary.add(".NET", "gi 1");
        CustomDictionary.add(".Net", "gi 1");
        CustomDictionary.add("App Store", "n 1");
        CustomDictionary.add("AppStore", "n 1");
        CustomDictionary.add("Appstore", "n 1");
        CustomDictionary.add("appstore", "n 1");

        CustomDictionary.insert("算法", "gi 1");
        CustomDictionary.add("数据结构", "gi 1");
        CustomDictionary.add("设计模式", "gi 1");
        CustomDictionary.add("数据库", "gi 1");

    }

}
