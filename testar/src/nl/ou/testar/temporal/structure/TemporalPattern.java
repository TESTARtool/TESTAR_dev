package nl.ou.testar.temporal.structure;

import nl.ou.testar.temporal.util.TemporalType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TemporalPattern implements Cloneable{



    private long id;// sequential nr
    private TemporalType temporalType;
    private String alias; //short description e.g. spec pattern name
    private String propertytemplate;  // e.g. G(b0->Fb1)
    private List<String> params; //b0,b1,b2,bn
    private int paramcount;



    private List<String> filtertags;

    private List<String> comments;



    public TemporalPattern() {
    }

    public TemporalPattern(long id, TemporalType tlType, String alias, String propertypattern) {
        this.id = id;
        this.temporalType = tlType;
        this.alias = alias;
        this.propertytemplate = propertypattern;
        this.comments=new ArrayList<String>();
        this.modifieddate= LocalDateTime.now().toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPropertytemplate() {
        return propertytemplate;
    }

    public void setPropertytemplate(String propertytemplate) {
        this.propertytemplate = propertytemplate;
    }

    public List<String> getParams() {
        return params;
    }

    public int getParamcount() {
        return paramcount;
    }

    public void setParamcount(int paramcount) {
        this.paramcount = paramcount;
    }

    public List<String> getFiltertags() {
        return filtertags;
    }

    public void setFiltertags(List<String> filtertags) {
        this.filtertags = filtertags;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public List<String> getComments() {
        return comments;
    }

    public String getModifieddate() {
        return modifieddate;
    }

    public void setModifieddate(String modifieddate) {
        this.modifieddate = modifieddate;
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }

    private String modifieddate;
    private String formatVersion="20190603";

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
    public Object clone() throws            CloneNotSupportedException
    {
        return super.clone();
    }

}