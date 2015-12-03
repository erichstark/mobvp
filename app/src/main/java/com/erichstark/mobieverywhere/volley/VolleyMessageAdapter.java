package com.erichstark.mobieverywhere.volley;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.erichstark.mobieverywhere.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erich on 01/12/15.
 */
public class VolleyMessageAdapter extends RecyclerView.Adapter<VolleyMessageAdapter.VolleyViewHolder> {

    private List<VolleyMessageEntity> messageList;
    private LayoutInflater inflater;
    private Context context;

    public VolleyMessageAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        messageList = new ArrayList<>();
        this.context = context;
    }

    public void addMessage(VolleyMessageEntity message) {
        messageList.add(message);
        notifyDataSetChanged();
    }

    public void addMessages(List<VolleyMessageEntity> messages) {
        messageList.addAll(messages);
        notifyDataSetChanged();
    }

    public VolleyMessageEntity getMessage(int position) {
        return messageList.get(position);
    }

    @Override
    public VolleyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_volley_message, parent, false);
        return new VolleyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VolleyViewHolder holder, int position) {
        VolleyMessageEntity message = messageList.get(position);
        holder.textView.setText(message.getText());
        switch (message.getStatus()) {
            case VolleyMessageEntity.STATUS_SENT:
                //holder.statusIcon.setVisibility(View.GONE);
                holder.textView.setBackgroundColor(Color.GREEN);
                break;
            case VolleyMessageEntity.STATUS_ERROR: {
                holder.textView.setBackgroundColor(Color.RED);
                //holder.statusIcon.setVisibility(View.VISIBLE);
                //Drawable mDrawable = context.getResources().getDrawable(R.drawable.ic_error_white_18dp);
                //assert mDrawable != null;
                //mDrawable.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY));
                //holder.statusIcon.setImageDrawable(mDrawable);
            }
            break;
            case VolleyMessageEntity.STATUS_SENDING: {
                holder.textView.setBackgroundColor(Color.YELLOW);
                //holder.statusIcon.setVisibility(View.VISIBLE);
                //Drawable mDrawable = context.getResources().getDrawable(R.drawable.ic_cloud_upload_white_18dp);
                //assert mDrawable != null;
                //mDrawable.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(R.color.primary), PorterDuff.Mode.MULTIPLY));
                //holder.statusIcon.setImageDrawable(mDrawable);
            }
            break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class VolleyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView statusIcon;

        public VolleyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            statusIcon = (ImageView) itemView.findViewById(R.id.status);
        }
    }
}