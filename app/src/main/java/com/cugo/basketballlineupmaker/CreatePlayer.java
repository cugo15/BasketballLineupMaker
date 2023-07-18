package com.cugo.basketballlineupmaker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import com.cugo.basketballlineupmaker.databinding.ActivityCreatePlayerBinding;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;

public class CreatePlayer extends AppCompatActivity {
    private ActivityCreatePlayerBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePlayerBinding.inflate(getLayoutInflater());
        View view  = binding.getRoot();
        setContentView(view);
        registerLauncher();
        database = this.openOrCreateDatabase("Players",MODE_PRIVATE,null);
        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if (info.equals("update")){
            //update
            int artId = intent.getIntExtra("playerId",1);
            binding.save.setVisibility(View.INVISIBLE);
            binding.update.setVisibility(View.VISIBLE);
            binding.delete.setVisibility(View.VISIBLE);
            binding.textView3.setText("Update Player");

            binding.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String delete ="DELETE FROM players WHERE id ="+artId;
                    database.execSQL(delete);
                    Intent intent = new Intent(CreatePlayer.this,BasketballPlayers.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            binding.update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String updatedname = binding.fname.getText().toString();
                    String upposition = binding.spinnerposition.getSelectedItem().toString();
                    String upheight = binding.height.getText().toString();
                    String upage = binding.age.getText().toString();
                    String upjer = binding.jerseynumber.getText().toString();
                      if(binding.fname.getText().toString().equals("")){
                        Toast.makeText(CreatePlayer.this,"Enter the name please.",Toast.LENGTH_LONG).show();
                    }
                    else if(binding.height.getText().toString().equals("")){
                        Toast.makeText(CreatePlayer.this,"Enter the height please.",Toast.LENGTH_LONG).show();
                    }
                    else if(binding.spinnerposition.getSelectedItem().toString().equals("")){
                        Toast.makeText(CreatePlayer.this,"Enter the position please.",Toast.LENGTH_LONG).show();
                    }
                    else if(binding.age.getText().toString().equals("")){
                        Toast.makeText(CreatePlayer.this,"Enter the age please.",Toast.LENGTH_LONG).show();
                    }
                    else if(binding.jerseynumber.getText().toString().equals("")||Integer.parseInt(binding.jerseynumber.getText().toString())>99){
                        Toast.makeText(CreatePlayer.this,"Enter the jersey number please.\n(1-99)",Toast.LENGTH_LONG).show();
                    }
                    else{

                    String sqlname = "UPDATE players SET name = '"+updatedname+"' WHERE id ="+artId;
                    String sqlpos = "UPDATE players SET position = '"+upposition+"' WHERE id ="+artId;
                    String sqlheight = "UPDATE players SET height = '"+upheight+"' WHERE id ="+artId;
                    String sqlage = "UPDATE players SET age = '"+upage+"' WHERE id ="+artId;
                    String sqljersey = "UPDATE players SET jerseynumber = '"+upjer+"' WHERE id ="+artId;

                    database.execSQL(sqlname);
                    database.execSQL(sqlpos);
                    database.execSQL(sqlheight);
                    database.execSQL(sqlage);
                    database.execSQL(sqljersey);
                    Intent intent = new Intent(CreatePlayer.this,BasketballPlayers.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    }
                }
            });
            binding.update.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    try{
                    Bitmap smallImage = makeSmallerImage(selectedImage,245);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
                    byte[] byteArray =outputStream.toByteArray();
                    ContentValues cv = new ContentValues();
                    cv.put("image", byteArray);
                    database.update("players", cv, "id="+artId, null);
                    Intent intent = new Intent(CreatePlayer.this,BasketballPlayers.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);}
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            try{
                Cursor cursor = database.rawQuery("SELECT * FROM players WHERE id = ?",new String[]{String.valueOf(artId)});
                int nameIX = cursor.getColumnIndex("name");
                int idIX =cursor.getColumnIndex("id");
                int heightIX =cursor.getColumnIndex("height");
                int ageIX =cursor.getColumnIndex("age");
                int posIX =cursor.getColumnIndex("position");
                int jerIX =cursor.getColumnIndex("jerseynumber");
                int imageIX =cursor.getColumnIndex("image");
                while(cursor.moveToNext()){
                    binding.fname.setText(cursor.getString(nameIX));
                    binding.height.setText(cursor.getString(heightIX));
                    binding.age.setText(cursor.getString(ageIX));
                    binding.jerseynumber.setText(cursor.getString(jerIX));
                    byte[] image = cursor.getBlob(imageIX);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
                    binding.imageView.setImageBitmap(bitmap);
                    String c = cursor.getString(posIX);
                    if(c.equals("PG")){

                        binding.spinnerposition.setSelection(1);
                    }
                    else if(c.equals("SG")){
                        binding.spinnerposition.setSelection(2);
                    }
                    else if(c.equals("SF")){
                        binding.spinnerposition.setSelection(3);
                    }
                    else if(c.equals("PF")){
                        binding.spinnerposition.setSelection(4);
                    }
                    else if(c.equals("C")){
                        binding.spinnerposition.setSelection(5);
                    }
                    else{
                        binding.spinnerposition.setSelection(0);
                    }
                }
                cursor.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        else{
            // new player
            binding.fname.setText("");
            binding.age.setText("");
            binding.height.setText("");
            binding.jerseynumber.setText("");
            binding.save.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.selectimage);
        }
    }
    public void save(View view){
        String name = binding.fname.getText().toString();
        String height = binding.height.getText().toString();
        String position = binding.spinnerposition.getSelectedItem().toString();
        String age = binding.age.getText().toString();
        String jerseynumber = binding.jerseynumber.getText().toString();

        if(selectedImage==null){
            Toast.makeText(CreatePlayer.this,"Select photo please.",Toast.LENGTH_LONG).show();
        }
        else if(binding.fname.getText().toString().equals("")){
            Toast.makeText(CreatePlayer.this,"Enter the name please.",Toast.LENGTH_LONG).show();
        }
        else if(binding.height.getText().toString().equals("")){
            Toast.makeText(CreatePlayer.this,"Enter the height please.",Toast.LENGTH_LONG).show();
        }
        else if(binding.spinnerposition.getSelectedItem().toString().equals("")){
            Toast.makeText(CreatePlayer.this,"Enter the position please.",Toast.LENGTH_LONG).show();
        }
        else if(binding.age.getText().toString().equals("")){
            Toast.makeText(CreatePlayer.this,"Enter the age please.",Toast.LENGTH_LONG).show();
        }
        else if(binding.jerseynumber.getText().toString().equals("")||Integer.parseInt(binding.jerseynumber.getText().toString())>99){
            Toast.makeText(CreatePlayer.this,"Enter the jersey number please.\n(1-99)",Toast.LENGTH_LONG).show();
        }
        else {
            Bitmap smallImage = makeSmallerImage(selectedImage, 245);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS players(id INTEGER PRIMARY KEY , name VARCHAR,position VARCHAR , height VARCHAR ,age VARCHAR,jerseynumber VARCHAR,image BLOB)");
                String sqlString = "INSERT INTO players (name,position,height,age,jerseynumber,image)VALUES(?,?,?,?,?,?)";
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                sqLiteStatement.bindString(1, name);
                sqLiteStatement.bindString(2, position);
                sqLiteStatement.bindString(3, height);
                sqLiteStatement.bindString(4, age);
                sqLiteStatement.bindString(5, jerseynumber);
                sqLiteStatement.bindBlob(6, byteArray);
                sqLiteStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(CreatePlayer.this, BasketballPlayers.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("infom","see");
            startActivity(intent);
        }
    }
    public Bitmap makeSmallerImage(Bitmap selectedImage,int maximumSize){
        int width = selectedImage.getWidth();
        int height = selectedImage.getHeight();
        float bitMapRatio = (float)width/(float)height;
        if(bitMapRatio>1){
            width = maximumSize;
            height = (int) (width/bitMapRatio);
        }
        else{
            height = maximumSize;
            width = (int) (height*bitMapRatio);
        }
        return selectedImage.createScaledBitmap(selectedImage,width,height,true);
    }

    public void selectImage(View view){
    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            Snackbar.make(view,"Permision needed for the gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permision", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //request pernission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }).show();
        }
        else{
            //request permission
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
    else{
        //go gallery
        Intent intoGalletry = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intoGalletry);

    }
    }
    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
             if(result.getResultCode() == RESULT_OK){
                 Intent intentFromResult = result.getData();
             if(intentFromResult != null){
                 Uri imageData = intentFromResult.getData();
                 try {
                     if(Build.VERSION.SDK_INT>=28){
                     ImageDecoder.Source source = ImageDecoder.createSource(CreatePlayer.this.getContentResolver(),imageData); // sadece api 28 ve üstü
                     selectedImage = ImageDecoder.decodeBitmap(source);
                     binding.imageView.setImageBitmap(selectedImage);
                     }
                     else{
                         selectedImage = MediaStore.Images.Media.getBitmap(CreatePlayer.this.getContentResolver(),imageData);
                         binding.imageView.setImageBitmap(selectedImage);
                     }
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
                    Intent intoGalletry = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intoGalletry);
                }
                else{
                    //permision denied
                    Toast.makeText(CreatePlayer.this,"Permission needed!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}