package com.example.bilal.findfastestambulance;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

public class Text_To_Speech
{
    TextToSpeech t1;
    Context ctx;
    String txt;

    public Text_To_Speech(Context ctx1,String txt1) {
        this.ctx = ctx1;
        this.txt = txt1;
        t1 = new TextToSpeech(ctx1, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    t1.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }
}
