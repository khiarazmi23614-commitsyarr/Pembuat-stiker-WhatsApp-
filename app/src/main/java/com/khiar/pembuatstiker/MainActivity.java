package com.khiar.pembuatstiker;

import android.app.*;import android.os.*;import android.content.*;import android.graphics.*;import android.net.Uri;import android.provider.MediaStore;import android.view.*;import android.widget.*;import java.io.*;import java.util.*;

public class MainActivity extends Activity {
 LinearLayout root; FrameLayout canvas; ImageView preview; Uri selected; Bitmap edited; ArrayList<TextItem> texts=new ArrayList<>();
 static class TextItem { String value; float x=256,y=420,size=52; String font="BOLD"; TextItem(String v){value=v;} }
 public void onCreate(Bundle b){super.onCreate(b);build();}
 TextView title(String s){TextView t=new TextView(this);t.setText(s);t.setTextSize(24);t.setTypeface(Typeface.DEFAULT_BOLD);t.setPadding(8,18,8,12);return t;}
 Button btn(String s){Button b=new Button(this);b.setText(s);return b;}
 void build(){
  root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(16,10,16,12);root.addView(title("Pembuat Stiker WhatsApp"));
  Button add=btn("＋ TAMBAH FOTO");root.addView(add);
  canvas=new FrameLayout(this);canvas.setBackgroundColor(Color.rgb(248,248,248));root.addView(canvas,new LinearLayout.LayoutParams(-1,0,1));
  preview=new ImageView(this);preview.setAdjustViewBounds(true);preview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);canvas.addView(preview,new FrameLayout.LayoutParams(-1,-1));
  LinearLayout tools=new LinearLayout(this);tools.setOrientation(LinearLayout.HORIZONTAL);
  Button edit=btn("✏ EDIT");Button addText=btn("T TEKS");tools.addView(edit,new LinearLayout.LayoutParams(0,-2,1));tools.addView(addText,new LinearLayout.LayoutParams(0,-2,1));root.addView(tools);
  Button wa=btn("🟢 SIMPAN KE WHATSAPP");root.addView(wa);setContentView(root);
  add.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,10);});
  edit.setOnClickListener(v->{if(selected==null)toast("Pilih foto dulu.");else editDialog();});
  addText.setOnClickListener(v->{if(selected==null)toast("Pilih foto dulu.");else editDialog();});
  wa.setOnClickListener(v->{if(selected==null){toast("Pilih foto dulu.");return;}edited=makeSticker();if(edited!=null)addToWhatsApp();});
 }
 void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
 protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==10&&c==RESULT_OK&&d!=null){selected=d.getData();try{getContentResolver().takePersistableUriPermission(selected,d.getFlags()&Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception e){}texts.clear();edited=null;while(canvas.getChildCount()>1)canvas.removeViewAt(1);preview.setImageURI(selected);}}
 void editDialog(){
  LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(18,0,18,0);
  EditText e=new EditText(this);e.setHint("Tulis teks stiker");box.addView(e);
  Spinner font=new Spinner(this);font.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"BOLD","SANS","SERIF","MONOSPACE","ITALIC"}));box.addView(font);
  TextView info=new TextView(this);info.setText("Setelah ditambahkan, teks bisa digeser dengan jari.");box.addView(info);
  SeekBar size=new SeekBar(this);size.setMax(100);size.setProgress(52);box.addView(size);
  new AlertDialog.Builder(this).setTitle("Edit stiker").setView(box).setPositiveButton("TAMBAH",(d,w)->{String s=e.getText().toString();if(!s.isEmpty()){TextItem t=new TextItem(s);t.size=24+size.getProgress();t.font=font.getSelectedItem().toString();texts.add(t);addTextView(t);}}).setNegativeButton("BATAL",null).show();
 }
 void addTextView(final TextItem item){
  final TextView tv=new TextView(this);tv.setText(item.value);tv.setTextSize(item.size/2f);tv.setTextColor(Color.WHITE);tv.setGravity(Gravity.CENTER);tv.setPadding(12,6,12,6);applyFont(tv,item.font);tv.setShadowLayer(5,2,2,Color.BLACK);
  FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(-2,-2);lp.leftMargin=80;lp.topMargin=80;canvas.addView(tv,lp);
  tv.setOnTouchListener(new View.OnTouchListener(){float dx,dy;public boolean onTouch(View v,MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN){dx=v.getX()-e.getRawX();dy=v.getY()-e.getRawY();return true;}if(e.getAction()==MotionEvent.ACTION_MOVE){float nx=e.getRawX()+dx,ny=e.getRawY()+dy;v.setX(Math.max(0,Math.min(canvas.getWidth()-v.getWidth(),nx)));v.setY(Math.max(0,Math.min(canvas.getHeight()-v.getHeight(),ny)));item.x=v.getX()/Math.max(1,canvas.getWidth())*512f;item.y=v.getY()/Math.max(1,canvas.getHeight())*512f+item.size;return true;}return true;}});
 }
 void applyFont(TextView tv,String f){if(f.equals("SERIF"))tv.setTypeface(Typeface.SERIF);else if(f.equals("MONOSPACE"))tv.setTypeface(Typeface.MONOSPACE);else if(f.equals("ITALIC"))tv.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));else if(f.equals("BOLD"))tv.setTypeface(Typeface.DEFAULT_BOLD);else tv.setTypeface(Typeface.DEFAULT);}
 Bitmap makeSticker(){try{Bitmap src=MediaStore.Images.Media.getBitmap(getContentResolver(),selected);float scale=Math.min(1f,Math.min(512f/src.getWidth(),512f/src.getHeight()));Bitmap b=Bitmap.createScaledBitmap(src,Math.max(1,(int)(src.getWidth()*scale)),Math.max(1,(int)(src.getHeight()*scale)),true);Bitmap out=Bitmap.createBitmap(512,512,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(out);c.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);c.drawBitmap(b,(512-b.getWidth())/2f,(512-b.getHeight())/2f,null);for(TextItem t:texts){Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);p.setColor(Color.WHITE);p.setTextSize(t.size);p.setTextAlign(Paint.Align.CENTER);p.setStyle(Paint.Style.FILL);if(t.font.equals("SERIF"))p.setTypeface(Typeface.SERIF);else if(t.font.equals("MONOSPACE"))p.setTypeface(Typeface.MONOSPACE);else if(t.font.equals("ITALIC"))p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));else if(t.font.equals("BOLD"))p.setTypeface(Typeface.DEFAULT_BOLD);else p.setTypeface(Typeface.DEFAULT);p.setShadowLayer(8,2,2,Color.BLACK);c.drawText(t.value,t.x,t.y,p);}return out;}catch(Exception e){toast("Gagal memproses foto.");return null;}}
 void addToWhatsApp(){try{File dir=new File(getFilesDir(),"stickers");dir.mkdirs();File[] old=dir.listFiles();if(old!=null)for(File f:old)f.delete();File f=new File(dir,"sticker_1.webp");FileOutputStream os=new FileOutputStream(f);edited.compress(Bitmap.CompressFormat.WEBP,90,os);os.close();Intent i=new Intent("com.whatsapp.intent.action.ENABLE_STICKER_PACK");i.putExtra("sticker_pack_id","khiar_pack");i.putExtra("sticker_pack_authority",StickerProvider.AUTH);startActivity(i);}catch(Exception e){toast("Gagal membuka penambahan paket WhatsApp.");}}
}
