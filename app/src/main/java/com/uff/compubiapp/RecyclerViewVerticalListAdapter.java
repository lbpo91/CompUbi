package com.uff.compubiapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Collections;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class RecyclerViewVerticalListAdapter  extends RecyclerView.Adapter<RecyclerViewVerticalListAdapter.ItemViewHolder> {
    private List<ListItem> verticalList;
    Context context;

    public RecyclerViewVerticalListAdapter(List<ListItem> verticalList, Context context){
        this.verticalList = verticalList;
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View linkItemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.vertical_list_item, parent, false);
        ItemViewHolder gvh = new ItemViewHolder(linkItemView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        holder.txtview.setText(verticalList.get(position).getMensagem());
        holder.imgView.setImageResource(R.drawable.ic_location_on_black_24dp);

        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Toast.makeText(context, linkURL + " is selected", Toast.LENGTH_SHORT).show();
                String gpsInfo = verticalList.get(position).getGpsInfo();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+gpsInfo+"(Origem+do+sinal)");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                Intent mapIntent = new Intent();
                mapIntent.setAction(Intent.ACTION_VIEW);
                mapIntent.setData(gmmIntentUri);
                mapIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return verticalList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView txtview;
        ImageView imgView;

        public ItemViewHolder(View view) {
            super(view);
            txtview=view.findViewById(R.id.idItemMsg);
            imgView=view.findViewById(R.id.idItemImage);
        }

    }
}
