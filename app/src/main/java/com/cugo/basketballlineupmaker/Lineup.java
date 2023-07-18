package com.cugo.basketballlineupmaker;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.cugo.basketballlineupmaker.databinding.ActivityLineupBinding;
import com.cugo.basketballlineupmaker.databinding.SavetacticsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yuku.ambilwarna.AmbilWarnaDialog;

public class Lineup extends AppCompatActivity implements View.OnDragListener, View.OnLongClickListener {
    private ActivityLineupBinding binding;
    SQLiteDatabase database;
    LineupAdapter lineupAdapter;
    ArrayList<PlayersData> arrayList;
    TacticsDatabase tdb;
    TacticsDao tacticsDao;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    int defaultColor;
    static int c;
    private static final String SG_MARKER="SG";
    private static final String PG_MARKER="PG";
    private static final String SF_MARKER="SF";
    private static final String PF_MARKER="PF";
    private static final String C_MARKER="C";
    private static final String BALL_MARKER="BALL";
    private RelativeLayout.LayoutParams layoutParams;
    static PlayersData selectedone;
    SharedPreferences sharedPreferences;
    SavetacticsBinding savetacticsBinding;
    BottomSheetDialog saveDio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLineupBinding.inflate(getLayoutInflater());
        View view  = binding.getRoot();
        setContentView(view);
        saveDio = new BottomSheetDialog(this);
        askPermission();

        defaultColor = ContextCompat.getColor(Lineup.this,R.color.black);
        tdb = Room.databaseBuilder(getApplicationContext(),TacticsDatabase.class,"Tactics").build();
        tacticsDao =tdb.tacticsDao();
        binding.buttonsg.setTag(SG_MARKER);
        binding.buttonpg.setTag(PG_MARKER);
        binding.buttonsf.setTag(SF_MARKER);
        binding.buttonpf.setTag(PF_MARKER);
        binding.buttonc.setTag(C_MARKER);
        binding.buttonball.setTag(BALL_MARKER);
        binding.recycw.setVisibility(View.GONE);
        binding.linearLayout.setVisibility(View.VISIBLE);
        arrayList = new ArrayList<>();
        binding.buttonpg.setOnLongClickListener(this);
        binding.buttonsg.setOnLongClickListener(this);
        binding.buttonsf.setOnLongClickListener(this);
        binding.buttonpf.setOnLongClickListener(this);
        binding.buttonc.setOnLongClickListener(this);
        binding.buttonball.setOnLongClickListener(this);
        binding.relativeLayout.setOnDragListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.recycw.setLayoutManager(layoutManager);
        lineupAdapter = new LineupAdapter(arrayList);
        binding.recycw.setAdapter(lineupAdapter);
        sharedPreferences = this.getSharedPreferences("com.cugo.basketballlineupmaker", Context.MODE_PRIVATE);
        database = this.openOrCreateDatabase("Players",MODE_PRIVATE,null);
        try{
            getData();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(c==1){
            if(selectedone!= null){
            sharedPreferences.edit().putString("storedpg",selectedone.name).apply();
            sharedPreferences.edit().putString("pgjnum",selectedone.jerseynumber).apply();}
        }
        if(c==2){
            if(selectedone!= null) {
                sharedPreferences.edit().putString("storedsg", selectedone.name).apply();
                sharedPreferences.edit().putString("sgjnum", selectedone.jerseynumber).apply();
            }
        }
        if(c==3){
            if(selectedone!= null){
                sharedPreferences.edit().putString("storedsf",selectedone.name).apply();
                sharedPreferences.edit().putString("sfjnum",selectedone.jerseynumber).apply();
            }
        }
        if(c==4){
            if(selectedone!= null){
                sharedPreferences.edit().putString("storedpf",selectedone.name).apply();
                sharedPreferences.edit().putString("pfjnum",selectedone.jerseynumber).apply();
            }
        }
        if(c==5){
            if(selectedone!= null){
                sharedPreferences.edit().putString("storedc",selectedone.name).apply();
                sharedPreferences.edit().putString("cjnum",selectedone.jerseynumber).apply();
            }
        }

        String namepg = sharedPreferences.getString("storedpg","");
        String namesg = sharedPreferences.getString("storedsg","");
        String namesf = sharedPreferences.getString("storedsf","");
        String namepf = sharedPreferences.getString("storedpf","");
        String namec = sharedPreferences.getString("storedc","");
        String jerseypg = sharedPreferences.getString("pgjnum","");
        String jerseysg = sharedPreferences.getString("sgjnum","");
        String jerseysf = sharedPreferences.getString("sfjnum","");
        String jerseypf = sharedPreferences.getString("pfjnum","");
        String jerseyc = sharedPreferences.getString("cjnum","");

        binding.startfivepg.setText("PG : "+jerseypg+" "+namepg);
        binding.startfivesg.setText("SG : "+jerseysg+" "+namesg);
        binding.startfivesf.setText("SF : "+jerseysf+" "+namesf);
        binding.startfivepf.setText("PF : "+jerseypf+" "+namepf);
        binding.startfivepc.setText("C  : " +jerseyc+" "+namec);
        if(!jerseypg.equals(""))
        {
            binding.buttonpg.setText(jerseypg);
        }
        else{
            binding.buttonpg.setText("PG");
        }
        if(!jerseysg.equals(""))
        {
            binding.buttonsg.setText(jerseysg);
        }
        if(!jerseysf.equals(""))
        {
            binding.buttonsf.setText(jerseysf);
        }
        if(!jerseypf.equals(""))
        {
            binding.buttonpf.setText(jerseypf);
        }
        if(!jerseyc.equals(""))
        {
            binding.buttonc.setText(jerseyc);
        }

        binding.btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });
        binding.pensize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.txtpensize.setText(i+"dp");
                binding.signatureView.setPenSize(i);
                seekBar.setMax(50);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.signatureView.clearCanvas();
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensaveDialog();
                savetacticsBinding.diosavebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(savetacticsBinding.tacticname.getText().toString().length()>9||savetacticsBinding.tacticname.getText().toString().equals("")){
                            Toast.makeText(Lineup.this,"Tactic name must be between (1-10) character",Toast.LENGTH_LONG).show();
                        }
                        else{
                            View root = binding.relativeLayout;
                            root.setDrawingCacheEnabled(true);
                            Bitmap bitmap = Bitmap.createBitmap(root.getDrawingCache());
                            root.setDrawingCacheEnabled(false);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
                            byte[] byteArray =outputStream.toByteArray();

                            Tactics tactics = new Tactics(savetacticsBinding.tacticname.getText().toString(),savetacticsBinding.tacticnote.getText().toString(),byteArray,false);
                            compositeDisposable.add(tacticsDao.insert(tactics).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(Lineup.this::handleResponse));
                            saveDio.dismiss();
                        }

                    }
                });

            }
        });

    }



    private void askPermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void openColorPicker() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(Lineup.this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                binding.signatureView.setPenColor(color);
            }
        });
        ambilWarnaDialog.show();
    }

    public void buttonpg (View view){
        binding.tbtext.setText("SelectPlayer");
        binding.linearLayout.setVisibility(View.GONE);
        binding.recycw.setVisibility(View.VISIBLE);
        c=1;
    }
    public void buttonsg (View view){
        binding.tbtext.setText("SelectPlayer");
        binding.linearLayout.setVisibility(View.GONE);
        binding.recycw.setVisibility(View.VISIBLE);
        c=2;
    }
    public void buttonsf (View view){
        binding.tbtext.setText("SelectPlayer");
        binding.linearLayout.setVisibility(View.GONE);
        binding.recycw.setVisibility(View.VISIBLE);
        c=3;
    }
    public void buttonpf (View view){
        binding.tbtext.setText("SelectPlayer");
        binding.linearLayout.setVisibility(View.GONE);
        binding.recycw.setVisibility(View.VISIBLE);
        c=4;
    }
    public void buttonc (View view){
        binding.tbtext.setText("SelectPlayer");
        binding.linearLayout.setVisibility(View.GONE);
        binding.recycw.setVisibility(View.VISIBLE);
        c=5;
    }



    private void getData(){
        try{
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Players",MODE_PRIVATE,null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT*FROM players",null);
            int nameIX = cursor.getColumnIndex("name");
            int idIX =cursor.getColumnIndex("id");
            int heightIX =cursor.getColumnIndex("height");
            int ageIX =cursor.getColumnIndex("age");
            int posIX =cursor.getColumnIndex("position");
            int imageIX =cursor.getColumnIndex("image");
            int jerIX =cursor.getColumnIndex("jerseynumber");

            while(cursor.moveToNext()){
                String name = cursor.getString(nameIX);
                int id = cursor.getInt(idIX);
                String position = cursor.getString(posIX);
                String height = cursor.getString(heightIX);
                String age = cursor.getString(ageIX);
                String jerseynumber = cursor.getString(jerIX);
                byte[] image = cursor.getBlob(imageIX);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
                PlayersData playersData = new PlayersData(id,name,position,height,age,jerseynumber,bitmap);
                arrayList.add(playersData);
            }
            lineupAdapter.notifyDataSetChanged();
            cursor.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        switch (dragEvent.getAction()){
            case DragEvent.ACTION_DRAG_STARTED:
                if(dragEvent.getLocalState()==binding.buttonpg){
                    layoutParams = (RelativeLayout.LayoutParams) binding.buttonpg.getLayoutParams();
                }
                else if(dragEvent.getLocalState()==binding.buttonsg){
                    layoutParams = (RelativeLayout.LayoutParams) binding.buttonsg.getLayoutParams();
                }
                else if(dragEvent.getLocalState()==binding.buttonsf){
                    layoutParams = (RelativeLayout.LayoutParams) binding.buttonsf.getLayoutParams();
                }
                else if(dragEvent.getLocalState()==binding.buttonpf){
                    layoutParams = (RelativeLayout.LayoutParams) binding.buttonpf.getLayoutParams();
                }
                else if(dragEvent.getLocalState()==binding.buttonball){
                    layoutParams = (RelativeLayout.LayoutParams) binding.buttonball.getLayoutParams();
                }
                else{
                    layoutParams = (RelativeLayout.LayoutParams) binding.buttonc.getLayoutParams();
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            case DragEvent.ACTION_DROP:
                layoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams.removeRule(RelativeLayout.CENTER_VERTICAL);
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
                layoutParams.rightMargin=0;
                layoutParams.leftMargin=0;
                layoutParams.topMargin=0;
                layoutParams.bottomMargin=0;
                layoutParams.leftMargin = (int) dragEvent.getX() - binding.buttonpg.getWidth() / 2;
                layoutParams.topMargin = (int) dragEvent.getY() - binding.buttonpg.getHeight() / 2;
                View gorselNesne = (View) dragEvent.getLocalState();
                ViewGroup eskitasarımalanı = (ViewGroup) gorselNesne.getParent();
                eskitasarımalanı.removeView(gorselNesne);
                RelativeLayout hedef = (RelativeLayout) view;
                hedef.addView(gorselNesne,layoutParams);
                gorselNesne.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onLongClick(View view) {
        ClipData.Item item = new ClipData.Item((CharSequence)view.getTag());
        String [] mimetype = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData clipData = new ClipData(view.getTag().toString(),mimetype,item);
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(clipData,shadowBuilder,view,0);
        view.setVisibility(View.INVISIBLE);
        return true;
    }

    public void opensaveDialog(){
        savetacticsBinding = SavetacticsBinding.inflate(getLayoutInflater());
        saveDio.setContentView(savetacticsBinding.getRoot());
        saveDio.setCanceledOnTouchOutside(false);
        saveDio.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        saveDio.show();
        saveDio.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        savetacticsBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveDio.dismiss();
            }
        });

    }
    private void handleResponse(){
        Intent intent = new Intent(Lineup.this,TacticList.class);
        intent.putExtra("info","all");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}