package com.cugo.basketballlineupmaker;

import static com.cugo.basketballlineupmaker.Lineup.selectedone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cugo.basketballlineupmaker.databinding.ChoosePlayersBinding;
import java.util.ArrayList;

public class LineupAdapter extends RecyclerView.Adapter<LineupAdapter.LineupHolder> {
    ArrayList<PlayersData> arrayList;
    public LineupAdapter(ArrayList<PlayersData> arrayList) {
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public LineupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChoosePlayersBinding choosePlayersBinding = ChoosePlayersBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new LineupHolder(choosePlayersBinding);
    }
    @Override
    public void onBindViewHolder(@NonNull LineupHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.textView.setText(arrayList.get(position).name);
        holder.binding.imageView6.setImageBitmap(arrayList.get(position).image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedone = arrayList.get(position);
                Intent intent = new Intent(holder.itemView.getContext(),Lineup.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class LineupHolder extends RecyclerView.ViewHolder{
        private ChoosePlayersBinding binding;
        public LineupHolder(ChoosePlayersBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
