package tv.jirareps

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BacklogReportController {

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
		return report.toString()
	}
}