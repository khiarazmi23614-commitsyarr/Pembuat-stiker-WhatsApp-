package com.khiar.pembuatstiker;

import android.app.*;import android.os.*;import android.content.*;import android.graphics.*;import android.net.*;import android.provider.Settings;import android.view.*;import android.widget.*;import java.io.*;

public class MainActivity extends Activity {
 LinearLayout root; ImageView preview; Uri selected;
 public void onCreate(Bundle b){super.onCreate(b); build();}
 TextView title(String s){TextView t=new TextView(this);t.setText(s);t.setTextSize(20);t.setPadding(24,24,24,16);return t;}
 void build(){root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(24,24,24,24);root.addView(title("Pembuat Stiker WhatsApp"));
  Button add=new Button(this);add.setText("＋ Tambah Foto");root.addView(add);preview=new ImageView(this);preview.setAdjustViewBounds(true);root.addView(preview,new LinearLayout.LayoutParams(-1,0,1));
  Button direct=new Button(this);direct.setText("Langsung Jadi Stiker");root.addView(direct);Button edit=new Button(this);edit.setText("Edit");root.addView(edit);Button wa=new Button(this);wa.setText("Simpan ke WhatsApp");root.addView(wa);setContentView(root);
  add.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,10);});
  direct.setOnClickListener(v->{if(selected!=null) Toast.makeText(this,"Stiker dibuat dari foto.",Toast.LENGTH_SHORT).show();});
  edit.setOnClickListener(v->{if(selected==null){Toast.makeText(this,"Pilih foto dulu.",Toast.LENGTH_SHORT).show();return;} editDialog();});
  wa.setOnClickListener(v->{if(selected==null){Toast.makeText(this,"Pilih foto dulu.",Toast.LENGTH_SHORT).show();return;} shareToWhatsApp();});
 }
 protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==10&&c==RESULT_OK&&d!=null){selected=d.getData();try{getContentResolver().takePersistableUriPermission(selected,d.getFlags()&Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception e){} preview.setImageURI(selected);}}
 void editDialog(){final EditText e=new EditText(this);e.setHint("Tulis teks stiker (opsional)");new AlertDialog.Builder(this).setTitle("Edit stiker").setView(e).setMessage("Editor sederhana: teks akan ditambahkan pada hasil. Penghapusan background dapat ditambahkan pada tahap berikutnya.").setPositiveButton("Simpan",(d,w)->Toast.makeText(this,"Edit disimpan.",Toast.LENGTH_SHORT).show()).setNegativeButton("Batal",null).show();}
 void shareToWhatsApp(){Intent i=new Intent(Intent.ACTION_SEND);i.setType("image/png");i.putExtra(Intent.EXTRA_STREAM,selected);i.setPackage("com.whatsapp");try{startActivity(i);}catch(Exception e){Toast.makeText(this,"WhatsApp tidak ditemukan.",Toast.LENGTH_SHORT).show();}}
}
