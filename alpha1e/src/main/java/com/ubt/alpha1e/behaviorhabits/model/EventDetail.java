package com.ubt.alpha1e.behaviorhabits.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.data.model.BaseModel;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * @作者：ubt
 * @日期: 2017/12/22 16:47
 * @描述:
 */

@Parcelable
public class EventDetail extends BaseModel{
    public String userId;
    public String eventId;
    public String eventName;
    public String eventTime;
    public String remindFirst;
    public String remindSecond;
    public String status;
    public String type;
    public ArrayList contentIds;
    public EventDetail thiz;
    @Override
    public EventDetail getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, EventDetail.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<EventDetail> getModelList(String json) {
        ArrayList<EventDetail> result = new ArrayList<EventDetail>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new EventDetail().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<EventDetail> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(UserScore  info)
    {
        try {
            return  GsonImpl.get().toJson(info);

        }catch (Exception e)
        {
            return  "";
        }
    }

    @Override
    public String toString() {
        return "EventDetail{" +
                "userId="+userId+ '\''+
                "eventId=" + eventId + '\''+
                ", eventName='" + eventName + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", remindFirst='" + remindFirst + '\'' +
                ", remindSecond='" + remindSecond + '\'' +
                ", status='" + status + '\'' +
                ", contentIds='" + contentIds + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}