package com.svrpublicschool.ui.chat;

import com.svrpublicschool.models.ChatEntity;

import java.util.List;

public interface IChatListener {
    void addListItem(List<ChatEntity> list, boolean addItemBottom);

    void addItem(ChatEntity chatEntity);

    void lastPage(boolean isLastPage);

    void onError(String message);
}