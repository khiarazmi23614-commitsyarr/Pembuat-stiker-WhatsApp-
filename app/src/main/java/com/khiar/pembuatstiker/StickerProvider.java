package com.khiar.pembuatstiker;

import android.content.*;import android.database.Cursor;import android.database.MatrixCursor;import android.net.Uri;import android.os.ParcelFileDescriptor;import java.io.*;import java.util.*;

public class StickerProvider extends ContentProvider {
 public static final String AUTH="com.khiar.pembuatstiker.stickercontentprovider";
 public boolean onCreate(){return true;}
 public String getType(Uri u){return "image/webp";}
 public Cursor query(Uri u,String[] p,String s,String[] a,String sort){
  if(u.getPath()!=null&&u.getPath().contains("metadata")){MatrixCursor c=new MatrixCursor(new String[]{"sticker_pack_identifier","sticker_pack_name","sticker_pack_publisher","sticker_pack_icon","android_play_store_link","ios_app_store_link"});c.addRow(new Object[]{"khiar_pack","Stiker Saya","Khiar", "icon.png", "", ""});return c;}
  MatrixCursor c=new MatrixCursor(new String[]{"sticker_file_name","sticker_emoji"});File d=new File(getContext().getFilesDir(),"stickers");File[] fs=d.listFiles();if(fs!=null)for(File f:fs)c.addRow(new Object[]{f.getName(),"😀"});return c;
 }
 public ParcelFileDescriptor openFile(Uri u,String mode)throws FileNotFoundException{String n=u.getLastPathSegment();File f=new File(new File(getContext().getFilesDir(),"stickers"),n);return ParcelFileDescriptor.open(f,ParcelFileDescriptor.MODE_READ_ONLY);}
 public int delete(Uri u,String s,String[] a){return 0;}public int update(Uri u,ContentValues v,String s,String[] a){return 0;}public Uri insert(Uri u,ContentValues v){return null;}
}
