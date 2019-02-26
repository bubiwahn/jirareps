package tv.jirareps;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BacklogReportController {

	@Autowired
	private BuildProperties buildProperties;

	@RequestMapping(path="/tbReport")
	public String tbReport() {

		// JIRA filter of Technik Backlog
		String jql='filter = 32081';
		Integer startAt=0
		Integer maxResults=1000

		Object result = JqlRequest.search(jql, startAt, maxResults)
		
		Report report = new Report();
		for(Object issue: result.issues) {
			report.addIssue(new Issue(issue))
		}
		report.complete();
		String buildVersion = buildProperties != null ? buildProperties.getVersion() : null
		String buildTime = buildProperties != null ? buildProperties.getTime() : null
		return "<html><pre>\nBacklog Report Version '" + buildVersion + "' built at '" + buildTime + "' ...\n" + report.toString() + "\n</pre></html>"
	}
}