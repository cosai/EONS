package simulator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.StringTokenizer;

public class Simulator {

	/**
	 * @param args
	 */
	static ArrayList<Node> nodes;
	static int tts=0;
	static double prob=0;
	static String fname=null;
	static int numberOfNodes;
	static int numberOfMessages,numberOfStarterNodes;
	static int numberOfBroadcasters;
	static double alpha;
	static double wantedProb;
	static String timeatrun;
	static boolean isProphet;
	static double checkavg;
	static Air air;
	static ArrayList<String> data;
	static int checktime=300;
	static String param,reporttoWrite,basicparam;
	static int lasttime;
	static int numberOfPingPong=0;
	static int allDataCount=0;
	static double lambda;
	static Routing r=null;
	static int timelimit;
	
	// in every 300 seconds the nodes will check if 
	//they are in contact with some other node
	
	public static void main(String[] args) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		long start=calendar.getTimeInMillis();
		
		initarguements(args);
		
		param="tts_"+tts+"_prob_"+prob+"_alpha_"+alpha+"_wantedProb_"+wantedProb+
				"_isProphet_"+isProphet+"_lambda_"+lambda+"_checkavg_"
				+checkavg+"_timelimit_"+timelimit;
		param=SimLib.shortparam(param);
		
		reporttoWrite="TTS "+tts+" Probability "+prob+" Alpha "+alpha+
	" WantedProb "+wantedProb+" isProphet "+isProphet+" lambda "
				+lambda+" checkavg "+checkavg+" timelimit "+timelimit;
		basicparam="TTS "+tts+" Probability "+prob+" Alpha "+alpha+" WantedProb "+
	wantedProb+" isProphet "+isProphet+" lambda "+lambda
				+" checkavg "+checkavg+" timelimit "+timelimit;
		reporttoWrite=SimLib.paramexpshorter(reporttoWrite);
		basicparam=SimLib.paramexpshorter(basicparam);
		
		data=Reporter.readTrace(fname);
		Reporter.init(param);
		
		lasttime=getLastTime();
		
		//ArrayList<String> datagiven=null;
		
		run(data);
		
		
		//System.out.println("Ratio of Ping Pong entries: "+
		//numberOfPingPong/(double)allDataCount);
		System.out.println("Simulation finished\n");
		calendar.setTime(new Date());
		long fin=calendar.getTimeInMillis();
		System.out.println("Simulation took "+((fin-start)/1000.0D)+" seconds to run");
		
	
	}
	
	private static void initarguements(String[] args) {

		isProphet=false;
		if(args.length==0){
			fname="traceStAndrews.txt";
			tts=-1;
			prob=1;
			numberOfNodes=-1;
			numberOfMessages=100;	
			numberOfBroadcasters=numberOfNodes;
			alpha=-1;
			wantedProb=-1;
			checkavg=0;
			lambda=0;
			timelimit=0;
			/*
			prob=0.75;
			alpha=0.98;
			wantedProb=0.25;
			isProphet=true;
			//*/
			//if both alpha and wantedProb is -1
			//then it will be rough probabilistic or epidemic
		}else{
			if(args.length <12){
				System.out.println("Too less parameters");
				System.exit(1);
			}
			fname=args[0];
			
			tts=Integer.parseInt(args[1]);
			prob=Double.parseDouble(args[2]);
			numberOfNodes=Integer.parseInt(args[3]);
			numberOfMessages=Integer.parseInt(args[4]);
			numberOfBroadcasters=Integer.parseInt(args[5]);
			alpha=Double.parseDouble(args[6]);
			wantedProb=Double.parseDouble(args[7]);
			String Prophet=args[8];
			lambda=Double.parseDouble(args[9]);
			checkavg=Double.parseDouble(args[10]);
			timelimit=Integer.parseInt(args[11]);
			if(Prophet.equals("1") || Prophet.equals("1.0")){
				isProphet=true;
			}
			
		}
		
	}

	public static void p(String x){
		System.out.println(x);
	}
	
	public static void route(Node node1,Node node2,int time){
		r.setReceiver(node2);
		r.setSender(node1);
		r.send(time+"");
	}
	
	public static void initRouter(){
		if(alpha==-1 && wantedProb==-1 && prob==1){
			r=new Epidemic(air);
		}else if(alpha == -1 && wantedProb==-1){
			r=new Probabilistic(air, prob);
		}else if(isProphet){
			r=new Prophet(air, alpha, wantedProb, prob);
			for(int i=0;i<nodes.size();i++){
				for(int j=0;j<nodes.size();j++){
					nodes.get(i).setProbProp(j+1, prob);
				}
			}
		}else if(alpha==-2 && wantedProb==-2 && prob==1){
			r=new SprayAndWait(air);
		}else if(alpha==-3 && wantedProb==-3 && prob==1){
			r=new DensityRouting(air);
			for(int i=0;i<nodes.size();i++){
				nodes.get(i).setDensityAC((int)lambda);
			}
		}else{//our model
			r=new SRouting(air,prob,alpha,wantedProb,lambda,checkavg,timelimit);
			for(int i=0;i<nodes.size();i++){
				nodes.get(i).setEncounterLimit(3);
				nodes.get(i).setProbToSend(prob);
			}
		}
	}
	
	public static void nodesfix(ArrayList<String> datasim){
		if(numberOfNodes != -1){
			for(int i=0;i<numberOfNodes;i++){
				Node n=new Node(i+1);	
				nodes.add(n);
			}
		}else{
			for(int i=0;i<datasim.size();i++){
				StringTokenizer st=new StringTokenizer(datasim.get(i));
				int n1=Integer.parseInt(st.nextToken());
				int n2=Integer.parseInt(st.nextToken());
				
				if(SimLib.getNode(n1, nodes) == null )
				{
					nodes.add(new Node(n1));
				}
				if(SimLib.getNode(n2, nodes)==null)
				{
					nodes.add(new Node(n2));
				}
			}
		}
		//System.out.println(nodes.size());
		Collections.sort(nodes, new Comparator<Node>(){
		    public int compare(Node s1, Node s2) {
		        return s1.getId()-s2.getId();
		    }
		});
	}
	
	public static int TimesGiven(ArrayList<String> datasim){
		String last=datasim.get(0);
		StringTokenizer st1=new StringTokenizer(last);
		st1.nextToken();		st1.nextToken();		
		String ltime=st1.nextToken();//parsing the start time
		int res=0;
		try{
			res=Integer.parseInt(ltime);
			return -1;
			//start time is in integer format. 
			//we will return -1 stating that start time can be 0
		}catch(Exception e){
			return Lib.cdate(ltime);
		}
	}
	
	public static int getLastTime(){
		String last=data.get(data.size()-1);
		StringTokenizer st1=new StringTokenizer(last);
		st1.nextToken();		st1.nextToken();		st1.nextToken();
		String ltime=st1.nextToken();
		int res=0;
		try{
			res=Integer.parseInt(ltime);
		}catch(Exception e){
			res=Lib.cdate(ltime);
		}
		return res;
	}
	
	public static void run(ArrayList<String> datasim){

		nodes=new ArrayList<Node>();
		
		air=new Air();
		nodesfix(datasim);
		initRouter();
		
		//Filling the nodes ArrayList
		
		
		if(numberOfBroadcasters == -1 ){
			numberOfBroadcasters=nodes.size();
		}
		
		int[][] posarr=Lib.posarr(numberOfMessages, lasttime,numberOfBroadcasters,nodes);
		
		
		int messageNumber=0;
		Message message = null;
		int time=TimesGiven(datasim);	
		if(time==-1){
			time=0;
		}
		
		for(;time<=lasttime;time++){
			for(int i=0;i<datasim.size();i++){
				StringTokenizer st=new StringTokenizer(datasim.get(i));
				int n1=Integer.parseInt(st.nextToken());
				int n2=Integer.parseInt(st.nextToken());
				String startstr=st.nextToken();
				String endstr=st.nextToken();
				int startTime=0;
				int endTime=0;
				try{
					startTime=Integer.parseInt(startstr);
					endTime=Integer.parseInt(endstr);
				}catch(Exception e){
					p("Can not parse Integers, Assuming dates");
					//if it can not parse then we assume the dates are in string format
					startTime=Lib.cdate(startstr);
					endTime=Lib.cdate(endstr);
				}
				Node node1=SimLib.searchNode(n1, nodes);
				Node node2=SimLib.searchNode(n2, nodes);
				
				//syntax check for the line
				SimLib.checkLine(n1,n2,startTime,endTime);
				
				String first=datasim.get(i);
				String second="";
				if(i==datasim.size()-1 || datasim.size()==1){
					second=first;
				}else{
					second=datasim.get(i+1);
				}
				boolean resultOfCheck=SimLib.checkLinesConsistency(first,second,time);
				
				if(resultOfCheck==false){
					//it means this line is an intersecting encounter
					//we will remove that line without making contact between nodes.
					numberOfPingPong++;
					//p(datasim.get(i));
					datasim.remove(i);
					i--;//very important
				}else if(time<startTime){
					break;
				}else if(startTime==endTime && startTime==time){
					//If the contact time is less than one second
					node1.addContact(n2,time);
					node2.addContact(n1,time);
					route(node1,node2,time);
					node1.removeContact(n2);
					node2.removeContact(n1);
					
					//we are finished with this line we can remove that now
					datasim.remove(i);
					i--;//very important
				}else if(time>=endTime){
					//remove that line
					//contact ended
					node1.removeContact(n2);
					node2.removeContact(n1);
					datasim.remove(i);
					i--;//very important
					//as we remove the position i the arraylist shifts and bypasses the next value
				}else if(time==startTime){

					if(node1.isInContactWith(n2) && node2.isInContactWith(n1)){
						numberOfPingPong++;
						continue;
						//it means the file is problematic (ping pong effect)
						//ignore for now.
						// id1 	id2  start end
						//1  2   15    78
						//2  1   12    45
						//we will behave as if the data is like this:
						//1	 2   12    78
						
					}
					node1.addContact(n2,time);
					node2.addContact(n1,time);
					
					route(node1,node2,time);
					
					//Reporter.writeToFile("Probab.txt",((Probabilistic)r).getProb()+"\r\n");
					//System.out.println(((Probabilistic)r).getProb());
				}else{
					//time is greater than start time less than or equal to endTime
					//in every 5 minutes while contact continues with another node
					//try to exchange packets to see if there is a new one
					if(time % checktime==0){
						//if the previous contact doesn't continue still then there is a problem
						if(!node1.isInContactWith(n2) && !node2.isInContactWith(n1)){
							//System.out.println(time+"PROBLEM. 
							//They should be in contact still "+n1+" "+n2+" "+startTime+"  "+endTime);
							//this means intersecting contacts
							//we wont give an error that will make the intersecting contact like this
							// if this is the case
							// 1   4   15    48
							//4   1   36   42
							//it will consider that this happened (taking the early finish time)
							//1   4  15  42
							//we will skip this operation
						node1.addContact(n2,time);
						node2.addContact(n1,time);
						numberOfPingPong++;
						}
						
						
						route(node1,node2,time);
							
					}
				}//end of if checks
				
			}//end of inner for loop
			
			int generatorNodeId=-1;
			
			//searching current time in the list of messages and their times to be created
			int posOfProbArr=SimLib.exists(posarr, time);
			if(posOfProbArr != -1){
				generatorNodeId=posarr[posOfProbArr][1];
				messageNumber++;
				//48 hours Expiration time
				String expiration=(posarr[posOfProbArr][0]+3600*24*2)+"";
				int prevPacketId=-1;
				int hopcount=0;
				int senderId=-1;
				//as the message is generated by the node the sender will be -1
				//receiver will be the node itself.
				message=new Message(prevPacketId, senderId,generatorNodeId, "This is The Message "+messageNumber,messageNumber, time+"",tts,expiration,hopcount);
				
				
				if((posarr[posOfProbArr][0]+3600*24*2)<=time){
					System.out.println("PROBLEM for expiration");
				}
				
				Node searchedsender=SimLib.searchNode(generatorNodeId, nodes);
				if(searchedsender==null){System.out.println("PROBLEM IN FINDING");}
				
				
				if(searchedsender.getId() != generatorNodeId){
					System.out.println("PROBLEM in finding correct node at Simulator.java "+searchedsender.getId()+" "+generatorNodeId);
				}
				
				boolean cond=searchedsender.addMessageToBuffer(message, time+"");
				if(cond==false){
					System.out.println("Can not add newly created message to buffer error at Simulator.java");
				}
			}
			
		}//end of outer loop

		/*
		for(int h=0;h<nodes.size();h++){
			System.out.println(nodes.get(h).allids());
		}
		System.out.println(nodes.size());
		//*/
		//clean all expired messages in the nodes
		//SimLib.cleanExpiredMessages(nodes,lasttime);
		
		//System.out.println(basicparam);
		//Reporter.writeConsolePacketInfo();

		
		ArrayList<Double> successRate=SimLib.successRate(numberOfMessages,nodes);
		double[] amdarray=SimLib.MessageDelayArray(numberOfMessages, nodes);
		//average message delay calculation
		double amd=Lib.mean(amdarray);
		amd=amd/3600;//convert to hour
		
		//hop count calculation
		ArrayList<Double> hc=SimLib.hopCounts(nodes);
		double ahc=SimLib.getAverage(hc);

		
		double avgsuccessrate=SimLib.averageSuccessRate(successRate);
		
		
		/*Writing number of received sent add messages */

		Reporter.writeToFile("report_packets_sent.txt", Reporter.numberOfSent+"\r\n");
		Reporter.writeToFile("report_packets_added.txt", Reporter.numberOfAddedToBuffer+"\r\n");
		Reporter.writeToFile("report_packets_received.txt", Reporter.numberOfReceived+"\r\n");
		Reporter.writeToFile("report_success_ratio.txt", avgsuccessrate+"\r\n");
		Reporter.writeToFile("report_avg_message_delay.txt", amd+"\r\n");
		Reporter.writeArrayToFile(amdarray, "Message_Delays.txt");
		SimLib.writeArrayListToFile(hc, "report_hop_count.txt");
		SimLib.writeArrayListToFile(successRate, "success_rate.txt");
		if(tts !=-1){
			ArrayList<Double> remain=SimLib.remainings(nodes);
			SimLib.writeArrayListToFile(remain, "remaining_tts.txt");	
		}
		
		
		reporttoWrite=reporttoWrite+"\r\n";
		reporttoWrite=reporttoWrite+Reporter.writePacketInfo();
		reporttoWrite=reporttoWrite+"Average success ratio: "+avgsuccessrate+"\r\n";
		reporttoWrite=reporttoWrite+"Average Message Delay in hours: "+amd+"\r\n";
		reporttoWrite=reporttoWrite+"Average Hop count is "+ahc+"\r\n";
		reporttoWrite=reporttoWrite+"************************************************\r\n";
		
		//System.out.println(reporttoWrite.replace("\r", ""));
		Reporter.writeToFile("report_"+basicparam+".txt", reporttoWrite);
		p(reporttoWrite);
	
		
		//p(numberOfPingPong+" times ping png");
	}
	
	public void nodeseavesdrop(){
		if(alpha > 0 && wantedProb >0){
			//this is ouur model
			//eavesdrop(Air air,String time);
		}
	}
}
