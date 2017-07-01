package com.example.browser.file;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Young on 2016/11/30.
 */

public class SortComparator implements Comparator<HashMap<String, Object>> {

    private FileShowManager.SROTTYPE sortType;

    public SortComparator(FileShowManager.SROTTYPE sortType) {
        this.sortType = sortType;
    }

    @Override
    public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
        switch (sortType) {
            case LETTER:
                //字母排序
                return String.valueOf(lhs.get("name")).compareTo(String.valueOf(rhs.get("name")));
            case DATE:
                //日期排序
                return String.valueOf(lhs.get("date")).compareTo(String.valueOf(rhs.get("date")));
            case CHILDNUMS:
                //子文件夹数量排序
                return String.valueOf(lhs.get("childnums")).compareTo(String.valueOf(rhs.get("childnums")));
        }
        return 0;
    }
}
