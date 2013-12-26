package com.realogi.orangemvv;

import java.io.File;
import java.util.Date;

public class InformationMessage {
	
	public String numeroTelephone;
	public String nomFichierMp3;
	public boolean dejaLue;
	public Date laDateDuMessage; 
	public int numeroMessage;
	public int nbSecondes;
	
	public InformationMessage(int numeroMessage)
	{
		this.numeroMessage=numeroMessage;
		dejaLue=false;
	}
	
	
}
