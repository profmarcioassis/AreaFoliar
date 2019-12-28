package com.example.jonas.areafoliar;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;

public class ActCamera extends AppCompatActivity implements View.OnClickListener{

    ImageView imageViewFoto;
    private Bitmap foto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageViewFoto = (ImageView)findViewById(R.id.imageViewFoto);
        //Bundle extras = this.getIntent().getExtras();

        //byte[] byteArray = extras.getByteArray("foto");
        //Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        if(ActCameraCv.bitmap == null){
            foto = ActMain.bitmap;
        }else{
            foto = ActCameraCv.bitmap;
        }

        Bitmap rotated = Bitmap.createBitmap(foto, 0, 0, foto.getWidth(),foto.getHeight(), matrix, true);
        imageViewFoto.setImageBitmap(rotated);

    }


    @Override
    public void onBackPressed() {
        Intent it2 = new Intent(this, ActCameraCv.class);
        startActivity(it2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download:
                ContentValues contentValues = new ContentValues();
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                OutputStream outputStream;

                try {
                    //Salva a imagem
                    outputStream = getContentResolver().openOutputStream(uri);
                    boolean compressed = ActCameraCv.bitmap.compress(Bitmap.CompressFormat.PNG, 0,
                            outputStream);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    ActCameraCv.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            case R.id.dados_camera:
                Intent it1 = new Intent(this, ActDados.class);
                startActivity(it1);
                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.act_camera,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.dados:
                Intent it1 = new Intent(this, ActDados.class);
                startActivity(it1);
                return true;
            case R.id.foto:
                Intent it2 = new Intent(this, ActCameraCv.class);
                startActivity(it2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
