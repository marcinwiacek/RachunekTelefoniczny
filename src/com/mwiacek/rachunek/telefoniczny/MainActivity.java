package com.mwiacek.rachunek.telefoniczny;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	final class Abonament {
		public GregorianCalendar Start;
		public GregorianCalendar Stop;
		public int Units;
		public int SMSUnits;
		public int MMSUnits;
		public int CallUnits;
		public String SMSRule;
		public String MMSRule;
		public List<String> CallRule;
		public Float Price;
	}
	
    public static TextView textView;
    
	StringBuilder s=new StringBuilder();    	
	List<List<String[]>> x1= new ArrayList<List<String[]>>();
	List<Float[]> x2= new ArrayList<Float[]>();
	List<List<Abonament>> x3 = new ArrayList<List<Abonament>>();
	GregorianCalendar l = new  GregorianCalendar (2015,12-1,21);
	GregorianCalendar n = new GregorianCalendar();
	List<String> opisy = new ArrayList<String>();	
	InputStream stream;
	List<String[]> nr = new ArrayList<String[]>();
    
	public String SiecNumeru2(String nrr) {
		
		//fixme
		if (!nrr.matches("^(0048)?((50)|(51)|(53)|(57)|(60)|(66)|(69)|(72)|(73)|(78)|(79)|(88)){1}\\d{7}")) return "";
		
		String nrrr="";
		
		if (nrr.length()==9) {
			nrrr = "48" + nrr;
		} else {
			nrrr = nrr.substring(2);
		}
		
		Iterator<String[]> itrr = nr.iterator();
 		   	
		while(itrr.hasNext()) {
			String[] s = itrr.next();
			if (s[0].equals(nrrr)) {
				return s[1];
			}
		}
		
	    try {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpGet get = new HttpGet(new URI("https", null, "developers.t-mobile.pl", 443, 
	    	  		"/api/database/mnp", "target="+nrrr+"&appkey=1&output=json", null).toASCIIString());
	  			
	        HttpResponse response = httpclient.execute(get);
	        if (response.getStatusLine().getStatusCode()==200) {
	        	InputStream stream = response.getEntity().getContent();
    			InputStreamReader inputreader = new InputStreamReader(stream);
    	        StringBuilder lines2 = new StringBuilder();
	            int numRead = 0;    	        
	            char[] bytes = new char[10000];							
				while ((numRead = inputreader.read(bytes,0,10000)) >= 0) {
					lines2.append(bytes, 0, numRead);
				}
				stream.close();
	        	
	        	//Log.d("nr2",lines2.toString());
	        	int i = lines2.toString().indexOf("\"mnc\": \"");
	        	if (i!=-1) {
	        		String[] s2 = new String[2];
	        		s2[0] = nrrr;
	        		s2[1] = "260"+lines2.toString().substring(i+8,lines2.toString().indexOf("\"",i+8));
	        		nr.add(s2);
	        		return s2[1];
	        	}
	        }
	        
	    } catch (URISyntaxException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
		}
	    return "";
	}
	
	public void AddFile(String name) {
		List<String[]> x= new ArrayList<String[]>();
		List<Abonament> z= new ArrayList<Abonament>();				
		Float[] f = new Float[4];
		
		f[0]=Float.valueOf("0.0");
		f[1]=Float.valueOf("0.0");
		f[2]=Float.valueOf("0.0");
		f[3]=Float.valueOf("0.0");

		try {
			stream = getAssets().open(name);
		     BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		     String reader = "";
		     opisy.add(in.readLine());
		     while ((reader = in.readLine()) != null){
		    	 if (!reader.contains("#")) {
		    		 if (reader.contains("abo")) {
		    			 GregorianCalendar l2=new GregorianCalendar();
		    			 l2.setTimeInMillis(l.getTimeInMillis());
		    			 
		    			 String[] s22 = reader.split(";");
		    			 Integer counter=0;
		    			 do  {

			    			 if (s22[1].equals("")) {
			    				 f[2]+=Float.valueOf(s22[1]);
			    			 } else {
				    			 if (s22[2].equals(Integer.toString(counter))) {
				    				 f[2]+=Float.valueOf(s22[3]);
				    			 } else {
				    				 f[2]+=Float.valueOf(s22[1]);
					    			 counter++;				    				 
				    			 }
			    			 }
		    				 l2.add(Calendar.MONTH, 1);		    				 
		    			 } while (n.compareTo(l2)!=-1);
		    			 
		    		 } else if (reader.contains("packet")) {
		    			 String[] s22 = reader.split(";");

		    			 GregorianCalendar l2=new GregorianCalendar();
		    			 l2.setTimeInMillis(l.getTimeInMillis());

		    			 Abonament abo;
		    			 do  {
			    			 abo=new Abonament();
			    			 //fixme dt parsing
			    			 abo.Start=new GregorianCalendar();
			    			 abo.Start.setTimeInMillis(l2.getTimeInMillis());
			    			 abo.Stop=new GregorianCalendar();
			    			 abo.Stop.setTimeInMillis(l2.getTimeInMillis());
			    			 abo.Stop.add(Calendar.MONTH, 1);
			    			 abo.Price = Float.valueOf(s22[1]);
			    			 abo.Units = Integer.valueOf(s22[3]);
			    			 abo.SMSUnits =Integer.valueOf(s22[4]); 
			    			 abo.SMSRule = s22[5];
			    			 abo.MMSUnits=Integer.valueOf(s22[6]);
			    			 abo.MMSRule=s22[7];
			    			 
			    			 abo.CallUnits =Integer.valueOf(s22[8]); 
			    			 abo.CallRule = new ArrayList<String>();
			    			 for(int i=9;i<s22.length;i++) {
			    				 abo.CallRule.add(s22[i]);
			    			 }

			    			 z.add(abo);
			    			 
			    			 l2.add(Calendar.MONTH, 1);		    				 
		    			 } while (n.compareTo(l2)!=-1);
		    			 
		    		 } else {
		    			 x.add(reader.split(";"));
		    		 }
		    	 }
		     }
		     in.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		x1.add(x);
		x2.add(f);
		x3.add(z);		
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_settings:
            item.setEnabled(false);
        	new Thread(new Runnable(){
        		public void run(){
        			Start();
        		}
        	}).start();
        	
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
    void Start() {
        this.runOnUiThread(new Runnable() {
        	public void run() {
        		textView.setText("Trwa liczenie...");
        	}
        });    	        	
	   	        
        Cursor cursor = getContentResolver().query( Uri.parse("content://sms/sent"), null, null, null, null);
        if (cursor != null) {
	        try {
	            if (cursor.moveToFirst()) {
	            	do {
	            		if (Long.valueOf(cursor.getString(cursor.getColumnIndex("date")))<l.getTimeInMillis()) {
	            			l.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex("date"))));
	            		}
	                } while (cursor.moveToNext());	                
	            }
	        } catch(SQLException e) {
	        	s.append(e.getMessage());
	        }
	        cursor.close();
        }     

        cursor = getContentResolver().query( Uri.parse("content://mms/sent"), null, null, null, null);
        if (cursor != null) {
	        try {
	            if (cursor.moveToFirst()) {
	            	do {
	            		if (Long.valueOf(cursor.getString(cursor.getColumnIndex("date")))*1000<l.getTimeInMillis()) {
	            			l.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex("date")))*1000);
	            		}
	                } while (cursor.moveToNext());	                
	            }
	        } catch(SQLException e) {
	        	s.append(e.getMessage());
	        }
	        cursor.close();
        }     
        
        String[] strFields = {
                android.provider.CallLog.Calls.NUMBER, 
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.DURATION
                };
         
        cursor = getContentResolver().query(
                android.provider.CallLog.Calls.CONTENT_URI,
                strFields,
                android.provider.CallLog.Calls.TYPE+"="+android.provider.CallLog.Calls.OUTGOING_TYPE+" and "+android.provider.CallLog.Calls.DURATION+"<>0",
                null,
                android.provider.CallLog.Calls.DATE + " DESC"
                );
        if (cursor != null) {
	        try {
	            if (cursor.moveToFirst()) {
	            	do {
	            		if (Long.valueOf(cursor.getString(cursor.getColumnIndex("date")))<l.getTimeInMillis()) {
	            			l.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex("date"))));
	            		}
	                } while (cursor.moveToNext());	                
	            }
	        } catch(SQLException e) {
	        	s.append(e.getMessage());
	        }
	        cursor.close();
        }   
        
        AddFile("mbankplaski.csv");
        AddFile("vmobile.csv");
        AddFile("vmobile50.csv");
        AddFile("era40.csv");
        
        cursor = getContentResolver().query( Uri.parse("content://sms/sent"), null, null, null, null);
        if (cursor != null) {
	        try {
	            if (cursor.moveToFirst()) {
	            	do {
            			s.append("sms "+android.text.format.DateFormat.format("yyMMdd hh:mm", Long.valueOf(cursor.getString(cursor.getColumnIndex("date")))));	            		
	            		s.append(" "+cursor.getString(cursor.getColumnIndex("address")));
            			//s.append(" "+SiecNumeru2(cursor.getString(cursor.getColumnIndex("address")).replace("+","00")));
	            		
	         		   	Iterator<List<String[]>> itr1 = x1.iterator();
	         		   	Iterator<Float[]> itr2 = x2.iterator();
	         		   	Iterator<List<Abonament>> itr3 = x3.iterator();
	         		   	while(itr1.hasNext()) {
	         		   		List<String[]> x0 = itr1.next();
	         		   		Float[] k0 = itr2.next();
		         		   	Iterator<String[]> itr = x0.iterator();
		         		   	
		         		   	Iterator<Abonament> itrr = itr3.next().iterator();
		         		   	
		         		   	GregorianCalendar gk = new GregorianCalendar();
		         		   	gk.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex("date"))));
		         		   	Boolean found=false;
		         		   	while(itrr.hasNext()) {
		         		   		Abonament abo = itrr.next();
		         		   		if (gk.after(abo.Start) && gk.before(abo.Stop)) {
		         		   			if (cursor.getString(cursor.getColumnIndex("address")).replace("+","00").matches(abo.SMSRule)) {
		         		   				if (abo.Units>=abo.SMSUnits) {
		         		   					abo.Units-=abo.SMSUnits;
		         		   					s.append(" abo");
		         		   					found=true;
		         		   					break;
		         		   				}
		         		   			}
		         		   		}
		         		   	}
		         		   	if (found) continue;
		         		   	
		         		   	while(itr.hasNext()) {
		         		   			String[] s2 = itr.next();
		         		   			if (s2[0].equals("smses")) {
		         		   				if (s2.length>2) {
		         		   					if (cursor.getString(cursor.getColumnIndex("address")).replace("+","00").matches(s2[2])) {
		         		   						s.append(" "+s2[1]);
		         		   						found=true;
		         		   						k0[0]+=Float.valueOf(s2[1]);
		         		   						break;
		         		   					}
		         		   				} else {
		         		   					s.append(" "+s2[1]);
		         		   					k0[0]+=Float.valueOf(s2[1]);
		         		   					found=true;
		         		   					break;
		         		   				}
		         		   			}
	            		    }
		         		   	
		         		   	if (!found) s.append(" n/a");
	         		   		
	         		   	}
	         		   	
	         		   	
	         		   	s.append("\n");
	                } while (cursor.moveToNext());	                
	            }
	        } catch(SQLException e) {
	        	s.append(e.getMessage());
	        }
	        cursor.close();
        }     

        cursor = getContentResolver().query( Uri.parse("content://mms/sent"), null, null, null, null);
        if (cursor != null) {
	        try {
	            if (cursor.moveToFirst()) {
	            	do {
            			s.append("mms "+android.text.format.DateFormat.format("yyMMdd hh:mm", Long.valueOf(cursor.getString(cursor.getColumnIndex("date")))* 1000));
	            		
	            		Cursor cursor2 = getContentResolver().query (Uri.parse("content://mms/" + cursor.getString (cursor.getColumnIndex ("_id")) + "/addr"), null, "type=151", null, null);
	                    if (cursor2 != null) {
	            	        try {
	            	            if (cursor2.moveToFirst()) {
	            	            	do {
	            	            		s.append(" "+cursor2.getString(cursor2.getColumnIndex("address")));
	                        			//s.append(" "+SiecNumeru2(cursor2.getString(cursor2.getColumnIndex("address")).replace("+","00")));
	            	            		
	            	         		   	Iterator<List<String[]>> itr1 = x1.iterator();
	            	         		   	Iterator<Float[]> itr2 = x2.iterator();
	            	         		   	Iterator<List<Abonament>> itr3 = x3.iterator();
	            	         		   	while(itr1.hasNext()) {
	            	         		   		List<String[]> x0 = itr1.next();
	            	         		   		Float[] k0 = itr2.next();
	            		         		   	Iterator<String[]> itr = x0.iterator();
	            		         		   	
	            		         		   	Iterator<Abonament> itrr = itr3.next().iterator();
	            		         		   	
	            		         		   	GregorianCalendar gk = new GregorianCalendar();
	            		         		   	gk.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex("date")))*1000);
	            		         		   	Boolean found=false;
	            		         		   	while(itrr.hasNext()) {
	            		         		   		Abonament abo = itrr.next();
	            		         		   		if (gk.after(abo.Start) && gk.before(abo.Stop)) {
	            		         		   			if (cursor2.getString(cursor2.getColumnIndex("address")).replace("+","00").matches(abo.MMSRule)) {
	            		         		   				if (abo.Units>=abo.MMSUnits) {
	            		         		   					abo.Units-=abo.MMSUnits;
	            		         		   					s.append(" abo");
	            		         		   					found=true;
	            		         		   					break;
	            		         		   				}
	            		         		   			}
	            		         		   		}
	            		         		   	}
	            		         		   	if (found) continue;
	            		         		   	
	            		         		   	while(itr.hasNext()) {
            		         		   			String[] s2 = itr.next();
            		         		   			if (s2[0].equals("mmses")) {
            		         		   				if (s2.length>2) {
            		         		   					if (cursor2.getString(cursor2.getColumnIndex("address")).replace("+","00").matches(s2[2])) {
            		         		   						s.append(" "+s2[1]);
            		         		   						found=true;
            		         		   						k0[3]+=Float.valueOf(s2[1]);
            		         		   						break;
            		         		   					}
            		         		   				} else {
            		         		   					s.append(" "+s2[1]);
            		         		   					k0[3]+=Float.valueOf(s2[1]);
            		         		   					found=true;
            		         		   					break;
            		         		   				}
            		         		   			}	            		         		   		
	            	            		    }
	            		         		   	
	            		         		   	if (!found) s.append(" n/a");
	            	         		   		
	            	         		   	}
	            	            		
	            	                } while (cursor2.moveToNext());	                
	            	            }
	            	        } catch(SQLException e) {
	            	        	s.append(e.getMessage());
	            	        }
	            	        cursor2.close();
	                    }
	            		
	         		   	s.append("\n");	            		
	                } while (cursor.moveToNext());	                
	            }
	        } catch(SQLException e) {
	        	s.append(e.getMessage());
	        }
	        cursor.close();
        }     
        
        cursor = getContentResolver().query(
                android.provider.CallLog.Calls.CONTENT_URI,
                strFields,
                android.provider.CallLog.Calls.TYPE+"="+android.provider.CallLog.Calls.OUTGOING_TYPE+" and "+android.provider.CallLog.Calls.DURATION+"<>0",
                null,
                android.provider.CallLog.Calls.DATE + " DESC"
                );
        if (cursor != null) {
	        try {
	            if (cursor.moveToFirst()) {
	            	do {
            			s.append("call "+android.text.format.DateFormat.format("yyMMdd hh:mm", Long.valueOf(cursor.getString(cursor.getColumnIndex("date")))));
            			s.append(" "+cursor.getString(cursor.getColumnIndex("number")));
            			s.append(" "+SiecNumeru2(cursor.getString(cursor.getColumnIndex("number")).replace("+","00")));
            			s.append(" "+cursor.getString(cursor.getColumnIndex("duration"))+"s");
	         		   	Iterator<List<String[]>> itr1 = x1.iterator();
	         		   	Iterator<Float[]> itr2 = x2.iterator();		         		   	
	         		   	Iterator<List<Abonament>> itr3 = x3.iterator();
	         		   	while(itr1.hasNext()) {
	         		   		List<String[]> x0 = itr1.next();
		         		   	Iterator<String[]> itr = x0.iterator();
	         		   		Float[] k0 = itr2.next();	
	         		   				         		   		
		         		   	Iterator<Abonament> itrr = itr3.next().iterator();
		         		   	
		         		   	GregorianCalendar gk = new GregorianCalendar();
		         		   	gk.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex("date"))));
		         		   	Boolean found=false;
		         		   	while(itrr.hasNext()) {
		         		   		Abonament abo = itrr.next();
		         		   		if (gk.after(abo.Start) && gk.before(abo.Stop)) {
		         		   			Iterator<String> itrrr = abo.CallRule.iterator();
				         		   	while(itrrr.hasNext()) {
			         		   			if (cursor.getString(cursor.getColumnIndex("number")).replace("+","00").matches(itrrr.next())) {
			         		   				if (abo.Units>=abo.CallUnits) {
			         		   					abo.Units-=abo.CallUnits;
			         		   					s.append(" abo");
			         		   					found=true;
			         		   					break;
			         		   				}
			         		   			}					         		   	
				         		   	}
		         		   			if (found) {
		         		   				break;
		         		   			}
		         		   		}
		         		   	}
		         		   	if (found) continue;
		         		   	
		         		   	
		         		   	while(itr.hasNext()) {
	         		   			String[] s2 = itr.next();
	         		   			if (s2[0].equals("voice")) {
	         		   				if (s2.length>4) {
	         		   					if (cursor.getString(cursor.getColumnIndex("number")).replace("+","00").matches(s2[2])) {
	         		   						if (s2[3].length()==0) {
			         		   					s.append(" "+String.format("%.2f", (Float.valueOf(s2[1])/Float.valueOf(s2[4])*Float.valueOf(cursor.getString(cursor.getColumnIndex("duration"))))));
			         		   					k0[1]+=(Float.valueOf(s2[1])/Float.valueOf(s2[4])*Float.valueOf(cursor.getString(cursor.getColumnIndex("duration"))));
			         		   					found=true;
		         		   						break;
	         		   							
	         		   						} else if (s2[3].equals(SiecNumeru2(cursor.getString(cursor.getColumnIndex("number")).replace("+","00")))) {
			         		   					s.append(" "+String.format("%.2f", (Float.valueOf(s2[1])/Float.valueOf(s2[4])*Float.valueOf(cursor.getString(cursor.getColumnIndex("duration"))))));
			         		   					k0[1]+=(Float.valueOf(s2[1])/Float.valueOf(s2[4])*Float.valueOf(cursor.getString(cursor.getColumnIndex("duration"))));
			         		   					found=true;
		         		   						break;
	         		   							
	         		   						}
	         		   					}
	         		   				} else {
	         		   					break;
	         		   				}
	         		   			}
		         		   	}
		         		   	if (!found) s.append(" n/a");
            		    }	    	        
            			s.append("\n");
	            		
	                } while (cursor.moveToNext());	                
	            }
	        } catch(SQLException e) {
	        	s.append(e.getMessage());
	        }
	        cursor.close();
        }     

    	StringBuilder s2=new StringBuilder();
	   	Iterator<Float[]> itr2 = x2.iterator();		         		   	
	   	Iterator<String> itr3 = opisy.iterator();
	   	
	   	while(itr2.hasNext()) {
	   		
	   		Float[] k0 = itr2.next();		   
	   		s2.append(itr3.next()+"\nsms "+String.format("%.2f", k0[0])+
	   				" mms "+String.format("%.2f", k0[3])+
	   				" call "+String.format("%.2f", k0[1])+
	   				" abo "+String.format("%.2f", k0[2])+
	   				" sum "+String.format("%.2f", (k0[0]+k0[1]+k0[2]+k0[3]))+
	   				" pln\n");		   		
	   	}

	   	s.insert(0, s2+"\n");
    
		s.insert(0,"Dane od "+android.text.format.DateFormat.format("yyMMdd hh:mm",l.getTimeInMillis())+"\n\n");	            		

        this.runOnUiThread(new Runnable() {
        	public void run() {
        		textView.setText(s);
        	}
        });    	        	
	   	
    	
    		
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.TextView1);
        textView.setText("Aplikacja to 'proof of concept'. Pobiera dane SMS, MMS i po³¹czeñ z logów telefonu i wylicza przybli¿one kwoty dla kilku taryf.\n\nObliczenia s¹ przybli¿one:\n\n1) z uwagi na mo¿liwoœæ innego zaokr¹glania czasu przez operatorów\n\n2) z uwagi na zastrze¿enia operatorów typu 'Z przyczyn technicznych minuty z Promocyjnego pakietu minut s¹ rozliczane wed³ug kolejnoœci zarejestrowania w systemie naliczaj¹cym op³aty za poszczególne jednostki (minuty, SMS, MMS).'\n\n3) z uwagi na traktowanie wszystkich po³¹czeñ jak g³osowe (brak informacji w logach Androida o typie po³¹czenia)\n\n4) z uwagi na to, ¿e polskie numery komórkowe mog³y przynale¿eæ do innej sieci ni¿ dziœ (aplikacja wysy³a ka¿dy polski numer komórkowy z u¿yciem metody GET do jednej z bramek w Internecie i tam to sprawdza)\n\nW koszcie nieuwzglêdnione s¹ dane pakietowe - brak ich iloœci w logach Androida lub s¹ one niedostêpne.\n\nJeœli chcesz rozpocz¹æ, wybierz Start z menu");
        

    	try {
      	  PackageManager manager = getPackageManager();
      	  PackageInfo info = manager.getPackageInfo(getPackageName(), 0);

      	  setTitle("Rachunek telefoniczny "+info.versionName);
    	} catch (Exception e) {          	  
    	}
        
    }        
        

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
