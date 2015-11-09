package simulator;

public class Probabilistic extends Epidemic{

	/**
	 * @param args
	 */
	double prob;
	
	public Probabilistic(Air gair,double p){
		super(gair);
		setProb(p);
	}
	
	public double getProb(){
		if(prob>1 || prob <0){
			System.out.println("Probability is greater than 1 or less than 0");
		}
		return prob;
	}
	
	public boolean readytosend(){
		if(getProb()==1){
			return true;
		}
		
		double rand=Math.random();
		if(rand<=getProb()){
			return true;
		}
		///System.out.println("NOT send");
		return false;
	}
	
	public void setProb(double pr){
		if(pr>1 || pr <0){
			System.out.println("Probability is greater than 1 or less than 0");
		}
		prob=pr;
	}
	
	public void send(String timet) {
		if(readytosend()){
			communicate(getSender(), getReceiver(), timet);
		}
		if(readytosend()){
			communicate(getReceiver(), getSender(), timet);
		}
		
	}
	
	public void communicate(Node Sender,Node Receiver,String time){
		super.communicate(Sender,Receiver,time);
	}

}
