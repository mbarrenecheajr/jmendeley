package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.json.JSONException;

import util.JMendeleyUIUtils;

public class Main {
	
	/** Consumer Key for accessing Mendeley API **/
	private static final String CONSUMER_KEY = "cfc24e1782a13e619030a531177df76504f811506";
	/** Consumer Secret for accessing Mendeley API **/
	private static final String CONSUMER_SECRET = "f7aac3649ec6f18f7d7a2cc7c3f7f3d9";
	
	protected static Scanner infile = null;
	
	public static void main(String args[]) throws FileNotFoundException, IOException, JSONException {
		// start up UI, ask user for info, etc
		// for now, just connect to the mand
		
		//Instantiate the singleton Authentication Manager
		AuthenticationManager am = AuthenticationManager.getInstance(CONSUMER_KEY, CONSUMER_SECRET);
		
		if (am.connectToMendeley() == true){
			
			//Create the AccountManager singleton that will use the AuthenticationManager to search
			//for the profile information (Account) for the authenticated user.
			AccountManager acm = AccountManager.getInstance(am);			
			
			if (acm.getAccount() == null){
				 
				JMendeleyUIUtils.showMessageDialog("Account unable to be retrieved. You will be re-prompted to sign into Mendeley.", 
						"Corrupted JMendeley Token File", JOptionPane.WARNING_MESSAGE);
				am.deleteFile();
				
				//Restart the main function.
				main(args);
				return;
			}
			
			//Instantiate the singleton Search Manager that is responsible for preparing and performing API searches.
			SearchManager sm = SearchManager.getInstance(acm, am);
			
			//Load up the GUI.
			SearchView.loadView(sm, acm, am);
			
		}//end main
		else
			System.exit(1);
	}
	
}
