package pku.ss.crawler.dao;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pku.ss.crawler.model.Job;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Blank
 * Date: 2015/6/11
 * Time: 21:20
 */
@Repository
public class JobDao {

    @Autowired
    private JdbcTemplate masterJdbcTemplate;

    private Logger logger = LoggerFactory.getLogger(JobDao.class);
    private static final String TABLE_TEST = "sedata";

    public int saveJobInfo(Job job) {
        final String sql = "INSERT INTO " + TABLE_TEST + "(id,com_name,position,skills,email,tel,need_num,workplace,publish_time,raw_text,url) VALUES (NULL,?,?,?,?,?,?,?,?,?,?)";
        List<Object> param = Lists.newArrayList();
        param.add(job.getCompanyName());
        param.add(job.getPosition());
        param.add(Joiner.on(" ").skipNulls().join(job.getSkills()));
        param.add(job.getEmail());
        param.add(job.getTel());
        param.add(job.getNeedNum());
        param.add(job.getWorkPlace());
        param.add(job.getPublishTime());
        param.add(job.getRawText());
        param.add(job.getUrl());
        return masterJdbcTemplate.update(sql, param.toArray());
    }

    public void setMasterJdbcTemplate(JdbcTemplate masterJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
    }
}
