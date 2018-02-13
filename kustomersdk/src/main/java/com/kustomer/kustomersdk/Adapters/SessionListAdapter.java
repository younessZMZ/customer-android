package com.kustomer.kustomersdk.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSChatSessionsDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.ViewHolders.AgentMessageViewHolder;
import com.kustomer.kustomersdk.ViewHolders.SessionViewHolder;
import com.kustomer.kustomersdk.ViewHolders.UserMessageViewHolder;

/**
 * Created by Junaid on 1/19/2018.
 */

public class SessionListAdapter extends RecyclerView.Adapter<SessionViewHolder>{

    //region Properties
    private KUSChatSessionsDataSource mChatSessionsDataSource;
    private KUSUserSession mUserSession;
    private onItemClickListener mListener;
    //endregion

    //region LifeCycle
    public SessionListAdapter(KUSChatSessionsDataSource chatSessionsDataSource,
                              KUSUserSession userSession, onItemClickListener listener){
        mChatSessionsDataSource = chatSessionsDataSource;
        mUserSession = userSession;
        mListener = listener;
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SessionViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_view_holder,parent,false));
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
        holder.onBind((KUSChatSession) mChatSessionsDataSource.get(position),mUserSession, mListener);
    }

    @Override
    public int getItemCount() {
        if(mChatSessionsDataSource == null)
            return 0;

        return mChatSessionsDataSource.getSize();
    }
    //endregion

    //region Listener
    public interface onItemClickListener{
        void onSessionItemClicked(KUSChatSession chatSession);
    }
    //endregion
}
