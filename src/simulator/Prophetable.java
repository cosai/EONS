package simulator;

//PROPHET related functions 

import java.util.ArrayList;

public abstract class Prophetable extends Encountarable {
	private ArrayList<Encounter> propEnc;
	private double[] probToSend;
	
	
	public Prophetable(){
		super();
		propEnc=new ArrayList<Encounter>();
		probToSend=new double[300];
	}
	
	public int getPropEncSize(){
		return propEnc.size();
	}
	
	public Encounter getEncProp(int i){
		return propEnc.get(i);
	}
	
	public void addEncProp(Encounter e){
		propEnc.add(e);
	}
	
	public double getProbProp(int nodeid){
		if(nodeid<1 || nodeid>probToSend.length){
			System.out.println("Problem in getting the probability at Prophetable.java");
			return -1;
		}
		return probToSend[nodeid];
	}
	
	public void setProbProp(int nodeid,double p){
		if(p<=1 && p>=0 && nodeid>0 && nodeid <probToSend.length){
			probToSend[nodeid]=p;
		}else{
			System.out.println("Problem in setting prob in Node.java"+p+"  "+nodeid);
		}
	}
	
	public int sizeProbProp(){
		return probToSend.length;
	}
}
