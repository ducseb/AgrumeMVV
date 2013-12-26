package com.realogi.orangemvv.DAL;

import java.util.Date;
import java.util.List;

import com.realogi.orangemvv.InformationMessage;

public class T_Messages {
	
	public int MES_I_ID;
	public int MES_I_ID_MAIL;
	public long MES_DT_DATE;
	public String MES_S_NUMERO;
	public String MES_S_CONTACT;
	public String MES_S_FICHIER;
	public int MES_I_LU;
	public int MES_I_NBSECONDE;

	
	public T_Messages(int MES_I_ID,int MES_I_ID_MAIL,long MES_DT_DATE,String MES_S_NUMERO,String MES_S_CONTACT,String MES_S_FICHIER,int MES_I_LU,int MES_I_NBSECONDE)
	{
		this.MES_I_ID=MES_I_ID;
		this.MES_I_ID_MAIL=MES_I_ID_MAIL;
		this.MES_DT_DATE=MES_DT_DATE;
		this.MES_S_NUMERO=MES_S_NUMERO;
		this.MES_S_CONTACT=MES_S_CONTACT;
		this.MES_S_FICHIER=MES_S_FICHIER;
		this.MES_I_LU=MES_I_LU;
		this.MES_I_NBSECONDE=MES_I_NBSECONDE;
	}
}
