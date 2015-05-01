package org.openmrs.module.mirebalais.setup;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.ConfigureIdGenerators;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.RuntimeProperties;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.mirebalaismetadata.metadata.MirebalaisLocations;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;

public class PatientIdentifierSetup {

    public static void setupIdentifierGeneratorsIfNecessary(MirebalaisHospitalService service,
                                                      IdentifierSourceService identifierSourceService,
                                                      LocationService locationService,
                                                      Config config,
                                                      RuntimeProperties customProperties) {

        ConfigureIdGenerators configureIdGenerators = new ConfigureIdGenerators(customProperties, identifierSourceService, locationService, service);

        createPatientIdGenerator(service, configureIdGenerators);

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.ARCHIVES)) {
            createDossierNumberGenerator(service, locationService, configureIdGenerators, config);
        }

    }

    private static void createPatientIdGenerator(MirebalaisHospitalService service, ConfigureIdGenerators configureIdGenerators) {
        PatientIdentifierType zlIdentifierType = service.getZlIdentifierType();
        RemoteIdentifierSource remoteZlIdentifierSource = configureIdGenerators.remoteZlIdentifierSource(zlIdentifierType);
        IdentifierPool localZlIdentifierPool = configureIdGenerators.localZlIdentifierSource(remoteZlIdentifierSource);
        configureIdGenerators.setAutoGenerationOptionsForZlIdentifier(localZlIdentifierPool);
    }

    private static void createDossierNumberGenerator(MirebalaisHospitalService service, LocationService locationService, ConfigureIdGenerators configureIdGenerators, Config config) {

        // TODO configure dossier generators for sites besides Mirebalais, if any of them start using the archives app
        if (config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
            PatientIdentifierType dossierIdentifierType = service.getDossierIdentifierType();

            SequentialIdentifierGenerator sequentialIdentifierGeneratorForUHM = configureIdGenerators
                    .sequentialIdentifierGeneratorForDossier(dossierIdentifierType,
                            MirebalaisConstants.UHM_DOSSIER_NUMBER_PREFIX,
                            MirebalaisConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);

            configureIdGenerators.setAutoGenerationOptionsForDossierNumberGenerator(sequentialIdentifierGeneratorForUHM,
                    locationService.getLocationByUuid(MirebalaisLocations.MIREBALAIS_HOSPITAL.uuid()));

            SequentialIdentifierGenerator sequentialIdentifierGeneratorForCDI = configureIdGenerators
                    .sequentialIdentifierGeneratorForDossier(dossierIdentifierType,
                            MirebalaisConstants.CDI_DOSSIER_NUMBER_PREFIX,
                            MirebalaisConstants.CDI_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);

            configureIdGenerators.setAutoGenerationOptionsForDossierNumberGenerator(sequentialIdentifierGeneratorForCDI,
                    locationService.getLocationByUuid(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL.uuid()));

        }

    }
}
