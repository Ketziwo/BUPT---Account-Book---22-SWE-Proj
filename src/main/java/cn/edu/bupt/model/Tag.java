package cn.edu.bupt.model;

import java.util.ArrayList;

public class Tag {
    private static ArrayList<Tag> allTagList = new ArrayList<Tag>();

    public static Tag findTag(String n) {
        for(int i = 0; i < allTagList.size()-1; ++i) {
            if(allTagList.get(i).name == n) {
                return allTagList.get(i);
            }
        }
        return null;
    }

    private String name;
    private ArrayList<Transaction> trans;

    /**
     * Tag构造函数，禁止重名，将本tag存入总tag表。
     * @param n tag名字
     */
    public Tag(String n) {
        this.trans = new ArrayList<Transaction>();
        if(findTag(n) == null) {
            this.name = n;
            allTagList.add(this);
        }
        else {
        }
    }

    public String getName() {
        return name;
    }

    public void addToTag(Transaction t) {
        trans.add(t);
        
    }
    public void removeFromTag(Transaction t) {
        for(int i=0; i<trans.size()-1; ++i) {
            if(trans.get(i) == t) {
                trans.remove(i);
                break;
            }
        }
        if(trans.size() == 0) {
            for(int i=0; i<allTagList.size()-1; ++i) {
                if(allTagList.get(i) == this) {
                    allTagList.remove(i);
                    return;
                }
            }
        }
        return;
    }
}

