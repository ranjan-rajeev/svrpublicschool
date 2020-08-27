package com.svrpublicschool.ui.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.models.UserEntity;
import com.svrpublicschool.ui.chat.viewholder.ImageReceivedViewHolder;
import com.svrpublicschool.ui.chat.viewholder.ImageSentViewHolder;
import com.svrpublicschool.ui.chat.viewholder.MsgReceivedViewHolder;
import com.svrpublicschool.ui.chat.viewholder.MsgSentViewHolder;
import com.svrpublicschool.ui.chat.viewholder.PdfReceivedViewHolder;
import com.svrpublicschool.ui.chat.viewholder.PdfSentViewHolder;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {

    public static final int TYPE_MSG_RECEIVED = 1;
    public static final int TYPE_IMG_RECEIVED = 2;
    public static final int TYPE_PDF_RECEIVED = 3;
    public static final int TYPE_IMG_SENT = 4;
    public static final int TYPE_PDF_SENT = 5;
    public static final int TYPE_MSG_SENT = 6;
    public static final int TYPE_VIDEO_RECEIVED = 7;
    public static final int TYPE_VIDEO_SENT = 8;
    public List<ChatEntity> list;
    public UserEntity senderEntity;
    Context mContext;

    public ChatAdapter(Context context, List<ChatEntity> data) {
        this.list = data;
        this.mContext = context;
    }

    public ChatAdapter(Context context, List<ChatEntity> data, UserEntity userEntity) {
        this.list = data;
        this.mContext = context;
        this.senderEntity = userEntity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case TYPE_MSG_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_msg_received, parent, false);
                return new MsgReceivedViewHolder(view);
            case TYPE_IMG_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_image_received, parent, false);
                return new ImageReceivedViewHolder(view);
            case TYPE_PDF_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_pdf_received, parent, false);
                return new PdfReceivedViewHolder(view);

            case TYPE_IMG_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_image_sent, parent, false);
                return new ImageSentViewHolder(view);
            case TYPE_PDF_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_pdf_sent, parent, false);
                return new PdfSentViewHolder(view);
            case TYPE_MSG_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_msg_sent, parent, false);
                return new MsgSentViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        int chatType = list.get(position).getChatType();
        if (list == null && list.size() < position) {
            return 0;
        }
        ChatEntity chatEntity = list.get(position);
        if (chatEntity != null && senderEntity != null) {

            if (chatType == TYPE_MSG_SENT || chatType == TYPE_MSG_RECEIVED) { //msg received/sent
                if (senderEntity.getfId().equals(chatEntity.getSender())) {
                    return TYPE_MSG_SENT;
                } else {
                    return TYPE_MSG_RECEIVED;
                }
            } else if (chatType == TYPE_IMG_SENT || chatType == TYPE_IMG_RECEIVED) { //msg received/sent
                if (senderEntity.getfId().equals(chatEntity.getSender())) {
                    return TYPE_IMG_SENT;
                } else {
                    return TYPE_IMG_RECEIVED;
                }
            } else if (chatType == TYPE_PDF_SENT || chatType == TYPE_PDF_RECEIVED) { //msg received/sent
                if (senderEntity.getfId().equals(chatEntity.getSender())) {
                    return TYPE_PDF_SENT;
                } else {
                    return TYPE_PDF_RECEIVED;
                }
            }
        } else {
            if (chatType == TYPE_MSG_SENT || chatType == TYPE_MSG_RECEIVED) { //msg received/sent
                return TYPE_MSG_RECEIVED;
            } else if (chatType == TYPE_IMG_SENT || chatType == TYPE_IMG_RECEIVED) { //msg received/sent
                return TYPE_IMG_RECEIVED;
            } else if (chatType == TYPE_PDF_SENT || chatType == TYPE_PDF_RECEIVED) { //msg received/sent
                return TYPE_PDF_RECEIVED;
            }
        }
        return chatType;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ChatEntity chatEntity = list.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_MSG_RECEIVED:
                ((MsgReceivedViewHolder) holder).setData(mContext, chatEntity,position);
                break;
            case TYPE_IMG_RECEIVED:
                ((ImageReceivedViewHolder) holder).setData(mContext, chatEntity,position);
                break;
            case TYPE_PDF_RECEIVED:
                ((PdfReceivedViewHolder) holder).setData(mContext, chatEntity,position);
                break;

            case TYPE_MSG_SENT:
                ((MsgSentViewHolder) holder).setData(mContext, chatEntity, senderEntity,position);
                break;
            case TYPE_IMG_SENT:
                ((ImageSentViewHolder) holder).setData(mContext, chatEntity,position);
                break;
            case TYPE_PDF_SENT:
                ((PdfSentViewHolder) holder).setData(mContext, chatEntity,position);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void updateList(List<ChatEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addChat(ChatEntity chatEntity) {
        list.add(chatEntity);
        notifyDataSetChanged();
    }
}