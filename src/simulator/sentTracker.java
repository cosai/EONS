package simulator;

import java.util.ArrayList;

/**
 * 
 * @author Kara
 * This class is for keeping track of the nodes that some message is sent
 * If a node wants to keep track of the nodes that it communicated
 * You can use this class
 * This class is extended by Node class
 */
public class sentTracker {

	private ArrayList<sentlist> listnodes;
	
	sentTracker(){
		listnodes=new ArrayList<sentlist>();	
	}
	
	public final ArrayList<Integer> getSentMessageIds(int id){
		if(listnodes.isEmpty()){
			return null;
		}
		
		for(int i=0;i<listnodes.size();i++){
			if(listnodes.get(i).nodeid==id){
				return listnodes.get(i).messages;
			}			
		}
		return null;
		
	}
	
	//if the node has sent that specific message mesid to that specific node nodeid 
	//return true else returns false
	public final boolean sentToBefore(int nodeid,int mesid){
		for(int i=0;i<listnodes.size();i++){
			if(listnodes.get(i).nodeid==nodeid){
				ArrayList<Integer> messagelist=listnodes.get(i).messages;
				if(messagelist!=null){
					for(int j=0;j<messagelist.size();j++){
						if(messagelist.get(j)==mesid){
							return true;
						}
					}
				}//if messagelist not empty check
			}//end of message id check			
		}//end of outer for
		return false;
	}
	
	public final void addSentMessages(int id,ArrayList<Integer> irr){
		boolean found=false;
		for(int i=0;i<listnodes.size();i++)
		{
			if(listnodes.get(i).nodeid==id){
				found=true;
				ArrayList<Integer> arr=listnodes.get(i).messages;
				for(int j=0;j<irr.size();j++){
					int mid=irr.get(j);
					if(!arr.contains(Integer.valueOf(mid))){
						arr.add(Integer.valueOf(mid));
					}
				}
			}
		}
		if(!found){
			sentlist s=new sentlist(id);
			s.setMessages(irr);
			listnodes.add(s);
		}
		
	}
	
	//inner class
	//holds the message ids (arraylist) sent to some specific nodeid
	private class sentlist{
		ArrayList<Integer> messages;
		int nodeid;
		
		sentlist(int id){
			messages=new ArrayList<Integer>();
			nodeid=id;
		}
		
		void setMessages(ArrayList<Integer> a){
			for(int i=0;i<a.size();i++){
				messages.add(a.get(i));
			}
		}		
	}
	
}
