package com.realogi.orangemvv.DAL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;

import com.realogi.orangemvv.InformationMessage;
import android.database.*;

public class MessageHistorique
{
	
	private List<T_Messages> listeMessageEnCache;
	
	private OrangeMVVDatabase _helper;
	  

	public MessageHistorique(Context context)
	{
    	    _helper = new OrangeMVVDatabase(context);
    }
	  
	  

	public List<T_Messages> getAllMessageFromDB() 
	{
		
		Cursor messageCursor = _helper.getReadableDatabase().query("T_MESSAGES", null, null, null, null, null, "MES_DT_DATE DESC");

		 List<T_Messages> lesMessages = new ArrayList<T_Messages>();

         while (messageCursor.moveToNext())
 	    {
        	 lesMessages.add(mapMessage(messageCursor));
 	    }
         
         listeMessageEnCache=lesMessages;
         return lesMessages;
	}
	
	public void deleteAllMessageFromDB()
	{
		if(listeMessageEnCache!=null)
		{
			for(int i=0;i<listeMessageEnCache.size();i++)
			{
				String nomFichier= listeMessageEnCache.get(0).MES_S_FICHIER;
				
				new  File(nomFichier).delete();
				
				
			}
		}
		
		
		_helper.getWritableDatabase().delete("T_MESSAGES", "1=1",null);
		getAllMessageFromDB();
		
		
	}


	public void ajouterUnMessage(InformationMessage leMessage) {
	
		ContentValues values = new ContentValues();

		
		values.put("MES_I_ID_MAIL", leMessage.numeroMessage);
		values.put("MES_S_NUMERO", leMessage.numeroTelephone);
		values.put("MES_S_CONTACT", "");
		values.put("MES_S_FICHIER", leMessage.nomFichierMp3);
		values.put("MES_I_NBSECONDE", leMessage.nbSecondes);
		
		
		if(leMessage.dejaLue) values.put("MES_I_LU", 1);
		else values.put("MES_I_LU", 0);		
		
        values.put("MES_DT_DATE", leMessage.laDateDuMessage.getTime());
        
       
        _helper.getWritableDatabase().insert("T_MESSAGES", null, values);
        getAllMessageFromDB();
        
	}
	
	
	 private T_Messages mapMessage(Cursor cursor)
     {
         return new T_Messages(cursor.getInt(0),
        		 				cursor.getInt(1),
        		 				cursor.getLong(2),
        		 				cursor.getString(3),
        		 				cursor.getString(4),
        		 				cursor.getString(5),
        		 				cursor.getInt(6),
        		 				cursor.getInt(7)
        		 			   );
         
     }

	 
	 
	 public int getNombreTotalDeMessage()
	 {
		 if(listeMessageEnCache!=null)
		 {
			 return listeMessageEnCache.size();
		 }
		 else return 0;			 
	 }
	 
	 public int getNombreMessageLue()
	 {
		 return getLesMessagesLue().size();
	 }
	 
	 public int getNombreMessageNonLues()
	 {
		 int nombreMessage=0;
		 
		 if(listeMessageEnCache!=null)
		 {			 
			 for(int i=0;i<listeMessageEnCache.size();i++)
			 {
				 if(listeMessageEnCache.get(i).MES_I_LU==0)nombreMessage++;
			 }
		 }
		 
		 
		 return nombreMessage;			 
	 }

	 public List<T_Messages> getLesMessagesNonLue()
	 {
		 List<T_Messages> messagesNonLue = new ArrayList<T_Messages>();
		 
		 if(listeMessageEnCache!=null)
		 {			 
			 for(int i=0;i<listeMessageEnCache.size();i++)
			 {
				 if(listeMessageEnCache.get(i).MES_I_LU==0)messagesNonLue.add(listeMessageEnCache.get(i));
			 }
		 }
		 
		 return messagesNonLue;
	 }
	 
	 public List<T_Messages> getLesMessagesLue()
	 {
		 List<T_Messages> messagesLue = new ArrayList<T_Messages>();
		 
		 if(listeMessageEnCache!=null)
		 {			 
			 for(int i=0;i<listeMessageEnCache.size();i++)
			 {
				 if(listeMessageEnCache.get(i).MES_I_LU==1)messagesLue.add(listeMessageEnCache.get(i));
			 }
		 }
		 
		 return messagesLue;
	 }
	 
	 
	 public void tagMessageLue(String nomFichier)
	 {
		 T_Messages leMessage = null;
		 for(int i=0;i<listeMessageEnCache.size();i++)
		 {
			 if(listeMessageEnCache.get(i).MES_S_FICHIER==nomFichier) leMessage = listeMessageEnCache.get(i);
		 }
		 
		 leMessage.MES_I_LU=1; // Update le message en base
		 
		 
		 //Update le message
		 ContentValues args = new ContentValues();
		 args.put("MES_I_LU", 1);
		 _helper.getWritableDatabase().update("T_MESSAGES", args, "MES_I_ID="+String.valueOf(leMessage.MES_I_ID), null);
		 
	 }
	 
	 
	 
}
