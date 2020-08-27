package com.svrpublicschool.ui.chat.viewholder;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.DateUtility;
import com.svrpublicschool.Util.GenericImageLoader;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.models.UserEntity;
import com.svrpublicschool.models.YoutubeMetaDataEntity;
import com.svrpublicschool.services.HttpService;
import com.svrpublicschool.ui.chat.GroupChatActivity;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MsgSentViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout rlParent, rlUrlPreview;
    TextView tvMsg, tvTime, tvTitle, tvSender;
    ImageView ivPreview;
    AppCompatImageView ivDoubleTick;
    Context context;
    ChatEntity chatEntity;
    UserEntity senderEntity;

    public MsgSentViewHolder(View itemView) {
        super(itemView);
        this.tvMsg = itemView.findViewById(R.id.tvMsg);
        this.rlParent = itemView.findViewById(R.id.rlParent);
        this.tvTime = itemView.findViewById(R.id.tvTime);
        this.ivPreview = itemView.findViewById(R.id.ivPreview);
        this.rlUrlPreview = itemView.findViewById(R.id.rlUrlPreview);
        this.tvTitle = itemView.findViewById(R.id.tvTitle);
        this.tvSender = itemView.findViewById(R.id.tvSender);
        this.ivDoubleTick = itemView.findViewById(R.id.ivDoubleTick);
    }

    public void setData(Context context, ChatEntity chatEntity, UserEntity senderEntity, int position) {
        this.context = context;
        this.chatEntity = chatEntity;
        this.senderEntity = senderEntity;
        tvSender.setText(chatEntity.getSenName());
        tvMsg.setText(chatEntity.getMsg());
        tvTime.setText("" + DateUtility.getFormattedTime(chatEntity.getCreatedAt()));
        if (chatEntity.getStatus() == Constants.NOT_SENT) {
            if (((GroupChatActivity) context).pushChatToFireBase(chatEntity)) {
                chatEntity.setStatus(Constants.READ);
            }
        }
        setUploadState(chatEntity.getStatus());
        if (URLUtil.isValidUrl(chatEntity.getMsg()) || isUrl(chatEntity.getMsg())) {

            tvMsg.setTextColor(context.getResources().getColor(R.color.blue));
            if (checkForYoutubeLink(chatEntity.getMsg())) {
                rlUrlPreview.setVisibility(View.VISIBLE);
                getYoutubeMetaData(chatEntity);
            } else {
                rlUrlPreview.setVisibility(View.GONE);
            }

            tvMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redirectUr(chatEntity.getMsg());
                }
            });
            rlUrlPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redirectUr(chatEntity.getMsg());
                }
            });
        } else {
            rlUrlPreview.setVisibility(View.GONE);
            tvMsg.setTextColor(context.getResources().getColor(R.color.black));
        }
        rlParent.setOnClickListener(v -> {
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (context instanceof GroupChatActivity) {
                    ((GroupChatActivity) context).openDeletePopUp(chatEntity,position);
                }
                return false;
            }
        });
    }

    private boolean checkForYoutubeLink(String url) {

        if (url.startsWith("https://www.youtube.com")
                || url.startsWith("https://youtu.be")
                || url.startsWith("www.youtu.be")
                || url.startsWith("www.youtube.com")) {
            return true;
        }
        return false;
    }

    private void getYoutubeMetaData(ChatEntity chatEntity) {
        String url = "https://www.youtube.com/oembed?url=";
        url = url + chatEntity.getMsg() + "&format=json";
        Observable<YoutubeMetaDataEntity> userModelObservable = HttpService.getInstance().getYoutubeMetaData(url);
        userModelObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<YoutubeMetaDataEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(YoutubeMetaDataEntity youtubeMetaDataEntity) {
                        //Logger.d(keyValueModel.getFaculty().get(0).getName());
                        if (youtubeMetaDataEntity != null) {
                            tvTitle.setText(youtubeMetaDataEntity.getTitle());
                            GenericImageLoader.loadImage(context, ivPreview, youtubeMetaDataEntity.getThumbnailUrl(), 0);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private boolean isUrl(String url) {
        if (url.startsWith("https:")
                || url.startsWith("http")
                || url.startsWith("www")
                || url.startsWith("bitly")) {
            return true;
        }
        return false;
    }

    private void redirectUr(String url) {
        if (url.startsWith("www")) {
            url = "https://" + url;
        }
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
        try {
            context.startActivity(webIntent);
        } catch (ActivityNotFoundException ex) {
        }
    }

    private void setUploadState(int status) {

        switch (status) {
            case Constants.NOT_SENT:
                ivDoubleTick.setImageResource(R.drawable.ic_not_sent);
                break;
            case Constants.SENT:
                ivDoubleTick.setImageResource(R.drawable.ic_single_tick);
                break;
            case Constants.RECEIVED:
                ivDoubleTick.setImageResource(R.drawable.ic_received);
                break;
            case Constants.READ:
                ivDoubleTick.setImageResource(R.drawable.ic_read);
                break;
        }
    }
}