FLOW.attributeTypeControl = Ember.Object.create({
  content: [
  Ember.Object.create({
    label: "text",
    value: "String"
  }), Ember.Object.create({
    label: "number",
    value: "Double"
  })]
});

FLOW.attributeControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,
  projectContent: null,

 setFilteredContent: function() {
    this.set('content', FLOW.store.filter(FLOW.Metric, function(item) {
      return true;
    }));
  },

  setProjectMetrics: function () {
	 var pId;
	 if (!Ember.none(FLOW.selectedControl.get('selectedSurvey'))){
		 pId = FLOW.selectedControl.selectedSurvey.get('projectId');
		 this.set('projectContent', FLOW.store.filter(FLOW.Metric, function(item) {
		      return item.get('projectId') == pId;
		 }));
	 }
  }.observes('FLOW.selectedControl.selectedSurvey'),

  // load all metrics
  populate: function() {
    FLOW.store.find(FLOW.Metric);
    this.setFilteredContent();
    this.set('sortProperties', ['name']);
    this.set('sortAscending', true);
  },

   getSortInfo: function() {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});

FLOW.surveyInstanceControl = Ember.ArrayController.create({
  sortProperties: ['collectionDate'],
  sortAscending: false,
  selectedSurvey: null,
  content: null,
  sinceArray: [],
  pageNumber:0,

  populate: function() {
    this.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {}));
  },

  doInstanceQuery: function(surveyId, deviceId, since, beginDate, endDate, submitterName) {
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
      'surveyId': surveyId,
      'deviceId': deviceId,
      'since': since,
      'beginDate': beginDate,
      'endDate': endDate,
      'submitterName':submitterName
    }));
  },

  allAreSelected: function(key, value) {
    if(arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    } else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function() {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  // fired from tableColumnView.sort
  getSortInfo: function() {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});


FLOW.questionAnswerControl = Ember.ArrayController.create({
  content: null,

  doQuestionAnswerQuery: function(surveyInstanceId) {
    this.set('content', FLOW.store.findQuery(FLOW.QuestionAnswer, {
      'surveyInstanceId': surveyInstanceId
    }));
  }
});