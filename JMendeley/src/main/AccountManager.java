package main;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Response;
import org.scribe.model.Verb;

public class AccountManager {
	
	/** Our singleton object **/
	private static AccountManager _singleton = null;
	/** Our reference to the AuthenticationManager **/
	private AuthenticationManager _authManager = null;
	/** The Account object associated with this AccountManager **/
	private Account _account = null;
	
	
	private AccountManager(AuthenticationManager am) throws JSONException{
		
		this._authManager = am;
		this._account = this.requestAccount();
		
	}
	
	
	public static AccountManager getInstance (AuthenticationManager am) throws JSONException{
		
		return (_singleton == null) ? new AccountManager(am) : _singleton;
	}
	
	
	public Account getAccount(){ return this._account;}
	
	 /**
	  * This method needs the AuthenticationManager singleton to send a request and retrieves a 
	  * response that represents the account information for the authenticated user. The method 
	  * works with JSON objects in order to construct a valid Account and returns it.
	  * 
	  * @return
	  * @throws JSONException
	  */
	 private Account requestAccount() throws JSONException{
		 
		//Get the response back in this object.
		 Response response = this._authManager.sendRequest(Verb.GET, "http://api.mendeley.com/oapi/profiles/info/me/");
		 
		 Account result = null;
		 int statusCode = response.getCode();
		 
		 //Check for status code variability.
		 switch (statusCode){
		 case 401: throw new JSONException ("Status Code " + statusCode + ": Unauthorized request");
		 case 404: throw new JSONException ("Status Code " + statusCode + ": Not found");
		 case 200: break;
		 default: throw new JSONException ("Status Code " + statusCode + ": Unexpected Status Code");
		 }
		 		 
		 try {
			 
			 //Wrap in a JSON Object, and also, we only care about its "main" section. 
			 JSONObject results = new JSONObject(response.getBody());
			 JSONObject main = results.getJSONObject("main");
		
			 String profileid, name, academic_status, research_interests, discipline, url;
			 
			 //Gather the <key,value> pairs found inside the JSON Object.
			 profileid = this.getValueFromJSONObject(main, "profile_id");
			 name = this.getValueFromJSONObject(main, "name");
			 academic_status = this.getValueFromJSONObject(main, "academic_status");
			 research_interests = this.getValueFromJSONObject(main, "research_interests");
			 discipline = this.getValueFromJSONObject(main, "discipline_name");
			 url = this.getValueFromJSONObject(main, "url");
		
			 //Create an Account object and return it.
			 result = new Account(profileid, name, academic_status, discipline, research_interests, url);
			 return result;
			 
		 } catch (JSONException e){ System.err.println(e);}
		 
		 //It didn't work - return null.
		 return null;
	 }
	
	 
	 public String getValueFromJSONObject (JSONObject json, String key){
		 
		 try { return json.getString(key);}
		 catch (JSONException e){
			 
			 return "";
		 }
	 }
	
	public String toString(){
		
		return "Account::= " + this._account;
		
	}
	

}