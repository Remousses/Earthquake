package com.example.earthquakeandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.earthquakeandroidapp.activities.EarthquakeActivity;
import com.example.earthquakeandroidapp.beans.EarthquakeBean;
import com.example.earthquakeandroidapp.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<EarthquakeBean> mData;
    private RequestOptions option;

    public RecyclerViewAdapter(Context mContext, List<EarthquakeBean> mData) {
        System.out.println("mData : " + mData);
        this.mContext = mContext;
        // elle concerne la liste affiché dans Recycler View
        this.mData = mData;
        // request option pour Glide
        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        // pour lire le xml où il y a la liste
        view = layoutInflater.inflate(R.layout.earthquake_row_item, viewGroup,false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        // ici, quand on appuie sur un element de la liste, il déclenche l'action vers le details de l element
        viewHolder.viewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, EarthquakeActivity.class);
                i.putExtra("earthquake_urlmap",mData.get(viewHolder.getAdapterPosition()).getUrlMap());

                mContext.startActivity(i);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int i) {

        holder.tvPlace.setText(mData.get(i).getPlace());
        holder.tvTime.setText(mData.get(i).getTime().toString());
        holder.tvMagnetude.setText(mData.get(i).getMagnetude().toString());

        //charger l'image de l'url et la stocker dans l'imageview grâce à Glide

        //Glide.with(mContext).load(mData.get(i).getImage()).apply(option).into(holder.imgThumbnail);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPlace;
        TextView tvTime;
        TextView tvMagnetude;
        ImageView imgThumbnail;
        LinearLayout viewContainer;


        public MyViewHolder(View itemView){
            super(itemView);
            // on instancie les objets en récupérant les id du fichier xml earthquake_row_item
            viewContainer = itemView.findViewById(R.id.container);
            tvPlace = itemView.findViewById(R.id.earthquakePlace);
            tvTime = itemView.findViewById(R.id.earthquakeTime);
            tvMagnetude = itemView.findViewById(R.id.earthquakeMagnetude);
        }
    }
}