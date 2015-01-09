package com.ksyun.ks3.services.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ksyun.ks3.model.Owner;
import com.ksyun.ks3.model.acl.AccessControlList;
import com.ksyun.ks3.model.acl.AccessControlPolicy;
import com.ksyun.ks3.model.acl.Grant;
import com.ksyun.ks3.model.acl.Grantee;
import com.ksyun.ks3.model.acl.GranteeEmail;
import com.ksyun.ks3.model.acl.GranteeId;
import com.ksyun.ks3.model.acl.GranteeUri;
import com.ksyun.ks3.model.acl.Permission;

public abstract class GetObjectACLResponseHandler extends Ks3HttpResponceHandler{

	public abstract void onFailure(int statesCode, Header[] responceHeaders,String response, Throwable paramThrowable);

	public abstract void onSuccess(int statesCode, Header[] responceHeaders,AccessControlPolicy accessControlPolicy);
	
	
	
	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		this.onSuccess(statesCode, responceHeaders, parseXml(responceHeaders, response));
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		this.onFailure(statesCode, responceHeaders, response==null?"":new String(response), throwable);
	}
	
	@Override
	public final void onProgress(int bytesWritten, int totalSize) {}

	@Override
	public final void onStart() {}

	@Override
	public final void onFinish() {}
	
	@Override
	public final void onCancel() {}
	
	
	private AccessControlPolicy parseXml(Header[] responceHeaders, byte[] response) {
		XmlPullParserFactory factory;
		Grantee grantee=null;
		Grant grant = null;
		AccessControlList accessControlList = null;
		Owner owner = null;
		Permission permission = null;    
		AccessControlPolicy accessControlPolicy  = null;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parse = factory.newPullParser();
			parse.setInput(new ByteArrayInputStream(response), "UTF-8");
			int eventType = parse.getEventType();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String nodeName = parse.getName();
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if("AccessControlPolicy".equalsIgnoreCase(nodeName)){
						accessControlPolicy = new AccessControlPolicy();
					}
					if ("Owner".equalsIgnoreCase(nodeName)) {
						owner = new Owner();
						parse.next();
						nodeName = parse.getName();
						System.out.println("Node name :" + nodeName);
						if ("ID".equalsIgnoreCase(nodeName)) {
							owner.setId(parse.nextText());
						}
					}
					
					if ("DisplayName".equalsIgnoreCase(nodeName)) {
						owner.setDisplayName(parse.nextText());
					}
					
					if (nodeName.equalsIgnoreCase("AccessControlList")) {
						accessControlList = new AccessControlList();
					}
					
					if ("Grant".equalsIgnoreCase(nodeName)) {
						grant = new Grant();
					}
					if ("Grantee".equalsIgnoreCase(nodeName)) {
						parse.next();
						nodeName = parse.getName();
						if ("EmailAddress".equalsIgnoreCase(nodeName)) {
							grantee = new GranteeEmail();
							grantee.setIdentifier(parse.nextText());
						}else if("URI".equalsIgnoreCase(nodeName)){
							grantee = GranteeUri.parse(parse.nextText());
						}else if("ID".equalsIgnoreCase(nodeName)){
							grantee = new GranteeId();
							grantee.setIdentifier(parse.nextText());
						}
					}
					if("Permission".equalsIgnoreCase(nodeName)){
						permission = Permission.getInstance(parse.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if (nodeName.equalsIgnoreCase("AccessControlList")) {
					}
					if ("Grant".equalsIgnoreCase(nodeName)) {
						grant.setGrantee(grantee);
						grant.setPermission(permission);
						accessControlList.addGrant(grant);
					}
					if("AccessControlPolicy".equalsIgnoreCase(nodeName)){
						accessControlPolicy.setAccessControlList(accessControlList);
						accessControlPolicy.setOwner(owner);
					}
					break;
				case XmlPullParser.TEXT:

					break;
				default:
					break;
				}
				eventType = parse.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessControlPolicy;
	}
}
