package simulator;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SprayAndWait extends Epidemic{

	//private int L;
	
	public SprayAndWait(Air gair) {
		super(gair);
		
		// TODO Auto-generated constructor stub
	}

	//int getL(){
	//	return L;
	//}

	public void send(String timet) {
		communicate(getSender(),getReceiver(),timet);
		communicate(getReceiver(),getSender(),timet);
	}
	
	public void communicate(Node Sender,Node Receiver,String time){
		sendNonExistentMessage(getAir(),Sender,Receiver,time);
	}
	
	//sender is sending the ids of the messages it has
	//and receiving the messages
	private void sendNonExistentMessage(Air air,Node sender, Node receiver,String time){
		int senderid=sender.getId();
		int receiverid=receiver.getId();
		
		if(sender.isBufferEmpty()){
			return;
		}
		
		/* SENDER IS SENDING HIS MESSAGE VECTOR WHICH CAN BE SENT**/
		String v=sender.getMessageVector(time);
		if(v.equals("all")){
			//v is empty which means all the messages are expired in this nodes buffer
			//nothing to send...
			return;
		}
		
		//if there are messages that sender has sent to receiver before
		//it should not send it
		
		ArrayList<Integer> sentmessagesbefore=sender.getSentMessageIds(receiverid);
		if(sentmessagesbefore != null){
			//sender has sent something to this receiver
			//we need to remove the ones in v
			
			ArrayList<Integer> sents=new ArrayList<Integer>();
			StringTokenizer st=new StringTokenizer(v,":");
			while(st.hasMoreTokens()){
				int t=Integer.parseInt(st.nextToken());
				//if not sent before
				if(!sentmessagesbefore.contains(Integer.valueOf(t))){
					sents.add(Integer.valueOf(t));
				}
			}
			v=Node.getStringFromMessageVector(sents);
		}
		//sender hasn't send anything to this receiver before
		//no need to change anything
		if(v.equals("all")){
			//v is empty which means all the messages are expired in this nodes buffer
			//nothing to send...
			return;
		}
		
		
		int messageid=-1;//that means the message is vector and it won't be put to buffer
		//creating message Vector
		Message sentmes=new Message(senderid,receiverid,v,messageid,time);
		//try to send the message until it is received correctly 
		Message receivedmes=null;
		do{
			receivedmes=sendreceive(air,sender,receiver,sentmes,time);
		}while(receivedmes == null);
		/*   SENDER SENT THE MESSAGE VECTOR, RECEIVER GOT IT */
		
		
		ArrayList<Integer> vd=receiver.vectorDifference(receivedmes);
		
		if(vd.isEmpty()){
		//If the sender has the exactly same messages with receiver then dont do anything close communication
			return;
		}
		
		
		/* RECEIVER IS SENDING THE IDS THAT IT WANTS*/
		String mv=Node.getStringFromMessageVector(vd);
		Message replymessage=new Message(receiverid,senderid,mv,messageid,time);
		Message receivedreply=null;
		do{
			receivedreply=sendreceive(air,receiver,sender,replymessage,time);
		}while(receivedreply == null);
		/* RECEIVER SENT THE MESSAGE AND SENDER GOT IT */
		
		/* Sender is sending the messages ids stated in the packet */
		ArrayList<Integer> varray=receivedreply.getVectorArray();
		if(varray==null){
			System.out.println("Problem in SprayAndWait.java: This message shouldn't be empty");
			//if the receiver has sent a message it means it needs some messages
			//if it doesn't need it wont send any message at all
		}
		
		//sender has sent this messages to the receiver
		//no ack is used so no need to check
		
		sender.addSentMessages(receiverid, varray);
		
		for(int i=0;i<varray.size();i++){
			int id=varray.get(i).intValue();	
			
			Message found=sender.getMessageFromBuffer(id);
			
			if(found == null){
				System.out.println("PROBLEM in SprayAndWait.java. No message with that id");
			}else{
				//message found in buffer
				//now we will create a copy of the message if it is suitable to create a copy of it
				//TTL and expiration is checked
				if(found.isSendable(time)){
					int newtts=found.getTTS()/2;
					found.setRemaining(newtts);
					Message sent=new Message(found.getPacketId(),
							senderid,//sender id
							receiverid,//message id
							found.getMessageText(),
							id,
							time,//creation time will be now which is time
							newtts,
							found.getExpiration(),
							found.getHopCount()
							);
					sent.incHop();
					sender.sendMessage(air,sent,time);
					Message receivedMesTemp=receiver.receiveMessage(air, time);
				}			
			}
			
			
			
		}

	}
	
	private Message sendreceive(Air air,Node sender,Node receiver,Message sentmes,String time){
		sender.sendMessage(air, sentmes, time);	
		Message receivedmes=receiver.receiveMessage(air, time);
		return receivedmes;
	}
}
