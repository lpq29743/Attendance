package com.attendance.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.attendance.R;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private View view;
        private Context context;
        private int themeResId;
        private String title;

        private DialogInterface.OnClickListener positiveButtonClickListener,
                negativeButtonClickListener;

        public Builder(Context context, int themeResId) {
            this.context = context;
            this.themeResId = themeResId;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setPositiveButton(DialogInterface.OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(DialogInterface.OnClickListener listener) {
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * 创建定制的对话框
         */
        public CustomDialog create() {
            final CustomDialog dialog = new CustomDialog(context, themeResId);
            dialog.addContentView(view, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // 设置对话框的标题
            ((TextView) view.findViewById(R.id.title_tv)).setText(title);
            // 设置确定按钮
            if (positiveButtonClickListener != null) {
                // 设置按钮的监听器
                ((TextView) view.findViewById(R.id.positive_tv))
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                positiveButtonClickListener.onClick(dialog,
                                        DialogInterface.BUTTON_POSITIVE);
                            }
                        });
            }
            // 设置取消按钮
            if (negativeButtonClickListener != null) {
                //设置按钮监听器
                (view.findViewById(R.id.negative_tv))
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                negativeButtonClickListener.onClick(dialog,
                                        DialogInterface.BUTTON_NEGATIVE);
                            }
                        });
            }
            dialog.setContentView(view);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            return dialog;
        }
    }

}

