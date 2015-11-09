package simulator;
/*
 * NOT USED
 * May need to be revised again
 * May not be working as expected
 */
public class RRouting extends SRouting {
	
	int contactLimit=7;
	
	public RRouting(Air a,double prob,double alpha,double wantedProb,double idleparam){
		super(a, prob, alpha, wantedProb, 1,idleparam,0);
		contactLimit=(int)idleparam;
		if(getSender().getEncounterLimit() != contactLimit){
			getSender().setEncounterLimit(contactLimit);
		}
		if(getReceiver().getEncounterLimit() != contactLimit) {
			getReceiver().setEncounterLimit(contactLimit);
		}
		
	}
	
	
	public static double f(int encountern){
		if(encountern >= 5){
			return 0.5;
		}
		return 1-0.1*encountern;
		
	}

	public void send(String time){
		if(getAlpha()== -1 && getWantedProb() == -1){
			super.send(time);
			return;
		}
		Node s=getSender();
		Node r=getReceiver();
		sendToReceiver(s,r,time);
		sendToReceiver(r,s,time);
		
	}
	
	public void sendToReceiver(Node s,Node r,String time){
		double oldp=getProb();
		
		double newprob=0;
		int timeconv=Integer.parseInt(time);
		s.setEncounterLimit(contactLimit);
		boolean b=s.enqueueAgain(r.getId(), timeconv);
		Encounter e=null;
		if(!b){
			e=new Encounter(s.getId(),r.getId(),timeconv);
			e.incEncounterCount();
			s.addEncounter(e);
		}
		newprob=f(e.getEncounterCount());
		
		setProb(newprob);
		if(readytosend()){
			communicate(s,r,time);
		}
	}
}
