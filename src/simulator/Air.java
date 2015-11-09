package simulator;
import java.util.*;

public class Air {


	private ArrayList<Message> messages;
	
	public Air(){
		messages=new ArrayList<Message>();
	}
	
	public boolean addMessage(Message mes){
		
		if(Erroneous.noError()){
			messages.add(mes);
			return true;
		}	
		return false;
		
	}
	
	//receive the message that is sent to id
	public Message receiveMessage(int id,String time){
		Message ret=null;
		Message checker=messages.get(messages.size()-1);//last message sent
		if(!messages.isEmpty() && checker.getTime().equals(time) &&
			checker.getReceiver()==id ){//messages sent newly
				ret=checker;
		}
		return ret;
	}

	//Eavesdropping message retrieval
	public ArrayList<Message> receiveEnvironmentMessages(String time){
		ArrayList<Message> ret=new ArrayList<Message>();
		for(int i=0;i<messages.size();i++){
			if(messages.get(i).getTime().equals(time) && messages.get(i).getId() != -1){
				//messages sent newly and also the ones that are 
				//real messages (not protocol related)
				ret.add(messages.get(i));
				
			}
		}
		//if we want eavesdropping property we shouldnt remove the packet

		return ret;
	}
}
