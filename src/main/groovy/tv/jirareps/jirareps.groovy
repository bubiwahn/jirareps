package tv.jirareps

String jql='project = LF AND (type = Epic AND labels = TechnikBacklog OR type="Implementation (Task)" AND issuefunction in linkedIssuesOf("labels = TechnikBacklog", "is epic of")) ORDER BY Rank ASC'
Integer startAt=0
Integer maxResults=500
Object result = JqlRequest.search(jql, startAt, maxResults, Report.fields)
println "---------------------------------"

println "startAt=" + result.startAt + ",  maxResults=" + result.maxResults + ", total=" + result.total
Report report = new Report()
for(Object issue: result.issues) {
	report.add(new Report.Issue(issue))
}
report.output()

