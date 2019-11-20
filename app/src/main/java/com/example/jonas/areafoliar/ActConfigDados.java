package com.example.jonas.areafoliar;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jonas.areafoliar.database.DadosOpenHelper;
import com.example.jonas.areafoliar.repositorio.FolhasRepositorio;

import java.util.ArrayList;
import java.util.List;

public class ActConfigDados extends AppCompatActivity {
    private EditText edtNome, edtArea, edtAltura, edtLargura;
    public static RecyclerView listDados;
    private ConstraintLayout layoutContentMain;
    private CoordinatorLayout coordinatorLayout;
    private FolhasRepositorio folhasRepositorio;
    private SQLiteDatabase conexao;
    private DadosOpenHelper dadosOpenHelper;
    private Folha folha;
    public static List<Folha> dados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_config_dados);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listDados = (RecyclerView) findViewById(R.id.listConfigDados);
        layoutContentMain = (ConstraintLayout)findViewById(R.id.layoutContentConfigDados);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorConfigLayout);
        criarConexao();
        listDados.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listDados.setLayoutManager(linearLayoutManager);
        folhasRepositorio = new FolhasRepositorio(conexao);
        dados = folhasRepositorio.consultar();
        edtNome = (EditText) findViewById(R.id.edtNome);
        edtArea = (EditText) findViewById(R.id.edtArea);
        edtAltura = (EditText) findViewById(R.id.edtAltura);
        edtLargura = (EditText) findViewById(R.id.edtLargura);
        criarConexao();
        verificaParametro();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.act_config_dados, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent it2 = new Intent(this, ActDados.class);
        startActivity(it2);
    }

    private void confirmar() {
        //validaCampos();
        Bundle bundle = getIntent().getExtras();
        //Toast.makeText(this,"Botão Cancelar Selecionado", Toast.LENGTH_SHORT).show();
        for(int i = 0; i < dados.size(); i ++){
            if(dados.get(i).getData().equals(bundle.getString("DATA"))){
                try {
                    if (dados.get(i).getCodigo() == 0) {
                        folhasRepositorio.inserir(dados.get(i));
                    } else {
                        folhasRepositorio.alterar(dados.get(i));
                    }
                    finish();
                } catch (SQLException ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean validaCampos() {
        String nome = edtNome.getText().toString();
        String area = edtArea.getText().toString();
        String altura = edtAltura.getText().toString();
        String largura = edtLargura.getText().toString();
        boolean res = false;

        folha.setNome(nome);
        folha.setArea(area);
        folha.setAltura(altura);
        folha.setLargura(largura);

        if (res) {
            Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    private void verificaParametro() {
        Bundle bundle = getIntent().getExtras();
        folha = new Folha();
        ArrayList<Folha> folhasSelecionadas = new ArrayList<>();
        for(int i = 0; i < dados.size(); i ++){
            if(dados.get(i).getData().equals(bundle.getString("DATA"))){
                folhasSelecionadas.add(dados.get(i));
            }
        }
        FolhasAdapter folhasAdapter = new FolhasAdapter(folhasSelecionadas);
        listDados.setAdapter(folhasAdapter);
        /*if ((bundle != null)) {
            folha.setNome(bundle.getString("NOME"));
            folha.setArea(bundle.getString("AREA"));
            folha.setAltura(bundle.getString("ALTURA"));
            folha.setLargura(bundle.getString("LARGURA"));
            folha.setData(bundle.getString("DATA"));
            folha.setCodigo(bundle.getInt("CODIGO"));
            edtNome.setText(folha.getNome());
            edtArea.setText(folha.getArea());
            edtAltura.setText(folha.getAltura());
            edtLargura.setText(folha.getLargura());
        }*/
    }

    public void criarConexao() {
        try {
            dadosOpenHelper = new DadosOpenHelper(this);
            conexao = dadosOpenHelper.getWritableDatabase();
            folhasRepositorio = new FolhasRepositorio(conexao);
            //Toast.makeText(getApplicationContext(), "Conexão criada com sucesso", Toast.LENGTH_SHORT).show();
        } catch (SQLException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_ok:
                //Toast.makeText(this,"Botão OK Selecionado", Toast.LENGTH_SHORT).show();
                confirmar();
                break;
            case R.id.action_excluir:
                Bundle bundle = getIntent().getExtras();
                //Toast.makeText(this,"Botão Cancelar Selecionado", Toast.LENGTH_SHORT).show();
                for(int i = 0; i < dados.size(); i ++){
                    if(dados.get(i).getData().equals(bundle.getString("DATA"))){
                        folhasRepositorio.excluir(dados.get(i).getCodigo());
                    }
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
