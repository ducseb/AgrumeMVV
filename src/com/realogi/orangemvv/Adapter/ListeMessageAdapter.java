package com.realogi.orangemvv.Adapter;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.realogi.orangemvv.R;
import com.realogi.orangemvv.RechercheRepertoire;
import com.realogi.orangemvv.DAL.T_Messages;

import java.util.Date;






public class ListeMessageAdapter extends android.widget.BaseAdapter
{
	 private LayoutInflater inflater;  
     private List<T_Messages> items;
     
     
     public ListeMessageAdapter(Context context, List<T_Messages> items) 
     { 
    	 
    	 inflater = LayoutInflater.from(context);
    	 this.items = items;    	 
     }
   
     

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		MediaPlayer unMediaPLayer;
		
		// Planet to display  
   	 	T_Messages laReponEnCours = (T_Messages)this.getItem(position);

   	 	TextView tvNomContact;
   	 	TextView tvTypeContact;
        TextView tvDateAppel;
        TextView tvDureeAppel;
        
        LinearLayout layoutElement;
        
        
        final LinearLayout panelLecteur;
        
        
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.message_element, null);
            tvNomContact = (TextView)convertView.findViewById(R.id.message_element_nomcontact);
            tvTypeContact = (TextView)convertView.findViewById(R.id.message_element_typecontact);
            tvDateAppel = (TextView)convertView.findViewById(R.id.message_element_date);
            tvDureeAppel =(TextView)convertView.findViewById(R.id.message_element_duree);
             
            layoutElement = (LinearLayout)convertView.findViewById(R.id.message_element_layout);
            
            
            convertView.setTag(new  ListeMessageViewHolder(tvNomContact,tvTypeContact,tvDateAppel,tvDureeAppel,layoutElement));
        }
        else
        {
       	 ListeMessageViewHolder viewHolder = (ListeMessageViewHolder)convertView.getTag();
       	 tvNomContact = viewHolder.tvNomContact;
       	 tvTypeContact = viewHolder.tvTypeContact;
       	 tvDateAppel = viewHolder.tvDateAppel;
       	 tvDureeAppel = viewHolder.tvDureeAppel;
       	layoutElement=viewHolder.layoutElement;    
        }
        
   
        
        
        String nomContact = RechercheRepertoire.getContactDisplayNameByNumber(laReponEnCours.MES_S_NUMERO,convertView.getContext());
        if(nomContact!="?")
        {
        	tvNomContact.setText(nomContact);
        }
        else
        {
        	tvNomContact.setText(laReponEnCours.MES_S_NUMERO);
        }
        
        
        
        
        tvTypeContact.setText("--");
        Date laDateAppel = new Date(laReponEnCours.MES_DT_DATE);
        Date dateDuMatin = new Date();
        dateDuMatin.setHours(0);
        dateDuMatin.setMinutes(0);
        dateDuMatin.setSeconds(0);
        layoutElement.setTag(laReponEnCours.MES_S_FICHIER);
        
        String laDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat ("dd/MM/yyyy" );   
        if(laDateAppel.after(dateDuMatin)) formatter = new SimpleDateFormat ("HH:mm" );   
        laDate= formatter.format(laDateAppel);      
       
        
       
        tvDateAppel.setText(laDate);
        
        tvDureeAppel.setText(Integer.toString(laReponEnCours.MES_I_NBSECONDE)+"s");
        
      
        


        


        return convertView;
	} 
	
	
     
     
     
}
