package com.realogi.orangemvv.Fragment;


import java.util.List;

import com.realogi.orangemvv.R;
import com.realogi.orangemvv.Adapter.*;
import com.realogi.orangemvv.DAL.*;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class Liste_message_fragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    public List<T_Messages> lesMessagesAssocie;
    
    public Liste_message_fragment(List<T_Messages> lesMessages) 
    {
    	lesMessagesAssocie=lesMessages;
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
    	
    	 return inflater.inflate(R.layout.liste_message_fragment, container, false);
    }*/
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
       
    	
    	
    	ListView uneListeElement = new ListView(container.getContext());
    	
    	ListeMessageAdapter adapter = new ListeMessageAdapter(container.getContext(),lesMessagesAssocie);
    	uneListeElement.setAdapter(adapter);
    	return uneListeElement;
    	
       
    	
      //  return  inflater.inflate(R.layout.liste_message_fragment, container, false);
        
    }
}
