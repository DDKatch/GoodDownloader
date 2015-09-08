package models;

import java.text.NumberFormat;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"secs","mins","hours","time", "longTime"})
public class Time {
	
	private long secs;
	private long mins;
	private long hours;	
	private String time;
	
	private boolean longTime;
	
	public Time(){
		secs = 0;
		mins = 0;
		hours = 0;	
		time = "";
	};
	
	public Time(long milisecs){
		setTime(milisecs);
	}
	
	public String getTime(){

		
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMinimumIntegerDigits (2);
		
		secs%=60;
		String s_secs = formatter.format(secs);
		
		mins%=60;
		String s_mins = formatter.format(mins);
		
		String s_hours ="";
		
		if(hours/24 < 1)
		{
			hours%=24;
			s_hours = formatter.format(hours);
		}
		else
			longTime = true;
		
		if(!longTime)
			time = s_hours + ':'+ s_mins + ':' + s_secs;
		else{
			time = "More than 1 day...=(";		
		}
		
		return time;
	}
	
	@Override
	public String toString(){
		String res = "time{";
		
		res += "secs" + Long.toString(secs)+
				"mins" + Long.toString(mins)+
				"hours" + Long.toString(hours)+
				"time" + time;
		
		res += "}";
		return res;
	}
	
	@XmlElement(name = "time")
	public void setTime(String time){
		this.time = time;
	}
	
	public void setTime(long milisecs){
		secs = milisecs / 1000;
		mins = secs / 60;
		hours = mins / 60;
		longTime = false;
	}

	public long getSecs() {
		return secs;
	}

	@XmlElement(name = "secs")
	public void setSecs(long secs) {
		this.secs = secs;
	}

	public long getMins() {
		return mins;
	}

	@XmlElement(name = "mins")
	public void setMins(long mins) {
		this.mins = mins;
	}

	public long getHours() {
		return hours;
	}

	@XmlElement(name = "hours")
	public void setHours(long hours) {
		this.hours = hours;
	}

	public boolean isLongTime() {
		return longTime;
	}

	@XmlElement(name = "longTime")
	public void setLongTime(boolean longTime) {
		this.longTime = longTime;
	}
}
