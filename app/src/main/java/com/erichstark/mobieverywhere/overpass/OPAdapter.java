package com.erichstark.mobieverywhere.overpass;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.erichstark.mobieverywhere.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erich on 02/12/15.
 */
public class OPAdapter extends RecyclerView.Adapter<OPAdapter.OverPassViewHolder> {

    private List<ItemPOI> bars;
    private LayoutInflater layoutInflater;
    private View.OnClickListener listener;
    private Long favoriteItemID;

    public OPAdapter(LayoutInflater layoutInflater, View.OnClickListener listener) {
        this.layoutInflater = layoutInflater;
        bars = new ArrayList<>(0);
        this.listener = listener;
    }

    @Override
    public OverPassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_overpass, parent, false);
        view.setOnClickListener(listener);
        return new OverPassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OverPassViewHolder holder, int position) {
        ItemPOI element = bars.get(position);
        holder.name.setText(element.getName());
        double distance = element.getDistance();
        String textDistance;
        if (distance < 1000) {
            textDistance = String.format("%3.0fm", distance);
        } else {
            textDistance = String.format("%.2fkm", distance / 1000);
        }
        holder.distance.setText(textDistance);
        holder.favorite.setChecked(favoriteItemID.equals(element.getId()));
    }

    @Override
    public int getItemCount() {
        return bars.size();
    }

    public Long getFavoriteItemID() {
        return favoriteItemID;
    }

    public void setFavoriteItemID(Long favoriteItemID) {
        this.favoriteItemID = favoriteItemID;
    }

    public List<ItemPOI> getBars() {
        return bars;
    }

    public void setBars(List<ItemPOI> list) {
        bars = list;
    }

    public static class OverPassViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView distance;
        public CheckBox favorite;

        public OverPassViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.itemTextPrimary);
            distance = (TextView) itemView.findViewById(R.id.itemDistance);
            favorite = (CheckBox) itemView.findViewById(R.id.itemPrefered);
        }
    }

}
