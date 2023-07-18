package com.cugo.basketballlineupmaker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.cugo.basketballlineupmaker.databinding.TacticRowBinding;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TacticsAdapter extends RecyclerView.Adapter<TacticsAdapter.TacticsHolder> {
    TacticsDatabase tdb;
    TacticsDao tacticsDao;
    List<Tactics> tactics;
    private static String fileName;
    File filepath =new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myTactics");

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public TacticsAdapter(List<Tactics> tactics) {
        this.tactics = tactics;
    }

    @NonNull
    @Override
    public TacticsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TacticRowBinding tacticRowBinding = TacticRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new TacticsHolder(tacticRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TacticsHolder holder, @SuppressLint("RecyclerView") int position) {
        if(tactics.get(position).fav != true){
            holder.tacticRowBinding.like.setImageResource(R.drawable.whiteheart);
        }
        else{
            holder.tacticRowBinding.like.setImageResource(R.drawable.redheart);
        }
        Bitmap bm= BitmapFactory.decodeByteArray(tactics.get(position).img, 0, tactics.get(position).img.length);
        holder.tacticRowBinding.imageView2.setImageBitmap(bm);
        holder.tacticRowBinding.tacnname.setText(tactics.get(position).title);
        holder.tacticRowBinding.teccnotes.setText(tactics.get(position).note);

        holder.tacticRowBinding.godelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tdb = Room.databaseBuilder(holder.itemView.getContext(),TacticsDatabase.class,"Tactics")
                        .build();
                tacticsDao = tdb.tacticsDao();
                compositeDisposable.add(tacticsDao.delete(tactics.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
                tactics.remove(position);
                notifyDataSetChanged();
                compositeDisposable.clear();
            }
        });
        holder.tacticRowBinding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tactics.get(position).fav != true){
                    tdb = Room.databaseBuilder(holder.itemView.getContext(),TacticsDatabase.class,"Tactics")
                            .build();
                    tacticsDao = tdb.tacticsDao();
                    tactics.get(position).fav=true;
                    compositeDisposable.add(tacticsDao.update(tactics.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
                    holder.tacticRowBinding.like.setImageResource(R.drawable.redheart);
                    notifyDataSetChanged();
                }
                else{
                    tdb = Room.databaseBuilder(holder.itemView.getContext(),TacticsDatabase.class,"Tactics")
                            .build();
                    tacticsDao = tdb.tacticsDao();
                    tactics.get(position).fav=false;
                    compositeDisposable.add(tacticsDao.update(tactics.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
                    holder.tacticRowBinding.like.setImageResource( R.drawable.whiteheart);
                    notifyDataSetChanged();
                }
            }
        });
        holder.tacticRowBinding.shareit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent intent = new Intent();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String date = format.format(new Date());
                fileName = filepath + "/" +date+".png";
                if(!filepath.exists()){
                    filepath.mkdirs();
                }
                File file = new File(fileName);
                View root = holder.tacticRowBinding.fullTactic;
                root.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(root.getDrawingCache());
                root.setDrawingCacheEnabled(false);
                file.getParentFile().mkdirs();
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    Uri uri = Uri.fromFile(file);
                    Toast.makeText(view.getContext(),"Saved Successfully Under the Directory /myTactics",Toast.LENGTH_SHORT).show();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM,uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                holder.itemView.getContext().startActivity(Intent.createChooser(intent,"share image"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return tactics.size();
    }

    public class TacticsHolder extends RecyclerView.ViewHolder{
        TacticRowBinding tacticRowBinding;
        public TacticsHolder(TacticRowBinding tacticRowBinding) {
            super(tacticRowBinding.getRoot());
            this.tacticRowBinding = tacticRowBinding;
        }
    }

}
