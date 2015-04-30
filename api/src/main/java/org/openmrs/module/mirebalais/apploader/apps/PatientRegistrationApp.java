package org.openmrs.module.mirebalais.apploader.apps;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.mirebalaismetadata.constants.PersonAttributeTypes;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.CoreMetadata;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.PersonAddressWithHierarchyWidget;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;
import org.openmrs.module.registrationapp.model.TextAreaWidget;
import org.openmrs.module.registrationapp.model.TextFieldWidget;

import java.util.Arrays;

/**
 * Helper class to help defining PatientRegistrationApp
 */
public class PatientRegistrationApp {

    public AppDescriptor build(Config config) {
        AppDescriptor d = new AppDescriptor();

        d.setId(CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION);
        d.setDescription("registrationapp.registerPatient");
        d.setLabel("registrationapp.app.registerPatient.label");
        d.setIcon("icon-user");
        d.setUrl("registrationapp/findPatient.page?appId=" + CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION);
        d.setRequiredPrivilege("App: registrationapp.registerPatient");

        RegistrationAppConfig c = new RegistrationAppConfig();
        c.setAfterCreatedUrl("mirebalais/patientRegistration/afterRegistration.page?patientId={{patientId}}&encounterId={{encounterId}}");
        c.setPatientDashboardLink(MirebalaisConstants.PATIENT_DASHBOARD_LINK);
        c.setRegistrationEncounter(CoreMetadata.EncounterTypes.PATIENT_REGISTRATION, CoreMetadata.EncounterRoles.ADMINISTRATIVE_CLERK);
        c.setAllowRetrospectiveEntry(true);
        c.setAllowUnknownPatients(true);
        c.setAllowManualIdentifier(true);

        c.addSection(getDemographicsSection());
        c.addSection(getContactInfoSection());
        c.addSection(getSocialSection());
        c.addSection(getContactsSection());

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.ID_CARD_PRINTING)) {
            c.addSection(getIdentifierSection());
        }

        d.setConfig(toObjectNode(c));

        return d;
    }

    public Section getDemographicsSection() {
        Section s = new Section();
        s.setId("demographics");
        s.setLabel("");
        s.addQuestion(getMothersNameQuestion());
        s.addQuestion(getBirthplaceQuestion());
        return s;
    }

    public Question getMothersNameQuestion() {
        Question q = new Question();
        q.setId("mothersFirstNameLabel");
        q.setLegend("zl.registration.patient.mothersFirstName.label");

        Field f = new Field();
        f.setFormFieldName("mothersFirstName");
        f.setLabel("zl.registration.patient.mothersFirstName.question");
        f.setType("personAttribute");
        f.setUuid(PersonAttributeTypes.MOTHERS_FIRST_NAME.uuid());
        f.setWidget(getTextFieldWidget());
        f.setCssClasses(Arrays.asList("required"));
        q.addField(f);

        return q;
    }

    public Question getBirthplaceQuestion() {
        Question q = new Question();
        q.setId("birthplaceLabel");
        q.setLegend("zl.registration.patient.birthplace.label");

        Field f = new Field();
        f.setFormFieldName("birthplace");
        f.setLabel("zl.registration.patient.birthplace.question");
        f.setType("personAttribute");
        f.setUuid(PersonAttributeTypes.BIRTHPLACE.uuid());
        f.setCssClasses(Arrays.asList("required"));
        f.setWidget(getTextAreaWidget(50));

        q.addField(f);
        return q;
    }

    public Section getContactInfoSection() {
        Section s = new Section();
        s.setId("contactInfo");
        s.setLabel("registrationapp.patient.contactInfo.label");
        s.addQuestion(getAddressQuestion());
        s.addQuestion(getTelephoneNumberQuestion());
        return s;
    }

    public Question getAddressQuestion() {
        Question q = new Question();
        q.setId("personAddressQuestion");
        q.setLegend("Person.address");
        q.setDisplayTemplate("{{nvl field.[6] '-'}}, {{field.[5]}}, {{field.[4]}}, {{field.[3]}}, {{field.[2]}}");

        Field f = new Field();
        f.setLabel("registrationapp.patient.address.question");
        f.setType("personAddress");

        PersonAddressWithHierarchyWidget w = new PersonAddressWithHierarchyWidget();
        w.getConfig().setShortcutFor("address1");
        w.getConfig().addManualField("address2");
        f.setWidget(toObjectNode(w));

        q.addField(f);
        return q;
    }

    public Question getTelephoneNumberQuestion() {
        Question q = new Question();
        q.setId("phoneNumberLabel");
        q.setLegend("registrationapp.patient.phone.label");

        Field f = new Field();
        f.setFormFieldName("phoneNumber");
        f.setLabel("registrationapp.patient.phone.question");
        f.setType("personAttribute");
        f.setUuid(PersonAttributeTypes.TELEPHONE_NUMBER.uuid());
        f.setWidget(getTextFieldWidget());

        q.addField(f);
        return q;
    }

    public Section getSocialSection() {
        Section s = new Section();
        s.setId("social");
        s.setLabel("zl.registration.patient.social.label");
        s.addQuestion(getCivilStatusQuestion());
        s.addQuestion(getOccupationQuestion());
        s.addQuestion(getReligionQuestion());
        return s;
    }

    public Question getCivilStatusQuestion() {
        Question q = new Question();
        q.setId("civilStatusLabel");
        q.setLegend("zl.registration.patient.civilStatus.label");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:CIVIL STATUS");
        f.setLabel("zl.registration.patient.civilStatus.question");
        f.setType("obs");

        DropdownWidget w = new DropdownWidget();
        w.getConfig().setExpanded(true);
        w.getConfig().addOption("PIH:SINGLE OR A CHILD", "zl.registration.patient.civilStatus.single.label");
        w.getConfig().addOption("PIH:MARRIED", "zl.registration.patient.civilStatus.married.label");
        w.getConfig().addOption("PIH:LIVING WITH PARTNER", "zl.registration.patient.civilStatus.livingWithPartner.label");
        w.getConfig().addOption("PIH:SEPARATED", "zl.registration.patient.civilStatus.separated.label");
        w.getConfig().addOption("PIH:DIVORCED", "zl.registration.patient.civilStatus.divorced.label");
        w.getConfig().addOption("PIH:WIDOWED", "zl.registration.patient.civilStatus.widowed.label");
        f.setWidget(toObjectNode(w));

        q.addField(f);
        return q;
    }

    public Question getOccupationQuestion() {
        Question q = new Question();
        q.setId("occupationLabel");
        q.setLegend("zl.registration.patient.occupation.label");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:2452");
        f.setLabel("zl.registration.patient.occupation.question");
        f.setType("obs");
        f.setWidget(getTextFieldWidget());
        q.addField(f);

        return q;
    }

    public Question getReligionQuestion() {
        Question q = new Question();
        q.setId("religionLabel");
        q.setLegend("zl.registration.patient.religion.label");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:Religion");
        f.setLabel("zl.registration.patient.religion.question");
        f.setType("obs");

        DropdownWidget w = new DropdownWidget();
        w.getConfig().setExpanded(true);
        w.getConfig().addOption("PIH:Voodoo", "zl.registration.patient.religion.voodoo.label");
        w.getConfig().addOption("PIH:Catholic", "zl.registration.patient.religion.catholic.label");
        w.getConfig().addOption("PIH:Baptist", "zl.registration.patient.religion.baptist.label");
        w.getConfig().addOption("PIH:Islam", "zl.registration.patient.religion.islam.label");
        w.getConfig().addOption("PIH:Pentecostal", "zl.registration.patient.religion.pentecostal.label");
        w.getConfig().addOption("PIH:Seventh Day Adventist", "zl.registration.patient.religion.adventist.label");
        w.getConfig().addOption("PIH:Jehovah's Witness", "zl.registration.patient.religion.jehovahsWitness.label");
        w.getConfig().addOption("PIH:OTHER NON-CODED", "zl.registration.patient.religion.other.label");
        f.setWidget(toObjectNode(w));

        q.addField(f);
        return q;
    }

    public Section getContactsSection() {
        Section s = new Section();
        s.setId("contacts");
        s.setLabel("zl.registration.patient.contactPerson.label");
        s.addQuestion(getContactQuestion());
        return s;
    }

    public Question getContactQuestion() {
        Question q = new Question();
        q.setId("contactNameLabel");
        q.setLegend("zl.registration.patient.contactPerson.label");
        q.setHeader("zl.registration.patient.contactPerson.question");

        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:NAMES AND FIRSTNAMES OF CONTACT");
            f.setLabel("zl.registration.patient.contactPerson.contactName.question");
            f.setType("obsgroup");
            f.setCssClasses(Arrays.asList("required"));
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:RELATIONSHIPS OF CONTACT");
            f.setLabel("zl.registration.patient.contactPerson.relationships.label");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:ADDRESS OF PATIENT CONTACT");
            f.setLabel("zl.registration.patient.contactPerson.contactAddress.label");
            f.setType("obsgroup");
            f.setCssClasses(Arrays.asList("required"));
            f.setWidget(getTextAreaWidget(50));
            q.addField(f);
        }
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:TELEPHONE NUMBER OF CONTACT");
            f.setLabel("registrationapp.patient.phone.label");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }

        return q;
    }

    public Section getIdentifierSection() {
        Section s = new Section();
        s.setId("patient-identification-section");
        s.setLabel("registrationapp.patient.identifiers.label");
        s.addQuestion(getIdCardPrintQuestion());
        return s;
    }

    public Question getIdCardPrintQuestion() {
        Question q = new Question();
        q.setId("idcardLabel");
        q.setLegend("zl.registration.patient.idcard.label");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:ID Card Printing Requested");
        f.setLabel("zl.registration.patient.idcard.question");
        f.setType("obs");

        DropdownWidget w = new DropdownWidget();
        w.getConfig().setExpanded(true);
        w.getConfig().setHideEmptyLabel(true);
        w.getConfig().setInitialValue("PIH:YES");
        w.getConfig().addOption("PIH:YES", "emr.yes");
        w.getConfig().addOption("PIH:NO", "emr.no");
        f.setWidget(toObjectNode(w));

        q.addField(f);
        return q;
    }

    protected ObjectNode getTextFieldWidget() {
        return getTextFieldWidget(null);
    }

    protected ObjectNode getTextFieldWidget(Integer size) {
        TextFieldWidget w = new TextFieldWidget();
        if (size != null) {
            w.getConfig().setSize(size);
        }
        return toObjectNode(w);
    }

    protected ObjectNode getTextAreaWidget(Integer maxLength) {
        TextAreaWidget w = new TextAreaWidget();
        if (maxLength != null) {
            w.getConfig().setMaxlength(maxLength);
        }
        return toObjectNode(w);
    }

    protected ObjectNode toObjectNode(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.convertValue(o, ObjectNode.class);
    }
}
