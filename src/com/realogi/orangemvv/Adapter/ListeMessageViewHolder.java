package com.realogi.orangemvv.Adapter;


import android.media.MediaPlayer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListeMessageViewHolder
{
	 public TextView tvNomContact;
	 public TextView tvTypeContact;
	 public TextView tvDateAppel;
	 public TextView tvDureeAppel;
    
	 public LinearLayout layoutElement;



	 
	 
   


    public ListeMessageViewHolder() { }
    public ListeMessageViewHolder(TextView tvNomContact, TextView tvTypeContact, TextView tvDateAppel,TextView tvDureeAppel,LinearLayout layoutElement)
    {
        this.tvNomContact = tvNomContact;
        this.tvTypeContact = tvTypeContact;
        this.tvDateAppel = tvDateAppel;
        this.tvDureeAppel = tvDureeAppel;
        this.layoutElement = layoutElement;

    }
   
}  