package simulator;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class Reporter {

	static String fname;
	static BufferedWriter bw;
	static int numberOfReceived=0;
	static int numberOfAcks=0;
	static int numberOfDropped=0;
	static int numberOfSent=0;
	static int numberOfAddedToBuffer=0;
	//static BufferedWriter[] r;
	static String foldername="Outputs";
	
	
	public static void init(String params){
		foldername=foldername+"_"+params;
		File fold=new File(foldername);
		if(!fold.exists()  || !fold.isDirectory()){
			fold.mkdir();
		}
		resetNumbers();
	}
	
	public static void resetNumbers(){
		numberOfAddedToBuffer=0;
		numberOfDropped=0;
		numberOfReceived=0;
		numberOfSent=0;
		numberOfAcks=0;
	}
	
	public static void addPacketSent(int sender,int receiver,String time){
		numberOfSent++;
	}
	
	public static void addPacketAddedToBuffer(int receiver,int sender,String time){
		numberOfAddedToBuffer++;
	}
	
	public static void addPacketDropped(int sender,int receiver,String time){
		numberOfDropped++;
	}
	
	public static void addPacketReceived(int sender,int receiver,String time){
		//addAckSent(sender,receiver,time);
		numberOfReceived++;
	}

	public static void bufferFull(int id,String time){
		/*
		String line="bufferFull for node "+id+" time "+time; 
		try{
			bw.write(line+"\r\n");
		}catch(Exception e){
			e.printStackTrace();
		}
		//*/
	}
	
	public static ArrayList<String> readTrace(String fname){
		ArrayList<String> a=new ArrayList<String>();
		String line;
		try{
			BufferedReader br = new BufferedReader(new FileReader(fname));
			while ((line = br.readLine()) != null) {
			   a.add(line);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return a;
	}
	
	public static void closeFile(){
		writePacketInfo();
		try{
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void writeTrace(ArrayList<String> s,String fname){
		BufferedWriter bwriter;
		try{
			bwriter=new BufferedWriter(new FileWriter(fname));
			for(int i=0;i<s.size();i++){
				StringTokenizer st=new StringTokenizer(s.get(i));
				while(st.hasMoreTokens()){
					String nt=st.nextToken();
					System.out.print(nt+" ");
					bwriter.write(nt+"\t");
				}
				System.out.println();
				bwriter.write("\r\n");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(String fileName,String s){
		BufferedWriter bwriter;
		try{
			bwriter=new BufferedWriter(new FileWriter(foldername+"/"+fileName,true));
			bwriter.write(s);
			bwriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	public static String writePacketInfo(){
		String res="************STATS************\r\n"+
		numberOfReceived+" packets received\r\n"+
		numberOfAddedToBuffer+" packets added to Buffer\r\n"+
		numberOfDropped+" packets dropped\r\n"+
		numberOfSent+" packets sent\r\n"+
		//numberOfAcks+" packets sent\r\n"+
		"*****************************\r\n";
		return res;
	}

	/*
	public static void openfilesFolder(String foldername,String fname,int num){
		File f=new File(foldername);
		if(!f.exists() || f.isDirectory()){
			f.mkdir();
		}
		
		r=new BufferedWriter[num+1];
		for(int i=1;i<r.length;i++){
			try{
				r[i]=new BufferedWriter(new FileWriter(foldername+"/"+fname+"_"+i+".txt"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	public static void writeToFolder(int num,String s){
		try{
			r[num].write(s);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void closefilesFolder(){
		for(int i=1;i<r.length;i++){
			try{
				r[i].close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	*/
	
	//writes message_delays and success rates
	public static void writeAllTextFile(){
		String fnameDelay=foldername+"/Message_Delays.txt";
		String fnameSuccess=foldername+"/success_rate.txt";
		File allf=new File("all.txt");
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(allf, true)))) {
		    fnameDelay="./"+fnameDelay;
		    fnameSuccess="./"+fnameSuccess;
		    //fnameDelay=fnameDelay.replace("/", "//");
		    //fnameSuccess=fnameSuccess.replace("/", "//");
			out.println(fnameDelay+"\r\n"+fnameSuccess);
			//System.out.println(fnameDelay+"\r\n"+fnameSuccess);
		}catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	public static void writeArrayToFile(double[] arr,String s){
		//String all="Message Delay Time for each message \r\n";
		String all="";
		
		for(int i=0;i<arr.length;i++){
			//it is possible that message may not be broadcasted further
			//at this time the delay will be 0
			//don't write the message if delay is 0
			if(arr[i] != 0){
				all=all+Lib.precstr(Lib.prec(arr[i], 4),4)+"\r\n";
			}
		}
				
		writeToFile(s, all);
	}
	
	/*
	public static void main(String[] args){
		double[] arr={0.01,0.25,0.5,0.75,0.99};
		
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr.length;j++){
				for(int k=0;k<arr.length;k++){
					Simulator.p("java -jar Simulator.jar trace.txt -1 1.0 -1 100 -1 "+
							arr[i]+" "+arr[j]+" 0 "+arr[k]+" 100");
				}
			}
		}
	}
	//*/
}
