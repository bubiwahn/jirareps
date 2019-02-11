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
		
	static class Epic {
		final String name
		Integer countIssuesTotal = 0
		Integer countIssuesEstimated = 0
		Integer countIssuesCompleted = 0
		Double estimatedEffortEpic = 0.0
		Double estimatedEffortIssues = 0.0
		Double effortCompleted = 0.0
		Epic (String name) {
			this.name = name
		}
	}
	private Map<String, Epic> epics = new HashMap()
	
	private Integer countIssues = 0;
	private Integer countEpics = 0
	private Integer countUserStories = 0
	private Integer countDefects = 0
	
	private Epic getEpic(Issue issue) {
		Epic epic = epics.get(issue.type == Issue.Type.Epic ? issue.key : issue.epicLink);
		if(epic == null) {
			epic = new Epic(issue.epicName);
			epics.put(issue.key, epic)
		}
		return epic
	}
	
	void addIssue(Issue issue) {
		if(issue.type == Issue.Type.Epic) {
			countEpics++
			Epic epic = getEpic(issue);
			epic.estimatedEffortEpic = issue.estimatedEffortDays 
		}
		else {
     		countIssues++
			if(issue.type == Issue.Type.UserStory) 
				countUserStories++
			else if(issue.type == Issue.Type.Defect)
				countDefects++
			Epic epic = getEpic(issue)
			epic.countIssuesTotal++
			epic.estimatedEffortIssues += issue.estimatedEffortDays != null ? issue.estimatedEffortDays : 0.0
			if(issue.status == Report.Issue.Status.Completed) {
				epic.countIssuesCompleted++
				epic.effortCompleted += issue.estimatedEffortDays
			}
		}
		println issue.key + ", " + issue.epicLink + ", " + issue.epicName + ", " + issue.estimatedEffortDays + ", " + issue.status + ", " + issue.type + ", " + issue.fixVersion 
	}
	
	void make() {
		
	}
	
	void print() {
		println "#Epics = ${countEpics}"
		println "#UserStories = ${countUserStories}"
		println "#Defects = ${countDefects}"
		println "#Issues = ${countIssues}"
	}
}
