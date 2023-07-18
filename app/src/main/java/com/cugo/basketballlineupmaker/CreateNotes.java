package com.cugo.basketballlineupmaker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.cugo.basketballlineupmaker.databinding.ActivityCreateNotesBinding;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateNotes extends AppCompatActivity {
    ActivityCreateNotesBinding createNotesBinding;
    NotesDatabase nd;
    NotesDao notesDao;
    Bitmap selectedImage = null;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerLauncher();
        createNotesBinding = ActivityCreateNotesBinding.inflate(getLayoutInflater());
        View view  = createNotesBinding.getRoot();
        setContentView(view);
        nd= Room.databaseBuilder(getApplicationContext(),NotesDatabase.class,"Notes").build();
        notesDao = nd.notesDao();
        createNotesBinding.noteimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(CreateNotes.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(CreateNotes.this,Manifest.permission.CAMERA)){
                        Snackbar.make(view,"Permision needed for the camera",Snackbar.LENGTH_INDEFINITE).setAction("Give permision", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //request pernission
                                permissionLauncher.launch(Manifest.permission.CAMERA);
                            }
                        }).show();
                    }
                    else{
                        //request permission
                        permissionLauncher.launch(Manifest.permission.CAMERA);
                    }
                }
                else{
                    //go cam
                    Intent gocam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activityResultLauncher.launch(gocam);

                }
            }
        });
        createNotesBinding.savenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedImage==null){
                    Notes notes = new Notes(createNotesBinding.notetitle.getText().toString(),createNotesBinding.notenote.getText().toString(),null,false);
                    compositeDisposable.add(notesDao.insert(notes).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(CreateNotes.this::handleResponse));
                }
                else{
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                    byte[] byteArray =outputStream.toByteArray();
                    Notes notes = new Notes(createNotesBinding.notetitle.getText().toString(),createNotesBinding.notenote.getText().toString(),byteArray,false);
                    compositeDisposable.add(notesDao.insert(notes).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(CreateNotes.this::handleResponse));
                }

            }
        });
    }

    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    Bitmap bm = (Bitmap)intentFromResult.getExtras().get("data");
                    if(intentFromResult != null){

                        try {
                                selectedImage = bm;
                                createNotesBinding.noteimage.setImageBitmap(selectedImage);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //permission granted
                    Intent gocam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activityResultLauncher.launch(gocam);
                }
                else{
                    //permision denied
                    Toast.makeText(CreateNotes.this,"Permission needed!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void handleResponse(){

        Intent intent = new Intent(CreateNotes.this,NoteList.class);
        intent.putExtra("infoo","all");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}