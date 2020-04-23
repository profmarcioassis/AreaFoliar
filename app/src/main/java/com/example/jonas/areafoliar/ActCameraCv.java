package com.example.jonas.areafoliar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.jonas.areafoliar.database.DadosOpenHelper;
import com.example.jonas.areafoliar.repositorio.FolhasRepositorio;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ActCameraCv extends AppCompatActivity implements CvCameraViewListener2, View.OnClickListener {
    private static final String TAG = "MYAPP::OPENCV";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat result;
    private double altQuad, largQuad;
    private Mat ImageMat;
    public static Bitmap bitmap;
    int total = 0;
    double altura = 0,largura = 0,areaQuadradoPx = 0,areaFolhaCm = 0;
    private FolhasRepositorio folhaRepositorio;
    private int cont = 1;
    private String data_completa;

    BaseLoaderCallback mCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == BaseLoaderCallback.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

   static {
       if(!OpenCVLoader.initDebug()){
           Log.i("OpenCv", "OpenCV loaded failed");
       }else {
           Log.i("OpenCv", "OpenCV loaded successfully");
       }
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.act_camera_cv);
        (findViewById(R.id.camera)).setOnClickListener(this);
        (findViewById(R.id.gallery)).setOnClickListener(this);
        mOpenCvCameraView = findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        criarConexao();
    }

    public void criarConexao(){
        try{
            DadosOpenHelper dadosOpenHelper = new DadosOpenHelper(this);
            SQLiteDatabase conexao = dadosOpenHelper.getWritableDatabase();
            folhaRepositorio = new FolhasRepositorio(conexao);
            //Toast.makeText(getApplicationContext(), "Conexão criada com sucesso", Toast.LENGTH_SHORT).show();
        }catch (SQLException ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent it2 = new Intent(this, ActMain.class);
        startActivity(it2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                Mat src = result.clone();
                //Realiza a criação das bounding boxes
                Mat result = createBoundingBoxes(src);
                //Realiza a cnoversão de Mat para Bitmap
                bitmap = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(result, bitmap);
                //ContentValues contentValues = new ContentValues();
                //Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                //Salva a imagem
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                Intent it = new Intent(this, ActCamera.class);
                startActivity(it);

                break;

            case R.id.gallery:
                Intent intentPegaFoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentPegaFoto, 1);
                break;

        }
    }

    static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    protected Mat createBoundingBoxes(Mat src) {
        double mediaAltura = 0,mediaLargura = 0,mediaArea = 0;

        Mat cannyOutput = new Mat();
        Imgproc.threshold(src, cannyOutput, 111.56, 255, Imgproc.THRESH_OTSU);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
        RotatedRect[] rotatedRect = new RotatedRect[contours.size()];
        Point[] centers = new Point[contours.size()];
        float[][] radius = new float[contours.size()][1];
        for (int i = 0; i < contours.size(); i++) {
            contoursPoly[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true) * 0.02, true);
            rotatedRect[i] = Imgproc.minAreaRect(contoursPoly[i]);
            centers[i] = new Point();
            Imgproc.minEnclosingCircle(contoursPoly[i], centers[i], radius[i]);
        }
        //Mat drawing = Mat.zeros(src.size(), CvType.CV_8UC3);
        List<MatOfPoint> contoursPolyList = new ArrayList<>(contoursPoly.length);

        for (MatOfPoint2f poly : contoursPoly) {
            contoursPolyList.add(new MatOfPoint(poly.toArray()));
        }
        int areaLimitante = 10000;
        int areaMaxima = 8000000;
        int areaQuadradoCm;
        int alturaQuadrado;

        //sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        //String result = sharedPreferences.getString(getString(R.string.pref_text), "");
        //alturaQuadrado = Integer.parseInt(result);
        SharedPreferences sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
        alturaQuadrado = sharedPreferences.getInt("lado", 5);
        areaQuadradoCm = alturaQuadrado * alturaQuadrado;

        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contoursPolyList.get(i));
            if (contoursPolyList.get(i).toArray().length == 4 && area > areaLimitante && area < areaMaxima) {
                double maxCosine = 0;
                total = contoursPolyList.get(i).toArray().length;
                Point bl = new Point(rotatedRect[i].boundingRect().tl().x, rotatedRect[i].boundingRect().br().y);
                double cosine = angle(rotatedRect[i].boundingRect().tl(), rotatedRect[i].boundingRect().br(), bl);
                maxCosine = Math.max(maxCosine, cosine);
                if (maxCosine < 0.3) {
                    Scalar color = new Scalar(0, 255, 0);
                    Imgproc.drawContours(ImageMat, contours, i, color, 3);
                    //Imgproc.rectangle(ImageMat, rotatedRect[i].tl(), rotatedRect[i].br(), color, 3);
                    //Imgproc.circle(drawing, centers[i], (int) radius[i][0], color, 3);
                    //areaQuadradoPx = Core.countNonZero(src.submat(rotatedRect[i]));
                    areaQuadradoPx = Imgproc.contourArea(contours.get(i));
                    altQuad = rotatedRect[i].boundingRect().height;
                    largQuad = rotatedRect[i].boundingRect().width;
                }
            } else if (area > areaLimitante && area < areaMaxima && rotatedRect[i].boundingRect().height * alturaQuadrado / altQuad > 1) {
                Scalar color = new Scalar(255, 0, 0);
                Imgproc.drawContours(ImageMat, contours, i ,color, 3);
                double x = rotatedRect[i].boundingRect().x +  0.5 * rotatedRect[i].boundingRect().width;
                double y = rotatedRect[i].boundingRect().y +  0.5 * rotatedRect[i].boundingRect().height;
                Imgproc.putText (ImageMat,cont + "",new Point(x,y),Core.FONT_HERSHEY_SIMPLEX ,5,new Scalar(255, 0, 0), 10);
                //Imgproc.rectangle(ImageMat, rotatedRect[i].tl(), rotatedRect[i].br(), color, 3);
                //Imgproc.circle(drawing, centers[i], (int) radius[i][0], color, 3);
                //result = src.submat(rotatedRect[i]);
                //areaFolhaCm = Core.countNonZero(result) * areaQuadradoCm / areaQuadradoPx;
                //areaFolhaCm = (Imgproc.contourArea(contours.get(i)) * areaQuadradoCm) / areaQuadradoPx;
                areaFolhaCm = ((Imgproc.contourArea(contours.get(i)) * areaQuadradoCm) / areaQuadradoPx);
                altura = rotatedRect[i].boundingRect().height * alturaQuadrado / altQuad;
                largura = rotatedRect[i].boundingRect().width * alturaQuadrado / largQuad;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                Date data = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(data);
                Date data_atual = cal.getTime();
                data_completa = dateFormat.format(data_atual);
                Folha folha = new Folha();
                folha.setNome(data_completa + " " + cont);
                folha.setArea(areaFolhaCm + "");
                folha.setAltura(altura + "");
                folha.setLargura(largura + "");
                folha.setData(data_completa);
                folha.setTipo(0);
                folhaRepositorio.inserir(folha);
                cont ++;
                mediaAltura += altura;
                mediaLargura += largura;
                mediaArea += areaFolhaCm;
                //Folha folha = new Folha("Folha " + (folhas.size() + 1), areaFolhaCm + "", altura + "", largura + "");
                //folhas.add(folha);
            }
        }
        Folha folhaMedia = new Folha();
        folhaMedia.setNome(data_completa + " - Nome do teste");
        BigDecimal bdArea = new BigDecimal((mediaArea/cont)).setScale(4, RoundingMode.HALF_EVEN);
        folhaMedia.setArea(bdArea + "");
        BigDecimal bdAltura = new BigDecimal((mediaAltura/cont)).setScale(4, RoundingMode.HALF_EVEN);
        folhaMedia.setAltura(bdAltura + "");
        BigDecimal bdLargura = new BigDecimal((mediaLargura/cont)).setScale(4, RoundingMode.HALF_EVEN);
        folhaMedia.setLargura(bdLargura + "");
        folhaMedia.setData(data_completa);
        folhaMedia.setTipo(1);
        folhaRepositorio.inserir(folhaMedia);
        return ImageMat;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (data == null){
                //Toast.makeText(getApplicationContext(), "Escolha uma foto", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Choose a photo", Toast.LENGTH_LONG).show();
            }else{
                Uri imagemSelecionada = data.getData();
                String[] colunaArquivo = {MediaStore.Images.Media.DATA};
                assert imagemSelecionada != null;
                @SuppressLint("Recycle") Cursor c = getContentResolver().query(imagemSelecionada, colunaArquivo, null, null, null);
                assert c != null;
                c.moveToFirst();
                int columIndex = c.getColumnIndex(colunaArquivo[0]);
                String picPath = c.getString(columIndex);
                bitmap = BitmapFactory.decodeFile(picPath);

                ImageMat = new Mat();
                //Cria um bitmap com a configuração ARGB_8888
                Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                //Transforma o Bitmap em Mat
                Utils.bitmapToMat(bmp, ImageMat);
                //Cria uma matriz com o tamanho e o tipo do Mat posterior
                result = new Mat(ImageMat.size(), ImageMat.type());
                //Converte a imagem em tons de cinza
                Imgproc.cvtColor(ImageMat, result, Imgproc.COLOR_RGB2GRAY);
                //Cria as bounding boxes
                result = createBoundingBoxes(result);
                //Converte o Mat em bitmap para salvar na tela
                Utils.matToBitmap(result, bitmap);
                //Cria objeto de ByteArray
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //Converte o bitmap para JPEG
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                //Abre a tela para mostrar o resultado
                Intent it = new Intent(this, ActCamera.class);
                //Inicia a intent
                startActivity(it);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallBack.onManagerConnected(BaseLoaderCallback.SUCCESS);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //Converte a imagem fornecida pela camera em tons de cinza
        ImageMat = inputFrame.gray();
        //Cria um matriz
        result = new Mat();
        //Converte o Mat em tons de cinza
        Imgproc.threshold(ImageMat, result, 80, 255, Imgproc.THRESH_BINARY_INV);
        //Imgproc.adaptiveThreshold(src,result,255, Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY_INV,19,5);
        return result;
    }
}