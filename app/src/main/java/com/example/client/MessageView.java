package com.example.client;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;


public class MessageView extends AppCompatTextView {

    public MessageView(Context context) {
        super(context);
        init();
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //hex for black
        setBackgroundColor(0xFFFFFFFF);
        setTextSize(12f);
        setVerticalScrollBarEnabled(true);
        setMovementMethod(new ScrollingMovementMethod());
    }

    public void appendSystemMessage(String msg) {
        final String prefixStr = getResources().getString(R.string.msg_prefix_sys);
        appendMessage(prefixStr, msg);
    }

    public void appendServerMessage(String msg) {
        final String prefixStr = getResources().getString(R.string.msg_prefix_server);
        appendMessage(prefixStr, msg);
    }

    public void appendClientMessage(String msg) {
        final String prefixStr = getResources().getString(R.string.msg_prefix_client);
        appendMessage(prefixStr, msg);
    }

    public void appendMessage(String prefixText, String msgBody) {
        final SpannableString str = new SpannableString(prefixText.concat(msgBody).concat("\n"));
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, prefixText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        append(str);
    }
}
