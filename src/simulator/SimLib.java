package simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

public class SimLib {

	/**
	 * Simulator class's Library functions that are dealing with
	 * arraylists, searching etc...
	 */

	public static int howManyReceived(ArrayList<Node> nodes,Message message){
		int count=0;
		for(int i=0; i<nodes.size(); i++){
			if(nodes.get(i).searchBufferMessageId(message.getId()) != -1){
				count++;
			}
		}
		return count;
		
	}
	
	public static String allbuf(ArrayList<Node> nodes){
		String sum="";
		for(int i=0; i<nodes.size(); i++){
			String s=nodes.get(i).getAllBuffer();
			if(s!=null){
				sum=sum+(i+1)+" node buffer "+s+"\r\n";
			}
		}
		return sum;
	}
	
	public static int findId(ArrayList<Node> nodes,int id){
		for(int i=0;i<nodes.size();i++){
			if(nodes.get(i).getId()==id){
				return i;
			}
		}
		return -1;
	}
	
	public static int exists(int[][] arr,int el){
		for(int i=0;i<arr.length;i++){
			if(arr[i][0]==el){
				return i;
			}
		}
		return -1;
	}
	
	public static ArrayList<Double> hopCounts(ArrayList<Node> nodes){
		ArrayList<Double> arr=new ArrayList<Double>();
		
		for(int i=0;i<nodes.size();i++){
			ArrayList<Message> buf=nodes.get(i).getMessageBuffer();
			if(buf !=null){
				for(int j=0;j<buf.size();j++){
					int hopn=buf.get(j).getHopCount();
					if(hopn != 0){
						arr.add(new Double((double) hopn));
					}
				}
			}//if buff not null
		}
		return arr; 
	}
	
	/*** Success rate related functions      ****************/
	public static ArrayList<Double> successRate(final int messageNumber,ArrayList<Node> nodes){
		double[] srate=new double[messageNumber];
		
		ArrayList<Double> sratearr=new ArrayList<Double>();
		for(int i=0;i<nodes.size();i++){
			ArrayList<Message> list=nodes.get(i).getMessageBuffer();
			if(list != null && !list.isEmpty()){
				for(int j=0;j<list.size();j++){
					srate[list.get(j).getId()-1]++;	
				}
			}
		}
		
		for(int i=0;i<srate.length;i++){
				srate[i]=srate[i]/nodes.size();
				if(srate[i]>1){
					System.out.println("PROBLEM in SimLib.java Success rate can not be greater than 1");
				}
				sratearr.add(new Double(srate[i]));
		}
		 

		Collections.sort(sratearr);
		return sratearr;		
	}
	
	public static void writeArrayListToFile(ArrayList<Double> srate,String fname){
		String all="";
		for(int i=0;i<srate.size();i++){
			all += Lib.precstr(Lib.prec(srate.get(i).doubleValue(),4),4)+"\r\n";
		}
		
		Reporter.writeToFile(fname, all);
	}
	
	public static double averageSuccessRate(ArrayList<Double> srate){
		double sum=0;
		
		for(int i=0;i<srate.size();i++){
			sum += srate.get(i).doubleValue();
		}
		return sum/srate.size();
		
	}
	
	/*********Success rate related functions  ***************/
	public static double[] MessageDelayArray(int numberOfMessages,ArrayList<Node> nodes){
		ArrayList<ArrayList<Message>> messageArr=new ArrayList<ArrayList<Message>>(); 
		for(int h=0;h<numberOfMessages;h++){
			messageArr.add(new ArrayList<Message>());
		}
		
		for(int i=0;i<nodes.size();i++){
			Node n=nodes.get(i);
			if( !n.isBufferEmpty() ){
				ArrayList<Message> m=n.getMessageBuffer();
				
				for(int j=0;j<m.size();j++){
					Message message=m.get(j);
					int pos=message.getId();
					messageArr.get(pos-1).add(message);			
				}
			}
		}
		
		
		double[] mdelay=new double[numberOfMessages];
		for(int i=0;i<messageArr.size();i++){
			ArrayList<Message> m=messageArr.get(i);
			//System.out.println(i);
			if(m != null && !m.isEmpty() && m.size() > 1){
				
				int firstPacketTime=0;
				
				boolean entered=false;
				
				//there should be at least one packet whose sender id is -1
				//stating that this is the first packet created
				//the others will be the sent ones
				for(int j=0;!entered && j<m.size();j++){
					if(m.get(j).getSender()<0)
					{
						firstPacketTime=Integer.parseInt(m.get(j).getTime());	
						entered=true;
						//System.out.println(i);
					}
				}
				if(!entered || firstPacketTime<0)
				{
					//packet sent time can not be less than 0
					System.out.println("PROBLEM in average Message Delay Calculation in SimLib.java");
				}
				
				int s=0;
				for(int j=0;j<m.size();j++){
					s=s+(Integer.parseInt(m.get(j).getTime()) - firstPacketTime);
				}
				
				if(s<=0){
					System.out.println("s less than zero in SimLib PROBLEM!!");
					for(int j=0;j<m.size();j++){
						System.out.println(m.get(j));
					}
				}
				
				mdelay[i]=(double)s/(double)(m.size()-1);	
				
			}//message is empty null >1 check
		}
		Arrays.sort(mdelay);
		
		return mdelay;
	}
	
	public static String paramexpshorter(String param){
		StringTokenizer st=new StringTokenizer(param," ");
		ArrayList<String> arr=new ArrayList<String>();
		while(st.hasMoreTokens()){
			arr.add(st.nextToken());
		}
		
		if(arr.get(1).equals("-1")){//ttl
			param=param.substring(6);
		}
		if(arr.get(3).equals("1.0")){//prob
			param=param.replace(" Probability 1.0", "");
		}
		double alpha=Double.parseDouble(arr.get(5));
		double pw=Double.parseDouble(arr.get(7));
		if(alpha<0 && pw<0){
			param=param.replace(" WantedProb "+pw, "");
			param=param.replace(" Alpha "+alpha, "");
		}
		
		if(arr.get(11).equals("0.0")){//lambda
			param=param.replace(" lambda 0.0", "");
		}
		
		if(arr.get(15).equals("0")){//timelimit
			param=param.replace(" timelimit 0", "");
		}
		
		if(arr.get(9).equals("false")){
			param=param.replace(" isProphet false", "");
		}else{
			param="PROPHET";
		}
		
		if(arr.get(13).equals("0.0")){//checkavg
			param=param.replace(" checkavg 0.0", "");
		}
		
		param=param.trim();
		if(param.length()==0){
			param="Epidemic";
		}
		if(alpha==-3 && pw==-3){
			param="Density_"+arr.get(11);
		}
		return param;
	}
	
	public static String shortparam(String param){
		StringTokenizer st=new StringTokenizer(param,"_");
		ArrayList<String> arr=new ArrayList<String>();
		while(st.hasMoreTokens()){
			arr.add(st.nextToken());
		}
		if(arr.get(1).equals("-1")){ //TTL
			param=param.substring(7);
		}
		double alpha=Double.parseDouble(arr.get(5));
		double pw=Double.parseDouble(arr.get(7));
		if(alpha<0 && pw<0){//alpha & wanted
			param=param.replace("_wantedProb_"+pw, "");
			param=param.replace("_alpha_"+alpha, "");
		}
		if(arr.get(3).equals("1.0")){//prob
			param=param.replace("_prob_1.0", "");
		}
		
		if(arr.get(11).equals("0.0")){//
			param=param.replace("_lambda_0.0", "");
		}
		
		if(arr.get(9).equals("false")){//isPROPHET
			param=param.replace("_isProphet_false", "");
		}else{
			param="PROPHET";
		}
		
		if(arr.get(13).equals("0.0")){//
			param=param.replace("_checkavg_0.0", "");
		}
		
		if(arr.get(15).equals("0")){//timelimit
			param=param.replace("_timelimit_0", "");
		}
		
		if(param.equals("_") || param.equals("") || param.equals("__")){
			param="Epidemic";
		}
		if(alpha==-3 && pw==-3){
			param="Density_"+arr.get(11);
		}
		return param;
	}
	
	public static void checkLine(int n1,int n2,int startTime,int endTime){
		if(n1 ==n2){
			System.out.println("Error in data for node with id "+n1+" encountered with itself");
			System.exit(-1);
		}
		if(startTime>endTime){
			System.out.println("Error for data "+n1+" "+n2);
			System.out.println("Encounter start time is greater than end time "+startTime+" "+endTime);
			System.exit(-1);
		}
		if(startTime <0 || endTime <0){
			System.out.println("Error for data "+n1+" "+n2);
			System.out.println("Either Encounter start time or end time is less than 0 "+startTime+" "+endTime);
			System.exit(-1);
		}
	}
	
	//returns true the lines are consistent
	//stops the execution if the data is not ordered.
	public static boolean checkLinesConsistency(String first,String second,int time){
		if(!second.equals(first)){
			StringTokenizer st=new StringTokenizer(first);
			int n1f=Integer.parseInt(st.nextToken());
			int n2f=Integer.parseInt(st.nextToken());
			int startTimef=Integer.parseInt(st.nextToken());
			int endTimef=Integer.parseInt(st.nextToken());
			
			st=new StringTokenizer(second);
			int n1s=Integer.parseInt(st.nextToken());
			int n2s=Integer.parseInt(st.nextToken());
			int startTimes=Integer.parseInt(st.nextToken());
			int endTimes=Integer.parseInt(st.nextToken());
			
			if(startTimes<startTimef){
				System.out.println("The text file ile not ordered in starttimes");
				System.out.println(first+"\n"+second);
				System.exit(-1);
			}
			
			if(n1s==n2f && n2s==n1f){
				if(startTimes<endTimef){
					//System.out.println("Problem at time "+(time)+" contacts intersecting"+n1s+" "+n2s);
					return false;
				}
			}
			
		}
		return true;
	}
	
	public static Node getNode(int id,ArrayList<Node> a){
		if(a==null || a.size()==0){
			return null;
		}
		for(int i=0;i<a.size();i++){
			if(a.get(i).getId()==id){
				return a.get(i);
			}
		}
		return null;
	}
	
	public static double getAverage(ArrayList<Double> arr){
		double sum=0;
		for(int i=0;i<arr.size();i++){
			sum=sum+arr.get(i).doubleValue();
		}
		return sum/arr.size();
	}
	
	private static Node searchrec(int id, ArrayList<Node> a, int lo, int hi) {
        // possible key indices in [lo, hi)
        if (hi <= lo) return null;
        int mid = lo + (hi - lo) / 2;
        int cmp = a.get(mid).getId()-id;
        if      (cmp > 0) return searchrec(id, a, lo, mid);
        else if (cmp < 0) return searchrec(id, a, mid+1, hi);
        else              return a.get(mid);
    }
	
	public static Node searchNode(int id, ArrayList<Node> a) {
        return searchrec(id, a, 0, a.size());
    }
	
	public static ArrayList<Double> remainings(ArrayList<Node> nodes){
		ArrayList<Double> arr=new ArrayList<Double>();
		
		for(int i=0;i<nodes.size();i++){
			ArrayList<Message> buf=nodes.get(i).getMessageBuffer();
			if(buf !=null){
				for(int j=0;j<buf.size();j++){
					int rem=buf.get(j).getRemaining();
					arr.add(new Double((double) rem));
				}
			}//if buff not null
		}
		return arr; 
	}
	
	/* NOt used
	public static void cleanExpiredMessages(ArrayList<Node> arr,int lasttime){
		for(int i=0;i<arr.size();i++){
			arr.get(i).cleanExpired(lasttime+"");
		}
	}
	*/
}
