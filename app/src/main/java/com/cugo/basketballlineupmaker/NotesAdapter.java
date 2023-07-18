package com.cugo.basketballlineupmaker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.cugo.basketballlineupmaker.databinding.NoteRowBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder> {
    List<Notes> notesList;
    NotesDao notesDao;
    NotesDatabase notesDatabase;
    private static String fileName;
    File filepath =new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myNotes");
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public NotesAdapter(List<Notes> notesList) {
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteRowBinding noteRowBinding = NoteRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new NoteHolder(noteRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, @SuppressLint("RecyclerView") int position) {
        if(notesList.get(position).fav != true){
            holder.noteRowBinding.favorinote.setImageResource(R.drawable.whiteheart);
        }
        else{
            holder.noteRowBinding.favorinote.setImageResource(R.drawable.redheart);
        }
        if(notesList.get(position).img == null){
            holder.noteRowBinding.rowimage.setVisibility(View.GONE);
            holder.noteRowBinding.imageView3.setVisibility(View.GONE);
        }
        else{
            holder.noteRowBinding.imageView3.setVisibility(View.VISIBLE);
            holder.noteRowBinding.rowimage.setVisibility(View.VISIBLE);
            Bitmap bm= BitmapFactory.decodeByteArray(notesList.get(position).img, 0, notesList.get(position).img.length);
            holder.noteRowBinding.rowimage.setImageBitmap(bm);
        }
        holder.noteRowBinding.rowtitle.setText(notesList.get(position).title);
        holder.noteRowBinding.rowdescrription.setText(notesList.get(position).not);

        holder.noteRowBinding.deletenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesDatabase = Room.databaseBuilder(holder.itemView.getContext(),NotesDatabase.class,"Notes")
                        .build();
                notesDao = notesDatabase.notesDao();
                compositeDisposable.add(notesDao.delete(notesList.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
                notesList.remove(position);
                notifyDataSetChanged();
                compositeDisposable.clear();
            }
        });
        holder.noteRowBinding.sharenote.setOnClickListener(new View.OnClickListener() {
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
                View root = holder.noteRowBinding.sharecv;
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
                    Toast.makeText(view.getContext(),"Saved Successfully Under the Directory /myNotes",Toast.LENGTH_SHORT).show();
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
        holder.noteRowBinding.favorinote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notesList.get(position).fav == false){
                    notesDatabase = Room.databaseBuilder(holder.itemView.getContext(),NotesDatabase.class,"Notes")
                            .build();
                    notesDao = notesDatabase.notesDao();
                    notesList.get(position).fav=true;
                    compositeDisposable.add(notesDao.update(notesList.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
                    holder.noteRowBinding.favorinote.setImageResource(R.drawable.redheart);
                    notifyDataSetChanged();
                }
                else{
                    notesDatabase = Room.databaseBuilder(holder.itemView.getContext(),NotesDatabase.class,"Notes")
                            .build();
                    notesDao = notesDatabase.notesDao();
                    notesList.get(position).fav=false;
                    compositeDisposable.add(notesDao.update(notesList.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
                    holder.noteRowBinding.favorinote.setImageResource( R.drawable.whiteheart);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class NoteHolder extends RecyclerView.ViewHolder{
    NoteRowBinding noteRowBinding;
    public NoteHolder(NoteRowBinding noteRowBinding) {
        super(noteRowBinding.getRoot());
        this.noteRowBinding = noteRowBinding;
    }
}
}
