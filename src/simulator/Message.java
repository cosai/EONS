package simulator;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Message {

	private int receiverId;
	private int senderId;
	private String message;
	private static int staticmesid=0;
	private int packetId;
	private int messageId;
	private String time;
	private int tts;
	private int remaining;
	private String expirationTime;
	private int hopCount;
	private int prevPacketId;
	
	//this is used in sending the vector packet in epidemic routing
	//also for asking to send the message again
	public Message(int sender,int receiver,String mes,int messageid,String timegiven){
		prevPacketId=-1;
		receiverId=receiver;
		senderId=sender;
		message=mes;
		time=timegiven;
		staticmesid++;
		packetId=staticmesid;
		messageId=messageid;
		remaining=-1;
		tts=-1;
		expirationTime="-1";
		hopCount=0;
	}
	
	/*//empty vector message
	public Message(int sender,int receiver,String timegiven){
		this(sender,receiver,"Empty Vector",-2,timegiven);
	}*/
	
	//real message
	Message(int gprevPacketId,int sender,int receiver,String mes,int messageid,
			String timegiven,int ttsgiven,String expirationgiven,int hopcount){
		prevPacketId=gprevPacketId;
		receiverId=receiver;
		senderId=sender;
		message=mes;
		time=timegiven;
		staticmesid++;
		packetId=staticmesid;
		messageId=messageid;
		tts=ttsgiven;
		//TTL Value is not changed but remaining is being changed.
		//this is for the reason that if TTL values are to be set differently
		//for the newly messages we would like to have data of the setted TTL
		//and the remaining tts for each of the messages
		remaining=tts;
		expirationTime=expirationgiven;
		hopCount=hopcount;
	}
	
	public String getMessageText(){
		return message;
	}
	
	public String getTime(){
		return time;
	}
	
	public int getReceiver(){
		return receiverId;	
	}
	
	public int getSender(){
		return senderId;
	}
	
	public int getId(){
		return messageId;
	}
	
	public int getHopCount(){
		return hopCount;
	}
	
	public String getExpiration(){
		return expirationTime;
	}
	
	public int getTTS(){
		return tts;
	}
	
	public void setRemaining(int remnew){
		remaining=remnew;
	}
	
	public int getRemaining(){
		if(remaining<0 && tts != -1)
				System.out.println("remaining value is less than 0 but tts is not infinite, PROBLEM in message.java");
		return remaining;
	}
	
	public boolean isTTSEnabled(){
		return tts>0;
	}
	
	public void incHop(){
		hopCount++;
	}
	
	public int getPacketId(){
		return packetId;
	}
	
	public int getPrevPacketId(){
		return  prevPacketId;
	}
	
	//decrease tts till 0
	//returns true if it decreased which means the message can be sent
	// if this returns false it means message can not be sent.
	public void decRemaining(){
		if(isTTSEnabled()){
			if(getRemaining()>0){
				remaining--;
			}else{
				System.out.println("Remaining is less than or equal to 0");
			}
		}		
	}
	
	//if remaining is more than 0 and isnotexpired
	//then it means message is sendable
	public boolean isSendable(String timegiven){
		if(!isExpired(timegiven)){
			if(isTTSEnabled()){
				if(getRemaining()>0){
					return true;
				}
			}else{
				//not expired and tts is not greater than 0
				//which means tts is disabled
				return true;
			}
		}
		return false;
	}
	
	public boolean isExpired(String timegiven){
		if(getExpiration().equals("-1")){
			//it will never expire
			return false;
		}else{
			int current=Integer.parseInt(timegiven);
			int expirationTime=Integer.parseInt(getExpiration());
			if(current>expirationTime){
				return true;
			}
			
		}
		
		return false;
	}
	
	public String toString(){
		return "sender "+getSender()+"\r\nreceiver " +getReceiver()+"\r\nid "+getId()+
				"\r\nmessage "+getMessageText()+"\r\nhop Count "+getHopCount()+"\r\ntts "+getTTS()+
				"\r\nExpiration Date "+getExpiration()+"\r\nCreation Time "+getTime();
	}
	
	public boolean equals(Message m){
		if(m.getMessageText()==getMessageText() && m.getReceiver()==getReceiver() 
				&& m.getSender()==getSender() && m.getId()==getId() && m.getTTS()==getTTS() 
				&& m.getExpiration()==getExpiration() && m.getPacketId()==getPacketId()){
			return true;
		}
		return false;
	}
	
	public boolean messageTextSame(Message m){
		if(m.getMessageText().equals(getMessageText()) ){
			return true;
		}
		return false;
	}
	
	//given there is a message that contains vector of message ids
	// that will return the ids in the int array
	public ArrayList<Integer> getVectorArray(){
		String s=getMessageText();
		ArrayList<Integer> ret=new ArrayList<Integer>();
		
		//if there are no messages in the text it means sender has no messages
		if(s.equals("all")){
			return null;
		}

		StringTokenizer st=new StringTokenizer(s,":");
		while(st.hasMoreTokens()){
			ret.add(Integer.valueOf(Integer.parseInt(st.nextToken())));
		}
		return ret;
	}
}
