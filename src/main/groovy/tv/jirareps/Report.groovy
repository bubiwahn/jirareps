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
		
	static class Item {
		final String key
		String name
		String fixVersion
		Double effortEstimated = 0.0
		Integer issuesTotal = 0
		Integer issuesEstimated = 0
		Integer issuesDone = 0
		Double effortIssuesTotal = 0.0
		Double effortIssuesDone = 0.0
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
		println issue.key + ", " + issue.epicLink + ", " + issue.epicName + ", " + issue.estimatedEffortDays + ", " + issue.status + ", " + issue.type + ", " + issue.fixVersion 
		final Double effortEstimated = issue.estimatedEffortDays != null ? issue.estimatedEffortDays : 0.0
		if(issue.type == Issue.Type.Epic) {
			Item epic = getItem(issue.key, epics)
			epic.name = issue.epicName
			epic.fixVersion = issue.fixVersion
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
			}
		}
	}
	
	Integer numberOfIssuesEstimatedTotal = 0
	Double sumOfEffortIssuesEstimatedTotal = 0.0
	Double estimatedEffortPerIssueAverage = 0.0
	
	void make() {
		for(Item epic: epics.values()) {
			numberOfIssuesEstimatedTotal += epic.issuesEstimated
			sumOfEffortIssuesEstimatedTotal += epic.effortIssuesTotal
		}
		estimatedEffortPerIssueAverage = sumOfEffortIssuesEstimatedTotal / numberOfIssuesEstimatedTotal
	}
	
	void print() {
		for(Item epic: epics.values()) {
			println " > ${epic.key}, ${epic.fixVersion}, ${epic.issuesTotal}, ${epic.issuesEstimated}, ${epic.issuesDone}, ${epic.effortEstimated}, ${epic.effortIssuesTotal}, ${epic.effortIssuesDone}, ${epic.name}"
		}
		println "number of issues estimated total = ${numberOfIssuesEstimatedTotal}"
		println "sum of effort of estimated issues total = ${sumOfEffortIssuesEstimatedTotal}"
		println "estimated effort per issue average = ${estimatedEffortPerIssueAverage}"
		
		
//		println "#Epics = ${countEpics}"
//		println "#UserStories = ${countUserStories}"
//		println "#Defects = ${countDefects}"
//		println "#Issues = ${countIssues}"
	}
}
