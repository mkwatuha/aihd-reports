package org.openmrs.module.aihdreports.reporting.library.cohort;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.module.aihdreports.reporting.metadata.Dictionary;
import org.openmrs.module.aihdreports.reporting.utils.ReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MonthlyReportingCohort {

    @Autowired
    private CommonCohortLibrary commonCohortLibrary;

    public CohortDefinition newDiagnosedCases(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        Concept diabeticQuestion = Dictionary.getConcept(Dictionary.DIABETIC_VISIT_TYPE);
        Concept hypertensionQuestion = Dictionary.getConcept(Dictionary.HYPERTENSION_VISIT_TYPE);
        Concept newDmPatient = Dictionary.getConcept(Dictionary.NEW_DIABETIC_PATIENT);
        Concept newHTNPatient = Dictionary.getConcept(Dictionary.NEW_HYPERTENSION_PATIENT);

        cd.setName("New diagnosed cases");
        cd.addParameter(new Parameter("onOrAfter", "Start date", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "End date", Date.class));
        cd.addParameter(new Parameter("locationList", "Facility", Location.class));

        cd.addSearch("newDiabetic", ReportUtils.map(commonCohortLibrary.hasObs(diabeticQuestion, newDmPatient), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.addSearch("newHypertension", ReportUtils.map(commonCohortLibrary.hasObs(hypertensionQuestion, newHTNPatient), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.addSearch("location", ReportUtils.map(commonCohortLibrary.hasEncounter(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${locationList}"));
        cd.setCompositionString("newDiabetic AND newHypertension AND location");

        return cd;
    }

    public CohortDefinition hasObsOnLocation(Concept q, Concept ... a){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.setName("Has obs in a location");
        cd.addParameter(new Parameter("onOrAfter", "Start date", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "End date", Date.class));
        cd.addParameter(new Parameter("locationList", "Facility", Location.class));

        cd.addSearch("location", ReportUtils.map(commonCohortLibrary.hasEncounter(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${locationList}"));
        cd.addSearch("hasObs", ReportUtils.map(commonCohortLibrary.hasObs(q, a), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.setCompositionString("location AND hasObs");
        return cd;
    }

}
