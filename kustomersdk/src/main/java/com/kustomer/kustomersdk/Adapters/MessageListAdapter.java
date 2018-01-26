package com.kustomer.kustomersdk.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.ViewHolders.AgentMessageViewHolder;
import com.kustomer.kustomersdk.ViewHolders.UserMessageViewHolder;

/**
 * Created by Junaid on 1/19/2018.
 */

public class MessageListAdapter extends RecyclerView.Adapter{

    //region Properties
    private static final int AGENT_VIEW = 0;
    private static final int USER_VIEW = 1;

    private KUSPaginatedDataSource mPaginatedDataSource;
    //endregion

    //region LifeCycle
    public MessageListAdapter(KUSPaginatedDataSource paginatedDataSource){
        mPaginatedDataSource = paginatedDataSource;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == USER_VIEW)
            return new UserMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_view_holder,parent,false));
        else
            return new AgentMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agent_view_holder,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == USER_VIEW) {
            ((UserMessageViewHolder)holder).onBind((KUSChatMessage) mPaginatedDataSource.get(position));
        }else{
            ((AgentMessageViewHolder)holder).onBind((KUSChatMessage) mPaginatedDataSource.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        KUSChatMessage chatMessage = (KUSChatMessage) mPaginatedDataSource.get(position);

        boolean currentUser = KUSChatMessage.KUSChatMessageSentByUser(chatMessage);

        if(currentUser)
            return USER_VIEW;
        else
            return AGENT_VIEW;
    }

    @Override
    public int getItemCount() {
        return mPaginatedDataSource.getSize();
    }
    //endregion
}
