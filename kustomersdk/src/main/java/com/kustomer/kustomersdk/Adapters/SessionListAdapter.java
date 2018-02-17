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
import com.kustomer.kustomersdk.Utils.KUSUtils;
import com.kustomer.kustomersdk.ViewHolders.AgentMessageViewHolder;
import com.kustomer.kustomersdk.ViewHolders.DummyViewHolder;
import com.kustomer.kustomersdk.ViewHolders.SessionViewHolder;
import com.kustomer.kustomersdk.ViewHolders.UserMessageViewHolder;

/**
 * Created by Junaid on 1/19/2018.
 */

public class SessionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //region Properties
    private KUSChatSessionsDataSource mChatSessionsDataSource;
    private KUSUserSession mUserSession;
    private onItemClickListener mListener;

    private int minimumRowCount = 0;
    private final int SESSION_VIEW_TYPE = 0;
    private final int DUMMY_VIEW_TYPE = 1;
    //endregion

    //region LifeCycle
    public SessionListAdapter(KUSChatSessionsDataSource chatSessionsDataSource,
                              KUSUserSession userSession, onItemClickListener listener){
        mChatSessionsDataSource = chatSessionsDataSource;
        mUserSession = userSession;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == SESSION_VIEW_TYPE)
            return new SessionViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_session_view_holder,parent,false));
        else
            return new DummyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_session_dummy_view_holder,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position < mChatSessionsDataSource.getSize())
            ((SessionViewHolder)holder).onBind((KUSChatSession) mChatSessionsDataSource.get(position),mUserSession, mListener);
    }

    @Override
    public int getItemViewType(int position) {
        if(position < mChatSessionsDataSource.getSize())
            return SESSION_VIEW_TYPE;
        else
            return DUMMY_VIEW_TYPE;

    }

    @Override
    public int getItemCount() {
        if(mChatSessionsDataSource == null)
            return 0;

        if(mChatSessionsDataSource.getSize()<minimumRowCount)
            return minimumRowCount;

        return mChatSessionsDataSource.getSize();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        float visibleRecyclerViewHeight = recyclerView.getHeight() - recyclerView.getPaddingBottom();
        float rowCountThatFitsHeight = visibleRecyclerViewHeight / KUSUtils.dipToPixels(recyclerView.getContext(),75);
        minimumRowCount = (int)Math.floor(rowCountThatFitsHeight);
    }

    //endregion

    //region Listener
    public interface onItemClickListener{
        void onSessionItemClicked(KUSChatSession chatSession);
    }
    //endregion
}
