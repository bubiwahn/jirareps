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
		final Object fixVersions = fields.getAt(JqlRequest.fixVersions)
		this.fixVersion = (fixVersions != null && fixVersions[0] != null) ? fixVersions[0].name : null 
		this.epicLink = fields.getAt(JqlRequest.Epic_Link)
		this.epicName = fields.getAt(JqlRequest.Epic_Name)
		String estimatedEffortDays = fields.getAt(JqlRequest.Estimated_Effort_Days)
		this.estimatedEffortDays = estimatedEffortDays != null ? Double.valueOf(estimatedEffortDays) : null
		
		String type = fields.getAt(JqlRequest.Type).name
		if (type.startsWith(JqlRequest.Type_Epic))
			this.type = Type.Epic 
		else if (type.startsWith(JqlRequest.Type_ImplementationTask))
			this.type = Type.UserStory
		else if(type.startsWith(JqlRequest.Type_Defect))
			this.type = Type.Defect
		else
			throw new Exception("IllegalType");
			
		String status = fields.getAt(JqlRequest.Status).name
		String resolution = fields.getAt(JqlRequest.Resolution)
		//println "Status:'" + status + "', Resolution:'" + resolution + "'"
		if(status.startsWith(JqlRequest.Status_InProgress))
			this.status = Status.InProgress
		else if(status.startsWith(JqlRequest.Status_Resolved))
			this.status = Status.Resolved
		else if(status.startsWith(JqlRequest.Status_Closed)) {
			this.status = Status.Completed
		if(!resolution.equals(JqlRequest.Resolution_Done))
				this.status = Issue.Status.Cancelled
		} 
		else
			this.status = Status.Open	
	}
}
