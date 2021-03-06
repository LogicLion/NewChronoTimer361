package Streams;
import java.io.DataOutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class IndividualStream implements IStream {

private int _runNumber;
private Queue<TimingRecord> runs;
private Queue<TimingRecord> pendingRuns;
private ArrayList<TimingRecord> completedRuns;

public IndividualStream(int runNumber)	
{
	_runNumber = runNumber;
	runs = new LinkedList<TimingRecord>();
	pendingRuns = new LinkedList<TimingRecord>();
	completedRuns = new ArrayList<TimingRecord>();
}

public void num(int BIB)
{
	pendingRuns.add(new TimingRecord(BIB));
}

public void cancelRecord()
{
	runs.peek().cancel();
	completedRuns.add(runs.poll());
	System.out.println("Next Racer");
}

public void startRecord(LocalDateTime start)
{
	TimingRecord current = pendingRuns.poll();
	current.start(start);
	runs.add(current);
}

public void finishRecord(LocalDateTime finish)
{
	runs.peek().finish(finish);
	completedRuns.add(runs.poll());
		
}

public void DNFRecord()
{
	runs.peek().DNF();
	completedRuns.add(runs.poll());
}


@Override
public String toString() 
{
	StringBuilder concat = new StringBuilder("RUN\tBIB\tTIME\n");
	for(TimingRecord i : completedRuns)
	{
		concat.append(_runNumber + "\t" + i.toString() + "\n");
	}
	return concat.toString();
}

public void finishRecord(LocalDateTime finish, int channel)
{
	return;
	
}

public String displayRecords(Clock clock){
	String records = "";
	LocalDateTime now = LocalDateTime.now(clock);
	for(TimingRecord t : pendingRuns){
		records += t.get_BIB() + "\t" + now + "\n";
	}
	for(TimingRecord t : runs){
		Duration time = Duration.between(t.get_start(), now);
		records +=  t.get_BIB() + "\t" + time.getSeconds() + "." + time.getNano() + "\n";
	}
	if(completedRuns != null && completedRuns.size() != 0){
		TimingRecord lastRun = completedRuns.get(completedRuns.size() - 1);
		records += lastRun.toString();
	}
	return records;
}



public String toJSON(Clock clock){
	Gson g = new Gson();
	ArrayList<ServerExportRecordFile> serfs = new ArrayList<ServerExportRecordFile>();
	for(TimingRecord i : completedRuns)
	{
		serfs.add(new ServerExportRecordFile(_runNumber, i.get_duration() , i.get_BIB(), i.get_eventCode()));
	}	
	String listItOut = g.toJson(serfs);
	System.out.println(listItOut);
	return listItOut;
}

}