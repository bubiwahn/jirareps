package tv.jirareps

import groovy.json.JsonSlurper

class JqlRequest {
	
	final static String base = "http://jira/rest/api/2/"
	
	final static String Epic_Link="customfield_11631"
	final static String Epic_Name="customfield_11633"
	final static String Estimated_Effort_Days="customfield_10702"
	final static String FixVersions="fixVersions"
	final static String Resolution="resolution"
	final static String Resolution_Done="Done"
	final static String Status="status"
	final static String Status_InProgress="In Progress"
	final static String Status_Resolved="Resolved"
	final static String Status_Closed="Closed"
	final static String Status_Waiting="Waiting"
	final static String Type="issuetype"
	final static String Type_Epic="Epic"
	final static String Type_ImplementationTask="Implementation (Task)"
	final static String Type_Defect="Defect"

	final static String fields="${FixVersions},${Type},${Epic_Link},${Epic_Name},${Estimated_Effort_Days},${Status}"

	static Object search(String jql, Integer startAt, Integer maxResults) throws IOException {
		
		String.metaClass.encodeURL = {
			java.net.URLEncoder.encode(delegate, "UTF-8")
		}
		Object result = invoke("search", "jql=" + jql.encodeURL(), "startAt=" + startAt.toString().encodeURL(), "maxResults=" + maxResults.toString().encodeURL(), "fields=" + fields.encodeURL())
		
		println "result: startAt=${result.startAt}, maxResults=${result.maxResults}, total=${result.total}"
		if(maxResults < result.total)
			throw new RuntimeException("Invocation result exceeds number of expected elements. Please implement a better solution in the BacklogReportController")

        return result    
	}
	
	/**
	 * Get the JQL result set from the service by building a parameterized URL,
	 * fetching the content from the service and covert it into a JSON object
	 * representation.
	 * @param service name of the service
	 * @param args arguments added to the URL
	 * @return service invocation result represented as JSON object  
	 * @throws IOException in case of error
	 */
	static Object invoke(String service, String ... args) throws IOException {
		String parameters = "";
		int i = 0;
		for (String arg: args) {
			parameters += (i == 0 ? "" : "&") + arg
			i++
		}
		URL url = (base + service + "?" + parameters).toURL()
		println url.toString()
		URLConnection uc = url.openConnection();
		uc.setRequestProperty ("Authorization", Authorization.getBasicAuth());
		
		JsonSlurper jsonSlurper= new JsonSlurper();
		Object result = jsonSlurper.parse(uc.getInputStream());
		
		return result
	} 

}
