package com.lcz.file.entity;

import java.io.Serializable;

/**
 * Created by xx on 2017/3/7.
 */
public class RuleEntity implements Serializable {
    private String parentNode;
    private String childNode;
    private String head;

    public String getParentNode() {
        return parentNode;
    }

    public void setParentNode(String parentNode) {
        this.parentNode = parentNode;
    }

    public String getChildNode() {
        return childNode;
    }

    public void setChildNode(String childNode) {
        this.childNode = childNode;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    @Override
    public String toString() {
        return "RuleEntity{" +
                "parentNode='" + parentNode + '\'' +
                ", childNode='" + childNode + '\'' +
                ", head='" + head + '\'' +
                '}';
    }
}
