package tv.jirareps

class Report {
	
	final static String Epic_Link="customfield_11631"
	final static String Estimated_Effort_Days="customfield_10702"
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
	final static String fields="${Report.Type},${Report.Epic_Link},${Report.Estimated_Effort_Days},${Report.Status}"

	static class Issue {
		
		enum Type {Epic, UserStory}
		enum Status {Open, InProgress, Resolved, Completed, Cancelled}
		
		final String key 
		final Object fields
		final String epicLink
		final String estimatedEffortDays 
		final Status status
		final Type type

		Issue (Object issue) {
			this.key = issue.key
			final Object fields = issue.fields
			this.epicLink = fields.getAt(Epic_Link)
			this.estimatedEffortDays = fields.getAt(Estimated_Effort_Days)
			
			String type = fields.getAt(Type).name
			if (type.startsWith(Report.Type_Epic))
				this.type = Issue.Type.Epic 
			else if (type.startsWith(Report.Type_ImplementationTask))
				this.type = Issue.Type.UserStory
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
		
	private Integer countIssues = 0;
	private Integer countEpics = 0
	private Integer countUserStories = 0

	void add(Issue issue) {
		switch (issue.type) {
			case Issue.Type.Epic:
				countEpics++
				break
			case Issue.Type.UserStory:
				countUserStories++
				break			
		}
		countIssues++
		println issue.key + ", " + issue.epicLink + ", " + issue.estimatedEffortDays + ", " + issue.status + ", " + issue.type 
	}
	
	void output() {
		println "#Epics = ${countEpics}"
		println "#UserStories = ${countUserStories}"
		println "#Issues = ${countIssues}"
	}
}
