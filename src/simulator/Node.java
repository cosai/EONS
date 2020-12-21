package simulator;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Node extends Prophetable{

	private int id;
	private ArrayList<Message> messageBuffer;
	private ArrayList<Encounter> encList;
	private ArrayList<Integer> sents;//not used
	private int limit;//buffer limit
	private boolean isIdle;
	private String lastEnc;
	private String mempolicy="lrr";//message removing policy in memory
	private boolean gotNewPacket;
	private double probToSend;
	private int densityAC;
	//policy for deleting a message in the buffer if buffer is full
	
	
	Node(int givenid){
		super();
		id=givenid;
		encList=new ArrayList<Encounter>();
		messageBuffer=new ArrayList<Message>();
		isIdle=false;
		sents=new ArrayList<Integer>();//not used
		limit=-1;//buffer limit is infinite
		mempolicy="lrr";//least recently received
		lastEnc=null;
		gotNewPacket=false;
		probToSend=1;
		densityAC=0;
	}	
	
	public boolean gotNewPacket(){
		return gotNewPacket;
	}
	
	public void setGotNewPacket(boolean g){
		gotNewPacket=g;
	}
	
	//Density routing methods
	public int getDensityAC(){
		return densityAC;
	}
	
	public void setDensityAC(int ac){
		densityAC=ac;
	}
	//density routing
	
	public ArrayList<Message> getMessageBuffer(){
		return messageBuffer;
	}
	
	public int getId(){
		return id;
	}
	
	public boolean isIdle(){
		return isIdle;
	}
	
	public void setIdle(boolean b){
		isIdle=b;
	}
	
	public String getLastEncTime(){
		return lastEnc;
	}
	
	public void setLastEncTime(String s){
		lastEnc=s;
	}
	
	public String getMemPolicy(){
		return mempolicy;
	}
	
	public void setMemPolicy(String newPolicy){
		mempolicy=newPolicy;
	}
	
	boolean isBufferEmpty(){
		return messageBuffer.isEmpty();
	}
	
	public void setProbToSend(double p){
		probToSend=p;
	}
	
	public double getProbToSend(){
		return probToSend;
	}
	
	public boolean isBufferFull(){
		if(limit==-1)
			return false;
		if(limit>=messageBuffer.size()){
			return true;
		}
		return false;
	}
	
	//cleans the expired messages in the buffer
	/* not used!
	public void cleanExpired(String timestr){
		int time=Integer.parseInt(timestr);
		for(int i=0;i<messageBuffer.size();i++){
			int exp=Integer.parseInt(messageBuffer.get(i).getExpiration());
			if(exp != -1 && time>exp){
				messageBuffer.remove(i);
			}
		}		
	}
	//*/
	//adding received message to the buffer
	//buffer message replacement policy is implemented also
	boolean addMessageToBuffer(Message message,String time){	
		if(searchBufferMessageId(message.getId()) == -1){//message is not received before
			if(isBufferFull()){
				//delete the expired messages in the buffer
				for(int i=0;i<messageBuffer.size();i++){
					Message t=messageBuffer.get(i);
					if(t.isExpired(time)){
						deleteMessageFromBuffer(t.getId());
					}
				}
				
				if(isBufferFull()){
					/*the expired messages are deleted but there is still not enough space
					 the policy is least recently received which will be the first one in the buffer
					least recently encountered gives the best performance
					but it is not implemented yet (04/17/2015) See the article below to implement
					Davis, J.A.; Fagg, A.H.; Levine, B.N.,
					"Wearable computers as packet transport mechanisms in 
					highly-partitioned ad-hoc networks," Wearable Computers, 2001. 
					Proceedings. Fifth International Symposium on , vol., no., pp.141,148, 2001
					*/
					Reporter.bufferFull(getId(), time);
					String policy=getMemPolicy();
					if(policy.equals("lrr")){
						messageBuffer.remove(0);//remove the first one which is the oldest
					}else if(policy.equals("random")){
						/*
						Random r=new Random();
						int deletedPos=r.nextInt(limit);
						messageBuffer.remove(deletedPos);
						*/
					}
					
				}
				
			}
			messageBuffer.add(message);
			//we managed to add it to the buffer
		}
		//message is received before so do nothing
		return true;
	}
	
	//returns true if the message is deleted
	//false if the message is not found and not deleted
	public boolean deleteMessageFromBuffer(int messageid){
		if(isBufferEmpty()){
			System.out.println("Message buffer is empty, PROBLEM in Node.java ");
			return false;
		}
		
		for(int i=0;i<messageBuffer.size();i++){
			Message t=messageBuffer.get(i);
			if(t.getId()==messageid){
				messageBuffer.remove(i);
				//break;
				return true;
			}
		}
		return false;
	}
	
	//gets the message from the buffer where messageid is given in parameter
	public Message getMessageFromBuffer(int messageid){
		if(isBufferEmpty()){
			return null;
		}
		
		for(int i=0;i<messageBuffer.size();i++){
			Message t=messageBuffer.get(i);
			if(t.getId()==messageid){
				return t;
			}
		}
		return null;
	}
	
	public int searchBufferMessageId(int messageid){
		if(isBufferEmpty()){
			return -1;
		}
		for(int i=0;i<messageBuffer.size();i++){
			Message t=messageBuffer.get(i);
			if(t.getId()==messageid){
				return i;
			}
		}
		return -1;
	}
	
	/*
	public boolean sentToBefore(int receiverid){
		int pos=sents.indexOf(new Integer(receiverid));
		if(pos==-1){//node hasn't sent packet to this receiver before
			return false;
		}
		return true;
	}
	*/
	
	//return the all messages in the buffer
	public String getAllBuffer(){
		String sum="";
		for(int i=0;i<messageBuffer.size();i++){
			sum =sum+messageBuffer.get(i)+"\r\n***************\r\n";
		}
		if(sum.equals("")){
			return null;
		}
		
		return sum;
	}
	
	public String allids(){
		String all="";
		ArrayList<Integer> a=new ArrayList<Integer>();
		for(int y=0;y<messageBuffer.size();y++){
			int idm=messageBuffer.get(y).getId();
			if(!a.contains(Integer.valueOf(idm))){
				a.add(Integer.valueOf(idm));
				all=all+","+idm;
			}
		}
		if(messageBuffer.size() != a.size()){
			System.out.println("Node.java allids method problem");
		}
		return all;
	}
	
	//given a message vector this method is getting the id of the messages 
	//that doesn't exist in its buffer
	public ArrayList<Integer> vectorDifference(Message v){
		ArrayList<Integer> ret=new ArrayList<Integer>();
		ArrayList<Integer> messageIds=v.getVectorArray();
		
		if(isBufferEmpty()){
			// I have nothing in my buffer so I want all of the messages you have
			return messageIds;
		}
		
		
		for(int i=0;i<messageIds.size();i++){
			int mid=messageIds.get(i).intValue();
			if( searchBufferMessageId(mid) ==-1){
				ret.add(Integer.valueOf(mid));
			}
		}
		return ret;
	}
	
	//this method returns the string version of arraylist v with elements concatenated with :
	public static String getStringFromMessageVector(ArrayList<Integer> v){
		if(v.isEmpty()){
			return "all";
		}
	
		String all="";
		for(int i=0;i<v.size();i++){
			int el=v.get(i).intValue();
			all=all+el+":";
		}
		//remove the last ":" character from the all string
		all=all.substring(0,all.length()-1);
		return all;
	}
	
	//specify the tts with the message to be sent
	public void sendMessageFromBufferDiffTTS(Air a,int messageId,int receiverId,String time,int tts){
		Message found=getMessageFromBuffer(messageId);
		if(found == null){
			System.out.println("PROBLEM in Node.java. No message with that id");
		}else{
			//message found in buffer
			//now we will create a copy of the message if it is suitable to create a copy of it
			//TTS and expiration is checked
			if(found.isSendable(time)){
				found.decRemaining();
				Message sent=new Message(found.getPacketId(),
						this.getId(),//sender id
						receiverId,//message id
						found.getMessageText(),
						messageId,
						time,//creation time will be now which is time
						tts,
						found.getExpiration(),
						found.getHopCount()
						);
				sent.incHop();
				sendMessage(a,sent,time);
			}
		
		}
	}
	
	public boolean sendMessageFromBuffer(Air a,int messageId,int receiverId,String time){
		Message found=getMessageFromBuffer(messageId);
		if(found == null){
			System.out.println("PROBLEM in Node.java. No message with that id");
		}else{
			//message found in buffer
			//now we will create a copy of the message if it is suitable to create a copy of it
			//TTS and expiration is checked
			if(found.isSendable(time)){
				found.decRemaining();
				Message sent=new Message(found.getPacketId(),
						this.getId(),//sender id
						receiverId,//receiver id
						found.getMessageText(),
						messageId,
						time,//creation time will be now which is time
						found.getTTS(),
						found.getExpiration(),
						found.getHopCount()
						);
				sent.incHop();
				return sendMessage(a,sent,time);
			}else{
				System.out.println("PROBLEM in Node.java. message not sendable");
			}
			
		}
		return false;
	}
	
	//returns true if sent to air successfully
	//returns false if the packet is dropped
	public boolean sendMessage(Air air,Message m,String time){
		int receiverNodeId=m.getReceiver();
		if(air==null){
			System.out.println("air null problem in node.java");
		}
		
		//node sent the message. It may be dropped or received but it sent already
		
		Reporter.addPacketSent(getId(), receiverNodeId, time);
		
		if(air.addMessage(m) == false){
			Reporter.addPacketDropped(getId(), receiverNodeId, time);
			return false;
		}
	
		
		return true;
	}
	
	public boolean addtoBuffer(Message m,String time){
		if(m.getId()<0){
			System.out.println("PROBLEM in Node.java: Trying to add neg id message to buffer");
			return false;
		}
		
		boolean b=addMessageToBuffer(m, time);
		//this above function should return true as it is responsible for
		//replacement policy etc..
		if(b){
			Reporter.addPacketAddedToBuffer(m.getReceiver(),m.getSender(),time);
			return true;
		}
		//there is no like that message in the air
		// or the buffer is full
		//probably problem 
		return false;
	}
	
	public Message receiveMessage(Air air,String time){
		Message message=air.receiveMessage(getId(),time);
		if(message==null){
			//there is no message or message dropped
			return null;
		}
		Reporter.addPacketReceived(message.getSender(),message.getReceiver(),time);
		receiveRealMessage(message,time);
		return message;
		
	}		
	
	///internal private message to be used in receiveMessage Methods
	//this is too detailed message that shouldn't be called outside
	// this message decides if the message is protocol message or not
	//to store it in buffer
	private void receiveRealMessage(Message message,String time){
		if(message.getId() > 0 ){
			//if the message is real message
			//it means it is not a message vector
		
			boolean b=addtoBuffer(message, time);
		
			if(!b){
				System.out.println("Can not add to buffer PROBLEM in Node.java");
				//return null;//cant add to buffer so no message
				//maybe buffer is full and replacement cant be happened
				//this case is in fact practically shouldnt be possible
			}
		}
	}
		
	//this method returns the id of the messages that the node has as a string
	public String getMessageVector(String time){
		if(isBufferEmpty()){
			return "all";
		}
		ArrayList<Integer> v=new ArrayList<Integer>();
		for(int i=0;i<messageBuffer.size();i++){
			int el=messageBuffer.get(i).getId();
			if(!v.contains(Integer.valueOf(el)) && messageBuffer.get(i).isSendable(time)){
				v.add(el);
			}
		}
		//it is possible that the node's buffer is not empty but contains all expired messages
		return getStringFromMessageVector(v);
	}
	
	//this method returns the id of the messages that the node has created as a string
	public String getCreatedMessageVector(String time){
		if(isBufferEmpty()){
			return "all";
		}
		ArrayList<Integer> v=new ArrayList<Integer>();
		for(int i=0;i<messageBuffer.size();i++){
			int el=messageBuffer.get(i).getId();
			if(messageBuffer.get(i).getPrevPacketId()==-1){
				//it means the message is created by the node itself
				if(!v.contains(Integer.valueOf(el)) && messageBuffer.get(i).isSendable(time)){
					v.add(el);
				}
			}
			
		}
		//it is possible that the node's buffer is not empty but contains all expired messages
		return getStringFromMessageVector(v);
	}

	/**********Contact Related Methods******************/
	/**********Contact Related Methods******************/
	/**********Contact Related Methods******************/
	/**We are using Encounter object for recording current contacts
	 * This Encounter object is also used by PROPHET 
	 * It is advised that if PROPHET is going to be extended
	 * Don't use these methods as it will make an impact to consistency of the code*/
	public int getOldestContact(){
		if(encList.isEmpty()){
			return -1;
		}
		
		int min=encList.get(0).getTime();
		for(int i=1;i<encList.size();i++){
			if(encList.get(i).getTime()<min)
			{
				min=encList.get(i).getTime();
			}
		}
		return min;
	}
	
	public void addContact(int idcon,int time){
		for(int i=0;i<encList.size();i++){
			if(encList.get(i).getReceiverId()==idcon)
			{
				// already exists
				return;
			}
		}
		//it doesn't exist
		Encounter e=new Encounter(getId(),idcon,time);
		encList.add(e);
	}
	
	public void removeContact(int idcon){
		if(!encList.isEmpty()){
			for(int i=0;i<encList.size();i++){
				if(encList.get(i).getReceiverId()==idcon)
				{
					encList.remove(i);
					return;
				}
			}
			//System.out.println("Contact not found in Node.java removeContact");
		}else{
			//System.out.println("Contact List is empty. problem in Node.java"+id+" "+idcon+"time "+time);
			//possibly again intersecting contacts
			//we wont give an error that will make the intersecting contact like this
			// if this is the case
			// 1   4   15    48
			//4   1   36   42
			//it will consider that this happened (taking the early finish time)
			//1   4  15  42
			//System.exit(1);
		}
	}
	
	public boolean isInContactWith(int idcon){
		for(int i=0;i<encList.size();i++){
			if(encList.get(i).getReceiverId()==idcon)
			{
				return true;
			}
		}
		return false;
	}
	
	public int getNeighborCount(){
		return encList.size();
	}

	/**********Contact Related Methods******************/
	/**********Contact Related Methods******************/
	/**********Contact Related Methods******************/
	
	/***************************************************/
	/**********EAVESDROPPING METHODS********************/
	/***************************************************/
	//this method is for eavesdropping
	//node takes the all messages around and adds them to its buffer if they are
	//real messages not found in its buffer
	public void eavesdrop(Air air,String time){
		ArrayList<Message> allmes=air.receiveEnvironmentMessages(time);
		if(allmes==null || allmes.isEmpty()){
			//there is no message
			;
		}
		for(int i=0;i<allmes.size();i++){
			Message m=allmes.get(i);
			//if the node itself has sent that message
			//or if the message is some message that the receiver is the actual node
			//it shouldn't eavesdrop
			if(m.getReceiver() != getId() && m.getSender() != getId()){
				Reporter.addPacketReceived(m.getSender(),m.getReceiver(),time);
				//the node will receive all messages but 
				//add only the ones that are not in its buffer
				if(m.getId() > 0 ){
					//if the message is real message
					//it means it is not a message vector
				
					boolean b=addtoBuffer(m, time);
				
					if(!b){
						System.out.println("Can not add to buffer PROBLEM in Node.java");
						//return null;//cant add to buffer so no message
						//maybe buffer is full and replacement cant be happened
						//this case is in fact practically shouldnt be possible
					}
				}else{
					//Eavesdropping should only take messages with id>0
					//if this is not the case then there is problem.
					System.out.println("Problem in Node.java at method eavesdrop");
				}
				
				
			}//check if the node is receiver or sender of that message
		}		
	}
	
	/***************************************************/
	/**********EAVESDROPPING METHODS********************/
	/***************************************************/
	
	public String toString(){
		return "Node id: "+id+" message buffer size "+messageBuffer.size();
	}
}
