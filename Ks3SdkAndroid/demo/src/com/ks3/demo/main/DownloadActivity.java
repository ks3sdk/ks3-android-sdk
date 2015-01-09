package com.ks3.demo.main;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import android.widget.Toast;

import com.ks3.demo.main.BucketInpuDialog.OnBucketDialogListener;
import com.ksyun.ks3.model.Ks3ObjectSummary;
import com.ksyun.ks3.model.ObjectListing;
import com.ksyun.ks3.model.result.GetObjectResult;
import com.ksyun.ks3.services.AuthListener;
import com.ksyun.ks3.services.Ks3Client;
import com.ksyun.ks3.services.Ks3ClientConfiguration;
import com.ksyun.ks3.services.handler.GetObjectResponseHandler;
import com.ksyun.ks3.services.handler.ListObjectsResponseHandler;
import com.ksyun.ks3.services.request.GetObjectRequest;
import com.ksyun.ks3.services.request.ListObjectsRequest;
import com.ksyun.ks3.util.StringUtils;

/**
 * 
 * Download相关API使用示例,如getObject，listObject等
 * 
 */
public class DownloadActivity extends Activity implements OnItemClickListener {

	private ListView mListView;
	private ProgressBar mProgressBar;
	private Ks3ClientConfiguration configuration;
	private Ks3Client client;
	private Map<String, RemoteFile> dataSource;
	private RemoteFileAdapter adapter;
	private TextView currentBucketTextView;
	private File storeForder;
	private myHandler mHandler = new myHandler();
	private BucketInpuDialog bucketInpuDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		setUp();
		bucketInpuDialog = new BucketInpuDialog(DownloadActivity.this);
		prepareStoreForder();
		bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
			@Override
			public void confirmBucket(String name) {
				// 输入框确获取Bucket之后，开始ListObjects操作
				listObjects(name, null);
			}
		});
		bucketInpuDialog.show();
	}

	private void prepareStoreForder() {
		storeForder = new File(Environment.getExternalStorageDirectory(),
				"ksyun_download");
		if (!storeForder.exists()) {
			storeForder.mkdirs();
		} else if (storeForder.isFile()) {
			storeForder.delete();
		}
	}

	private void listObjects(String bucketName, final String prefix) {
		final ListObjectsRequest request = new ListObjectsRequest(bucketName);
		if (!StringUtils.isBlank(prefix))
			request.setPrefix(prefix);
		request.setDelimiter("/");
		client.listObjects(request, new ListObjectsResponseHandler() {
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					ObjectListing objectListing) {
				mProgressBar.setVisibility(View.GONE);
				currentBucketTextView.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.VISIBLE);
				currentBucketTextView.setText("当前路径:Bucket:"
						+ request.getBucketname() + "-" + prefix);

				List<Ks3ObjectSummary> objectSummaries = objectListing
						.getObjectSummaries();
				if (objectSummaries != null && objectSummaries.size() != 0) {
					for (Ks3ObjectSummary object : objectSummaries) {
						String objectKey = object.getKey();
						if (dataSource.get(objectKey) == null) {
							RemoteFile remoteFile = new RemoteFile();
							remoteFile.bucketName = objectListing
									.getBucketName();
							remoteFile.objectKey = objectKey;
							remoteFile.isCommomPrefix = false;
							remoteFile.icon = DemoUtils.matchImage(
									DownloadActivity.this, false, objectKey);
							remoteFile.size = object.getSize();
							remoteFile.lastModified = object.getLastModified();
							remoteFile.progress = 0;
							remoteFile.status = RemoteFile.STATUS_NOT_START;
							dataSource.put(objectKey, remoteFile);
						}
					}
				}
				List<String> commonPrefixes = objectListing.getCommonPrefixes();
				if (commonPrefixes != null && commonPrefixes.size() != 0) {
					for (String prifix : commonPrefixes) {
						RemoteFile remoteFile = new RemoteFile();
						remoteFile.bucketName = objectListing.getBucketName();
						remoteFile.objectKey = prifix;
						remoteFile.icon = DemoUtils.matchImage(
								DownloadActivity.this, true, prifix);
						remoteFile.progress = 0;
						remoteFile.isCommomPrefix = true;
						remoteFile.status = RemoteFile.STATUS_NOT_START;
						dataSource.put(prifix, remoteFile);
					}
				}
				adapter.fillDatas();
			}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {

			}
		});

	}

	private void setUp() {
		// Ks3Client初始化
		configuration = Ks3ClientConfiguration.getDefaultConfiguration();
		client = new Ks3Client(Constants.ACCESS_KEY__ID,
				Constants.ACCESS_KEY_SECRET, DownloadActivity.this);
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
		// }, DownloadActivity.this);

		// UI初始化
		currentBucketTextView = (TextView) findViewById(R.id.current_bucket_tv);
		mListView = (ListView) findViewById(R.id.object_list);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		dataSource = new HashMap<String, RemoteFile>();
		adapter = new RemoteFileAdapter(this, null);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
	}

	class RemoteFileAdapter extends BaseAdapter {
		private List<RemoteFile> mRemoteFiles;
		private LayoutInflater mInflater;

		RemoteFileAdapter(Context context, List<RemoteFile> remoteFiles) {
			this.mRemoteFiles = new ArrayList<RemoteFile>();
			if (remoteFiles != null)
				this.mRemoteFiles.addAll(remoteFiles);
			this.mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mRemoteFiles.size();
		}

		@Override
		public RemoteFile getItem(int position) {
			return this.mRemoteFiles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void fillDatas() {
			this.mRemoteFiles.clear();
			for (Entry<String, RemoteFile> entry : dataSource.entrySet()) {
				this.mRemoteFiles.add(entry.getValue());
			}
			this.notifyDataSetChanged();
		}

		public void updateCurrent() {
			this.notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.download_list_item,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.remoteObjectIcon = (ImageView) convertView
						.findViewById(R.id.remote_object_icon);
				viewHolder.remoteObjectKeyTextView = (TextView) convertView
						.findViewById(R.id.remote_object_key);
				viewHolder.remoteObjectSummaryLayout = (LinearLayout) convertView
						.findViewById(R.id.remote_object_summary_layout);
				viewHolder.remoteObjectSizeTextView = (TextView) convertView
						.findViewById(R.id.remote_object_size);
				viewHolder.remoteObjectModiyTextView = (TextView) convertView
						.findViewById(R.id.remote_object_last_modiy);
				viewHolder.downloadSummaryLayout = (LinearLayout) convertView
						.findViewById(R.id.progress_summary_layout);
				viewHolder.downloadProgressBar = (ProgressBar) convertView
						.findViewById(R.id.download_progress_bar);
				viewHolder.progressTextView = (TextView) convertView
						.findViewById(R.id.download_progress_txt);
				viewHolder.downloadBtn = (ImageView) convertView
						.findViewById(R.id.download_btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.remoteObjectIcon.setImageDrawable(mRemoteFiles
					.get(position).icon);
			viewHolder.remoteObjectKeyTextView.setText(mRemoteFiles
					.get(position).objectKey);

			if (!mRemoteFiles.get(position).isCommomPrefix) {
				viewHolder.remoteObjectKeyTextView.setVisibility(View.VISIBLE);
				long size = mRemoteFiles.get(position).size;
				String sizeStr = "unkown-size";

				if (size >= 1024 * 1024) {
					sizeStr = ((int) (size / 1024 / 1024) * 100) / 100 + "MB";
				} else {
					sizeStr = (size / 1024) + "kb";
				}
				viewHolder.remoteObjectSizeTextView.setText(sizeStr);
				String modiyStr = DemoUtils.formatDate(mRemoteFiles
						.get(position).lastModified);
				viewHolder.remoteObjectModiyTextView.setText(modiyStr);
				if (mRemoteFiles.get(position).status > RemoteFile.STATUS_NOT_START) {
					viewHolder.downloadSummaryLayout
							.setVisibility(View.VISIBLE);
					viewHolder.downloadProgressBar.setProgress(mRemoteFiles
							.get(position).progress);
					viewHolder.remoteObjectSummaryLayout
							.setVisibility(View.GONE);
					viewHolder.downloadBtn.setVisibility(View.GONE);
					switch (mRemoteFiles.get(position).status) {
					case RemoteFile.STATUS_STARTED:
						viewHolder.progressTextView.setText("准备");
						break;
					case RemoteFile.STATUS_DOWNLOADING:
						viewHolder.progressTextView.setText(mRemoteFiles
								.get(position).progress + "%");
						break;
					case RemoteFile.STATUS_FINISH:
						viewHolder.progressTextView.setText("完成下载");
						break;
					case RemoteFile.STATUS_FAIL:
						viewHolder.progressTextView.setText("下载失败");
						break;
					}
				} else {
					viewHolder.remoteObjectSummaryLayout
							.setVisibility(View.VISIBLE);
					viewHolder.downloadBtn.setVisibility(View.VISIBLE);
					viewHolder.downloadSummaryLayout.setVisibility(View.GONE);
				}
			} else {
				viewHolder.remoteObjectSummaryLayout.setVisibility(View.GONE);
				viewHolder.downloadSummaryLayout.setVisibility(View.GONE);
				viewHolder.downloadBtn.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	class ViewHolder {
		ImageView remoteObjectIcon;
		TextView remoteObjectKeyTextView;
		LinearLayout remoteObjectSummaryLayout;
		TextView remoteObjectSizeTextView;
		TextView remoteObjectModiyTextView;
		LinearLayout downloadSummaryLayout;
		ProgressBar downloadProgressBar;
		TextView progressTextView;
		ImageView downloadBtn;
	}

	class RemoteFile {
		static final int STATUS_NOT_START = 0;
		static final int STATUS_STARTED = 1;
		static final int STATUS_DOWNLOADING = 2;
		static final int STATUS_FINISH = 3;
		static final int STATUS_FAIL = 4;
		Drawable icon;
		String bucketName;
		String objectKey;
		long size;
		Date lastModified;
		int progress;
		int status;
		boolean isCommomPrefix;

		@Override
		public String toString() {
			return bucketName + ",download?" + status + ",progress:" + progress;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adpterView, final View view,
			final int position, long arg3) {
		final RemoteFile item = adapter.getItem(position);
		if (item.isCommomPrefix) {
			// listObjects(item.bucketName, item.objectKey);
			Toast.makeText(this, "请自行实现下一级目录的检索", Toast.LENGTH_LONG).show();
		} else {
			if (item.status == RemoteFile.STATUS_STARTED
					|| item.status == RemoteFile.STATUS_DOWNLOADING
					|| item.status == RemoteFile.STATUS_FINISH)
				return;
			// 下载操作示例
			final GetObjectRequest request = new GetObjectRequest(
					item.bucketName, item.objectKey);
			String objectName = item.objectKey.substring(item.objectKey
					.lastIndexOf("/") == -1 ? 0 : item.objectKey
					.lastIndexOf("/"));
//			request.setCallBack(callBackUrl, callBackBody, callBackHeaders);
			File file = new File(storeForder, objectName);
			client.getObject(request, new GetObjectResponseHandler(file,
					item.bucketName, item.objectKey) {

				@Override
				public void onTaskSuccess(int paramInt,
						Header[] paramArrayOfHeader,
						GetObjectResult getObjectResult) {

				}

				@Override
				public void onTaskStart() {
					RemoteFile remoteFile = dataSource.get(item.objectKey);
					remoteFile.status = RemoteFile.STATUS_STARTED;
					remoteFile.progress = 0;
					item.status = RemoteFile.STATUS_STARTED;
					item.progress = 0;
					mHandler.sendEmptyMessage(0);

				}

				@Override
				public void onTaskProgress(double progress) {
//					if (progress > 50.0) {
//						request.abort();
//					}
					RemoteFile remoteFile = dataSource.get(item.objectKey);
					remoteFile.status = RemoteFile.STATUS_DOWNLOADING;
					remoteFile.progress = (int) progress;
					item.status = RemoteFile.STATUS_DOWNLOADING;
					item.progress = (int) progress;
					mHandler.sendEmptyMessage(0);
				}

				@Override
				public void onTaskFinish() {
					RemoteFile remoteFile = dataSource.get(item.objectKey);
					remoteFile.status = RemoteFile.STATUS_FINISH;
					remoteFile.progress = 100;
					item.status = RemoteFile.STATUS_FINISH;
					item.progress = 100;
					mHandler.sendEmptyMessage(0);

				}

				@Override
				public void onTaskFailure(int paramInt,
						Header[] paramArrayOfHeader, Throwable paramThrowable,
						File paramFile) {
					Log.d(com.ksyun.ks3.util.Constants.LOG_TAG,
							"failure: reason = " + paramThrowable.toString());
					RemoteFile remoteFile = dataSource.get(item.objectKey);
					remoteFile.status = RemoteFile.STATUS_FAIL;
					item.status = RemoteFile.STATUS_FAIL;
					mHandler.sendEmptyMessage(0);
				}

				@Override
				public void onTaskCancel() {
					Log.d(com.ksyun.ks3.util.Constants.LOG_TAG, "cancle ok");
				}
			});
		}

	}

	@SuppressLint("HandlerLeak")
	class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case 0:
				adapter.updateCurrent();
				break;
			default:
				break;
			}
		}
	}

}
