package com.realogi.orangemvv;

import java.util.ArrayList;
import java.util.Date; 
import java.util.List;
import java.util.Properties; 
import javax.activation.CommandMap; 
import javax.activation.DataHandler; 
import javax.activation.DataSource; 
import javax.activation.FileDataSource; 
import javax.activation.MailcapCommandMap; 
import javax.mail.BodyPart; 
import javax.mail.Multipart; 
import javax.mail.PasswordAuthentication; 
import javax.mail.Session; 
import javax.mail.Transport; 
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeBodyPart; 
import javax.mail.internet.MimeMessage; 
import javax.mail.internet.MimeMultipart; 


import java.io.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.DateTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

public class InboxReader {



	
	
	public static String saveMp3FromMultiPartEmail(	MimeBodyPart bodyPart,String cheminSauvegarde,String nomFichier) throws IOException
	{
		Log.i("EmailReader", "Debut de lecture du message");

		
		
		
		try {
			InputStream is = bodyPart.getInputStream();
		
		
			
		Log.i("EmailReader", "Nom du message: "+cheminSauvegarde+nomFichier);
		File f = new File(cheminSauvegarde+nomFichier);                        	
		if(f.exists())f.delete();
		
		FileOutputStream fos = new FileOutputStream(f);
		byte[] buf = new byte[4096];
		int bytesRead;

		while((bytesRead = is.read(buf))!=-1)
		{
			fos.write(buf, 0, bytesRead);
		}
		Log.i("EmailReader", "Fin de lecture du message");
		fos.close();
		Log.i("EmailReader", "Sauvegarde OK");
		} catch (IOException e) {			
			throw(e);
		} catch (MessagingException e) {			
			e.printStackTrace();
		}
		return cheminSauvegarde+nomFichier;
	}
	
	
	public static String getNumeroTelephoneContact(String messageTexte)
	{
		String numeroTelephone=null;
		
		if(messageTexte.contains("Ce message a été déposé")==false)
		{
			numeroTelephone="N° masqué";
		}
		else
		{
			Pattern monPatternTelephone = Pattern.compile("Ce message a été déposé par le (.*?)\r\n\r\n");
	        Matcher matcher = monPatternTelephone.matcher(messageTexte);
			
	        while(matcher.find())
	        {
	        	numeroTelephone=matcher.group(1);
	        }
		}
		
		
	
        
     /*   "Vous avez reçu un nouveau message le 26/12/2012 à 12:40:57 sur le"
		"0607569373"
		"La durée du message vocal ci-joint est de 33 secondes."
		"Ce message a été déposé par le 0627333031"*/
		
		
		
		return numeroTelephone;
	}
	
	public static String getDureeDuMessage(String messageTexte)
	{
		String duree="0";
		
		
		
		Pattern monPatternTelephone = Pattern.compile(" ci-joint est de (.*?) secondes.");
        Matcher matcher = monPatternTelephone.matcher(messageTexte);
		
        while(matcher.find())
        {
        	duree=matcher.group(1);
        }
        
        
		
		
		
		return duree;
	}



	
	public static List<InformationMessage> getMessageFromInbox(boolean tousNonLu,Date dateDeRecherche,String imapServer,String username,String password,String cheminDossier) throws Exception 
	{

		
		List<InformationMessage> laListeDesNouveauxMessages = new ArrayList<InformationMessage>();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 



		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		try 
		{
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect(imapServer, username, password);
			
			
			

			Folder inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_WRITE);
			//Message messages[] = inbox.getMessages();
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);


			SearchTerm term =new SubjectTerm("Réception d'un message sur la messagerie Orange ");   
			
			//Recherche par date			
			SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT,new Date() );
			SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, dateDeRecherche);
			SearchTerm andTerm = new AndTerm(olderThan, newerThan);
			SearchTerm rechercheParDAte = new AndTerm(andTerm, term);

			//Recherche par non lues			
			SearchTerm noLue = new FlagTerm(new Flags(Flags.Flag.SEEN),false);
			SearchTerm rechercheParNonLue = new AndTerm(noLue, term);


			Message messagesOrange[] = null;

			if(tousNonLu) messagesOrange=inbox.search(rechercheParNonLue);
			else  messagesOrange=inbox.search(rechercheParDAte);


			
			
			
			
			int i =0;
			for(Message message:messagesOrange) //Défilement sur tous les messages
			{

				InformationMessage unMessageInfo = new InformationMessage(message.getMessageNumber());
				
				unMessageInfo.laDateDuMessage = message.getReceivedDate();				
				
				Multipart mp = (Multipart)message.getContent();   // On récèpère le contenu d'un message multipart			
				
				
				
				for (int j = 0; j < mp.getCount(); j++) // On passe en revue l'ensemble des element du multipart
				{					
					MimeBodyPart bodyPart = (MimeBodyPart)mp.getBodyPart(j);

					File directory = new File(Environment.getExternalStorageDirectory()+cheminDossier);
					directory.mkdir();					
					directory=null;
					
					// Si on trouve un contenu audio mp3 on enregistre le fichier
					if(bodyPart.isMimeType("audio/mpeg")) unMessageInfo.nomFichierMp3=saveMp3FromMultiPartEmail(bodyPart,Environment.getExternalStorageDirectory()+cheminDossier+ "/",Integer.toString(message.getMessageNumber())+".mp3");	
					
					
					
					if(bodyPart.isMimeType("multipart/alternative")) // L'autre partie du mail divisé entre texte brut et html
					{
						Multipart mp2 = (Multipart)bodyPart.getContent(); //Meme principe on récupère le contenu 
						for(int k=0;k<mp2.getCount();k++) // Et on parse les différents sous partie du contenu
						{
							MimeBodyPart bodyPart2 = (MimeBodyPart)mp2.getBodyPart(k);
							if(bodyPart2.isMimeType("text/plain"))
							{
								unMessageInfo.numeroTelephone=getNumeroTelephoneContact((String)bodyPart2.getContent());  
								unMessageInfo.nbSecondes =Integer.parseInt(getDureeDuMessage((String)bodyPart2.getContent()));
							}
						}
						
					}
							
				}   
				
				message.setFlags(new Flags(Flags.Flag.SEEN),true);
				
				
				
				laListeDesNouveauxMessages.add(unMessageInfo);
			}

			
				
			


		} 
		catch (NoSuchProviderException e) {
			throw(e);
		} catch (MessagingException e) {
			throw(e);
		} catch (IOException e) {
			throw(e);
		}
		catch(Exception e)
		{
			throw(e);
		}



		return laListeDesNouveauxMessages;

	}

}