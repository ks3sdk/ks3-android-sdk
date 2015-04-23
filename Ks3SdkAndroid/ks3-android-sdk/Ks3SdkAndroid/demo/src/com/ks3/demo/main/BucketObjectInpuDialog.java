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
 * 自定义Dialog
 * 
 */
public class BucketObjectInpuDialog extends Dialog implements
		android.view.View.OnClickListener {
	public interface OnBucketObjectDialogListener {
		public void confirmBucketAndObject(String name, String key);
	}

	public OnBucketObjectDialogListener listener;
	private EditText bucketField;
	private Button okBtn;
	private Button cancelBtn;
	private Context context;
	private EditText objectField;

	public BucketObjectInpuDialog(Context context) {
		super(context);
		this.context = context;
	}

	public void setOnBucketObjectDialogListener(
			OnBucketObjectDialogListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_bucket_object_input);
		setTitle("Input Bucket Name");
		setUpViews();
	}

	private void setUpViews() {
		bucketField = (EditText) findViewById(R.id.input);
		objectField = (EditText) findViewById(R.id.input_second);
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
				if (TextUtils.isEmpty(bucketField.getText().toString())
						|| TextUtils.isEmpty(objectField.getText().toString())) {
					Toast.makeText(context,
							"Bucket name or Object key should not be null",
							Toast.LENGTH_SHORT).show();
				} else {
					String name = bucketField.getText().toString();
					String key = objectField.getText().toString();
					listener.confirmBucketAndObject(name, key);
					BucketObjectInpuDialog.this.dismiss();
				}
				break;
			case R.id.cancel:
				BucketObjectInpuDialog.this.dismiss();
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
