package simulator;

import java.util.ArrayList;

public class SRouting extends Probabilistic {

	/**
	 * @param args
	 */
	private final double alpha;
	private final double wantedprob;
	private static int oldtime=0;
	private final double lambda;
	private final double chkavg;
	private final int timelimit;
	
	public SRouting(Air a,double prob,double alpha,
			double wantedProb,double lambda,double chkavg,int giventimelimit){
		super(a,prob);
	
		this.alpha=alpha;
		this.lambda=lambda;
		this.chkavg=chkavg;
		wantedprob=wantedProb;
		timelimit=giventimelimit;
		
	}
	
	public void setSender(Node sn){
		super.setSender(sn);
	}
	
	public void setReceiver(Node rn){
		super.setReceiver(rn);
	}
	
	public double getWantedProb(){
		return wantedprob;
	}
	
	public double getAlpha(){
		return alpha;
	}
	
	public double getChkAvg(){
		return chkavg;
	}
	
	public void communicate(Node Sender,Node Receiver,String time){
		super.communicate(Sender,Receiver,time);
	}
	
	//overriden method
	public void send(String time){
		if(alpha== -1 && wantedprob == -1){
			System.out.println("Parameters are negative in SRouting.java");
			super.send(time);
			return;
		}
		Node s=getSender();
		Node r=getReceiver();
		
		sendToReceiver(s,r,time);
		sendToReceiver(r,s,time);
		
	}
	
	public void sendToReceiver(Node s,Node r,String time){
		
		double oldp=s.getProbToSend();
		setProb(oldp);
		
		double newprob=0;
		int timeconv=Integer.parseInt(time);
		s.setIdle(isNodeIdle(s,r, timeconv));
		if(s.isIdle()){
			newprob=f(oldp,getAlpha(),getWantedProb())*lambda;
		}else{
			newprob=f(oldp,getAlpha(),getWantedProb());	
		}
		
		/*
		if( ( s.getId()==43 && r.getId()==13 )|| ( r.getId()==43 && s.getId()==13 ) )
		System.out.println("Prob : "+newprob+"  sender: "+s.getId()+" receiver:"+r.getId()+" "+time+"  "+(timeconv-oldtime));
		//*/
		
		oldtime=timeconv;
		s.setProbToSend(newprob);
		
		setProb(newprob);
		//System.out.println(newprob);
		if(readytosend()){
			communicate(s,r,time);
		}
		
	}
	
	public final boolean isNodeIdle(Node sender,Node receiver,int time){
		
		Encounter e=new Encounter(sender.getId(),receiver.getId(),time);
		sender.addEncounter(e);
		if(sender.getSizeOfEncounter() ==3){
			
			double currentlast=sender.getEncEl(2).getTime() -sender.getEncEl(1).getTime();
			if(timelimit>0 && currentlast>timelimit){
				return false;
			}
			double lastprev=(sender.getEncEl(1).getTime()-sender.getEncEl(0).getTime());
			if(lastprev/currentlast > getChkAvg()+0.0){
				return true;//idle
			}
		}
		return false;
		
	}
	
	public static double f(double p1,double alpha,double wantedProb){
		return p1*alpha*alpha+(1-alpha)*wantedProb;
	}

	public final static boolean ifAllEncountersSame(Node sender){
		//if all encounters are with same person
		if(sender.getEncEl(2).getReceiverId() == sender.getEncEl(1).getReceiverId() &&
				sender.getEncEl(1).getReceiverId()==sender.getEncEl(0).getReceiverId()){
			return true;
		}
		return false;
	}
	
}
