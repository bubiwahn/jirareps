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
		final String name
		final String status
		Double effortEstimated = 0.0
		Integer issuesTotal = 0
		Integer issuesEstimated = 0
		Integer issuesDone = 0
		Double effortTodo = 0.0
		Double effortDone = 0.0
		Item (String name) {
			this.name = name
		}
	} 
	
	final Map<String, Item> epics = new HashMap()
	final Map<String, Item> releases = new HashMap()
	final Item all = new Item()
	
	Item getItem(String name, Map<String, Item> map) {
		Item item = map.get(name)
		if(item == null) {
			item = new Item(name)
			map.put(name, item)
		}
		return item	
	}
	
			
	void addIssue(Issue issue) {
		println issue.key + ", " + issue.epicLink + ", " + issue.epicName + ", " + issue.estimatedEffortDays + ", " + issue.status + ", " + issue.type + ", " + issue.fixVersion 
		final Double effortEstimated = issue.estimatedEffortDays != null ? issue.estimatedEffortDays : 0.0
		if(issue.type == Issue.Type.Epic) {
			Item epic = getItem(issue.epicName)
			epic.effortEstimated = effortEstimated 
			Item release = getItem(issue.fixVersion != null ? issue.fixVersion : "EverythingElse", releases)
			release.effortEstimated += effortEstimated 
		}
		else {
			Item epic = getItem(issue.epicLink)
			epic.issuesTotal++
			switch(issue.status) {
				case Report.Issue.Status.Completed:
				case Report.Issue.Status.Cancelled:
				    epic.issuesDone++
	                epic.effortDone += effortEstimated		
				    break
				default:
					epic.effortTodo += effortEstimated
			}
			if(issue.estimatedEffortDays != null) epic.issuesEstimated++ 
		}
	}
	
	void make() {
		
	}
	
	void print() {
		for(Item epic: epics) {
			println  " > '${epic.name}'"
		}
		println "#Epics = ${countEpics}"
		println "#UserStories = ${countUserStories}"
		println "#Defects = ${countDefects}"
		println "#Issues = ${countIssues}"
	}
}
