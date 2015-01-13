package com.ks3.demo.main;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ks3.demo.main.BucketInpuDialog.OnBucketDialogListener;
import com.ksyun.ks3.model.PartETag;
import com.ksyun.ks3.model.result.CompleteMultipartUploadResult;
import com.ksyun.ks3.model.result.InitiateMultipartUploadResult;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.services.AuthListener;
import com.ksyun.ks3.services.Ks3Client;
import com.ksyun.ks3.services.Ks3ClientConfiguration;
import com.ksyun.ks3.services.handler.CompleteMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.InitiateMultipartUploadResponceHandler;
import com.ksyun.ks3.services.handler.ListPartsResponseHandler;
import com.ksyun.ks3.services.handler.PutObjectResponseHandler;
import com.ksyun.ks3.services.handler.UploadPartResponceHandler;
import com.ksyun.ks3.services.request.CompleteMultipartUploadRequest;
import com.ksyun.ks3.services.request.InitiateMultipartUploadRequest;
import com.ksyun.ks3.services.request.ListPartsRequest;
import com.ksyun.ks3.services.request.PutObjectRequest;
import com.ksyun.ks3.services.request.UploadPartRequest;

/**
 * 
 * Upload相关API使用示例，Initiate Multipart Upload，Upload Part，List Parts， Complete
 * Multipart Upload等
 */
public class UploadActivity extends Activity implements OnItemClickListener {
	public static final long PART_SIZE = 5 * 1024 * 1024;
	private Ks3ClientConfiguration configuration;
	private Ks3Client client;
	private File currentDir;
	private ListView listView;
	private TextView currentDirTextView;
	private Map<String, List<UploadFile>> dataSource;
	private FileAdapter adapter;
	private final myHandler mHandler = new myHandler();
	private BucketInpuDialog bucketInpuDialog;
	private String mBucketName;

	class ViewHolder {
		ImageView fileIcon;
		TextView fileNameTextView;
		LinearLayout fileSummaryLayout;
		TextView fileSizeTextView;
		TextView fileModiyTextView;
		LinearLayout uploadSummaryLayout;
		ProgressBar uploadProgressBar;
		TextView progressTextView;
		ImageView uploadBtn;
	}

	class FileAdapter extends BaseAdapter {
		private List<UploadFile> mUploadFiles;
		private LayoutInflater mInflater;

		FileAdapter(Context context, List<UploadFile> uploadFiles) {
			this.mUploadFiles = new ArrayList<UploadActivity.UploadFile>();
			if (uploadFiles != null)
				this.mUploadFiles.addAll(uploadFiles);
			this.mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mUploadFiles.size();
		}

		@Override
		public UploadFile getItem(int position) {
			return this.mUploadFiles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.upload_list_item,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.fileIcon = (ImageView) convertView
						.findViewById(R.id.file_icon);
				viewHolder.fileNameTextView = (TextView) convertView
						.findViewById(R.id.file_name);
				viewHolder.fileSummaryLayout = (LinearLayout) convertView
						.findViewById(R.id.file_summary_layout);
				viewHolder.fileSizeTextView = (TextView) convertView
						.findViewById(R.id.file_size);
				viewHolder.fileModiyTextView = (TextView) convertView
						.findViewById(R.id.file_last_modiy);
				viewHolder.uploadSummaryLayout = (LinearLayout) convertView
						.findViewById(R.id.progress_summary_layout);
				viewHolder.uploadProgressBar = (ProgressBar) convertView
						.findViewById(R.id.upload_progress_bar);
				viewHolder.progressTextView = (TextView) convertView
						.findViewById(R.id.upload_progress_txt);
				viewHolder.uploadBtn = (ImageView) convertView
						.findViewById(R.id.upload_btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.fileIcon
					.setImageDrawable(mUploadFiles.get(position).icon);
			viewHolder.fileNameTextView.setText(mUploadFiles.get(position).file
					.getName());

			if (!mUploadFiles.get(position).file.isDirectory()) {
				viewHolder.fileSizeTextView.setVisibility(View.VISIBLE);
				long size = mUploadFiles.get(position).file.length();
				String sizeStr = "unkown-size";

				if (size >= 1024 * 1024) {
					sizeStr = ((int) (size / 1024 / 1024) * 100) / 100 + "MB";
				} else {
					sizeStr = (mUploadFiles.get(position).file.length() / 1024)
							+ "kb";
				}
				viewHolder.fileSizeTextView.setText(sizeStr);
				String modiyStr = DemoUtils.formatDate(mUploadFiles
						.get(position).file.lastModified());
				viewHolder.fileModiyTextView.setText(modiyStr);
				if (mUploadFiles.get(position).status > UploadFile.STATUS_NOT_START) {
					viewHolder.uploadSummaryLayout.setVisibility(View.VISIBLE);
					viewHolder.uploadProgressBar.setProgress(mUploadFiles
							.get(position).progress);
					viewHolder.fileSummaryLayout.setVisibility(View.GONE);
					viewHolder.uploadBtn.setVisibility(View.GONE);
					switch (mUploadFiles.get(position).status) {
					case UploadFile.STATUS_STARTED:
						viewHolder.progressTextView.setText("准备上传");
						break;
					case UploadFile.STATUS_INIT:
						viewHolder.progressTextView.setText("初始化完成");
						break;
					case UploadFile.STATUS_UPLOADPART:
						viewHolder.progressTextView.setText("分块上传.."
								+ mUploadFiles.get(position).progress + "%");
						break;
					case UploadFile.STATUS_LISTING:
						viewHolder.progressTextView.setText("获取上传的快..");
						break;
					case UploadFile.STATUS_COMPLETE:
						viewHolder.progressTextView.setText("合并完成");
						break;
					case UploadFile.STATUS_UPLOADING:
						viewHolder.progressTextView.setText(mUploadFiles
								.get(position).progress + "%");
						break;
					case UploadFile.STATUS_FINISH:
						viewHolder.progressTextView.setText("完成上传");
						break;
					case UploadFile.STATUS_FAIL:
						viewHolder.progressTextView.setText("上传失败");
						break;

					case UploadFile.STATUS_INIT_FAIL:
						viewHolder.progressTextView.setText("上传失败");
						break;
					case UploadFile.STATUS_UPLOADPART_FAIL:
						viewHolder.progressTextView.setText("分块上传失败");
						break;
					case UploadFile.STATUS_LISTING_FAIL:
						viewHolder.progressTextView.setText("获取块失败");
						break;
					case UploadFile.STATUS_COMPLETE_FAIL:
						viewHolder.progressTextView.setText("文件合并失败");
						break;
					}
				} else {
					viewHolder.fileSummaryLayout.setVisibility(View.VISIBLE);
					viewHolder.uploadBtn.setVisibility(View.VISIBLE);
					viewHolder.uploadSummaryLayout.setVisibility(View.GONE);
				}
			} else {
				viewHolder.fileSummaryLayout.setVisibility(View.GONE);
				viewHolder.uploadSummaryLayout.setVisibility(View.GONE);
				viewHolder.uploadBtn.setVisibility(View.GONE);
			}

			return convertView;
		}

		public void fillDatas() {
			this.mUploadFiles.clear();
			this.mUploadFiles.addAll(dataSource.get(currentDir.getPath()));
			this.notifyDataSetChanged();
		}

		public void updateCurrent() {
			this.notifyDataSetChanged();
		}
	}

	class UploadFile implements Serializable {
		private static final long serialVersionUID = 1L;
		static final int STATUS_NOT_START = 0;
		static final int STATUS_STARTED = 1;
		static final int STATUS_UPLOADING = 2;
		static final int STATUS_INIT = 3;
		static final int STATUS_UPLOADPART = 4;
		static final int STATUS_LISTING = 5;
		static final int STATUS_COMPLETE = 6;
		static final int STATUS_FINISH = 7;
		static final int STATUS_FAIL = 8;
		static final int STATUS_INIT_FAIL = 9;
		static final int STATUS_UPLOADPART_FAIL = 10;
		static final int STATUS_LISTING_FAIL = 11;
		static final int STATUS_COMPLETE_FAIL = 12;
		Drawable icon;
		File file;
		int progress;
		int status;

		@Override
		public String toString() {
			return file.getName() + ",upload?" + status + ",progress:"
					+ progress;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		listView = (ListView) findViewById(R.id.files);
		currentDirTextView = (TextView) findViewById(R.id.current_dir_tv);
		currentDir = Environment.getExternalStorageDirectory();
		setUp();

	}

	private void setUp() {
		// 初始化Ks3Client
		configuration = Ks3ClientConfiguration.getDefaultConfiguration();
		client = new Ks3Client(Constants.ACCESS_KEY__ID,
				Constants.ACCESS_KEY_SECRET, UploadActivity.this);
		client.setConfiguration(configuration);

		// AuthListener方式初始化
		// client = new Ks3Client(new AuthListener() {
		// @Override
		// public String onCalculateAuth(final String httpMethod,
		// final String ContentType, final String Date,
		// final String ContentMD5, final String Resource,
		// final String Headers) {
		// // 此处应由APP端向业务服务器发送post请求返回Token。
		// // 需要注意该回调方法运行在非主线程
		// // 此处内部写法仅为示例，开发者请根据自身情况修改
		// StringBuffer result = new StringBuffer();
		// HttpPost request = new HttpPost(Constants.APP_SERTVER_HOST);
		// StringEntity se;
		// try {
		// JSONObject object = new JSONObject();
		// object.put("http_method", httpMethod.toString());
		// object.put("content_type", ContentType);
		// object.put("date", Date);
		// object.put("content_md5", ContentMD5);
		// object.put("resource", Resource);
		// object.put("headers", Headers);
		// se = new StringEntity(object.toString());
		// request.setEntity(se);
		// HttpResponse httpResponse = new DefaultHttpClient().execute(request);
		// String retSrc = EntityUtils.toString(httpResponse
		// .getEntity());
		// result.append(retSrc);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// return result.toString();
		// }
		// }, UploadActivity.this);

		// 输入框确获取Bucket之后，允许选择文件，开始Upload操作
		bucketInpuDialog = new BucketInpuDialog(UploadActivity.this);
		bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
			@Override
			public void confirmBucket(String name) {
				mBucketName = name;
				dataSource = new HashMap<String, List<UploadFile>>();
				adapter = new FileAdapter(UploadActivity.this, null);
				listView.setOnItemClickListener(UploadActivity.this);
				listView.setAdapter(adapter);
				switchDir(Environment.getExternalStorageDirectory());
			}
		});
		bucketInpuDialog.show();
		client.setConfiguration(configuration);
	}

	private void switchDir(File dir) {
		if (dir == null || !dir.exists() || dir.isFile())
			throw new IllegalArgumentException("illegal dir");

		this.currentDir = dir;
		this.currentDirTextView.setText("当前目录:" + this.currentDir.getPath());
		List<UploadFile> uploadFiles = dataSource.get(dir.getPath());
		if (uploadFiles != null && uploadFiles.size() > 0) {
			adapter.fillDatas();
			return;
		}

		uploadFiles = new ArrayList<UploadActivity.UploadFile>();
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				UploadFile uploadFile = new UploadFile();
				uploadFile.status = UploadFile.STATUS_NOT_START;
				uploadFile.file = file;
				uploadFile.icon = DemoUtils.matchImage(this,
						file.isDirectory(), file.getName());
				uploadFile.progress = 0;
				uploadFiles.add(uploadFile);
			}
		}
		dataSource.put(dir.getPath(), uploadFiles);
		adapter.fillDatas();
	}

	@Override
	public void onItemClick(AdapterView<?> adpterView, final View view,
			final int position, long arg3) {
		final UploadFile item = adapter.getItem(position);
		if (item.file.isDirectory()) {
			switchDir(item.file);
		} else {
			if (item.status == UploadFile.STATUS_STARTED
					|| item.status == UploadFile.STATUS_UPLOADING
					|| item.status == UploadFile.STATUS_FINISH)
				return;
			// 上传操作
			doUpload(mBucketName, item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (currentDir.getPath().equalsIgnoreCase(
					Environment.getExternalStorageDirectory().getPath())) {
				return super.onKeyDown(keyCode, event);
			} else {
				switchDir(currentDir.getParentFile());
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void doUpload(String bucketName, UploadFile item) {
		if (item.file == null)
			throw new IllegalArgumentException("file can not be null");
		// 根据指定的文件大小，选择用直接上传或者分块上传
		if (item.file.length() >= Constants.MULTI_UPLOAD_THREADHOLD)
			doMultipartUpload(bucketName, item);
		else
			doSingleUpload(bucketName, item);
	}

	// 上传文件
	private void doSingleUpload(final String bucketName, final UploadFile item) {
		final PutObjectRequest request = new PutObjectRequest(bucketName,
				item.file.getName(), item.file);
		//自定义参数，必须以kss:开头
		Map<String,String> params = new HashMap<String, String>();
		params.put("kss:customkey", "customvalue");
		request.setCallBack("http://127.0.0.1:19091/kss/call_back", "objectKey=bugDownload.txt", params);
		client.putObject(request, new PutObjectResponseHandler() {

			@Override
			public void onTaskProgress(double progress) {
				// if (progress > 50.0) {
				// request.abort();
				// }
				List<UploadFile> uploadFiles = dataSource.get(currentDir
						.getPath());
				for (UploadFile file : uploadFiles) {
					if (file.file.getPath().equalsIgnoreCase(
							item.file.getPath())) {
						file.status = UploadFile.STATUS_UPLOADING;
						file.progress = (int) progress;
						item.status = UploadFile.STATUS_UPLOADING;
						item.progress = (int) progress;
					}
				}
				mHandler.sendEmptyMessage(UPDATE_SINGLE_UPLOAD_STATUS);
			}

			@Override
			public void onTaskSuccess(int statesCode, Header[] responceHeaders) {
				Log.d(com.ksyun.ks3.util.Constants.LOG_TAG, "success");
			}

			@Override
			public void onTaskStart() {
				List<UploadFile> uploadFiles = dataSource.get(currentDir
						.getPath());
				for (UploadFile file : uploadFiles) {
					if (file.file.getPath().equalsIgnoreCase(
							item.file.getPath())) {
						file.status = UploadFile.STATUS_STARTED;
						file.progress = 0;
						item.status = UploadFile.STATUS_STARTED;
						item.progress = 0;
					}
				}
				mHandler.sendEmptyMessage(UPDATE_SINGLE_UPLOAD_STATUS);
			}

			@Override
			public void onTaskFinish() {
				List<UploadFile> uploadFiles = dataSource.get(currentDir
						.getPath());
				for (UploadFile file : uploadFiles) {
					if (file.file.getPath().equalsIgnoreCase(
							item.file.getPath())) {
						file.status = UploadFile.STATUS_FINISH;
						file.progress = 100;
						item.status = UploadFile.STATUS_FINISH;
						file.progress = 100;
					}
				}
				mHandler.sendEmptyMessage(UPDATE_SINGLE_UPLOAD_STATUS);
			}

			@Override
			public void onTaskFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {
				Log.d(com.ksyun.ks3.util.Constants.LOG_TAG,
						paramThrowable.toString());
				Log.d(com.ksyun.ks3.util.Constants.LOG_TAG,
						response);
				List<UploadFile> uploadFiles = dataSource.get(currentDir
						.getPath());
				for (UploadFile file : uploadFiles) {
					if (file.file.getPath().equalsIgnoreCase(
							item.file.getPath())) {
						file.status = UploadFile.STATUS_FAIL;
						item.status = UploadFile.STATUS_FAIL;
					}
				}
				mHandler.sendEmptyMessage(UPDATE_SINGLE_UPLOAD_STATUS);

			}

			@Override
			public void onTaskCancel() {
				Log.d(com.ksyun.ks3.util.Constants.LOG_TAG, "cancle ok");
			}
		});
	}

	// 分快上传
	private void doMultipartUpload(final String bucketName,
			final UploadFile item) {
		InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(
				bucketName, item.file.getName());
		initiateMultipartUpload(request, item);
	}

	private void initiateMultipartUpload(
			final InitiateMultipartUploadRequest request, final UploadFile item) {
		client.initiateMultipartUpload(request,
				new InitiateMultipartUploadResponceHandler() {
					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							InitiateMultipartUploadResult result) {
						List<UploadFile> uploadFiles = dataSource
								.get(currentDir.getPath());
						for (UploadFile file : uploadFiles) {
							if (file.file.getPath().equalsIgnoreCase(
									item.file.getPath())) {
								file.status = UploadFile.STATUS_INIT;
								item.status = UploadFile.STATUS_INIT;
							}
						}
						mHandler.sendEmptyMessage(UPDATE_SINGLE_UPLOAD_STATUS);

						beginMultiUpload(result, item);
					}

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						List<UploadFile> uploadFiles = dataSource
								.get(currentDir.getPath());
						for (UploadFile file : uploadFiles) {
							if (file.file.getPath().equalsIgnoreCase(
									item.file.getPath())) {
								file.status = UploadFile.STATUS_INIT_FAIL;
								item.status = UploadFile.STATUS_INIT_FAIL;
							}
						}
						mHandler.sendEmptyMessage(UPDATE_SINGLE_UPLOAD_STATUS);
					}
				});
	}

	private void beginMultiUpload(InitiateMultipartUploadResult initResult,
			UploadFile item) {
		UploadPartRequestFactory localUploadPartRequestFactory = new UploadPartRequestFactory(
				initResult.getBucket(), initResult.getKey(),
				initResult.getUploadId(), item.file, PART_SIZE);
		Message message = mHandler.obtainMessage();
		message.what = UPLOAD_NEXT_PART;
		Bundle bundle = new Bundle();
		bundle.putSerializable("requestFactory", localUploadPartRequestFactory);
		bundle.putSerializable("uploadFile", item);
		message.setData(bundle);
		mHandler.sendMessage(message);
	}

	private void uploadpart(final UploadPartRequestFactory requestFactory,
			final UploadFile item) {
		if (requestFactory.hasMoreRequests()) {
			final UploadPartRequest request = requestFactory
					.getNextUploadPartRequest();
			client.uploadPart(request, new UploadPartResponceHandler() {
				double progressInFile = 0;

				@Override
				public void onTaskProgress(double progress) {
					long uploadedInpart = (long) (progress / 100 * request.contentLength);
					long uploadedInFile = uploadedInpart
							+ requestFactory.getUploadedSize();
					progressInFile = Double
							.valueOf(request.getFile().length() > 0 ? uploadedInFile
									* 1.0D
									/ request.getFile().length()
									* 100.0D
									: -1.0D);

					List<UploadFile> uploadFiles = dataSource.get(currentDir
							.getPath());
					for (UploadFile file : uploadFiles) {
						if (file.file.getPath().equalsIgnoreCase(
								item.file.getPath())) {
							file.status = UploadFile.STATUS_UPLOADPART;
							file.progress = (int) progressInFile;
							item.status = UploadFile.STATUS_UPLOADPART;
							file.progress = (int) progressInFile;
						}
					}
					mHandler.sendEmptyMessage(UPDATE_SINGLE_UPLOAD_STATUS);
				}

				@Override
				public void onSuccess(int statesCode, Header[] responceHeaders,
						PartETag result) {
					Message message = mHandler.obtainMessage();
					message.what = UPLOAD_NEXT_PART;
					Bundle bundle = new Bundle();
					bundle.putSerializable("requestFactory", requestFactory);
					bundle.putSerializable("uploadFile", item);
					message.setData(bundle);
					mHandler.sendMessage(message);
				}

				@Override
				public void onFailure(int statesCode, Header[] responceHeaders,
						String response, Throwable throwable) {
					List<UploadFile> uploadFiles = dataSource.get(currentDir
							.getPath());
					for (UploadFile file : uploadFiles) {
						if (file.file.getPath().equalsIgnoreCase(
								item.file.getPath())) {
							file.status = UploadFile.STATUS_UPLOADPART_FAIL;
							file.progress = (int) progressInFile;
							item.status = UploadFile.STATUS_UPLOADPART_FAIL;
							file.progress = (int) progressInFile;
						}
					}
					mHandler.sendEmptyMessage(UPDATE_SINGLE_UPLOAD_STATUS);
				}
			});
		} else {
			Message message = mHandler.obtainMessage();
			message.what = UPLOAD_PART_FINISH;
			Bundle bundle = new Bundle();
			bundle.putSerializable("requestFactory", requestFactory);
			bundle.putSerializable("uploadFile", item);
			message.setData(bundle);
			mHandler.sendMessage(message);
		}
	}

	private void listParts(final ListPartsRequest request, final UploadFile item) {
		client.listParts(request, new ListPartsResponseHandler() {
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					ListPartsResult result) {
				Message message = mHandler.obtainMessage();
				message.what = LIST_PART_FINISH;
				message.obj = result;
				Bundle bundle = new Bundle();
				bundle.putSerializable("uploadFile", item);
				message.setData(bundle);
				mHandler.sendMessage(message);
			}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {
				List<UploadFile> uploadFiles = dataSource.get(currentDir
						.getPath());
				for (UploadFile file : uploadFiles) {
					if (file.file.getPath().equalsIgnoreCase(
							item.file.getPath())) {
						file.status = UploadFile.STATUS_LISTING_FAIL;
						item.status = UploadFile.STATUS_LISTING_FAIL;
					}
				}
				mHandler.sendEmptyMessage(UPDATE_SINGLE_UPLOAD_STATUS);
			}
		});
	}

	private void completeUploadPart(
			final CompleteMultipartUploadRequest request, final UploadFile item) {
		client.completeMultipartUpload(request,
				new CompleteMultipartUploadResponseHandler() {
					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							CompleteMultipartUploadResult result) {
						List<UploadFile> uploadFiles = dataSource
								.get(currentDir.getPath());
						for (UploadFile file : uploadFiles) {
							if (file.file.getPath().equalsIgnoreCase(
									item.file.getPath())) {
								file.status = UploadFile.STATUS_COMPLETE;
								item.status = UploadFile.STATUS_COMPLETE;
							}
						}
						mHandler.sendEmptyMessage(0);
					}

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						List<UploadFile> uploadFiles = dataSource
								.get(currentDir.getPath());
						for (UploadFile file : uploadFiles) {
							if (file.file.getPath().equalsIgnoreCase(
									item.file.getPath())) {
								file.status = UploadFile.STATUS_COMPLETE_FAIL;
								file.progress = 100;
								item.status = UploadFile.STATUS_COMPLETE_FAIL;
								item.progress = 100;
							}
						}
						mHandler.sendEmptyMessage(0);
					}
				});
	}

	private static final int UPDATE_SINGLE_UPLOAD_STATUS = 0;
	private static final int UPLOAD_NEXT_PART = 2;
	private static final int UPLOAD_PART_FINISH = 3;
	private static final int LIST_PART_FINISH = 4;

	@SuppressLint("HandlerLeak")
	class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			Bundle bundle;
			UploadFile item;
			UploadPartRequestFactory requestFactory;
			switch (what) {
			case UPDATE_SINGLE_UPLOAD_STATUS:
				adapter.updateCurrent();
				break;
			case UPLOAD_NEXT_PART:
				bundle = msg.getData();
				requestFactory = (UploadPartRequestFactory) bundle
						.get("requestFactory");
				item = (UploadFile) bundle.get("uploadFile");
				uploadpart(requestFactory, item);
				break;
			case UPLOAD_PART_FINISH:
				bundle = msg.getData();
				requestFactory = (UploadPartRequestFactory) bundle
						.get("requestFactory");
				item = (UploadFile) bundle.get("uploadFile");
				ListPartsRequest listRequest = new ListPartsRequest(
						requestFactory.getBucketName(),
						requestFactory.getObjectKey(),
						requestFactory.getUploadId());
				listParts(listRequest, item);
				break;
			case LIST_PART_FINISH:
				ListPartsResult listResult = (ListPartsResult) msg.obj;
				bundle = msg.getData();
				item = (UploadFile) bundle.get("uploadFile");
				CompleteMultipartUploadRequest comRequest = new CompleteMultipartUploadRequest(
						listResult);
				completeUploadPart(comRequest, item);
				break;
			default:
				break;
			}
		}
	}
}
