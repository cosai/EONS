package simulator;

public abstract class Routing {

	private Air air;
	private Node sender,receiver;
	
	Routing(Air ag){
		air=ag;
		sender=null;
		receiver=null;
	}
	
	public void setSender(Node gs){
		sender=gs;
	}
	
	public void setReceiver(Node gr){
		receiver=gr;
	}
	
	
	public Node getSender(){
		if(sender==null){
			System.out.println("sender null");
		}
		return sender;
	}
	
	public Node getReceiver(){
		if(receiver==null){
			System.out.println("receiver null");
		}
		return receiver;
	}
	
	public Air getAir(){
		return air;
	}
	
	public abstract void send(String time);
	
	//what to do if sender encounters receiver
	//this method normally called once for sender as sender
	//once for receiver as sender in send(String time) method
	public abstract void communicate(Node Sender,Node Receiver,String time);
}
