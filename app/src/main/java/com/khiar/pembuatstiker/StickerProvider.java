package com.khiar.pembuatstiker;

import android.content.*;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.graphics.*;
import java.io.*;
import java.util.*;

public class StickerProvider extends ContentProvider {
 public static final String AUTH="com.khiar.pembuatstiker.stickercontentprovider";
 private static final String PACK="khiar_pack";
 private File dir;
 @Override public boolean onCreate(){dir=new File(getContext().getFilesDir(),"stickers");dir.mkdirs();return true;}
 @Override public String getType(Uri u){return u.getPath()!=null&&u.getPath().endsWith(".png")?"image/png":"image/webp";}
 @Override public Cursor query(Uri u,String[] p,String s,String[] a,String sort){
  String path=u.getPath()==null?"":u.getPath();
  if(path.equals("/metadata")||path.equals("/metadata/"+PACK)){
   ensureTray();
   MatrixCursor c=new MatrixCursor(new String[]{"sticker_pack_identifier","sticker_pack_name","sticker_pack_publisher","sticker_pack_icon","android_play_store_link","ios_app_store_link","publisher_email","publisher_website","privacy_policy_website","license_agreement_website","image_data_version","avoid_cache","animated_sticker_pack"});
   c.addRow(new Object[]{PACK,"Stiker Saya","Khiar","tray.png","","","","","","","2",0,0});
   return c;
  }
  if(path.equals("/stickers/"+PACK)){
   MatrixCursor c=new MatrixCursor(new String[]{"sticker_file_name","sticker_emoji","sticker_accessibility_text"});
   for(File f:files())c.addRow(new Object[]{f.getName(),"😀","Stiker"});
   return c;
  }
  return null;
 }
 @Override public ParcelFileDescriptor openFile(Uri u,String mode)throws FileNotFoundException{
  String path=u.getPath()==null?"":u.getPath();
  String prefix="/stickers_asset/"+PACK+"/";
  if(path.startsWith(prefix)){String name=path.substring(prefix.length());File f=new File(dir,name);try{if(!f.getCanonicalPath().startsWith(dir.getCanonicalPath()+File.separator))throw new FileNotFoundException();}catch(IOException e){throw new FileNotFoundException();}return ParcelFileDescriptor.open(f,ParcelFileDescriptor.MODE_READ_ONLY);}
  if(path.equals("/tray/"+PACK+"/tray.png")||path.endsWith("/tray.png")){ensureTray();return ParcelFileDescriptor.open(new File(dir,"tray.png"),ParcelFileDescriptor.MODE_READ_ONLY);}
  throw new FileNotFoundException(u.toString());
 }
 private File[] files(){File[] f=dir.listFiles((d,n)->n.startsWith("sticker_")&&n.endsWith(".webp"));if(f==null)return new File[0];Arrays.sort(f,Comparator.comparing(File::getName));return f;}
 private void ensureTray(){try{File tray=new File(dir,"tray.png");if(tray.exists())return;File[] fs=files();Bitmap b=fs.length>0?BitmapFactory.decodeFile(fs[0].getAbsolutePath()):Bitmap.createBitmap(96,96,Bitmap.Config.ARGB_8888);Bitmap t=Bitmap.createScaledBitmap(b,96,96,true);FileOutputStream o=new FileOutputStream(tray);t.compress(Bitmap.CompressFormat.PNG,100,o);o.close();}catch(Exception ignored){}}
 @Override public int delete(Uri u,String s,String[] a){return 0;}
 @Override public int update(Uri u,ContentValues v,String s,String[] a){return 0;}
 @Override public Uri insert(Uri u,ContentValues v){return null;}
}
