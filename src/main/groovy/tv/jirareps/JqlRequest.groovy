package tv.jirareps

import groovy.json.JsonSlurper

class JqlRequest {
	
	final static String base = "http://jira/rest/api/2/"
	
	static Object search(String jql, Integer startAt, Integer maxResults, String fields) throws IOException {
		
		String.metaClass.encodeURL = {
			java.net.URLEncoder.encode(delegate, "UTF-8")
		}
		return invoke("search", "jql=" + jql.encodeURL(), "startAt=" + startAt.toString().encodeURL(), "maxResults=" + maxResults.toString().encodeURL(), , "fields=" + fields.encodeURL())
	}
	
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
