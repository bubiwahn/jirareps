package tv.jirareps

import tv.jirareps.Issue.Status
import tv.jirareps.Issue.Type

class Issue {

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

	/**
	 * Construct an issue by parsing given raw JQL result set element.
	 * @param single element from them given JQL result set
	 */
	Issue (Object element) {
		this.key = element.key
		final Object fields = element.fields
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
