package simulator;

public class DensityRouting extends Epidemic{

	/**
	 * @param args
	 */
	
	DensityRouting(Air a){
		super(a);
	}
	
	public void send(String timet) {
		if(getSender().getDensityAC()<=getSender().getNeighborCount()){
			communicate(getSender(),getReceiver(),timet);
		}
			getSender().setDensityAC(getSender().getNeighborCount());
		if(getReceiver().getDensityAC()<=getReceiver().getNeighborCount()){
			communicate(getReceiver(),getSender(),timet);
		}
			getReceiver().setDensityAC(getReceiver().getNeighborCount());
	}
}
