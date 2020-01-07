/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.calculation

import models.{readNullable, readNullableList}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, OFormat, OWrites, Reads, _}

case class TaxBand(name: String, rate: BigDecimal, income: BigDecimal, taxAmount: BigDecimal)

object TaxBand {
  implicit val format: OFormat[TaxBand] = Json.format[TaxBand]
}

case class PayPensionsProfit(totalSelfEmploymentProfit: Option[BigDecimal] = None,
                             totalPropertyProfit: Option[BigDecimal] = None,
                             incomeTaxAmount: Option[BigDecimal] = None,
                             taxableIncome: Option[BigDecimal] = None,
                             bands: List[TaxBand] = Nil)

object PayPensionsProfit {
  implicit val reads: Reads[PayPensionsProfit] = (
    readNullable[BigDecimal](__ \ "taxableIncome" \ "detail" \ "payPensionsProfit" \ "totalSelfEmploymentProfit") and
      readNullable[BigDecimal](__ \ "taxableIncome" \ "detail" \ "payPensionsProfit" \ "totalPropertyProfit") and
      readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "detail" \ "incomeTax" \ "payPensionsProfit" \ "incomeTaxAmount") and
      readNullable[BigDecimal](__ \ "taxableIncome" \ "detail" \ "payPensionsProfit" \ "taxableIncome") and
      readNullableList[TaxBand](__ \ "incomeTaxAndNicsCalculated" \ "detail" \ "incomeTax" \ "payPensionsProfit" \ "taxBands")
    ) (PayPensionsProfit.apply _)
  implicit val writes: OWrites[PayPensionsProfit] = Json.writes[PayPensionsProfit]
}

case class Dividends(incomeTaxAmount: Option[BigDecimal] = None,
                     taxableIncome: Option[BigDecimal] = None,
                     bands: List[TaxBand] = Nil)

object Dividends {
  implicit val reads: Reads[Dividends] = (
    readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "detail" \ "incomeTax" \ "dividends" \ "incomeTaxAmount") and
      readNullable[BigDecimal](__ \ "taxableIncome" \ "detail" \ "dividends" \ "taxableIncome") and
      readNullableList[TaxBand](__ \ "incomeTaxAndNicsCalculated" \ "detail" \ "incomeTax" \ "dividends" \ "taxBands")
    ) (Dividends.apply _)
  implicit val writes: OWrites[Dividends] = Json.writes[Dividends]
}

case class SavingsAndGains(incomeTaxAmount: Option[BigDecimal] = None,
                           taxableIncome: Option[BigDecimal] = None,
                           bands: List[TaxBand] = Nil)

object SavingsAndGains {
  implicit val reads: Reads[SavingsAndGains] = (
    readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "detail" \ "incomeTax" \ "savingsAndGains" \ "incomeTaxAmount") and
      readNullable[BigDecimal](__ \ "taxableIncome" \ "detail" \ "savingsAndGains" \ "taxableIncome") and
      readNullableList[TaxBand](__ \ "incomeTaxAndNicsCalculated" \ "detail" \ "incomeTax" \ "savingsAndGains" \ "taxBands")
    ) (SavingsAndGains.apply _)
  implicit val writes: OWrites[SavingsAndGains] = Json.writes[SavingsAndGains]
}

case class AllowancesAndDeductions(personalAllowance: Option[BigDecimal] = None,
                                   giftOfInvestmentsAndPropertyToCharity: Option[BigDecimal] = None,
                                   totalAllowancesAndDeductions: Option[BigDecimal] = None,
                                   totalReliefs: Option[BigDecimal] = None) {

  val totalAllowancesDeductionsReliefs: Option[BigDecimal] = (totalAllowancesAndDeductions ++ totalReliefs).reduceOption(_ + _)

}

object AllowancesAndDeductions {
  implicit val reads: Reads[AllowancesAndDeductions] = (
    readNullable[BigDecimal](__ \ "allowancesDeductionsAndReliefs" \ "detail" \ "allowancesAndDeductions" \ "personalAllowance") and
      readNullable[BigDecimal](__ \ "allowancesDeductionsAndReliefs" \ "detail" \ "allowancesAndDeductions" \ "giftOfInvestmentsAndPropertyToCharity") and
      readNullable[BigDecimal](__ \ "allowancesDeductionsAndReliefs" \ "summary" \ "totalAllowancesAndDeductions") and
      readNullable[BigDecimal](__ \ "allowancesDeductionsAndReliefs" \ "summary" \ "totalReliefs")
    ) (AllowancesAndDeductions.apply _)
  implicit val writes: OWrites[AllowancesAndDeductions] = Json.writes[AllowancesAndDeductions]
}

case class GiftAid(payments: Option[BigDecimal] = None,
                   rate: Option[BigDecimal] = None,
                   giftAidTax: Option[BigDecimal] = None)

object GiftAid {
  implicit val reads: Reads[GiftAid] = (
    readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "detail" \ "incomeTax" \ "giftAid" \ "grossGiftAidPayments") and
      readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "detail" \ "incomeTax" \ "giftAid" \ "rate") and
      readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "detail" \ "incomeTax" \ "giftAid" \ "giftAidTax")
    ) (GiftAid.apply _)
  implicit val writes: OWrites[GiftAid] = Json.writes[GiftAid]
}

case class Nic(class2: Option[BigDecimal] = None,
               class4: Option[BigDecimal] = None,
               totalNic: Option[BigDecimal] = None)

object Nic {
  implicit val reads: Reads[Nic] = (
    readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "summary" \ "nics" \ "class2NicsAmount") and
      readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "summary" \ "nics" \ "class4NicsAmount") and
      readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "summary" \ "nics" \ "totalNic")
    ) (Nic.apply _)
  implicit val writes: OWrites[Nic] = Json.writes[Nic]
}

case class Calculation(totalIncomeTaxAndNicsDue: Option[BigDecimal] = None,
                       totalIncomeTaxNicsCharged: Option[BigDecimal] = None,
                       totalTaxableIncome: Option[BigDecimal] = None,
                       incomeTaxNicAmount: Option[BigDecimal] = None,
                       timestamp: Option[String] = None,
                       crystallised: Boolean,
                       nationalRegime: Option[String] = None,
                       payPensionsProfit: PayPensionsProfit = PayPensionsProfit(),
                       savingsAndGains: SavingsAndGains = SavingsAndGains(),
                       dividends: Dividends = Dividends(),
                       allowancesAndDeductions: AllowancesAndDeductions = AllowancesAndDeductions(),
                       nic: Nic = Nic(),
                       giftAid: GiftAid = GiftAid())

object Calculation {
  implicit val reads: Reads[Calculation] = (
    readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "summary" \ "totalIncomeTaxAndNicsDue") and
      readNullable[BigDecimal](__ \ "incomeTaxAndNicsCalculated" \ "summary" \ "totalIncomeTaxNicsCharged") and
      readNullable[BigDecimal](__ \ "taxableIncome" \ "summary" \ "totalTaxableIncome") and
      readNullable[BigDecimal](__ \ "endOfYearEstimate" \ "summary" \ "incomeTaxNicAmount") and
      readNullable[String](__ \ "metadata" \ "calculationTimestamp") and
      (__ \ "metadata" \ "crystallised").read[Boolean] and
      readNullable[String](__ \ "incomeTaxAndNicsCalculated" \ "summary" \ "taxRegime") and
      __.read[PayPensionsProfit] and
      __.read[SavingsAndGains] and
      __.read[Dividends] and
      __.read[AllowancesAndDeductions] and
      __.read[Nic] and
      __.read[GiftAid]
    ) (Calculation.apply _)
  implicit val writes: OWrites[Calculation] = Json.writes[Calculation]
}