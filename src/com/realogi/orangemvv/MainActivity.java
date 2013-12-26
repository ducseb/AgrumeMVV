package com.realogi.orangemvv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

import com.realogi.orangemvv.DAL.MessageHistorique;
import com.realogi.orangemvv.Fragment.Liste_message_fragment;
import com.realogi.orangemvv.Store.ApplicationStore;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends FragmentActivity implements ActionBar.TabListener,OnPreparedListener,MediaController.MediaPlayerControl,DialogInterface.OnClickListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current tab position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    
    private Tab tabActionBar1;
    private Tab tabActionBar2;
    
    private Handler handler = new Handler();
    private MediaController mediaController;
    MediaPlayer mPlayer = new MediaPlayer();
    
    
    boolean readEnCours=false;
    String nomFichierEncoursdeLecture=null;
    boolean sortieHP=false;
    
    ProgressDialog progressDialogog=null;
    
   
    
    private AudioManager managementAudio=null;
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

       
        
        // Set up the action bar to show tabs.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        tabActionBar1 = actionBar.newTab();
        tabActionBar1.setText(R.string.title_section1);
       
      
        
        tabActionBar2 = actionBar.newTab();
        tabActionBar2.setText(R.string.title_section2);
        
      
        
       
      
        
        
        
        managementAudio = (AudioManager) getSystemService(AUDIO_SERVICE);
        initialiseMediaPlayer(true);
        
        ChargementDesMessagesEtRecuperationDepuisLaBase();
        
       // RecuperationDesNouveauxMessageIMAPetRefreshDB();
        
       
        tabActionBar1.setTabListener(this);
        tabActionBar2.setTabListener(this);
        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(tabActionBar1);
        actionBar.addTab(tabActionBar2);
        
    	UpdateTabInformation();
    	
    	
    }
    
    
    
    @Override
    protected void onResume() {
        super.onResume();
       
        CheckAutomatiqueNouveauMessage();
    }
    
    
    
    private void CheckAutomatiqueNouveauMessage()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);    		
		if(prefs.getBoolean("main_autorefreshCheck", false))
		{
			RecuperationDesNouveauxMessageIMAPetRefreshDB();
		}
		
		
    }
    
    
    
    
    
    
    
    private void UpdateTabInformation()
    {
    	int nombreMessageNonLue=ApplicationStore.lesMessagesEnBase.getNombreMessageNonLues();
    	int nombreMessageTotal=ApplicationStore.lesMessagesEnBase.getNombreMessageLue();
    	
    	if(nombreMessageNonLue>0) tabActionBar1.setText(getString(R.string.title_section1)+" ("+String.valueOf(nombreMessageNonLue)+")");    	
    	else tabActionBar1.setText(getString(R.string.title_section1));
    	
    	
    	if(nombreMessageTotal>0) tabActionBar2.setText(getString(R.string.title_section2)+" ("+String.valueOf(nombreMessageTotal)+")");
    	else tabActionBar2.setText(getString(R.string.title_section2));
    }
    
    
    
    
    private void ChargementDesMessagesEtRecuperationDepuisLaBase()
    {
    	 //Chargement de la base de données et On conserve la base en mémoire
        ApplicationStore.lesMessagesEnBase=new MessageHistorique(this);
        ApplicationStore.lesMessagesEnBase.getAllMessageFromDB();
      
        rafraichissementTabNonLue();
        rafraichissementTabLue();
    	UpdateTabInformation();
    }
    
    
    private void RecuperationDesNouveauxMessageIMAPetRefreshDB()
    {
    	 
    	progressDialogog=ProgressDialog.show(this,"","Chargement des nouveaux messages");
    	 
    	 LoadNewMessageTask calcul=new LoadNewMessageTask(this);
    	
    		 calcul.execute("");
    	
    	
		 
		 
    
       
        
      
    }
    
        	 
        	 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) 
    	{
    		case R.id.menu_emptydb:
    			viderLaBase();
    			return true;    		
    		case R.id.menu_refresh_message:
    			RecuperationDesNouveauxMessageIMAPetRefreshDB();
    			return true;  
    		case R.id.menu_hautparleur:
    			changerModeDeSortieSon(item);
    			return true;
    		case R.id.menu_settings:
    			lancerReglages();
    		case R.id.menu_tuto:
    			messageTutoriel();
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
    
    
    
    public void lancerReglages()
    {
    	Intent i =new Intent(this,SettingsActivity.class);
    	startActivity(i);
    }
    
    public void changerModeDeSortieSon(MenuItem menuItem)
    {
    	
    	if(sortieHP==false)
    	{
    			
    			managementAudio.setSpeakerphoneOn(true);    		
    			//mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);   			
    			
    			sortieHP=true;
    	}
    	else
    	{
    		
    		managementAudio.setSpeakerphoneOn(false);
    		
    		//mPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
    		
    		
    		sortieHP=false;
    	}
    		
    	
    	
    }
    
    
    public void viderLaBase()
    {
    	
    	
    	 new AlertDialog.Builder(this).setTitle("Êtes-vous sur ?")
    	 .setMessage("Êtes-vous certain de vouloir vider les messages enregistrés dans le AgrumeMVV ?")
    	 .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
    		 
    		  @Override
              public void onClick(DialogInterface dialog, int which) {
    			ApplicationStore.lesMessagesEnBase.deleteAllMessageFromDB();    			
    			UpdateTabInformation();
      	    	rafraichissementTabNonLue();
      	    	
      	    	Toast.makeText(MainActivity.this,"Tous les messages sont supprimés", 3000).show();   
              }
    		  
    		  
    		
    	 })
    	 
    	 .setNegativeButton("Non", null)    	 
    	 .show();

    	
    	
    }
    
    
    public void messageTutoriel()
    {
    	
    	ApplicationStore.lesMessagesEnBase.deleteAllMessageFromDB();
    	 new AlertDialog.Builder(this).setTitle("Information")
    	 .setMessage(R.string.info_tuto)
    	 .setPositiveButton("OK", null)    		
    	 .setNegativeButton("Infos détaillées", new DialogInterface.OnClickListener() {
    		 
   		  @Override
             public void onClick(DialogInterface dialog, int which) {

   			  String url = "http://www.realogi.fr";
   			  Intent i = new Intent(Intent.ACTION_VIEW);
   			  i.setData(Uri.parse(url));
   			  startActivity(i);
             }
   		  
   		  
   		
    	 })   
    	 .show();

    	
    	
    }
    
    
    
        	 
        	 

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current tab position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current tab position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, show the tab contents in the
        // container view.
    	
    
    	
    	if(tab==tabActionBar1)
    	{
    		rafraichissementTabNonLue();
    	}
    	else if(tab==tabActionBar2)
    	{
    		
    		rafraichissementTabLue();
    	}
    		
    	
    	
        
    }
    
    
    public void rafraichissementTabNonLue()
    {
    	Fragment fragment = new Liste_message_fragment(ApplicationStore.lesMessagesEnBase.getLesMessagesNonLue());
        /*Bundle args = new Bundle();
        args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, tab.getPosition() + 1);
        fragment.setArguments(args);*/
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();    
    }
    
    public void rafraichissementTabLue()
    {
    	Fragment fragment = new Liste_message_fragment(ApplicationStore.lesMessagesEnBase.getLesMessagesLue());
        /*Bundle args = new Bundle();
        args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, tab.getPosition() + 1);
        fragment.setArguments(args);*/
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();   
	  
    }
    
    

   

    
    public void initialiseMediaPlayer(boolean first)
    {
    	mPlayer= new MediaPlayer();
    	  mPlayer.setOnPreparedListener(this);
     	 mediaController = new MediaController(this);
     	managementAudio.setSpeakerphoneOn(false);
        mPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        
        if(sortieHP)managementAudio.setSpeakerphoneOn(true);
     	/*if(first)
     	{
     		managementAudio.setSpeakerphoneOn(false);
            mPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
     	}
     	else
     	{
     		if(sortieHP)
        	{        			
        			managementAudio.setSpeakerphoneOn(true);
        			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        			
        	}
        	else
        	{        		
        		managementAudio.setSpeakerphoneOn(false);
        		mPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        		
        	}
        		
     	}*/
    }
    
    
    public void setMediaPLayerFile(String cheminFichier)
    {
    	 //mPlayer = MediaPlayer.create(unContext,uneUri);
    	if(mPlayer.isPlaying())
    	{
    		mPlayer.release();
    		mPlayer=null;
    		initialiseMediaPlayer(false);
    	}
    	
    	try {
    		
			mPlayer.setDataSource(cheminFichier);
			mPlayer.prepare();
			mPlayer.start();
		} 
    	catch (IllegalArgumentException e1) {
    		Log.e("AudioPlayer", "Problème d'argument",e1);
		} catch (SecurityException e1) {
			Log.e("AudioPlayer", "Exception de sécurité",e1);
		} catch (IllegalStateException e1) {
			Toast.makeText(this, "Etat du fichier indéterminé", 2000);
			Log.e("AudioPlayer", "Etat du fichier indéterminé",e1);
		} catch (IOException e1) {
			Toast.makeText(this, "Impossible de charger le fichier son", 2000);
			 Log.e("AudioPlayer", "Impossible de charger le fichier son",e1);
		}
    	 
    }
    
    
    
    
    public void buttonPlayMessage(View v)
    {
    	String nomFichierALire = (String)v.findViewById(R.id.message_element_layout).getTag();
    	
    	//File openFile = new File(nomFichierALire);				
		//Uri uneUri= Uri.fromFile(openFile);
    	ApplicationStore.lesMessagesEnBase.tagMessageLue(nomFichierALire);
    	
    	/*LinearLayout layoutMessage =	(LinearLayout)v.findViewById(R.id.message_element_layout);
    	
    	Drawable d = getResources().getDrawable(android.R.drawable.list_selector_background);
    	layoutMessage.setBackground(d);*/
		
		setMediaPLayerFile(nomFichierALire);
	}
	
	
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
    
 



    
    


	@Override
	public boolean canPause() {
		return true;
	}





	@Override
	public boolean canSeekBackward() {
		
		return true;
	}





	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return true;
	}





	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}





	@Override
	public int getCurrentPosition() {
		return mPlayer.getCurrentPosition();
		
	}





	@Override
	public int getDuration() {
		return mPlayer.getDuration();
	}





	@Override
	public boolean isPlaying() {
		return mPlayer.isPlaying();
	}





	@Override
	public void pause() {
		mPlayer.pause();
		
	}





	@Override
	public void seekTo(int pos) {
		mPlayer.seekTo(pos);
		
	}





	@Override
	public void start() {
		mPlayer.start();
		
	}





	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d("AubioPlayer", "Préparation du média");
		mediaController.setMediaPlayer(this);
		mediaController.setAnchorView(this.getWindow().getDecorView());
		 
		 handler.post(new Runnable() {
		      public void run() {
		        mediaController.setEnabled(true);
		        mediaController.show(500000);
		       
		      }
		    });
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	   private class LoadNewMessageTask extends AsyncTask<String, Integer, List<InformationMessage>>
	    {
	    	private Context leContexte;
	    	private Exception error;
	    	private String messageError;
	    	
	    	public LoadNewMessageTask(Context contexteFenetre)
	    	{
	    		leContexte=contexteFenetre;
	    		error=null;
	    	}
	    	
	    	@Override
	    	protected void onPreExecute() {
	    		
	    		
	    	}
	    	
	    	
	    	@Override
	    	protected List<InformationMessage> doInBackground(String... params) {
	    		
	    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);    		
	    		String serveur=prefs.getString("mail_server", "imap.googlemail.com");
	    		String login=prefs.getString("mail_username", "");
	    		String password=prefs.getString("mail_password", "");
	    		String cheminDossier=prefs.getString("general_dossierSave", "/AgrumeMvv");
	    		List<InformationMessage> nouveauxMessage = null;
	    		
	    		try
	    		{
	    			nouveauxMessage = InboxReader.getMessageFromInbox(true, null, serveur,login, password,cheminDossier);
	    		}
	    		catch (NoSuchProviderException e)
	    		{
	    			messageError="Erreur de serveur de messagerie:\r\n"+e.getMessage();
	    			error=e;
	         		
	    		} 
	    		catch (MessagingException e)
	    		{
	    			if(e.getMessage().contains("Invalid credential"))
	    			{
	    				messageError="Problème de connexion:\r\n"+"Login ou mot de passe incorrect";
	    			}
	    			else
	    			{
	    				messageError="Problème de connexion:\r\n"+e.getMessage();
	    			}
	    			
	         		error=e;
	    		} 
	    		catch (IOException e) 
	    		{
	    			
	    			messageError="Erreur lors du stockage du fichier:\r\n"+e.getMessage();
	         		error=e;
	    		}
	    		catch(Exception exp)
	    		{
	    			
	    			messageError="Erreur lors de la recupération:\r\n"+exp.getMessage();
	         		error=exp;
	    		}
	    		
	    		
	    		
	    		
	    		return nouveauxMessage;
	    	}
	    	
	    	@Override
	    	protected void onProgressUpdate(Integer... progress) {
	    		
	        }
	    	
	    	

	    	@Override
	        protected void onPostExecute(List<InformationMessage> result) {
	    		progressDialogog.dismiss();
	    		if(error!=null)
	    		{
	    			Toast.makeText(leContexte,messageError, 3000).show();
	    		}
	    		else
	    		{    			
	    			if(result.isEmpty()==false)
	                {    
	           		 Toast.makeText(leContexte,Integer.toString(result.size())+" nouveau(x) message(s)", 3000).show();
	                	for(int i=0;i<result.size();i++)
	                	{
	                		ApplicationStore.lesMessagesEnBase.ajouterUnMessage(result.get(i));        		
	                	}   
	                	
	                	 
	                	 
	                	//Rechargement de la base de données        	  
	                	 ChargementDesMessagesEtRecuperationDepuisLaBase();
	                	 
	                	 tabActionBar1.select();
	                	 rafraichissementTabNonLue();
	                }
	           	 else
	           	 {
	           		 Toast.makeText(leContexte,"Aucun nouveau message", 3000).show();
	           	 }
	    		}
	        	 
	        }
	        
	    }


























	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
    

}
