<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.tms.threed.jpgGen.jpgGen2.generate.JpgGenerationProcess"%>
<%@page import="java.util.List"%>
<%@page import="com.tms.threed.threedCore.shared.SeriesKey"%>
<%@page import="java.util.concurrent.BlockingQueue"%>
<%@page import="com.tms.threed.jpgGen.jpgGen2.generate.FileForProcess"%>
<%@page import="com.tms.threed.jpgGen.jpgGen2.generate.ProcessStatus"%>
<%@page import="com.tms.threed.threedCore.shared.ThreedConfig"%>
<%@page import="com.tms.threed.jpgGen.jpgGen2.generate.LogEntry"%>
<%@page import="com.tms.threed.jpgGen.jpgGen2.generate.ProgressTracker"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Jpg Generator</title>
<style type="text/css">
	td {
		border: 1px black solid;
		padding: 5px;
	}
</style>
</head>
<body>


<%
	String series = request.getParameter("seriesName");
	String year = request.getParameter("seriesYear");
	String confirmed = request.getParameter("confirmed");

	boolean currentlyRunning = false;
	
	if( series != null && year != null )
	{
		if( confirmed == null )
		{
%>		
<h2>Confirm Jpg Generation Kickoff</h2>		
<form style="display: inline;" action="submit.jsp" method="post">
	<input type="hidden" name="seriesYear" value="<%=year %>"/>
	<input type="hidden" name="seriesName" value="<%=series %>"/>
	Click <input type="submit" name="confirmed" value="here"/> to start generation for <%= year + " " + series %> 
</form><form style="display: inline;" action="submit.jsp" method="post"> or <input type="submit" name="notConfirmed" value="here"/> to cancel.</form>
	<%
		}
		else
		{
			SeriesKey seriesKey = new SeriesKey(Integer.parseInt( year) , series );

			List<JpgGenerationProcess> monitorList = (List<JpgGenerationProcess>) application.getAttribute("monitorList");
			
			
			for( JpgGenerationProcess process : monitorList )
			{
				if( process.getSeriesKey().equals(seriesKey)
						&& ! ( process.getProcessStatus().isTerminal() ) )
				{
					currentlyRunning = true;
					break;
				}
					
			}
			
			if( !currentlyRunning )
			{
				ThreedConfig threedConfig = (ThreedConfig) application.getAttribute("threedConfig");
				
				try
				{
				
					JpgGenerationProcess jpgGenerationProcess = new JpgGenerationProcess( threedConfig, seriesKey);
					BlockingQueue<JpgGenerationProcess> analysisQueue = (BlockingQueue<JpgGenerationProcess>) application.getAttribute("analysisQueue");
					
					monitorList.add(jpgGenerationProcess);
					analysisQueue.put(jpgGenerationProcess);
					%>
					 Jpg Process has begun for <%= year + " " + series %>.
					<%
				}
				catch( Exception e )
				{
					%>
					 <h2 style="color: red;">Process Kickoff had an error for <%= year + " " + series %>.  Are the pngs and model.xml in place?</h2>
					<%
					
				}
			}
			else
			{
	%>
	 Jpg Process is already running for <%= year + " " + series %>.  Please wait for it to complete.
	<%				
			}
	}%>
	<br/><br/>
<%}%>












<%
	String creationTime = request.getParameter("creationTime");
	String doCancel = request.getParameter("doCancel");

	if( creationTime != null )
	{
		List<JpgGenerationProcess> monitorList = (List<JpgGenerationProcess>) application.getAttribute("monitorList");
		
		JpgGenerationProcess ourProcess = null;
		
		for( JpgGenerationProcess process : monitorList )
		{
			if( process.getCreationTime() == Long.parseLong(creationTime)
					&& ! ( process.getProcessStatus().isTerminal() ) )
			{
				ourProcess = process;
				break;
			}
				
		}
		
		
		if( ourProcess != null )
		{
			if( confirmed == null )
			{
%>		
<h2>Confirm Process Cancel</h2>		
<form style="display: inline;" action="submit.jsp" method="post">
	<input type="hidden" name="creationTime" value="<%=creationTime %>"/>
	<input type="hidden" name="doCancel" value="yes"/>
	Click <input type="submit" name="confirmed" value="here"/> to cancel process for <%= ourProcess.getSeriesKey().getYear() + " " + ourProcess.getSeriesKey().getName() %> 
</form><form style="display: inline;" action="submit.jsp" method="post"> or <input type="submit" name="notConfirmed" value="here"/> to cancel.</form>
	<%
			}
			else
			{
				ourProcess.setProcessStatus(ProcessStatus.Canceled);
			}
		}%>
		<br/><br/>
	<%}%>












<h2 style="display: inline;">Recent/Unfinished Processes</h2> <a href="submit.jsp">Refresh List</a><br/><br/>

<% List<JpgGenerationProcess> monitorList = (List<JpgGenerationProcess>) application.getAttribute("monitorList"); %>
<table>
	<tr>
		<td><strong>Series</strong></td>
		<td><strong>Status</strong></td>
		<td><strong>Start Time</strong></td>
		<td><strong>Analysis Stats</strong></td>
		<td><strong>Generation Stats</strong></td>
	</tr>

<% 
	java.util.Collections.sort(monitorList, new java.util.Comparator<JpgGenerationProcess>() {
		public int compare(JpgGenerationProcess arg0, JpgGenerationProcess arg1)
		{
			return new Long( arg1.getCreationTime()).compareTo(new Long(arg0.getCreationTime()));
		}
		
	});


	for( JpgGenerationProcess process : monitorList ) {
	if( process.getProgressTracker().getComplete() == process.getProgressTracker().getTotal() &&  process.getProgressTracker().getTotal() > 0 )
		process.setProcessStatus(ProcessStatus.Completed);
	
	
		if( ! process.getProcessStatus().isTerminal() 
				|| ((System.currentTimeMillis() - process.getCreationTime()) < 1000*60*60*24*7  ))
		{
	%>
	<tr>
		<td><%=process.getSeriesKey() %>
		<% if( ! process.getProcessStatus().isTerminal() ) {%>
		 (<a href="submit.jsp?doCancel=true&creationTime=<%= process.getCreationTime() %>">Cancel</a>)
		 <% } %>
		 </td>
		<td><%=process.getProcessStatus().name() %></td>
		<td><%=new java.util.Date(process.getCreationTime()) %> (<%= (System.currentTimeMillis() - process.getCreationTime())/ (1000 * 60) %> minutes ago)</td>
		<td><%=process.getAnalysisProgress().getComplete() %> / <%=process.getAnalysisProgress().getTotal() %> = <%= process.getAnalysisProgress().getPercent() %>% (<%= process.getJpgsToMake() %> jpgs)</td>
		<td><%=process.getProgressTracker().getComplete() %> / <%=process.getProgressTracker().getTotal() %> = <%= process.getProgressTracker().getPercent() %>%</td>
	</tr>
	<%	} 
	} %>
	
</table>

<br/>

<h2>Queue Depths</h2>
<table>
	<tr>
		<td><strong>Name</strong></td>
		<td><strong>Workers</strong></td>
		<td><strong>Depth</strong></td>
	</tr>
	<tr>
		<td><strong>Analysis Queue</strong></td>
		<td><strong>2</strong></td>
		<td><strong><%= ((BlockingQueue) application.getAttribute("analysisQueue")).size() %></strong></td>
	</tr>
	<tr>
		<td><strong>Generation Queue</strong></td>
		<td><strong><%= application.getInitParameter("workers") %></strong></td>
		<td><strong><%= ((BlockingQueue) application.getAttribute("workQueue")).size() %></strong></td>
	</tr>
	<br/>
</table>


<br/>

<h2>Generation Errors</h2>
<% 
List<FileForProcess> dlq = (List<FileForProcess>) application.getAttribute("dlq"); 
%>
<table>
	<tr>
		<td><strong>Series</strong></td>
		<td><strong>File</strong></td>
		<td><strong>Attempts</strong></td>
	</tr>

<% for( FileForProcess file : dlq ) { %>
	<tr>
		<td><%=file.getProcess().getSeriesKey() %></td>
		<td><%=file.getFile().getAbsolutePath() %></td>
		<td><%= file.getAttemptCount() %></td>
	</tr>
	<tr><td colspan="3"><strong>Error:</strong>
	<%= file.getThrowable() == null ? "" : file.getThrowable().toString() %></td></tr>
	<% } %>
	
</table>



</body>
</html>
