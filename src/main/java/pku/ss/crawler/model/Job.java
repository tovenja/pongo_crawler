package pku.ss.crawler.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Blank
 * Date: 2015/6/10
 * Time: 21:27
 */
public class Job implements Serializable{


    private String companyName;
    private String position;
    private Set<String> skills;
    private String email;
    private String tel;
    private int needNum;
    private String workPlace;
    private Date publishTime;
    private String rawText;
    private String url;


    public Job(String companyName, String position, Set<String> skills, String email, String tel, int needNum, String workPlace, Date publishTime, String rawText, String url) {
        this.companyName = companyName;
        this.position = position;
        this.skills = skills;
        this.email = email;
        this.tel = tel;
        this.needNum = needNum;
        this.workPlace = workPlace;
        this.publishTime = publishTime;
        this.rawText = rawText;
        this.url = url;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkill(String skill){
        this.skills.add(skill);
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getNeedNum() {
        return needNum;
    }

    public void setNeedNum(int needNum) {
        this.needNum = needNum;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }
}
