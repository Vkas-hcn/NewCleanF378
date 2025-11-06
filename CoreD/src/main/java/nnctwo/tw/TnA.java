package nnctwo.tw;


import android.os.Handler;
import android.os.Message;

import rgtf.uut.de;



public class TnA extends Handler {
    @Override
    public void handleMessage(Message message) {
        de.nbtw(message.what);
    }
}
