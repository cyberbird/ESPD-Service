package eu.europa.ec.grow.espd.xml.response.exclusion

import eu.europa.ec.grow.espd.domain.AvailableElectronically
import eu.europa.ec.grow.espd.domain.CriminalConvictions
import eu.europa.ec.grow.espd.domain.EspdDocument
import eu.europa.ec.grow.espd.domain.SelfCleaning
import eu.europa.ec.grow.espd.xml.base.AbstractExclusionCriteriaFixture
/**
 * Created by ratoico on 12/23/15 at 3:59 PM.
 */
class MixedExclusionCriteriaResponseTest extends AbstractExclusionCriteriaFixture {

    def "should contain mixed criminal convictions criteria"() {
        given:
        def participation = new CriminalConvictions(exists: true, convicted: "Hodor_01 was convicted")
        def corruption = new CriminalConvictions(exists: true, convicted: "Hodor_02 was convicted")
        def espd = new EspdDocument(criminalConvictions: participation, corruption: corruption)

        when:
        def request = parseResponseXml(espd)

        then:
        request.Criterion.size() == 2

        then: "check who has been convicted requirement in participation criterion"
        def participationSubGroup = request.Criterion[0].RequirementGroup[0]
        def participationReq = participationSubGroup.Requirement[3]
        checkRequirement(participationReq, "c5012430-14da-454c-9d01-34cedc6a7ded", "Who has been convicted", "DESCRIPTION")
        participationReq.Response.size() == 1
        participationReq.Response[0].Description.text() == "Hodor_01 was convicted"

        then: "check who has been convicted requirement in corruption criterion"
        def corruptionSubGroup = request.Criterion[1].RequirementGroup[0]
        def corruptionReq = corruptionSubGroup.Requirement[3]
        checkRequirement(corruptionReq, "c5012430-14da-454c-9d01-34cedc6a7ded", "Who has been convicted", "DESCRIPTION")
        corruptionReq.Response.size() == 1
        corruptionReq.Response[0].Description.text() == "Hodor_02 was convicted"
    }

    def "should contain mixed criminal convictions self cleaning requirements"() {
        given:
        def participation = new CriminalConvictions(exists: true, selfCleaning: new SelfCleaning(exists: true, description: "Hodor_01 is clean"))
        def corruption = new CriminalConvictions(exists: true, selfCleaning: new SelfCleaning(exists: true, description: "Hodor_02 is clean"))
        def espd = new EspdDocument(criminalConvictions: participation, corruption: corruption)

        when:
        def request = parseResponseXml(espd)

        then:
        request.Criterion.size() == 2

        then: "check self cleaning description requirement in participation criterion"
        def participationSubGroup = request.Criterion[0].RequirementGroup[0].RequirementGroup[0]
        def participationReq = participationSubGroup.Requirement[1]
        participationReq.Response[0].Description.text() == "Hodor_01 is clean"

        then: "check who has been convicted requirement in corruption criterion"
        def corruptionSubGroup = request.Criterion[1].RequirementGroup[0].RequirementGroup[0]
        def corruptionReq = corruptionSubGroup.Requirement[1]
        corruptionReq.Response.size() == 1
        corruptionReq.Response[0].Description.text() == "Hodor_02 is clean"
    }

    def "should contain mixed criminal convictions info electronically requirements"() {
        given:
        def participation = new CriminalConvictions(exists: true, availableElectronically: new AvailableElectronically(exists: true, url: "http://hodor_01.com"))
        def corruption = new CriminalConvictions(exists: true, availableElectronically: new AvailableElectronically(exists: true, url: "http://hodor_02.com"))
        def espd = new EspdDocument(criminalConvictions: participation, corruption: corruption)

        when:
        def request = parseResponseXml(espd)

        then:
        request.Criterion.size() == 2

        then: "check self cleaning description requirement in participation criterion"
        def participationSubGroup = request.Criterion[0].RequirementGroup[1]
        def participationReq = participationSubGroup.Requirement[1]
        participationReq.Response[0].Evidence.EvidenceDocumentReference.Attachment.ExternalReference.URI.text() == "http://hodor_01.com"

        then: "check who has been convicted requirement in corruption criterion"
        def corruptionSubGroup = request.Criterion[1].RequirementGroup[1]
        def corruptionReq = corruptionSubGroup.Requirement[1]
        corruptionReq.Response.size() == 1
        corruptionReq.Response[0].Evidence.EvidenceDocumentReference.Attachment.ExternalReference.URI.text() == "http://hodor_02.com"
    }

}