package com.example.jonas.areafoliar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jonas.areafoliar.database.DadosOpenHelper;
import com.example.jonas.areafoliar.repositorio.FolhasRepositorio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActDados extends AppCompatActivity{
    public static RecyclerView listDados;
    private SQLiteDatabase conexao;
    private DadosOpenHelper dadosOpenHelper;
    private ConstraintLayout layoutContentMain;
    private CoordinatorLayout coordinatorLayout;
    private FolhasRepositorio folhasRepositorio;
    private SimpleDateFormat dateFormat;
    private String data_completa,diaAtual,mesAtual,nomeMesAtual,diaFolha,mesFolha;
    private Date data_atual,data;
    private Calendar cal;
    public static List<Folha> dados;
    private ArrayList<Folha> maisAntigo = new ArrayList<>();
    private ArrayList<Folha> mesPassado = new ArrayList<>();
    private ArrayList<Folha> mesPresente = new ArrayList<>();
    private ArrayList<Folha> maisRecentes = new ArrayList<>();
    private SwipeController swipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dados);
        //ArrayList<Historico> historicos = new ArrayList<>();
        listDados = (RecyclerView) findViewById(R.id.listDados);
        layoutContentMain = (ConstraintLayout)findViewById(R.id.layoutContentDados);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        criarConexao();
        listDados.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listDados.setLayoutManager(linearLayoutManager);
        folhasRepositorio = new FolhasRepositorio(conexao);
        dados = folhasRepositorio.consultar();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        data = new Date();
        cal = Calendar.getInstance();
        cal.setTime(data);
        data_atual = cal.getTime();
        data_completa = dateFormat.format(data_atual);
        diaAtual = data_completa.substring(0,2);
        mesAtual = data_completa.substring(3,5);
        if(mesAtual.equals("01")){
            nomeMesAtual = "Janeiro";
        } else if(mesAtual.equals("02")){
            nomeMesAtual = "Fevereiro";
        } else if(mesAtual.equals("03")){
            nomeMesAtual = "Março";
        } else if(mesAtual.equals("04")){
            nomeMesAtual = "Abril";
        } else if(mesAtual.equals("05")){
            nomeMesAtual = "Maio";
        } else if(mesAtual.equals("06")){
            nomeMesAtual = "Junho";
        } else if(mesAtual.equals("07")){
            nomeMesAtual = "Julho";
        } else if(mesAtual.equals("08")){
            nomeMesAtual = "Agosto";
        } else if(mesAtual.equals("09")){
            nomeMesAtual = "Setembro";
        } else if(mesAtual.equals("10")){
            nomeMesAtual = "Outubro";
        } else if(mesAtual.equals("11")){
            nomeMesAtual = "Novembro";
        } else{
            nomeMesAtual = "Dezembro";
        }
        for(int i = 0; i < dados.size(); i ++){
            diaFolha = dados.get(i).getData().substring(0,2);
            mesFolha = dados.get(i).getData().substring(3,5);
            if(diaFolha.equals(diaAtual) && mesFolha.equals(mesAtual)){
                maisRecentes.add(dados.get(i));
            }else if(mesFolha.equals(mesAtual)){
                mesPresente.add(dados.get(i));
            }else if(Integer.parseInt(mesFolha) == (Integer.parseInt((mesAtual)) - 1)){
                mesPassado.add(dados.get(i));
            }else{
                maisAntigo.add(dados.get(i));
            }
        }
        ArrayList<Historico> historicos = new ArrayList<>();
        Historico antigos = new Historico("Mais antigo",calculaMedia(maisAntigo));
        historicos.add(antigos);
        Historico passado = new Historico("Mês passado",calculaMedia(mesPassado));
        historicos.add(passado);
        Historico presente = new Historico(nomeMesAtual,calculaMedia(mesPresente));
        historicos.add(presente);
        Historico recente = new Historico("Recentes",calculaMedia(maisRecentes));
        historicos.add(recente);

        Folhas2Adapter folhas2Adapter = new Folhas2Adapter(historicos);
        //folhasAdapter = new FolhasAdapter(dados);
        //listDados.setAdapter(folhasAdapter);
        listDados.setAdapter(folhas2Adapter);

        setupRecyclerView();
    }

    protected ArrayList<Folha> calculaMedia(ArrayList<Folha> folhasCarregadas){
        ArrayList<Folha> medias = new ArrayList<>();
        ArrayList<Folha> temp = new ArrayList<>();
        double mediaAltura = 0,mediaLargura = 0,mediaArea = 0;
        String nomeData, nomeTemp = " ",vetorData[],vetorTemp[];

        for(int i = 0; i < folhasCarregadas.size(); i ++){
            Folha folha = null;
            vetorData = folhasCarregadas.get(i).getData().split(" ");
            nomeData = vetorData[0];
            if(i == folhasCarregadas.size() - 1){
                temp.add(folhasCarregadas.get(i));
                for(int j = 0; j < temp.size(); j ++){
                    mediaAltura += Double.parseDouble(temp.get(j).getAltura());
                    mediaArea += Double.parseDouble(temp.get(j).getArea());
                    mediaLargura += Double.parseDouble(temp.get(j).getLargura());
                }
                BigDecimal bdArea = new BigDecimal((mediaArea / temp.size())).setScale(4, RoundingMode.HALF_EVEN);
                BigDecimal bdAltura = new BigDecimal((mediaAltura / temp.size())).setScale(4, RoundingMode.HALF_EVEN);
                BigDecimal bdLargura = new BigDecimal((mediaLargura / temp.size())).setScale(4, RoundingMode.HALF_EVEN);
                folha = new Folha(nomeData + " - Nome do teste", bdArea + "", bdAltura + "", bdLargura + "",nomeData);
                medias.add(folha);
                mediaAltura = 0;mediaLargura = 0;mediaArea = 0;
                temp.clear();
            }else{
                vetorTemp = folhasCarregadas.get(i + 1).getNome().split(" ");
                nomeTemp = vetorTemp[0];
                if(nomeTemp.equals(nomeData)){
                    temp.add(folhasCarregadas.get(i));
                }else{
                    if(temp.isEmpty()){
                        BigDecimal bdArea = new BigDecimal(folhasCarregadas.get(i).getArea()).setScale(4, RoundingMode.HALF_EVEN);
                        BigDecimal bdAltura = new BigDecimal(folhasCarregadas.get(i).getAltura()).setScale(4, RoundingMode.HALF_EVEN);
                        BigDecimal bdLargura = new BigDecimal(folhasCarregadas.get(i).getLargura()).setScale(4, RoundingMode.HALF_EVEN);
                        folha = new Folha(nomeData + " - Nome do teste",bdArea + "",bdAltura + "",bdLargura + "",nomeData);
                        medias.add(folha);
                        mediaAltura = 0;mediaLargura = 0;mediaArea = 0;
                    }else{
                        for(int j = 0; j < temp.size(); j ++){
                            mediaAltura += Double.parseDouble(temp.get(j).getAltura());
                            mediaArea += Double.parseDouble(temp.get(j).getArea());
                            mediaLargura += Double.parseDouble(temp.get(j).getLargura());
                        }
                        BigDecimal bdArea = new BigDecimal((mediaArea / temp.size())).setScale(4, RoundingMode.HALF_EVEN);
                        BigDecimal bdAltura = new BigDecimal((mediaAltura / temp.size())).setScale(4, RoundingMode.HALF_EVEN);
                        BigDecimal bdLargura = new BigDecimal((mediaLargura / temp.size())).setScale(4, RoundingMode.HALF_EVEN);
                        folha = new Folha(nomeData + " - Nome do teste", bdArea + "", bdAltura + "", bdLargura + "",nomeData);
                        medias.add(folha);
                        mediaAltura = 0;mediaLargura = 0;mediaArea = 0;
                        temp.clear();
                    }
                }
            }
            for(int j = 0; j < dados.size(); j ++){
                if(!dados.get(i).getNome().equals(nomeData)){
                    folhasRepositorio.inserir(folha);
                }
            }
        }
        temp.clear();
        return medias;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        dados = folhasRepositorio.consultar();
        maisRecentes.clear();
        mesPresente.clear();
        mesPassado.clear();
        maisAntigo.clear();
        for(int i = 0; i < dados.size(); i ++){
            diaFolha = dados.get(i).getData().substring(0,2);
            mesFolha = dados.get(i).getData().substring(3,5);
            if(diaFolha.equals(diaAtual) && mesFolha.equals(mesAtual)){
                maisRecentes.add(dados.get(i));
            }else if(mesFolha.equals(mesAtual)){
                mesPresente.add(dados.get(i));
            }else if(Integer.parseInt(mesFolha) == (Integer.parseInt((mesAtual)) - 1)){
                mesPassado.add(dados.get(i));
            }else{
                maisAntigo.add(dados.get(i));
            }
        }
        ArrayList<Historico> historicos = new ArrayList<>();
        Historico antigos = new Historico("Mais antigo",calculaMedia(maisAntigo));
        historicos.add(antigos);
        Historico passado = new Historico("Mês passado",calculaMedia(mesPassado));
        historicos.add(passado);
        Historico presente = new Historico(nomeMesAtual,calculaMedia(mesPresente));
        historicos.add(presente);
        Historico recente = new Historico("Recentes",calculaMedia(maisRecentes));
        historicos.add(recente);

        Folhas2Adapter folhas2Adapter = new Folhas2Adapter(historicos);
        //folhasAdapter = new FolhasAdapter(dados);
        //listDados.setAdapter(folhasAdapter);
        listDados.setAdapter(folhas2Adapter);
    }

    public void criarConexao(){
        try{
            dadosOpenHelper = new DadosOpenHelper(this);
            conexao = dadosOpenHelper.getWritableDatabase();
            folhasRepositorio = new FolhasRepositorio(conexao);
            Toast.makeText(getApplicationContext(), "Conexão criada com sucesso", Toast.LENGTH_SHORT).show();
        }catch (SQLException ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act_dados, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_dados) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Nesta tela você pode visualizar as análises que ja foram realizadas. Para " +
                    "uma melhor identificação, os dados foram separados de acordo com a data em que foram gerados.");
            builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {

            }
            public void onLeftClicked(int position) {

            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(listDados);

        listDados.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }
}
