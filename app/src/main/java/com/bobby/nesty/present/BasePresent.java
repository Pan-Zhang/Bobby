package com.bobby.nesty.present;

import android.content.ContentValues;

import com.bobby.nesty.model.Story;
import com.bobby.nesty.util.database.DatabaseManager;
import com.bobby.nesty.view.viewcontrol.BaseCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by corous360 on 2016/10/10.
 */
public class BasePresent<M extends BaseCallback>  {

    public M Callback;

    public BasePresent(M m){
        this.Callback = m;
    }

    private final String TABLE = "story";

    public void add(Story story) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", story.getTitle());
        contentValues.put("content", story.getContent());
        contentValues.put("date", story.getDate());
        DatabaseManager.getInstance().insert(TABLE, contentValues);
    }

    public List<Story> select() {
        List<Map<String, String>> list =  DatabaseManager.getInstance().listPersonMaps(TABLE, null, null, "storyid desc");
        List<Story> storyList = new ArrayList<Story>();
        Story _story;
        for(Map<String, String> map : list){
            _story = new Story();
            _story.setId(map.get("storyid"));
            _story.setTitle(map.get("title"));
            _story.setContent(map.get("content"));
            _story.setDate(map.get("date"));
            storyList.add(_story);
        }
        return storyList;
    }
}
