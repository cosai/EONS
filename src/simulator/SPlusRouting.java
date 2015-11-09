package simulator;
/*
 * 
 * NOT USED
 */
public class SPlusRouting extends Probabilistic {

	private double alpha;
	private double wantedprob;
	private double checkavg;
	private static int oldtime=0;
	private int maxNum;
	private int minNum;
	
	public SPlusRouting(Air a,Node sender,Node receiver,
				double prob,double alpha,double wantedProb,int maxNumberOfEnc,double checkavg,int minnum){
		super(a,prob);
		
		this.alpha=alpha;
		wantedprob=wantedProb;
		maxNum=maxNumberOfEnc;
		this.checkavg=checkavg;
		minNum=minnum;
		if(minNum<0){
			System.out.println("Minimum number of encounters can not be less than zero");
			System.exit(-1);
		}
		getSender().setEncounterLimit(maxNum);
		getReceiver().setEncounterLimit(maxNum);
	}
	
	public double getWantedProb(){
		return wantedprob;
	}
	
	public double getAlpha(){
		return alpha;
	}
	
	public double getCheckAvg(){
		return checkavg;
	}
		
	//overriden method
	public void send(String time){
		if(alpha== -1 && wantedprob == -1){
			super.send(time);
			return;
		}
		Node s=getSender();
		Node r=getReceiver();
		if(s.getId()==20 || r.getId()==20){
			
			System.out.println(s.getId()+"\t"+r.getId()+"\t"+time);
		}
		sendToReceiver(s,r,time);
		sendToReceiver(r,s,time);
	}
	
	public void sendToReceiver(Node s,Node r,String time){
		double oldp=getProb();
		
		double newprob=0;
		int timeconv=Integer.parseInt(time);
		
		Encounter e=new Encounter(s.getId(),r.getId(),timeconv);
		s.addEncounter(e);
		s.incNumberOfEncounters();
		if(s.getNumberOfEncounters() < minNum ){
			s.setIdle(false);
			s.updateAvg();
			
		}else{
			boolean changed=isThereChange(s);
			if(changed && s.isIdle()){
				s.setIdle(false);
			}
			if(changed && !s.isIdle()){
				s.setIdle(true);
			}
		}
		if(s.isIdle()){
			
			newprob=f(oldp,getAlpha(),getWantedProb());
		}else{
			newprob=oldp;
			
		}
		
		/*
		if( ( s.getId()==43 && r.getId()==13 )|| ( r.getId()==43 && s.getId()==13 ) )
		System.out.println("Prob : "+newprob+"  sender: "+s.getId()+" receiver:"+r.getId()+" "+time+"  "+(timeconv-oldtime));
		//*/
		
		oldtime=timeconv;
		setProb(newprob);
		if(readytosend()){
			communicate(s,r,time);
		}
	}
	
	public final boolean isThereChange(Node sender){
		
		double avgold=sender.getAvg();
		sender.updateAvg();
		double avgnew=sender.getAvg();
		double abs=Math.abs(avgnew-avgold);
		if(abs>avgold*checkavg){
			return true;
		}
		return false;
	}
	
	public static double f(double p1,double alpha,double wantedProb){
		return p1*alpha*alpha+(1-alpha)*wantedProb;
	}
}
