package com.ks3.demo.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * 自定义Dialog，Bucket输入对话框
 * 
 */
public class BucketInpuDialog extends Dialog implements
		android.view.View.OnClickListener {
	public interface OnBucketDialogListener {
		public void confirmBucket(String name);
	}

	public OnBucketDialogListener listener;
	private EditText bucketField;
	private Button okBtn;
	private Button cancelBtn;
	private Context context;

	public BucketInpuDialog(Context context) {
		super(context);
		this.context = context;
	}

	public void setOnBucketInputListener(OnBucketDialogListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_bucket_input);
		setTitle("Input Bucket Name");
		setUpViews();
	}

	private void setUpViews() {
		bucketField = (EditText) findViewById(R.id.input);
		okBtn = (Button) findViewById(R.id.ok);
		cancelBtn = (Button) findViewById(R.id.cancel);
		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (listener != null) {
			switch (v.getId()) {
			case R.id.ok:
				if (TextUtils.isEmpty(bucketField.getText().toString())) {
					Toast.makeText(context, "Bucket name should not be null",
							Toast.LENGTH_SHORT).show();
				} else {
					String result = bucketField.getText().toString();
					listener.confirmBucket(result);
					BucketInpuDialog.this.dismiss();
				}
				break;
			case R.id.cancel:
				BucketInpuDialog.this.dismiss();
				break;
			default:
				break;
			}
		} else {
			Toast.makeText(context, "Please set OnBucketInputListener first",
					Toast.LENGTH_SHORT).show();
		}

	}

}
