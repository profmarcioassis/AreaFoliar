package com.example.jonas.areafoliar;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

public class FolhasAdapter extends RecyclerView.Adapter<FolhasAdapter.ViewHolderFolhas>{

    private List<Folha> folhas;

    public FolhasAdapter(List<Folha> folhas){
        this.folhas = folhas;
    }

    @Override
    public FolhasAdapter.ViewHolderFolhas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.linha_config_dados,viewGroup,false);
        ViewHolderFolhas holderFolhas = new ViewHolderFolhas(view, viewGroup.getContext());
        return holderFolhas;
    }

    @Override
    public void onBindViewHolder(@NonNull FolhasAdapter.ViewHolderFolhas viewHolder, int i) {
        if (folhas!= null && (folhas.size() > 0)){
            Folha folha = folhas.get(i);
            viewHolder.lblNome.setText(folha.getNome());
            viewHolder.lblArea.setText(folha.getArea());
            viewHolder.lblAltura.setText(folha.getAltura());
            viewHolder.lblLargura.setText(folha.getLargura());
        }
    }

    @Override
    public int getItemCount() {
        return folhas.size();
    }

    public class ViewHolderFolhas extends RecyclerView.ViewHolder{
        public TextView lblNome,lblArea,lblAltura,lblLargura;

        public ViewHolderFolhas(@NonNull View itemView, final Context context) {
            super(itemView);
            lblNome = (TextView) itemView.findViewById(R.id.edtNome);
            lblArea = (TextView) itemView.findViewById(R.id.edtArea);
            lblAltura = (TextView) itemView.findViewById(R.id.edtAltura);
            lblLargura = (TextView) itemView.findViewById(R.id.edtLargura);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(folhas.size() > 0){
                        Folha folha = folhas.get(getLayoutPosition());
                        //Toast.makeText(context,"Cliente: " + cliente.nome,Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(context, ActConfigDados.class);
                        it.putExtra("FOLHA", (Serializable) folha);
                        ((AppCompatActivity)context).startActivityForResult(it,0);
                    }
                }
            });
        }
    }
}
