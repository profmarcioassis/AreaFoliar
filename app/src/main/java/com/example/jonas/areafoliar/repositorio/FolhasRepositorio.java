package com.example.jonas.areafoliar.repositorio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jonas.areafoliar.Folha;

import java.util.ArrayList;
import java.util.List;


public class FolhasRepositorio {
    private SQLiteDatabase conexao;

    public FolhasRepositorio(SQLiteDatabase conexao){
        this.conexao = conexao;
    }

    public void inserir(Folha folha){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOME",folha.getNome());
        contentValues.put("AREA",folha.getArea());
        contentValues.put("ALTURA",folha.getAltura());
        contentValues.put("LARGURA",folha.getLargura());
        contentValues.put("DATA",folha.getData());
        conexao.insertOrThrow("FOLHA",null,contentValues);
    }

    public void excluir(int codigo){
        String[] parametros  = new String[1];
        parametros[0] = String.valueOf(codigo);
        conexao.delete("FOLHA","CODIGO = ? ",parametros);
    }

    public void alterar(Folha folha){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOME",folha.getNome());
        contentValues.put("AREA",folha.getArea());
        contentValues.put("ALTURA",folha.getAltura());
        contentValues.put("LARGURA",folha.getLargura());
        contentValues.put("DATA",folha.getData());
        String[] parametros  = new String[1];
        parametros[0] = String.valueOf(folha.getCodigo());
        conexao.update("FOLHA",contentValues,"CODIGO = ? ",parametros);
    }

    public List<Folha> consultar(){
        List<Folha> folhas = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CODIGO,NOME,AREA,ALTURA,LARGURA,DATA ");
        sql.append("FROM FOLHA");
        Cursor resultado = conexao.rawQuery(sql.toString(),null);
        if (resultado.getCount() > 0){
            resultado.moveToFirst();
            do{
                Folha folha = new Folha();
                folha.setCodigo(resultado.getInt(resultado.getColumnIndexOrThrow("CODIGO")));
                folha.setNome(resultado.getString(resultado.getColumnIndexOrThrow("NOME")));
                folha.setArea(resultado.getString(resultado.getColumnIndexOrThrow("AREA")));
                folha.setAltura(resultado.getString(resultado.getColumnIndexOrThrow("ALTURA")));
                folha.setLargura(resultado.getString(resultado.getColumnIndexOrThrow("LARGURA")));
                folha.setData(resultado.getString(resultado.getColumnIndexOrThrow("DATA")));
                folhas.add(folha);
            }while(resultado.moveToNext());
        }
        return folhas;
    }

    public Folha buscarCliente(int codigo){
        Folha folha = new Folha();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CODIGO,NOME,AREA,ALTURA,LARGURA ");
        sql.append("FROM FOLHA ");
        sql.append("WHERE CODIGO = ?");

        String[] parametros  = new String[1];
        parametros[0] = String.valueOf(codigo);

        Cursor resultado = conexao.rawQuery(sql.toString(),null);

        if (resultado.getCount() > 0){
            resultado.moveToFirst();
            folha.setCodigo(resultado.getInt(resultado.getColumnIndexOrThrow("CODIGO")));
            folha.setNome(resultado.getString(resultado.getColumnIndexOrThrow("NOME")));
            folha.setArea(resultado.getString(resultado.getColumnIndexOrThrow("AREA")));
            folha.setAltura(resultado.getString(resultado.getColumnIndexOrThrow("ALTURA")));
            folha.setLargura(resultado.getString(resultado.getColumnIndexOrThrow("lARGURA")));
            return folha;
        }
        return null;
    }
}

