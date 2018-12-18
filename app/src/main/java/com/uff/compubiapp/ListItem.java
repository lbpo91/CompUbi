package com.uff.compubiapp;

import android.graphics.Color;

public class ListItem {
    public String mensagem;
    public String gpsInfo;

    public ListItem(String msg, String gpsInfo) {
        this.mensagem = msg;
        this.gpsInfo = gpsInfo;
    }

    public String getGpsInfo() {
        return gpsInfo;
    }

    public void setGpsInfo(String gpsInfo) {
        this.gpsInfo = gpsInfo;
    }

    public String getMensagem() {

        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
