package simulator;

public class Encounter {

	//these integers are for the id of the nodes
	private int sender,receiver;
	private int time;
	private int encounterCount;
	
	Encounter(){
		sender=-1;
		receiver=-1;
		time=-1;
		encounterCount=1;
	}
	
	Encounter(int senderg,int receiverg,int timegiven){
		sender=senderg;
		receiver=receiverg;
		time=timegiven;
		encounterCount=1;
	}
	
	//copy constructor
	Encounter(Encounter e){
		sender=e.sender;
		receiver=e.receiver;
		time=e.time;
		encounterCount=e.encounterCount;
	}
	
	public int getTime(){
		return time;
	}
	
	public void setTime(int timet){
		time=timet;
	}
	
	public int getSenderId(){
		return sender;
	}
	
	public int getReceiverId(){
		return receiver;
	}
	
	public void incEncounterCount(){
		encounterCount++;
	}
	
	public int getEncounterCount(){
		return encounterCount;
	}
}
