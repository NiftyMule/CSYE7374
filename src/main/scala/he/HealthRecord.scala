package he

import java.time.LocalDate
import java.util.Locale.IsoCountryCode
import java.util.UUID

/**
 * This is an example record to be encrypted using PHE.
 */

/**
 * Case class to represent a health record.
 *
 * @param pii                personally identifiable information.
 * @param dob                date of birth.
 * @param maybeY             optional gender identification: Some(true): male, Some(false): female, None: non-binary (or unknown).
 * @param conditions         a Set[Condition].
 * @param nonIdentifyingData non-identifying data.
 * @tparam X the type of the non-identifying data.
 */
case class HealthRecord[X](rowId: UUID, pii: PII, dob: LocalDate, maybeY: Option[Boolean], conditions: Set[Condition], nonPii: X) extends PHE_Record[X]

/**
 * Case class to represent personally identifiable information.
 *
 * @param id government ID which needs to be protected.
 * @param name
 * @param address
 */
case class PII(id: ID, name: Name, address: Address)

/**
 * Case class to represent a diagnosed condition.
 *
 * @param icd10 the ICD10 code.
 * @param onset the date of onset.             
 */
case class Condition(icd10: String, onset: LocalDate)

/**
 * Application-specific non-identifying data record.
 */
case class Data()

/**
 * Case class to represent a government-issued ID.
 *
 * @param id
 */
case class ID(id: String)

/**
 * Case class to define a Name.
 *
 * @param familyName       the most significant name for sorting purposes.
 * @param givenName        the less significant name for sorting purposes.
 * @param otherIdentifiers the other (least) significant components of a name for sorting purposes.
 */
case class Name(familyName: String, givenName: String, otherIdentifiers: Option[String])

/**
 * Case class to define an Address.
 *
 * @param countryCode   the ISO country code.
 * @param postCode      the post code, zip code, whatever.
 * @param province      the state, region, county, department, province--the largest division within the country.
 * @param city          the city name.
 * @param streetAddress the street address, including number, apartment, etc.
 */
case class Address(countryCode: IsoCountryCode, postCode: String, province: String, city: String, streetAddress: String)


