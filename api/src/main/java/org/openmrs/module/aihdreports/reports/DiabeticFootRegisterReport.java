package org.openmrs.module.aihdreports.reports;

import org.openmrs.module.aihdreports.reporting.dataset.definition.SharedDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;
import org.openmrs.module.aihdreports.reporting.metadata.Dictionary;
import org.openmrs.module.aihdreports.reporting.metadata.Metadata;
import org.openmrs.module.aihdreports.reporting.utils.CoreUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.converter.AgeConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.aihdreports.reporting.converter.CalculationResultConverter;
import org.openmrs.module.aihdreports.reporting.calculation.EncounterDateCalculation;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.aihdreports.reporting.converter.GenderConverter;
import org.openmrs.module.aihdreports.data.converter.ObsDataConverter;
import org.openmrs.module.aihdreports.definition.dataset.definition.CalculationDataDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class DiabeticFootRegisterReport extends AIHDDataExportManager{
    @Override
    public String getExcelDesignUuid() {
        return "4bc35404-0270-11e8-9058-5b0d24f9ec33";
    }

    @Override
    public ReportDesign buildReportDesign(ReportDefinition reportDefinition) {
        ReportDesign rd= createExcelTemplateDesign(getExcelDesignUuid(), reportDefinition, "diabetic_foot.xls");
        Properties props = new Properties();
        props.put("repeatingSections", "sheet:1,row:7,dataset:Df");
        props.put("sortWeight", "5000");
        rd.setProperties(props);
        return rd;
    }

    @Override
    public String getUuid() {
        return "59e83c20-0270-11e8-8691-e3f98920b873";
    }

    @Override
    public String getName() {
        return "Diabetic Foot Register";
    }

    @Override
    public String getDescription() {
        return "Collect diabetic foot information";
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        l.add(buildReportDesign(reportDefinition));
        return l;
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public ReportDefinition constructReportDefinition() {
        ReportDefinition rd = new ReportDefinition();
        rd.setUuid(getUuid());
        rd.setName(getName());
        rd.setDescription(getDescription());
        rd.addParameters(getParameters());
        rd.addDataSetDefinition("Df", Mapped.mapStraightThrough(dataSetDefinition()));
        return rd;
    }

    private DataSetDefinition dataSetDefinition() {
        PatientDataSetDefinition dsd = new PatientDataSetDefinition();
        SharedDataDefinition sdd= new SharedDataDefinition();
        PatientIdentifierType patientId = CoreUtils.getPatientIdentifierType(Metadata.Identifier.PATIENT_ID);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(patientId.getName(), patientId), identifierFormatter);

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);


		dsd.addColumn("id", new PersonIdDataDefinition(), "");
		dsd.addColumn("Date", encounterDate(), "", new CalculationResultConverter());
		dsd.addColumn("Patient No", identifierDef, "");
		dsd.addColumn("Names", nameDef, "");
		dsd.addColumn("Age", new AgeDataDefinition(), "", new AgeConverter());
        dsd.addColumn("Sex", new GenderDataDefinition(), "", new GenderConverter());
        dsd.addColumn("rbs", sdd.obsDdefinition("rbs",  Dictionary.getConcept(Dictionary.RBS)), "", new ObsDataConverter());
		dsd.addColumn("fbs", sdd.obsDdefinition("rbs",  Dictionary.getConcept(Dictionary.FBS)), "", new ObsDataConverter());
        dsd.addColumn("currentHbac", sdd.obsDdefinition("currentHbac",  Dictionary.getConcept(Dictionary.HBA1C)), "", new ObsDataConverter());
        dsd.addColumn("abi", sdd.obsDdefinition("abi",  Dictionary.getConcept(Dictionary.SYSTOLIC_BLOOD_PRESSURE)), "", new ObsDataConverter());
        dsd.addColumn("complains", sdd.obsDdefinition("complains",  Dictionary.getConcept(Dictionary.PROBLEM_ADDED)), "", new ObsDataConverter());
        return dsd;
    }

    private DataDefinition encounterDate(){
		CalculationDataDefinition cd = new CalculationDataDefinition("Date", new EncounterDateCalculation());
		return cd;
	}

}