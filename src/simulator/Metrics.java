package simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class Metrics {

	/**
	 * @param args
	 */
	
	public static int[][] marr=new int[50][50];
	public static ArrayList<Integer> ict=new ArrayList<Integer>();
	public static ArrayList<Integer> contacts=new ArrayList<Integer>();
	static String[] nodesencounter;
	public static ArrayList<ArrayList<String>> sarr;
	static Node[] nodes;
	//*
	public static void main(String[] args) {
		
		String fname="traceStAndrews.txt";
		String cdur="CDStAndrews.txt";
		String ictname="InterContactTimesStAndrews.txt";
		int nodenumber=25;
		icticd(fname,cdur,ictname,nodenumber);
		
		fname="trace_uni_cam.txt";
		nodenumber=12;
		cdur="CDCambridge.txt";
		ictname="InterContactTimesCambridge.txt";
		icticd(fname,cdur,ictname,nodenumber);
		
		fname="trace.txt";
		cdur="CDMilano.txt";
		ictname="InterContactTimesMilano.txt";
		nodenumber=50;
		//Node number for this trace should stay at 50
		//for the nodes that doesn't have any contact, they will be ignored
		icticd(fname,cdur,ictname,nodenumber);
		
		System.out.println("finished");
	}
	//*/
	
	public static void fixcopies(String inp,String outp){
		ArrayList<ArrayList<Integer>> arr=onlylines(inp);

		for(int i=0;i<arr.size()-1;i++){
			ArrayList<Integer> temp=arr.get(i);
			int n1=temp.get(0).intValue();
			int n2=temp.get(1).intValue();
			int stime=temp.get(2).intValue();
			int etime=temp.get(3).intValue();
			
			ArrayList<Integer> temp2=arr.get(i+1);
			int nn1=temp2.get(0).intValue();
			int nn2=temp2.get(1).intValue();
			int nstime=temp2.get(2).intValue();
			int netime=temp2.get(3).intValue();
			
			if(nn1==n1 && nn2==n2 && nstime==stime && netime==etime ){
				arr.remove(i);
				i--;
			}

			
		}

		writeTraceOverwrite(arr,outp);
	}
	
	
	public static void icticd(String fname,String cdur,String ictname,int nodenumber){
		ArrayList<Integer> a=contactDurations(fname);
		Collections.sort(a);
		printArrayList(a,cdur);
		ArrayList<Integer> ict=new ArrayList<Integer>();
		for(int i=1;i<nodenumber;i++){
			for(int j=0;j<i;j++){			
				ArrayList<Integer> array=ict(fname,i+1,j+1);
				//printConsoleArrayList(array);
				ict.addAll(array);
			}
		}
		Collections.sort(ict);
		printArrayList(ict,ictname);
	}
	
	public static void printArrayList(ArrayList<Integer> contacts,String fname){
		BufferedWriter bw=null;
		try{
			bw=new BufferedWriter(new FileWriter(fname));
			for(int j=0;j<contacts.size();j++){
				bw.write(contacts.get(j)+"\r\n");
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void printConsoleArrayList(ArrayList<Integer> contacts){
		for(int j=0;j<contacts.size();j++){
			System.out.print(contacts.get(j)+" ");
		}
		System.out.println();
	}
	
	public static void printarr(int[][] arr,String fname){
		BufferedWriter bw=null;
		double total=0;
		try{
			bw=new BufferedWriter(new FileWriter(fname));
			bw.write("<table border=\"1\"><tr><td></td>\n");
			for(int i=0;i<arr[0].length;i++){
				bw.write("<td>"+(i+1)+"</td>");
			}
			bw.write("<td>sum</td>\n");
			bw.write("</tr>\n");
			for(int i=0;i<arr.length;i++){
				String style="style=\"background-color:#fc6;\"";
				if(i%2==0){
					bw.write("<tr "+style+"><td>"+(i+1)+"</td>\n");
				}else{
					bw.write("<tr><td>"+(i+1)+"</td>\n");
				}
				int sum=0;
				int count=0;
				for(int j=0;j<arr[0].length;j++){
					if(arr[i][j] != 0){
						count++;
					}
					sum=sum+arr[i][j];
					bw.write("<td>"+arr[i][j]+"</td>\n");
				}
				contacts.add(sum);
				if(count==0){
					count=-1;
					sum=0;
				}
				double mean=((double)sum)/count;
				total=total+mean;
				bw.write("<td>"+mean+"</td>\n");
				bw.write("</tr>\n");
			}
			bw.write("</table>"+total+"\n");
			Collections.sort(contacts);
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}	
	}

	public static ArrayList<ArrayList<Integer>> lines(String fname){
		ArrayList<ArrayList<Integer>> arr=new ArrayList<ArrayList<Integer>>();
		String line="";
		BufferedReader br;
		int i=0;
		try{
			br=new BufferedReader(new FileReader(fname));
			while((line=br.readLine()) != null){
				StringTokenizer st=new StringTokenizer(line);
				arr.add(new ArrayList<Integer>());
				//while(st.hasMoreTokens()){
					String node1=st.nextToken();
					String node2=st.nextToken();
					String stime=st.nextToken();
					String etime=st.nextToken();	
					
					int n1=Integer.parseInt(node1);
					int n2=Integer.parseInt(node2);
					int s=Integer.parseInt(stime);// start time
					int e=Integer.parseInt(etime);//end time
					int dif=e-s;
					
					marr[n1][n2]++;
					marr[n2][n1]++;
					
					arr.get(i).add(n1);
					arr.get(i).add(n2);
					arr.get(i).add(s);
					arr.get(i).add(e);
					arr.get(i).add(dif);
					ict.add(e-s);
					
				//}
				i++;
			}
			br.close();
			Collections.sort(ict);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return arr;
	}
	
	public static ArrayList<Integer> contactDurations(String fname){
		int zeros=0;
		ArrayList<Integer> arr=new ArrayList<Integer>();
		String line="";
		BufferedReader br;
		int i=0;
	
		try{
			br=new BufferedReader(new FileReader(fname));
			while((line=br.readLine()) != null){
				StringTokenizer st=new StringTokenizer(line);
				String node1=st.nextToken();
				String node2=st.nextToken();
				String stime=st.nextToken();
				String etime=st.nextToken();	
				
				int n1=Integer.parseInt(node1);
				int n2=Integer.parseInt(node2);
				
				int s=Integer.parseInt(stime);// start time
				int e=Integer.parseInt(etime);//end time
				int dif=e-s;
				if(dif==0){
					zeros++;
				}
				arr.add(Integer.valueOf(dif));
				i++;
				
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("One second contact ratio: "+zeros/(double)i);
		return arr;
	}
	
	
	public static void linesread(String fname){
		ArrayList<ArrayList<Integer>> arr=new ArrayList<ArrayList<Integer>>();
		String line="";
		BufferedReader br;
		int i=0;
	
		try{
			br=new BufferedReader(new FileReader(fname));
			while((line=br.readLine()) != null){
				StringTokenizer st=new StringTokenizer(line);
				arr.add(new ArrayList<Integer>());
				//while(st.hasMoreTokens()){
					String node1=st.nextToken();
					String node2=st.nextToken();
					String stime=st.nextToken();
					String etime=st.nextToken();	
					
					int n1=Integer.parseInt(node1);
					int n2=Integer.parseInt(node2);
					
					int s=Integer.parseInt(stime);// start time
					int e=Integer.parseInt(etime);//end time
					int dif=e-s;
					
					marr[n1][n2]++;
					marr[n2][n1]++;
					
					
					arr.get(i).add(n1);
					arr.get(i).add(n2);
					arr.get(i).add(s);
					arr.get(i).add(e);
					arr.get(i).add(dif);

					
				//}
				i++;
				
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static ArrayList<Integer> ict(String fname,int id1,int id2){
		ArrayList<Integer> arr=new ArrayList<Integer>();
		BufferedReader br;
		int i=0;
		int first=0;
		String line="";
		try{
			br=new BufferedReader(new FileReader(fname));
			while((line=br.readLine()) != null){
				StringTokenizer st=new StringTokenizer(line);
				String node1=st.nextToken();
				String node2=st.nextToken();
				String stime=st.nextToken();
				String etime=st.nextToken();	
				
				int n1=Integer.parseInt(node1);
				int n2=Integer.parseInt(node2);
				
				int s=Integer.parseInt(stime);// start time
				int e=Integer.parseInt(etime);//end time
				int dif=e-s;
				if(n1==id1 && n2==id2){
					arr.add(Integer.valueOf(s-first));
					first=s;
				}
				
				i++;
				
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return arr;
	}
	
	public static ArrayList<ArrayList<Integer>> onlylines(String fname){
		ArrayList<ArrayList<Integer>> arr=new ArrayList<ArrayList<Integer>>();
		String line="";
		BufferedReader br;
		int i=0;
		try{
			br=new BufferedReader(new FileReader(fname));
			while((line=br.readLine()) != null){
				StringTokenizer st=new StringTokenizer(line);
				arr.add(new ArrayList<Integer>());
				//while(st.hasMoreTokens()){
					String node1=st.nextToken();
					String node2=st.nextToken();
					String stime=st.nextToken();
					String etime=st.nextToken();	
					
					int n1=Integer.parseInt(node1);
					int n2=Integer.parseInt(node2);
					int s=Integer.parseInt(stime);// start time
					int e=Integer.parseInt(etime);//end time
			
			
					arr.get(i).add(n1);
					arr.get(i).add(n2);
					arr.get(i).add(s);
					arr.get(i).add(e);
					
				//}
				i++;
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return arr;
	}
	
	public static void writeTraceOverwrite(ArrayList<ArrayList<Integer>> s,String fname){
		BufferedWriter bwriter;
		try{
			bwriter=new BufferedWriter(new FileWriter(fname,false));
			for(int i=0;i<s.size();i++){
					bwriter.write(s.get(i).get(0)+"\t"+
							s.get(i).get(1)+"\t"+
							s.get(i).get(2)+"\t"+
							s.get(i).get(3));
				bwriter.write("\r\n");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
