package simulator;
import java.util.ArrayList;

public abstract class Encountarable extends sentTracker{

	private ArrayList<Encounter> encounters;
	private int encounterLimit;
	private String policy;
	private int numberOfEncounters;
	private double avg;
	
	Encountarable(){
		super();
		encounters=new ArrayList<Encounter>();
		encounterLimit=-1;
		policy="oldestencountered";//encounter add policy
		numberOfEncounters=0;
		avg=0;
	}
	
	public double getAvg(){
		return avg;
	}
	
	public void updateAvg(){
		double sum=0;
		int limit=encounters.size();
		if(getEncounterLimit() != -1){
			limit=getEncounterLimit();
		}
		int numelements=0;
		for(int i=1;i<limit && i<encounters.size();i++){
			sum=sum+(encounters.get(i).getTime()-encounters.get(i-1).getTime());
			numelements++;
		}
		avg=sum/(numelements);
		
	}
	
	public void incNumberOfEncounters(){
		numberOfEncounters++;
	}
	
	public int getNumberOfEncounters(){
		return numberOfEncounters;
	}
	
	public int getSizeOfEncounter(){
		return encounters.size();
	}	
	
	public void setEncounterLimit(int enc){
		if(enc==1){
			System.out.println("encounter limit 1 is not possible & practical");
			System.exit(-1);
		}
		encounterLimit=enc;
	}
	
	public int getEncounterLimit(){
		return encounterLimit;
	}
	
	public void addEncounter(Encounter enc){
		//System.out.println(encounters.size()+"  "+encounterLimit);
		encounters.add(enc);
		if(encounterLimit != -1 && encounters.size()>encounterLimit){
			//removal policy
			int removalnodepos=0;
			if(policy.equals("oldestencountered")){
				removalnodepos=0;								
			}else if(policy.equals("leastencountered")){
				
				int limit=encounters.size();
				if(getEncounterLimit() != -1){
					limit=getEncounterLimit();
				}
				
				int minpos=0;
				
				for(int i=1;i<limit && i<encounters.size();i++){
					if( encounters.get(i).getEncounterCount()<encounters.get(minpos).getEncounterCount())
					{
						minpos=i;
					}
				}
				//position i is to be deleted
				
				removalnodepos=minpos;
			}
			//System.out.println("removalnodepos "+removalnodepos);
			encounters.remove(removalnodepos);
		}
		
	}
	
	public Encounter getEncEl(int i){
		if(getEncounterLimit() != -1){
			if(i>getEncounterLimit()){
				return null;//trying to access more elements than the limit provided
			}
		}
		
		if(i>=0 && i<encounters.size()){
			return encounters.get(i);
		}
		//problem
		return null;
	}
	
	public Encounter searchEncounters(int id){
		int limit=encounters.size();
		if(getEncounterLimit() != -1){
			limit=getEncounterLimit();
		}
		for(int i=0;i<limit && i<encounters.size();i++){
			if( encounters.get(i).getReceiverId()==id || encounters.get(i).getSenderId()==id){
				return encounters.get(i);
			}
		}
		return null;
	}

	public boolean enqueueAgain(int id,int time){
		int limit=encounters.size();
		if(getEncounterLimit() != -1){
			limit=getEncounterLimit();
		}
		for(int i=0;i<limit && i<encounters.size();i++){
			if( encounters.get(i).getReceiverId()==id || encounters.get(i).getSenderId()==id){
				Encounter e=new Encounter(encounters.get(i));
				encounters.remove(i);
				e.incEncounterCount();
				e.setTime(time);
				encounters.add(e);
				return true;
			}
		}
		return false;
	}

}
