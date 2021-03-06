package com.ubt.alpha1e.edu.business;

import java.util.List;

import com.ubt.alpha1e.edu.data.model.MessageInfo;

public interface MessageRecordManagerListener {

	public void onAddNoReadMessage();

	public void onReadUnReadRecords(List<Long> ids);

	public void onGetNewMessages(boolean isSuccess, String errorInfo,List<MessageInfo> messages,int type);
}
