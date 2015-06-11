package pku.ss.crawler.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * SkillMatchUtils Tester.
 *
 * @author <yangliqun_sx>
 * @version 1.0
 * @since 06/11/2015
 */


public class SkillMatchUtilsTest {
    public String text0 = "1、三年以上的LINUX + PHP + MYSQL开发经验； \n" +
            "2、熟悉HTML，CSS等页面知识，熟练应用JS，Ajax等Web相关技术； \n" +
            "3、对MYSQL有深入的认识，熟练掌握关系数据库理论； \n" +
            "4、熟悉Linux操作系统和Shell； \n" +
            "5、有良好的编码习惯和撰写文档习惯； \n" +
            "6、具备强烈的责任心、良好的团队精神，较强的自学能力； \n" +
            "7、有独立工作能力，工作踏实认真，以及较强的团队协作精神； \n" +
            "8、有解决问题、钻研技术的兴趣和能力，善于沟通和表达；";

    public String text1 = "1、 全日制统招大学本科及以上学历；\n" +
            "2、 6年以上互联网产品经验或系统类产品经验，独自完成过中小系统的产品设计，有CRM、数据录入、处理、质检类产品设计工作经验者优先；\n" +
            "3、 熟练使用office系列、visio、axure、思维导图等产品设计工具；\n" +
            "4、 热爱互联网，熟悉并经常使用相关各类产品，对用户行为与心理有深刻洞察。\n" +
            "5、 沟通能力好、责任心强，能承受较大的工作压力；具有良好的团队合作精神和敬业精神；\n" +
            "6、 工作认真、细致、有条理、善总结，善沟通及团队合作；";
    public String text2 = "?\t熟练掌握 JavaScript, 无论是否野生.\n" +
            "?\t了解最新技术趋势, 优秀的技术架构能力.\n" +
            "?\t对 HTTP / REST / NoSQL 烂熟于胸\n" +
            "\n" +
            "加分项\n" +
            "?\t拥有自己的独立博客\n" +
            "?\tAngular / React / Grunt / Mocha / Coffee / ionic / ansyc / go\n" +
            "?\tmobile app 开发经验";
    public String text3 = "、7年以上mysql DBA经验，精通数据库管理与优化；j2ee \n" +
            "2、精通库表设计； \n" +
            "3、精通sql优化； \n" +
            "4、精通脚本编写游标、存储、触发器等、 \n" +
            "5、具有团队合作精神，思维清晰，细致耐心，责任心强，能独自完成工作，承受较大工作压力。";
    public String text4 = "任职要求：\n" +
            "1. 计算机或相关专业本科学历四年以上java的开发经验，;\n" +
            "2. 熟悉Oracle、DB2等数据库，有设计经验优先;\n" +
            "3. 熟悉J2EE开发、精通SSH,SSI框架，EXT,JQUERY等页面技术。\n" +
            "4. 熟悉WEBSERVICE开发。\n" +
            "5 .精通Java语言，熟悉Java内核机制;\n" +
            "6. 熟悉websphere、weblogic软件;\n" +
            "7. 有JUNIT单元测试经验.\n" +
            "8. 工作积极主动认真踏实，具有良好的责任感和敬业精神；\n" +
            "9. 具有较强的沟通、交流能力及团队协作奉献精神；\n" +
            "10. 具有强烈的进取上进好学心。\n" +
            "11. 有金融相关项目经验优先。\n" +
            "特别注意：不符合任职资格第一条者勿投简历！";
    public String text5 = "1.计算机相关专业毕业, 本科及以上学历。\n" +
            "2.两年以上软件开发经验，具备2年以上C/C++ /Objective-C开发经验。\n" +
            "3. 熟悉Objective-C程序设计，熟悉iPhone SDK及相关开发工具。\n" +
            "4. 逻辑思维能力强，责任感强，工作积极主动，有良好的团队协作意识。\n" +
            "5. 工作踏实认真，对移动应用开发行业充满热情，有不断提升自己的学习意识。\n" +
            "6. 已有应用在苹果App Store上线者或者有多个完整的ios项目经验者优先考虑。";
    public String text6 = "任职要求：\n" +
            "1、linux开发3年以上，其中虚拟化开发2年以上。精通C/C++开发\n" +
            "2、熟悉X86架构计算机及对应的虚拟化原理\n" +
            "3、了解openvswitch,gfs,ceph,kvm/xen/vbox,libvirt等计算、网络、存储和虚拟化相关技术和实现,（至少熟悉其中的两种）\n" +
            "4、熟悉分布式系统架构，并有分布式高可用系统开发经验";
    public String text7 = "1、专科及以上学历，金融/财务/计算机相关专业；\n" +
            "\n" +
            "2、较强的客户服务意识，良好的语言表达能力，分析和解决问题的能力；\n" +
            "\n" +
            "3、有会计核算基础或证券事务基础或金融行业软件技术支持工作背景优先；\n" +
            "\n" +
            "4、强烈的责任感，团队合作精神，自学能力，能承受较大的工作压力；\n" +
            "\n" +
            "5、掌握Java语言或VB开发语言，了解大型数据库ORACLE、SQL SERVER、DB2中的一种优先考虑；";
    public String text8 = "1. 具有一年以上Web GIS开发开发经验；\n" +
            "2. 熟悉百度地图或者高德地图接口，具有相关开发经验者优先；\n" +
            "3. 精通JAVA、JAVA SCRIPT、Ext JS、JQuery、JQuery Mobile、HTML、XML、CSS，精通Spring、Struts2、hibernate、ibatis（MyBatis）等开源框架和技术。\n" +
            "4. 精通数据库如 Oracle、Sql Server、MySQL 等的开发。\n" +
            "5. 有相关GPS监控软件系统开发经验者优先。";
    public String text9 = "1、专科及以上学历，计算机或相关专业，三年及以上IOS实际开发经验；\n" +
            "2、精通Objective-C、Mac OS X、Xcode；\n" +
            "3、精通IOS SDK中的UI、网络、数据库、XML/JSON解析等开发技巧；\n" +
            "4、熟悉IOS各版本的特点，对手机软件性能优化、内存优化、UI适配有一定经验；\n" +
            "5、精通常用软件架构模式，熟悉各种算法与数据结构，多线程，网络编程（Socket、TCP/IP、http/web service）等；\n" +
            "6、对视频在线和离线缓存播放、阅读器、语音等有深入了解的优先；\n" +
            "7、学习能力强，逻辑思维强，具有一定的团队管理经验优先。";
    public String text10 = "1、本科及以上学历，熟悉任一门编程语言，如C++,JAVA,.NET,ERLANG等； \n" +
            "2、熟练操作 Linux/Unix 操作系统；熟悉常用的数据结构与算法，逻辑思维清晰； \n" +
            "3、能坚持编写高质量，高效率代码；\n" +
            "4、热爱游戏，对开发有热忱，学习能力强，喜欢挑战自我，追求进步； \n" +
            "5、有大型软件开发项目和团队合作经验者优先； \n" +
            "6、欢迎优秀的应届毕业生。";

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getKeyWords(String text)
     */
    @Test
    public void testGetKeyWords() throws Exception {


        for (int i = 0; i < 11; i++) {
            SkillMatchUtils.getKeyWords(this.getClass().getField("text" + i).get(this).toString());
        }
    }


} 
