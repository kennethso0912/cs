package com.ubtechinc.cruiser.wayguide.model;

import java.util.List;

/**
 * Created on 2017/6/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc 指令集的实体类
 */
public class CommandSet {
    public List<String> getStarts() {
        return starts;
    }

    public void setStarts(List<String> starts) {
        this.starts = starts;
    }

    public List<String> getPauses() {
        return pauses;
    }

    public void setPauses(List<String> pauses) {
        this.pauses = pauses;
    }

    public List<String> getEnds() {
        return ends;
    }

    public void setEnds(List<String> ends) {
        this.ends = ends;
    }

    public List<String> getIntros() {
        return intros;
    }

    public void setIntros(List<String> intros) {
        this.intros = intros;
    }

    public List<String> getContinues() {
        return continues;
    }

    public void setContinues(List<String> continues) {
        this.continues = continues;
    }

    List<String> starts;
    List<String> pauses;
    List<String> ends;
    List<String> intros;
    List<String> continues;

    List<String> viewdownload;

    public List<String> getViewdownload() {
        return viewdownload;
    }

    public void setViewdownload(List<String> viewdownload) {
        this.viewdownload = viewdownload;
    }

}
