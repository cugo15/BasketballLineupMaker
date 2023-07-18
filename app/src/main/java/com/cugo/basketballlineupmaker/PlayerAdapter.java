package com.cugo.basketballlineupmaker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cugo.basketballlineupmaker.databinding.RecyclerRowBinding;
import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerHolder> {

    ArrayList<PlayersData> playersDataArrayList;

    public PlayerAdapter(ArrayList<PlayersData> playersDataArrayList) {
        this.playersDataArrayList = playersDataArrayList;
    }

    @NonNull
    @Override
    public PlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false);
        return new PlayerHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.rname.setText(playersDataArrayList.get(position).name);
        holder.binding.rage.setText("Age : "+playersDataArrayList.get(position).age);
        holder.binding.rhe.setText("Height : "+playersDataArrayList.get(position).height);
        holder.binding.rpos.setText("Position : "+playersDataArrayList.get(position).position);
        holder.binding.rjerseynum.setText("Jersey Number : "+playersDataArrayList.get(position).jerseynumber);
        holder.binding.rimage.setImageBitmap(playersDataArrayList.get(position).image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(holder.itemView.getContext(),CreatePlayer.class);
            intent.putExtra("info","update");
            intent.putExtra("playerId",playersDataArrayList.get(position).id);
            holder.itemView.getContext().startActivity(intent);

            }
        });
    }
    @Override
    public int getItemCount() {
        return playersDataArrayList.size();
    }
    public class PlayerHolder extends RecyclerView.ViewHolder {
        private RecyclerRowBinding binding;
        public PlayerHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
