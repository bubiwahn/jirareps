package tv.jirareps

class Report {
			
	Integer numberOfItemsTotal = 0
	Integer numberOfEpicsTotal = 0
	Integer numberOfIssuesTotal = 0
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
	
	/**
	 * Add a single issue to the report.	
	 * @param issue issue to be added
	 */
	void addIssue(Issue issue) {
		numberOfItemsTotal++
		// println issue.key + ", " + issue.epicLink + ", " + issue.epicName + ", " + issue.estimatedEffortDays + ", " + issue.status + ", " + issue.type + ", " + issue.fixVersion 
		final Double effortEstimated = issue.estimatedEffortDays != null ? issue.estimatedEffortDays : 0.0
		if(issue.type == Issue.Type.Epic) {
			numberOfEpicsTotal++;
			Item epic = getItem(issue.key, epics)
			epic.name = issue.epicName
			epic.release = issue.fixVersion
			epic.effortEstimated = effortEstimated 
		}
		else {
			numberOfIssuesTotal++;
			Item epic = getItem(issue.epicLink, epics)
			epic.issuesTotal++
			epic.effortIssuesTotal += effortEstimated
			switch(issue.status) {
				case Issue.Status.Completed:
				case Issue.Status.Cancelled:
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
		Integer numberOfIssuesTotalInEpics = 0
		epics.values().each { item ->
			
			numberOfIssuesTotalInEpics += item.issuesTotal
			
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
		assert(numberOfIssuesTotalInEpics == numberOfIssuesTotal)
		
		// process releases
		Integer numberOfIssuesTotalInReleases = 0
		releases.values().each { item ->
					
			numberOfIssuesTotalInReleases += item.issuesTotal

			item.completed = item.effortIssuesDone / item.effortIssuesTotal
			
			all.release = "ALL Rel."
			all.name = "EVERYTHING" 
			all.issuesTotal += item.issuesTotal
			all.issuesEstimated += item.issuesEstimated
			all.issuesDone += item.issuesDone
			all.effortEstimated += item.effortEstimated
			all.effortIssuesTotal += item.effortIssuesTotal
			all.effortIssuesDone += item.effortIssuesDone
		}
		assert(numberOfIssuesTotalInReleases == numberOfIssuesTotal)
		assert(all.issuesTotal == numberOfIssuesTotal)
		
		all.completed = all.effortIssuesDone / all.effortIssuesTotal
	
	}
	
	/**
	 * Get content as formatted (HTML) string.
	 */
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
		
		printWriter.println "number of items total = ${numberOfItemsTotal}"
		printWriter.println "number of epics total = ${numberOfEpicsTotal}"
		printWriter.println "number of issue total = ${numberOfIssuesTotal}"
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
}
