package com.example.jonas.areafoliar;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Folha implements Serializable, Parcelable {
    private String nome,area,altura,largura,data;
    private int codigo;

    public Folha(String nome, String area, String altura, String largura, String data) {
        this.nome = nome;
        this.area = area;
        this.altura = altura;
        this.largura = largura;
        this.data = data;
    }

    public Folha(String nome, String area, String altura, String largura) {
        this.nome = nome;
        this.area = area;
        this.altura = altura;
        this.largura = largura;
    }

    public Folha(String nome){
        this.nome = nome;
    }

    public Folha(){
        this.codigo = 0;
    }

    protected Folha(Parcel in) {
        nome = in.readString();
        area = in.readString();
        altura = in.readString();
        largura = in.readString();
        data = in.readString();
        codigo = in.readInt();
    }

    public static final Creator<Folha> CREATOR = new Creator<Folha>() {
        @Override
        public Folha createFromParcel(Parcel in) {
            return new Folha(in);
        }

        @Override
        public Folha[] newArray(int size) {
            return new Folha[size];
        }
    };

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }

    public String getLargura() {
        return largura;
    }

    public void setLargura(String largura) {
        this.largura = largura;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
    }
}
