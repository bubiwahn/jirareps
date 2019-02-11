package tv.jirareps

//String jql='key=LF-51450'
String jql='project in (LF, IPL) AND (type = Epic AND labels = TechnikBacklog OR type in ("Implementation (Task)", Defect) AND issuefunction in linkedIssuesOf("labels = TechnikBacklog", "is epic of")) ORDER BY Rank ASC'
//String jql='filter = 32415'
Integer startAt=0
Integer maxResults=500
Object result = JqlRequest.search(jql, startAt, maxResults, Report.fields)
println "---------------------------------"

println "startAt=" + result.startAt + ",  maxResults=" + result.maxResults + ", total=" + result.total
Report report = new Report()
for(Object issue: result.issues) {
	report.addIssue(new Report.Issue(issue))
}
report.print()

