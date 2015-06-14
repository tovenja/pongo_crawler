package pku.ss.crawler.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by _blank_ on 2015/6/11.
 */
public class SkillMatchUtils {

    private static Logger logger = LoggerFactory.getLogger(SkillMatchUtils.class);
    private static Set<String> forbid = Sets.newHashSet();

    static {
        forbid.add("www");
        forbid.add("com");
        forbid.add("cn");
        forbid.add("net");
        forbid.add("--");
        forbid.add("qq");
        forbid.add("读写");
        forbid.add("链接");
        forbid.add("k-");
    }

    public static List<String> getKeyWords(String text) {
        text = StringUtils.replace(text, "\t", "");
        text = StringUtils.replace(text, "\n", "");
        configDict();
        List<Term> words = HanLP.segment(text);
        /*Segment nShortSegment = new NShortSegment().enableCustomDictionary(true).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        List<Term> words = nShortSegment.seg(text);*/
//        logger.info("Get keywords :[{}]", words);
        Map<String, String> res = Maps.newHashMap();
        for (Term term : words) {
            if (StringUtils.contains(term.word, "@") || StringUtils.contains(term.word, "%") || StringUtils.contains(term.word, "~") || forbid.contains(StringUtils.lowerCase(term.word))) {
                continue;
            }
            if ((term.nature.equals(Nature.nx) || term.nature.equals(Nature.gi)) && StringUtils.isNotBlank(term.word) && StringUtils.length(term.word) > 1 && StringUtils.length(term.word) < 10) {
                res.put(StringUtils.lowerCase(term.word), term.word);
            }
        }
        System.out.println(res.values());
        return Lists.newArrayList(res.values());
    }

    private static void configDict() {

        CustomDictionary.add("J2EE", "gi 1");
        CustomDictionary.add("j2ee", "gi 1");
        CustomDictionary.add("C++", "gi 1");
        CustomDictionary.add("c++", "gi 1");
        CustomDictionary.add("c/c++", "gi 1");
        CustomDictionary.add("C/C++", "gi 1");
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
        CustomDictionary.add("android OS", "gi 1");
        CustomDictionary.add("AndroidOS", "gi 1");
        CustomDictionary.add("Android OS", "gi 1");
        CustomDictionary.add("Node.js", "gi 1");
        CustomDictionary.add("node.js", "gi 1");
        CustomDictionary.add("NODE.js", "gi 1");
        CustomDictionary.add("NODE.JS", "gi 1");
        CustomDictionary.add("JAVA SCRIPT", "gi 1");
        CustomDictionary.add("Java Script", "gi 1");
        CustomDictionary.add("JavaScript", "gi 1");
        CustomDictionary.add("javaScript", "gi 1");
        CustomDictionary.add("javascript", "gi 1");
        CustomDictionary.add("JAVASCRIPT", "gi 1");
        CustomDictionary.add("Java EE", "gi 1");
        CustomDictionary.add("Java ee", "gi 1");
        CustomDictionary.add("JavaEE", "gi 1");
        CustomDictionary.add("javaEE", "gi 1");
        CustomDictionary.add("java EE", "gi 1");
        CustomDictionary.add("java ee", "gi 1");
        CustomDictionary.add("Spring MVC", "gi 1");
        CustomDictionary.add("SpringMVC", "gi 1");
        CustomDictionary.add("Spring mvc", "gi 1");
        CustomDictionary.add("spring MVC", "gi 1");
        CustomDictionary.add("spring mvc", "gi 1");

        CustomDictionary.insert("算法", "gi 1");
        CustomDictionary.add("数据结构", "gi 1");
        CustomDictionary.add("算法与数据结构", "gi 1");
        CustomDictionary.add("设计模式", "gi 1");
        CustomDictionary.add("数据库", "gi 1");
        CustomDictionary.add("读写", "n 1");
        CustomDictionary.add("开发者", "n 1");
        CustomDictionary.add("1-3", "n 1");
        CustomDictionary.add("2-3", "n 1");
        CustomDictionary.add("1-2", "n 1");
        CustomDictionary.add("3-4", "n 1");
        CustomDictionary.add("CET-6", "nx 1");
        CustomDictionary.add("CET-4", "nx 1");
        CustomDictionary.add("C＃", "gi 1");
        CustomDictionary.add("c＃", "gi 1");
        CustomDictionary.add("c#", "gi 1");
        CustomDictionary.add("C#", "gi 1");
        CustomDictionary.add("unity3d", "gi 1");
        CustomDictionary.add("Unity3d", "gi 1");
        CustomDictionary.add("Unity3D", "gi 1");
        CustomDictionary.add("Unity 3D", "gi 1");
        CustomDictionary.add("O2O", "gi 1");
        CustomDictionary.add("o2o", "gi 1");
        CustomDictionary.add("MAC", "n 1");
        CustomDictionary.add("Mac", "n 1");
        CustomDictionary.add("mac", "n 1");
        CustomDictionary.add("APPLE", "n 1");
        CustomDictionary.add("Apple", "n 1");
        CustomDictionary.add("apple", "n 1");
        CustomDictionary.add("com", "n 1");
        CustomDictionary.add("cn", "n 1");
        CustomDictionary.add("--", "n 1");
        CustomDictionary.add("CET6", "gi 1");
        CustomDictionary.add("CET4", "gi 1");
        CustomDictionary.add("cet4", "gi 1");
        CustomDictionary.add("cet6", "gi 1");
        CustomDictionary.add("Cet6", "gi 1");
        CustomDictionary.add("Cet4", "gi 1");
        CustomDictionary.add("多线程", "gi 1");
        CustomDictionary.add("高并发", "gi 1");
        CustomDictionary.add("", "gi 1");
        CustomDictionary.add("Cet4", "gi 1");

    }

}
