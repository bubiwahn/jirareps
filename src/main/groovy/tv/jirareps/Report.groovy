package tv.jirareps

class Report {
	
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
	final static String fields="${Report.FixVersions},${Report.Type},${Report.Epic_Link},${Report.Epic_Name},${Report.Estimated_Effort_Days},${Report.Status}"

	static class Issue {
		
		enum Type {Epic, UserStory, Defect}
		enum Status {Open, InProgress, Resolved, Completed, Cancelled}
		
		final String key 
		final Object fields
		final String fixVersion 
		final String epicLink
		final String epicName
		final Double estimatedEffortDays
		final Status status
		final Type type

		Issue (Object issue) {
			this.key = issue.key
			final Object fields = issue.fields
			final Object fixVersions = fields.getAt(fixVersions)
			this.fixVersion = (fixVersions != null && fixVersions[0] != null) ? fixVersions[0].name : null 
			this.epicLink = fields.getAt(Epic_Link)
			this.epicName = fields.getAt(Epic_Name)
			String estimatedEffortDays = fields.getAt(Estimated_Effort_Days)
			this.estimatedEffortDays = estimatedEffortDays != null ? Double.valueOf(estimatedEffortDays) : null
			
			String type = fields.getAt(Type).name
			if (type.startsWith(Report.Type_Epic))
				this.type = Issue.Type.Epic 
			else if (type.startsWith(Report.Type_ImplementationTask))
				this.type = Issue.Type.UserStory
			else if(type.startsWith(Report.Type_Defect))
				this.type = Issue.Type.Defect
			else
				throw new Exception("IllegalType");
				
			String status = fields.getAt(Status).name
			String resolution = fields.getAt(Resolution)
			//println "Status:'" + status + "', Resolution:'" + resolution + "'"
			if(status.startsWith(Report.Status_InProgress))
				this.status = Report.Issue.Status.InProgress
			else if(status.startsWith(Report.Status_Resolved))
				this.status = Report.Issue.Status.Resolved
			else if(status.startsWith(Report.Status_Closed)) {
				this.status = Report.Issue.Status.Completed
				if(!resolution.equals(Report.Resolution_Done))
					this.status = Report.Issue.Status.Cancelled
			} 
			else
				this.status = Report.Issue.Status.Open	
		}
		
	}
		
	Integer numberOfIssuesEstimatedTotal = 0
	Double sumOfEffortIssuesEstimatedTotal = 0.0
	Double estimatedEffortPerIssueAverage = 0.0

	static class Item {
		final String key
		String name
		String release
		Double effortEstimated = 0.0
		Integer issuesTotal = 0
		Integer issuesEstimated = 0
		Integer issuesDone = 0
		Double effortIssuesTotal = 0.0
		Double effortIssuesDone = 0.0
		Double effortRemaining = 0.0
		Double completed = 0.0
		Item (String key) {
			this.key = key
		}
	} 
	
	final Map<String, Item> epics = new HashMap()
	final Map<String, Item> releases = new HashMap()
	final Item all = new Item()
	
	Item getItem(String key, Map<String, Item> map) {
		Item item = map.get(key)
		if(item == null) {
			item = new Item(key)
			map.put(key, item)
		}
		return item	
	}
		
	void addIssue(Issue issue) {
		// println issue.key + ", " + issue.epicLink + ", " + issue.epicName + ", " + issue.estimatedEffortDays + ", " + issue.status + ", " + issue.type + ", " + issue.fixVersion 
		final Double effortEstimated = issue.estimatedEffortDays != null ? issue.estimatedEffortDays : 0.0
		if(issue.type == Issue.Type.Epic) {
			Item epic = getItem(issue.key, epics)
			epic.name = issue.epicName
			epic.release = issue.fixVersion
			epic.effortEstimated = effortEstimated 
		}
		else {
			Item epic = getItem(issue.epicLink, epics)
			epic.issuesTotal++
			epic.effortIssuesTotal += effortEstimated
			switch(issue.status) {
				case Report.Issue.Status.Completed:
				case Report.Issue.Status.Cancelled:
				    epic.issuesDone++
	                epic.effortIssuesDone += effortEstimated		
				    break
				default:
					break
			}
			if(issue.estimatedEffortDays != null) {
				epic.issuesEstimated++
				numberOfIssuesEstimatedTotal++
				sumOfEffortIssuesEstimatedTotal += effortEstimated
			}
		}
	}
	
	/**
	 * Make final completion.
	 */
	void complete() {
				
		// calculate average estimated effort per issue
		estimatedEffortPerIssueAverage = sumOfEffortIssuesEstimatedTotal / numberOfIssuesEstimatedTotal
		
		// process epics
		epics.values().each { item ->
			
			item.effortIssuesTotal += (item.issuesTotal - item.issuesEstimated) * estimatedEffortPerIssueAverage
			item.effortRemaining = item.effortIssuesTotal - item.effortIssuesDone
			item.completed = item.effortIssuesDone / item.effortIssuesTotal
			
			Item release = getItem(item.release, releases)
			release.release = item.release
			release.name = "RELEASE: ${(item.release != null ?  item.release : 'not assigned')}"
			release.issuesTotal += item.issuesTotal
			release.issuesEstimated += item.issuesEstimated
			release.issuesDone += item.issuesDone
			release.effortEstimated += item.effortEstimated
			release.effortIssuesTotal += item.effortIssuesTotal
			release.effortIssuesDone += item.effortIssuesDone
			release.effortRemaining += item.effortRemaining
		}

		// process releases
		releases.values().each { item ->
			item.completed = item.effortIssuesDone / item.effortIssuesTotal
			
			all.release = "ALL Rel."
			all.name = "EVERYTHING" 
			all.issuesTotal += item.issuesTotal
			all.issuesTotal += item.issuesTotal
			all.issuesEstimated += item.issuesEstimated
			all.issuesDone += item.issuesDone
			all.effortEstimated += item.effortEstimated
			all.effortIssuesTotal += item.effortIssuesTotal
			all.effortIssuesDone += item.effortIssuesDone
		}

		all.completed = all.effortIssuesDone / all.effortIssuesTotal
	
	}
	
	String toString() {
		
		StringWriter stringWriter = new StringWriter()
		PrintWriter printWriter = new PrintWriter(stringWriter)
		
		def printline = { item -> 
			final String key = "${item.key}".padLeft(8)
			final String release = "${item.release}".padLeft(8)
			final String issuesTotal = "${item.issuesTotal}".padLeft(6)
			final String issuesEstimated = "${item.issuesEstimated}".padLeft(6)
			final String issuesDone = "${item.issuesDone}".padLeft(6)
			final String effortEstimated = "${item.effortEstimated.round(2)}".padLeft(8)
			final String effortIssuesTotal = "${item.effortIssuesTotal.round(2)}".padLeft(8)
			final String effortIssuesDone = "${item.effortIssuesDone.round(2)}".padLeft(8)
			final String effortRemaining = "${item.effortRemaining.round(2)}".padLeft(8)
			final String completed = "${item.completed.round(2)}".padLeft(8)
			final String name = "${item.name}"
			printWriter.println "${key}, ${release}, ${issuesTotal}, ${issuesEstimated}, ${issuesDone}, ${effortEstimated}, ${effortIssuesTotal}, ${effortIssuesDone}, ${effortRemaining}, ${completed}, '${name}'"
		}
		
		printWriter.println "<html><pre>"
	    printWriter.println "        |         | number| number| number|  effort |  effort |  effort |  effort | effort  |     "
	    printWriter.println "        |         | issues| issues| issues|  epic   |  issues |  issues |  issues | based   |     "
	    printWriter.println "     key|  release| total | estim.| done  |  estim. |  total  |  done   |  remain.| progress| name"
		epics.values().each printline
		releases.values().each printline
		[all].each printline
		printWriter.println "number of issues estimated total = ${numberOfIssuesEstimatedTotal}"
		printWriter.println "sum of effort of estimated issues total = ${sumOfEffortIssuesEstimatedTotal}"
		printWriter.println "estimated effort per issue average = ${estimatedEffortPerIssueAverage}"
		printWriter.println "</pre></html>"
		
		
//		println "#Epics = ${countEpics}"
//		println "#UserStories = ${countUserStories}"
//		println "#Defects = ${countDefects}"
//		println "#Issues = ${countIssues}"
		
		return stringWriter.getBuffer().toString()
	}
	
	static Report build(Object data) {
		Report report = new Report();
		
		for(Object issue: data.issues) {
			report.addIssue(new Report.Issue(issue))
		}
		
		report.complete();
		return report
	}
}
