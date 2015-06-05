package com.ks3.demo.main;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ks3.demo.main.BucketInpuDialog.OnBucketDialogListener;
import com.ks3.demo.main.BucketObjectInpuDialog.OnBucketObjectDialogListener;
import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.Bucket;
import com.ksyun.ks3.model.Ks3ObjectSummary;
import com.ksyun.ks3.model.ObjectListing;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.Owner;
import com.ksyun.ks3.model.acl.AccessControlPolicy;
import com.ksyun.ks3.model.acl.CannedAccessControlList;
import com.ksyun.ks3.model.acl.Grant;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.services.AuthListener;
import com.ksyun.ks3.services.Ks3Client;
import com.ksyun.ks3.services.Ks3ClientConfiguration;
import com.ksyun.ks3.services.handler.ListObjectsResponseHandler;
import com.ksyun.ks3.services.handler.ListPartsResponseHandler;
import com.ksyun.ks3.services.handler.PutObjectResponseHandler;
import com.ksyun.ks3.services.request.ListObjectsRequest;

/**
 * 
 * 包含一系列资源管理操作Api使用示例
 * 
 */
public class MainActivity extends Activity {

	private static final String API = "api";
	private static final String RESULT = "result";
	private Ks3ClientConfiguration configuration;
	private Ks3Client client;
	private TextView resultTv;
	private ListView commandList;
	private String[] command_array;
	private Builder bucketDialogBuilder;
	// Bucket
	public static final int LIST_BUCKETS = 0;
	public static final int CREATE_BUCKET = 1;
	public static final int GET_BUCKET_ACL = 2;
	public static final int PUT_BUCKET_ACL = 3;
	public static final int HEAD_BUCKET = 4;
	public static final int DELETE_BUCKET = 5;
	// Object
	public static final int GET_OBJECT = 6;
	public static final int HEAD_OBJECT = 7;
	public static final int PUT_OBJECT = 8;
	public static final int DELETE_OBJECT = 9;
	public static final int GET_OBJECT_ACL = 10;
	public static final int PUT_OBJECT_ACL = 11;
	public static final int LIST_OBJECTS = 12;
	// Upload
	public static final int UPLOAD = 13;
	// Download
	public static final int DOWNLOAD = 14;
	public static final int LIST_PART = 15;
	private BucketInpuDialog bucketInpuDialog;
	private BucketObjectInpuDialog bucketObjectInpuDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dummy);
		setUpKs3Client();
		setUpUserInterface();
	}

	private void setUpUserInterface() {
		bucketInpuDialog = new BucketInpuDialog(MainActivity.this);
		bucketObjectInpuDialog = new BucketObjectInpuDialog(MainActivity.this);
		commandList = (ListView) findViewById(R.id.command_list);
		command_array = getResources().getStringArray(R.array.command_array);
		commandList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, command_array));
		commandList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case UPLOAD:
					Intent intent_upload = new Intent(MainActivity.this,
							UploadActivity.class);
					startActivity(intent_upload);
					break;
				case DOWNLOAD:
					Intent intent_download = new Intent(MainActivity.this,
							DownloadActivity.class);
					startActivity(intent_download);
					break;
				case LIST_BUCKETS:
					listBuckets();
					break;
				case CREATE_BUCKET:
					createBucket();
					break;
				case GET_BUCKET_ACL:
					getBucketACL();
					break;
				case PUT_BUCKET_ACL:
					putBucketACL();
					break;
				case HEAD_BUCKET:
					headBucket();
					break;
				case DELETE_BUCKET:
					deleteBucket();
					break;
				case GET_OBJECT:
					getObject();
					break;
				case HEAD_OBJECT:
					headObject();
					break;
				case PUT_OBJECT:
					putObject();
					break;
				case DELETE_OBJECT:
					deleteObject();
					break;
				case GET_OBJECT_ACL:
					getObjectACL();
					break;
				case PUT_OBJECT_ACL:
					putObjectACL();
					break;
				case LIST_OBJECTS:
					listObjects();
					break;
				case LIST_PART:
					listParts();
				default:
					break;
				}
			}

		});
	}

	private void putObject() {
		final File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + Constants.TEST_IMG);

		bucketObjectInpuDialog
				.setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
					@Override
					public void confirmBucketAndObject(String name, String key) {
						client.putObject(name, key, file,
								new PutObjectResponseHandler() {

									@Override
									public void onTaskSuccess(int statesCode,
											Header[] responceHeaders) {
										StringBuffer stringBuffer = new StringBuffer();
										stringBuffer.append(
												"upload file success,file = "
														+ Constants.TEST_IMG
														+ ",states code = "
														+ statesCode).append(
												"\n");
										Intent intent = new Intent(
												MainActivity.this,
												RESTAPITestResult.class);

										Bundle data = new Bundle();
										data.putString(RESULT,
												stringBuffer.toString());
										data.putString(API, "put object Result");
										intent.putExtras(data);
										startActivity(intent);
									}

									@Override
									public void onTaskStart() {

									}

									@Override
									public void onTaskFinish() {

									}


									@Override
									public void onTaskProgress(double progress) {

									}

									@Override
									public void onTaskCancel() {
										// TODO Auto-generated method stub

									}

									@Override
									public void onTaskFailure(int statesCode,
											Ks3Error error,
											Header[] responceHeaders,
											String response,
											Throwable paramThrowable) {
										// TODO Auto-generated method stub
										
									}
								});
					}
				});
		bucketObjectInpuDialog.show();

	}

	protected void listParts() {
		bucketObjectInpuDialog
				.setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
					@Override
					public void confirmBucketAndObject(String name, String key) {

						client.listParts(name, key, Constants.UPLOAD_ID,
								new ListPartsResponseHandler() {

									@Override
									public void onSuccess(int statesCode,
											Header[] responceHeaders,
											ListPartsResult listPartsResult) {

									}

									@Override
									public void onFailure(int statesCode,
											Ks3Error error,
											Header[] responceHeaders,
											String response,
											Throwable paramThrowable) {
										// TODO Auto-generated method stub
										
									}
								});
					}
				});
		bucketObjectInpuDialog.show();

	}

	private void setUpKs3Client() {
		// AK&SK形式直接初始化，仅建议测试时使用，正式环境下请替换AuthListener方式
		client = new Ks3Client(Constants.ACCESS_KEY__ID,
				Constants.ACCESS_KEY_SECRET, MainActivity.this);
		configuration = Ks3ClientConfiguration.getDefaultConfiguration();
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
		// }, MainActivity.this);
		// client.setConfiguration(configuration);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected void getObject() {
		Toast.makeText(MainActivity.this, "Please See Download Activity",
				Toast.LENGTH_SHORT).show();
	}

	private void headObject() {
		bucketObjectInpuDialog
				.setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
					@Override
					public void confirmBucketAndObject(String name, String key) {
					/*	client.headObject(name, key,
								new HeadObjectResponseHandler() {
									@Override
									public void onSuccess(int statesCode,
											Header[] responceHeaders,
											HeadObjectResult headObjectResult) {

										StringBuffer stringBuffer = new StringBuffer();
										stringBuffer
												.append("lastModifiedDate      = "
														+ headObjectResult
																.getLastmodified())
												.append("\n");
										stringBuffer.append(
												"ETag                  = "
														+ headObjectResult
																.getETag())
												.append("\n");
										ObjectMetadata metadata = headObjectResult
												.getObjectMetadata();
										stringBuffer.append(metadata);
										Intent intent = new Intent(
												MainActivity.this,
												RESTAPITestResult.class);

										Bundle data = new Bundle();
										data.putString(RESULT,
												stringBuffer.toString());
										data.putString(API,
												"head object Result");
										intent.putExtras(data);
										startActivity(intent);

									}

									@Override
									public void onFailure(int statesCode,
											Ks3Error error,
											Header[] responceHeaders,
											String response,
											Throwable paramThrowable) {
										StringBuffer stringBuffer = new StringBuffer();
										stringBuffer.append(
												"head object , states code :"
														+ statesCode).append(
												"\n");
										stringBuffer.append("Exception :"
												+ paramThrowable.toString());
										Intent intent = new Intent(
												MainActivity.this,
												RESTAPITestResult.class);
										Bundle data = new Bundle();
										data.putString(RESULT,
												stringBuffer.toString());
										data.putString(API,
												"head object Result");
										intent.putExtras(data);
										startActivity(intent);										
									}
								});*/

					}
				});
		bucketObjectInpuDialog.show();
	}

	private void getObjectACL() {
		bucketObjectInpuDialog
				.setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
					@Override
					public void confirmBucketAndObject(String name, String key) {
						/*client.getObjectACL(name, key,
								new GetObjectACLResponseHandler() {

									@Override
									public void onSuccess(
											int statesCode,
											Header[] responceHeaders,
											AccessControlPolicy accessControlPolicy) {
										StringBuffer stringBuffer = new StringBuffer();
										Owner owner = accessControlPolicy
												.getOwner();
										stringBuffer
												.append("=======Owner : ID "
														+ owner.getId()
														+ " ; NAME :"
														+ owner.getDisplayName())
												.append("\n");
										stringBuffer
												.append("==============ACL LIST=========");
										HashSet<Grant> grants = accessControlPolicy
												.getAccessControlList()
												.getGrants();
										for (Grant grant : grants) {
											stringBuffer
													.append(grant.getGrantee()
															.getIdentifier()
															+ "========>"
															+ grant.getPermission()
																	.toString())
													.append("\n");
										}
										Intent intent = new Intent(
												MainActivity.this,
												RESTAPITestResult.class);

										Bundle data = new Bundle();
										data.putString(RESULT,
												stringBuffer.toString());
										data.putString(API,
												"head object Result");
										intent.putExtras(data);
										startActivity(intent);

									}

									@Override
									public void onFailure(int statesCode,
											Ks3Error error,
											Header[] responceHeaders,
											String response,
											Throwable paramThrowable) {
										StringBuffer stringBuffer = new StringBuffer();
										stringBuffer.append(
												"get object ACL FAIL !!!!!!, states code :"
														+ statesCode).append(
												"\n");
										stringBuffer.append("Exception :"
												+ paramThrowable.toString());
										Intent intent = new Intent(
												MainActivity.this,
												RESTAPITestResult.class);
										Bundle data = new Bundle();
										data.putString(RESULT,
												stringBuffer.toString());
										data.putString(API,
												"GET Object ACL Result");
										intent.putExtras(data);
										startActivity(intent);
										
									}
								});*/

					}
				});
		bucketObjectInpuDialog.show();
	}

	private void deleteBucket() {
		bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
			@Override
			public void confirmBucket(String name) {
			/*	client.deleteBucket(name, new DeleteBucketResponceHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders) {
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer
								.append("Delete bucket success , states code :"
										+ statesCode);
						data.putString(RESULT, stringBuffer.toString());
						data.putString(API, "Delete Bucket Result");
						intent.putExtras(data);
						startActivity(intent);
					}

					@Override
					public void onFailure(int statesCode, Ks3Error error,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer
								.append("Delete bucket failed , states code :"
										+ statesCode);
						data.putString(RESULT, stringBuffer.toString());
						data.putString(API, "Delete Bucket Result");
						intent.putExtras(data);
						startActivity(intent);						
					}*/
//				});
			}
		});
//		bucketInpuDialog.show();
	}

	private void putObjectACL() {
		bucketObjectInpuDialog
				.setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
					@Override
					public void confirmBucketAndObject(String name, String key) {
						/*PutObjectACLRequest request = new PutObjectACLRequest(
								name, key);
						CannedAccessControlList cannedList = CannedAccessControlList.PublicRead;
						// AccessControlList acList = new AccessControlList();

						// GranteeId grantee = new GranteeId();
						// grantee.setIdentifier("123456");
						// grantee.setDisplayName("TESTTEST1");
						// acList.addGrant(grantee, Permission.Read);
						// GranteeId grantee1 = new GranteeId();
						// grantee1.setIdentifier("1235789");
						// grantee1.setDisplayName("TESTTEST1");
						// acList.addGrant(grantee1, Permission.FullControl);
						//
						// request.setAccessControlList(acList);
						request.setCannedAcl(cannedList);

						client.putObjectACL(request,
								new PutObjectACLResponseHandler() {

									@Override
									public void onSuccess(int statesCode,
											Header[] responceHeaders) {
										StringBuffer stringBuffer = new StringBuffer();
										stringBuffer
												.append("Put Object ACL success , states code :"
														+ statesCode);
										Intent intent = new Intent(
												MainActivity.this,
												RESTAPITestResult.class);
										Bundle data = new Bundle();
										data.putString(RESULT,
												stringBuffer.toString());
										data.putString(API,
												"Put Object ACL Result");
										intent.putExtras(data);
										startActivity(intent);

									}
									
									@Override
									public void onFailure(int statesCode,
											Ks3Error error,
											Header[] responceHeaders,
											String response,
											Throwable paramThrowable) {
										StringBuffer stringBuffer = new StringBuffer();
										stringBuffer.append(
												"PUT Object ACL FAIL !!!!!!!!!, states code :"
														+ statesCode).append(
												"\n");
										stringBuffer.append("Exception :"
												+ paramThrowable.toString());
										Intent intent = new Intent(
												MainActivity.this,
												RESTAPITestResult.class);
										Bundle data = new Bundle();
										data.putString(RESULT,
												stringBuffer.toString());
										data.putString(API,
												"PUT Object ACL Result");
										intent.putExtras(data);
										startActivity(intent);
										
									}
								});*/
					}
				});
		bucketObjectInpuDialog.show();
	}

	private void headBucket() {
		bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
			@Override
			public void confirmBucket(String name) {
				/*client.headBucket(name, new HeadBucketResponseHandler() {
					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders) {
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer
								.append("head Bucket success , states code :"
										+ statesCode);
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						data.putString(RESULT, stringBuffer.toString());
						data.putString(API, "head Bucket Result");
						intent.putExtras(data);
						startActivity(intent);
					}

					@Override
					public void onFailure(int statesCode, Ks3Error error,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append(
								"head Bucket Fail, states code :" + statesCode)
								.append("\n");
						stringBuffer.append("Exception :"
								+ paramThrowable.toString());
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						data.putString(RESULT, stringBuffer.toString());
						data.putString(API, "head Bucket Result");
						intent.putExtras(data);
						startActivity(intent);						
					}
				});*/
			}
		});
		bucketInpuDialog.show();
	}

	private void putBucketACL() {
		bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
			@Override
			public void confirmBucket(String name) {
			/*	PutBucketACLRequest request = new PutBucketACLRequest(name);
				// AccessControlList acl = new AccessControlList();
				// // GranteeUri urigrantee = GranteeUri.AllUsers;
				// // Permission permission = Permission.Read;
				//
				// GranteeEmail email = new GranteeEmail();
				// email.setEmail("guoli@gmail.com");
				// Permission permission = Permission.Read;
				// Grant g = new Grant(email, permission);
				//
				// GranteeUri uirGroup = GranteeUri.AllUsers;
				// Permission uripermission = Permission.Read;
				// Grant g1 = new Grant(uirGroup, uripermission);

				// acl.addGrant(g);
				// acl.addGrant(g1);

				// GranteeId grantee = new GranteeId() ;
				// grantee.setIdentifier("12773456");
				// grantee.setDisplayName("guoliTest222");
				// acl.addGrant(grantee, Permission.Read);

				// GranteeId grantee1 = new GranteeId() ;
				// grantee1.setIdentifier("123005789");
				// grantee1.setDisplayName("guoliTest2D2");
				// acl.addGrant(grantee1, Permission.Write);

				// request.setAccessControlList(acl) ;

				CannedAccessControlList cannedAcl = CannedAccessControlList.PublicReadWrite;
				request.setCannedAcl(cannedAcl);
				// request.setAccessControlList(acl);
				client.putBucketACL(request, new PutBucketACLResponseHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders) {
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer
								.append("Put Bucket ACL success, states code :"
										+ statesCode);
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						data.putString(RESULT, stringBuffer.toString());
						data.putString(API, "Put Bucket ACL Result");
						intent.putExtras(data);
						startActivity(intent);
					}

					@Override
					public void onFailure(int statesCode, Ks3Error error,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append(
								"PUT Bucket ACL FAIL, states code :"
										+ statesCode).append("\n");
						stringBuffer.append("Exception :"
								+ paramThrowable.toString());
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						data.putString(RESULT, stringBuffer.toString());
						data.putString(API, "PUT Bucket ACL Result");
						intent.putExtras(data);
						startActivity(intent);						
					}
				});*/
			}
		});
		bucketInpuDialog.show();

	}

	private void getBucketACL() {
		bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
			@Override
			public void confirmBucket(String name) {
				/*client.getBucketACL(name, new GetBucketACLResponceHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							AccessControlPolicy accessControlPolicy) {
						StringBuffer stringBuffer = new StringBuffer();
						Owner owner = accessControlPolicy.getOwner();
						stringBuffer.append(
								"=======Owner : ID " + owner.getId()
										+ " ; NAME :" + owner.getDisplayName())
								.append("\n");
						stringBuffer.append("==============ACL LIST=========");
						HashSet<Grant> grants = accessControlPolicy
								.getAccessControlList().getGrants();
						for (Grant grant : grants) {
							stringBuffer.append(
									grant.getGrantee().getIdentifier()
											+ "========>"
											+ grant.getPermission().toString())
									.append("\n");
						}
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						data.putString(RESULT, stringBuffer.toString());
						data.putString(API, "GET BUCKET ACL Result");
						intent.putExtras(data);
						startActivity(intent);
					}

					@Override
					public void onFailure(int statesCode, Ks3Error error,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append(
								"GET BUCKET ACL fail , states code :"
										+ statesCode).append("\n");
						stringBuffer.append("Exception :"
								+ paramThrowable.toString());
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						data.putString(RESULT, stringBuffer.toString());
						data.putString(API, "GET BUCKET ACL Result");
						intent.putExtras(data);
						startActivity(intent);						
					}
				});*/
			}
		});
		bucketInpuDialog.show();

	}

	private void deleteObject() {
		bucketObjectInpuDialog
				.setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
					@Override
					public void confirmBucketAndObject(String name, String key) {
					/*	DeleteObjectRequest request = new DeleteObjectRequest(
								name, key);
						client.deleteObject(request,
								new DeleteObjectRequestHandler() {

									@Override
									public void onSuccess(int statesCode,
											Header[] responceHeaders) {
										StringBuffer stringBuffer = new StringBuffer();
										stringBuffer
												.append("Delete success , states code :"
														+ statesCode);
										Intent intent = new Intent(
												MainActivity.this,
												RESTAPITestResult.class);
										Bundle data = new Bundle();
										data.putString(RESULT,
												stringBuffer.toString());
										data.putString(API,
												"Delete Object Result");
										intent.putExtras(data);
										startActivity(intent);
									}

									@Override
									public void onFailure(int statesCode,
											Ks3Error error,
											Header[] responceHeaders,
											String response,
											Throwable paramThrowable) {
										StringBuffer stringBuffer = new StringBuffer();
										stringBuffer.append(
												"Delete fail , states code :"
														+ statesCode).append(
												"\n");
										stringBuffer.append("Exception :"
												+ paramThrowable.toString());
										Intent intent = new Intent(
												MainActivity.this,
												RESTAPITestResult.class);
										Bundle data = new Bundle();
										data.putString(RESULT,
												stringBuffer.toString());
										data.putString(API,
												"Delete Object Result");
										intent.putExtras(data);
										startActivity(intent);										
									}
								});*/
					}
				});
		bucketObjectInpuDialog.show();

	}

	private void listObjects() {
		bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
			@Override
			public void confirmBucket(String name) {
				ListObjectsRequest request = new ListObjectsRequest(name);
				// request.setPrefix("android_test/");
				// request.setDelimiter("/");
				client.listObjects(request, new ListObjectsResponseHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							ObjectListing objectListing) {
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append(
								"name   =    " + objectListing.getBucketName())
								.append("\n");
						stringBuffer.append(
								"Prefix =    " + objectListing.getPrefix())
								.append("\n");
						stringBuffer.append(
								"Marker =    " + objectListing.getMarker())
								.append("\n");
						stringBuffer.append(
								"Delimiter = " + objectListing.getDelimiter())
								.append("\n");
						stringBuffer.append(
								"IsTruncated = " + objectListing.isTruncated())
								.append("\n");
						List<Ks3ObjectSummary> objectSummaries = objectListing
								.getObjectSummaries();
						Ks3ObjectSummary objectSummary = null;
						Owner owner = null;
						for (int i = 0; i < objectSummaries.size(); i++) {
							objectSummary = objectSummaries.get(i);
							owner = objectSummary.getOwner();
							stringBuffer.append(
									"================Object :" + i
											+ " ===================").append(
									"\n");
							stringBuffer.append(
									"     key             ="
											+ objectSummary.getKey()).append(
									"\n");
							stringBuffer.append(
									"     LastModified    ="
											+ objectSummary.getLastModified())
									.append("\n");
							stringBuffer.append(
									"     ETag   =" + objectSummary.getETag())
									.append("\n");
							stringBuffer.append(
									"     Size    =" + objectSummary.getSize())
									.append("\n");
							stringBuffer.append(
									"     owner.ID    =" + owner.getId())
									.append("\n");
							stringBuffer.append(
									"     Size.displayName    ="
											+ owner.getDisplayName()).append(
									"\n");
							stringBuffer.append(
									"     StorageClass    = "
											+ objectSummary.getStorageClass())
									.append("\n");
						}

						List<String> commonPrefixes = objectListing
								.getCommonPrefixes();
						for (int i = 0; i < commonPrefixes.size(); i++) {
							stringBuffer.append(
									"     commonPrefixes =>" + i + "="
											+ objectSummary.getStorageClass())
									.append("\n");
						}
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						data.putString(RESULT, stringBuffer.toString());
						data.putString(API, "RESULT for ListObjects");
						intent.putExtras(data);
						startActivity(intent);
					}

					@Override
					public void onFailure(int statesCode, Ks3Error error,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						
					}
				});
			}
		});
		bucketInpuDialog.show();

	}

	private void listBuckets() {
		/*client.listBuckets(new ListBucketsResponceHandler() {
			@Override
			public void onSuccess(int paramInt, Header[] paramArrayOfHeader,
					ArrayList<Bucket> resultList) {
				StringBuffer stringBuffer = new StringBuffer();
				for (Bucket bucket : resultList) {
					stringBuffer.append(bucket.getName()).append("\n");
					stringBuffer.append(bucket.getCreationDate()).append("\n");
					stringBuffer.append(bucket.getOwner().getDisplayName())
							.append("\n");
					stringBuffer.append(bucket.getOwner().getId()).append("\n");
				}
				Intent intent = new Intent(MainActivity.this,
						RESTAPITestResult.class);
				Bundle data = new Bundle();
				data.putString(RESULT, stringBuffer.toString());
				data.putString(API, "List Bucket Result");
				intent.putExtras(data);
				startActivity(intent);
			}

			@Override
			public void onFailure(int statesCode, Ks3Error error,
					Header[] responceHeaders, String response,
					Throwable paramThrowable) {
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(
						"list bucket fail , states code :" + statesCode)
						.append("\n");
				stringBuffer.append("Exception :" + paramThrowable.toString());
				Intent intent = new Intent(MainActivity.this,
						RESTAPITestResult.class);
				Bundle data = new Bundle();
				data.putString(RESULT, stringBuffer.toString());
				data.putString(API, "List Buckets");
				intent.putExtras(data);
				startActivity(intent);				
			}
		});*/
	}

	private void createBucket() {
		bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
			@Override
			public void confirmBucket(String name) {
				/*client.createBucket(name, new CreateBucketResponceHandler() {
					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders) {
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						data.putString(RESULT, "success");
						data.putString(API, "Create Bucket Result");
						intent.putExtras(data);
						startActivity(intent);
					}

					@Override
					public void onFailure(int statesCode, Ks3Error error,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append(
								"Delete fail , states code :" + statesCode)
								.append("\n");
						stringBuffer.append("Exception :"
								+ paramThrowable.toString());
						Intent intent = new Intent(MainActivity.this,
								RESTAPITestResult.class);
						Bundle data = new Bundle();
						data.putString(RESULT, stringBuffer.toString());
						data.putString(API, "List Buckets");
						intent.putExtras(data);
						startActivity(intent);						
					}
				});*/
			}
		});
		bucketInpuDialog.show();
	}

}
