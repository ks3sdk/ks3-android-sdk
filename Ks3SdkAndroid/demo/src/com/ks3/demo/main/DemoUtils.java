package com.ks3.demo.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;



public class DemoUtils {
	public static Drawable matchImage(Context context,boolean isDir , String fileName){
		Resources resources = context.getResources();
		if(isDir)
			return resources.getDrawable(R.drawable.ks_format_forder);
		if(fileName.endsWith(".apk")) return resources.getDrawable(R.drawable.ks_format_apk);
		else if(fileName.endsWith(".ogg")) return resources.getDrawable(R.drawable.ks_format_ogg);
		else if(fileName.endsWith(".chm")) return resources.getDrawable(R.drawable.ks_format_chm);
		else if(fileName.endsWith(".asf")) return resources.getDrawable(R.drawable.ks_format_asf);
		else if(fileName.endsWith(".avi")) return resources.getDrawable(R.drawable.ks_format_avi);
		else if(fileName.endsWith(".bat")) return resources.getDrawable(R.drawable.ks_format_bat);
		else if(fileName.endsWith(".bin")) return resources.getDrawable(R.drawable.ks_format_bin);
		else if(fileName.endsWith(".bmp")) return resources.getDrawable(R.drawable.ks_format_bmp);
		else if(fileName.endsWith(".dat")) return resources.getDrawable(R.drawable.ks_format_dat);
		else if(fileName.endsWith(".db")) return resources.getDrawable(R.drawable.ks_format_dat);
		else if(fileName.endsWith(".dll")) return resources.getDrawable(R.drawable.ks_format_dll);
		else if(fileName.endsWith(".doc")) return resources.getDrawable(R.drawable.ks_format_doc);
		else if(fileName.endsWith(".docx")) return resources.getDrawable(R.drawable.ks_format_docx);
		else if(fileName.endsWith(".flac")) return resources.getDrawable(R.drawable.ks_format_flac);
		else if(fileName.endsWith(".flv")) return resources.getDrawable(R.drawable.ks_format_flv);
		else if(fileName.endsWith(".gif")) return resources.getDrawable(R.drawable.ks_format_gif);
		else if(fileName.endsWith(".html")) return resources.getDrawable(R.drawable.ks_format_html);
		else if(fileName.endsWith(".info")) return resources.getDrawable(R.drawable.ks_format_inf);
		else if(fileName.endsWith(".inf")) return resources.getDrawable(R.drawable.ks_format_inf);
		else if(fileName.endsWith(".ini")) return resources.getDrawable(R.drawable.ks_format_ini);
		else if(fileName.endsWith(".java")) return resources.getDrawable(R.drawable.ks_format_java);
		else if(fileName.endsWith(".jif")) return resources.getDrawable(R.drawable.ks_format_jif);
		else if(fileName.endsWith(".jpg")) return resources.getDrawable(R.drawable.ks_format_jpg);
		else if(fileName.endsWith(".log")) return resources.getDrawable(R.drawable.ks_format_log);
		else if(fileName.endsWith(".m4a")) return resources.getDrawable(R.drawable.ks_format_m4a);
		else if(fileName.endsWith(".mid")) return resources.getDrawable(R.drawable.ks_format_mid);
		else if(fileName.endsWith(".mkv")) return resources.getDrawable(R.drawable.ks_format_mkv);
		else if(fileName.endsWith(".mp3")) return resources.getDrawable(R.drawable.ks_format_mp3);
		else if(fileName.endsWith(".mp4")) return resources.getDrawable(R.drawable.ks_format_mp4);
		else if(fileName.endsWith(".pdf")) return resources.getDrawable(R.drawable.ks_format_pdf);
		else if(fileName.endsWith(".png")) return resources.getDrawable(R.drawable.ks_format_png);
		else if(fileName.endsWith(".ppt")) return resources.getDrawable(R.drawable.ks_format_ppt);
		else if(fileName.endsWith(".pptx")) return resources.getDrawable(R.drawable.ks_format_pptx);
		else if(fileName.endsWith(".ram")) return resources.getDrawable(R.drawable.ks_format_ram);
		else if(fileName.endsWith(".rar")) return resources.getDrawable(R.drawable.ks_format_rar);
		else if(fileName.endsWith(".jar")) return resources.getDrawable(R.drawable.ks_format_rar);
		else if(fileName.endsWith(".reg")) return resources.getDrawable(R.drawable.ks_format_reg);
		else if(fileName.endsWith(".rm")) return resources.getDrawable(R.drawable.ks_format_rm);
		else if(fileName.endsWith(".rmvb")) return resources.getDrawable(R.drawable.ks_format_rmvb);
		else if(fileName.endsWith(".swf")) return resources.getDrawable(R.drawable.ks_format_swf);
		else if(fileName.endsWith(".txt")) return resources.getDrawable(R.drawable.ks_format_txt);
		else if(fileName.endsWith(".wav")) return resources.getDrawable(R.drawable.ks_format_wav);
		else if(fileName.endsWith(".wma")) return resources.getDrawable(R.drawable.ks_format_wma);
		else if(fileName.endsWith(".wmv")) return resources.getDrawable(R.drawable.ks_format_wmv);
		else if(fileName.endsWith(".xls")) return resources.getDrawable(R.drawable.ks_format_xls);
		else if(fileName.endsWith(".xlsx")) return resources.getDrawable(R.drawable.ks_format_xlsx);
		else if(fileName.endsWith(".zip")) return resources.getDrawable(R.drawable.ks_format_zip);
		else if(fileName.endsWith(".jpeg")) return resources.getDrawable(R.drawable.ks_format_jpg);
		else if(fileName.endsWith(".xml")) return resources.getDrawable(R.drawable.ks_format_xml);
		else return resources.getDrawable(R.drawable.ks_format_unkown);
	}
	
	public static String formatDate(long time){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd",Locale.US);
		return format.format(new Date(time));
	}
	public static String formatDate(Date date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd",Locale.US);
		return format.format(date);
	}
	
}
