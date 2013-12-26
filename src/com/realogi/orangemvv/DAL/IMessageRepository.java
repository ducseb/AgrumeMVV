package com.realogi.orangemvv.DAL;

import java.util.List;

import com.realogi.orangemvv.InformationMessage;

public interface IMessageRepository
{
	List<T_Messages> getAllMessageFromDB();	
    long ajouterUnMessage(InformationMessage leMessage);
}
