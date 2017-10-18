package cn.xiaolong.bluetoothconnectdemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class LoadingDialog extends Dialog {

    private TextView tvProgress;
    private String progressText;
    boolean cancelAble;

    protected LoadingDialog(Context context) {
        super(context, R.style.ProgressDialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
        tvProgress = (TextView) findViewById(R.id.text);
        setLoadText(progressText);
        setCancelable(cancelAble);
    }

    public void setLoadText(String text) {
        tvProgress.setVisibility((TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) ? View.GONE : View.VISIBLE);
        if(!TextUtils.isEmpty(text)) {
            tvProgress.setText(text);
        }
    }

    public static LoadingDialog show(Context context) {
        return show(context, null);
    }


    public static LoadingDialog show(Context context, String progressText) {
        return show(context, progressText, false);
    }

    public static LoadingDialog show(Context context, String progressText, boolean cancelAble) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.progressText = progressText;
        dialog.cancelAble = cancelAble;
        dialog.show();
        return dialog;
    }

}
