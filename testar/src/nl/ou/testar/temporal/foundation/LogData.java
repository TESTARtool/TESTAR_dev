package nl.ou.testar.temporal.foundation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;

import java.util.*;

import static nl.ou.testar.temporal.util.Common.prettyCurrentDateTime;

public class LogData {
protected static final String csvsep=";";

    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> _log;
    @JsonAlias("comments")
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> _userComments;
    @CsvBindByName
    private String _modifieddate;



    public LogData() {
        _log = new ArrayList<>();
        _userComments = new ArrayList<>();
        _modifieddate = prettyCurrentDateTime();
    }


    public void addLog(String log) {
        this._log.add(log);
    }


    public List<String> get_log() {
        return _log;
    }


    public void set_log(List<String> _log) {
        this._log = _log;
    }

    public List<String> get_userComments() {
        return _userComments;
    }


    public void set_userComments(List<String> _userComments) {
        this._userComments = _userComments;
    }

    public void addUserComments(String comment) {
        this._userComments.add(comment);
    }


    public String get_modifieddate() {
        return _modifieddate;
    }


    public void set_modifieddate(String _modifieddate) {
        this._modifieddate = _modifieddate;
    }


}
