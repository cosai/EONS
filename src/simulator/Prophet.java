package simulator;

public class Prophet extends Probabilistic {

	double gamma;
	double beta;
	double pinit;
	
	Prophet(Air ag, double gamma,double beta,double pinit) {
		super(ag,pinit);
		this.gamma=gamma;
		this.beta=beta;
		this.pinit=pinit;
	}

	@Override
	public void send(String time) {
		Node s=getSender();
		Node r=getReceiver();
		
		double p=updateProbabilities(s,r.getId(),time);
		setProb(p);
		super.send(time);
		//this will  be the last action to be done
		s.setLastEncTime(time);
	}
	
	private double updateProbabilities(Node sender,int receiverid,String current){
		int cur=Integer.parseInt(current);
		String laststr=sender.getLastEncTime();
		int k=0;
		if(laststr != null){
			int last=Integer.parseInt(laststr);
			k=cur-last;
			//if there is no last encounter k stays at 0
		}
		double returned=0;
		for(int i=1;i<sender.sizeProbProp();i++){
			double val=0;
			if(i==receiverid){
				//update this node's prob
				val=updatedVal( sender.getProbProp(i) );
				returned=val;
			}else{
				//age other ones
				val=age(sender.getProbProp(i) ,k);
			}
			sender.setProbProp(i, val );
		}
		return returned;
	}
	
	private double age(double pold,int k){
		return Math.pow(gamma, k) *pold;
	}
	
	private double updatedVal(double pold){
		return pold+(1-pold)*pinit;
	}
}
