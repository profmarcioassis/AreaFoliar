package com.example.jonas.areafoliar;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jonas.areafoliar.helper.BitmapHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ActCamera extends AppCompatActivity implements View.OnClickListener {

    ImageView imageViewFoto;
    private Bitmap foto;
    private int codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        setContentView(R.layout.act_camera);
        imageViewFoto = findViewById(R.id.imageViewFoto);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        codigo = bundle.getInt("CODIGO");
        foto = BitmapHelper.getInstance().getBitmap();
        Bitmap rotated = Bitmap.createBitmap(foto, 0, 0, foto.getWidth(), foto.getHeight(), matrix, true);
        imageViewFoto.setImageBitmap(rotated);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        if(ActCameraCv.bitmap != null){
            foto = ActCameraCv.bitmap;
        }else if(ActMain.bitmap != null){
            foto = ActMain.bitmap;
        }/*else{
            foto = ActDados.bitmap;
        }*/


    }

    @Override
    protected void onDestroy() {
        //imageViewFoto.setImageDrawable(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent it2 = new Intent(this, ActDados.class);
        it2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(it2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download:
                ContentValues contentValues = new ContentValues();
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                try {
                    //Salva a imagem
                    assert uri != null;
                    getContentResolver().openOutputStream(uri);
                    /* boolean compressed = ActCameraCv.bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream); */

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    foto.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.dados_camera:
                Intent it = new Intent(this, ActConfigDados.class);
                it.putExtra("CODIGO", codigo);
                startActivityForResult(it,0);
                //Intent it1 = new Intent(this, ActDados.class);
                //startActivity(it1);
                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.act_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
