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
		Object result = JqlRequest.search(jql, startAt, maxResults, Report.fields)
		println "---------------------------------"

		println "startAt=" + result.startAt + ",  maxResults=" + result.maxResults + ", total=" + result.total
		Report report = Report.build(result)
		return report
	}
}