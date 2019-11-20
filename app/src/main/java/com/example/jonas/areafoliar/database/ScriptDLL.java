package com.example.jonas.areafoliar.database;

public class ScriptDLL {
    public static String getCreateTableCliente(){
        StringBuilder sql = new StringBuilder();
        sql.append("  CREATE TABLE IF NOT EXISTS FOLHA( ");
        sql.append("  CODIGO   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,");
        sql.append("  NOME     VARCHAR (200) NOT NULL DEFAULT (''), " );
        sql.append("  AREA VARCHAR (255) NOT NULL DEFAULT (''), ");
        sql.append("  ALTURA    VARCHAR (200) NOT NULL DEFAULT (''),");
        sql.append("  LARGURA VARCHAR (200)  NOT NULL DEFAULT (''),");
        sql.append("  DATA VARCHAR (200) NOT NULL DEFAULT (''))");
        return sql.toString();
    }
}
