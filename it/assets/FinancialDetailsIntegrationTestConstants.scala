/*
 * Copyright 2021 HM Revenue & Customs
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

package assets

import java.time.LocalDate

import assets.BaseIntegrationTestConstants.{testErrorMessage, testErrorNotFoundStatus, testErrorStatus}
import models.financialDetails.{DocumentDetail, DocumentDetailWithDueDate, FinancialDetail, FinancialDetailsErrorModel,
  FinancialDetailsModel, SubItem, WhatYouOweChargesList}
import models.outstandingCharges.{OutstandingChargeModel, OutstandingChargesModel}
import play.api.libs.json.{JsValue, Json}

object FinancialDetailsIntegrationTestConstants {

  def documentDetailModel(taxYear: Int = 2018,
                          documentDescription: Option[String] = Some("ITSA- POA 1"),
                          outstandingAmount: Option[BigDecimal] = Some(1400.00),
                          originalAmount: Option[BigDecimal] = Some(1400.00)): DocumentDetail =
    DocumentDetail(
      taxYear = taxYear.toString,
      transactionId = "1040000123",
      documentDescription,
      outstandingAmount = outstandingAmount,
      originalAmount = originalAmount,
      documentDate = "2018-03-29"
    )

  def financialDetail(taxYear: Int = 2018): FinancialDetail = FinancialDetail(
    taxYear = taxYear.toString,
    mainType = Some("ITSA- POA 1"),
    items = Some(List(SubItem(dueDate = Some(LocalDate.of(2019, 5, 15).toString))))
  )

  def documentDetailWithDueDateModel(taxYear: Int = 2018,
                                     documentDescription: Option[String] = Some("ITSA- POA 1"),
                                     outstandingAmount: Option[BigDecimal] = Some(1400.00),
                                     originalAmount: Option[BigDecimal] = Some(1400.00),
                                     dueDate: Option[LocalDate] = Some(LocalDate.of(2019, 5, 15))): DocumentDetailWithDueDate =
    DocumentDetailWithDueDate(documentDetailModel(taxYear, documentDescription, outstandingAmount, originalAmount), dueDate)

  val fullDocumentDetailModel: DocumentDetail = documentDetailModel()
  val fullFinancialDetailModel: FinancialDetail = financialDetail()

  val fullDocumentDetailWithDueDateModel: DocumentDetailWithDueDate = DocumentDetailWithDueDate(fullDocumentDetailModel, Some(LocalDate.of(2019, 5, 15)))

  def financialDetailsModel(taxYear: Int = 2018, outstandingAmount: Option[BigDecimal] = Some(1400.0)): FinancialDetailsModel =
    FinancialDetailsModel(
      documentDetails = List(documentDetailModel(taxYear, outstandingAmount = outstandingAmount)),
      financialDetails = List(financialDetail(taxYear))
    )

  val testValidFinancialDetailsModel: FinancialDetailsModel = FinancialDetailsModel(
    documentDetails = List(
      DocumentDetail("2019", "1040000123", Some("ITSA- Bal Charge"), Some(10.33), Some(10.33), "2018-03-29"),
      DocumentDetail("2020", "1040000124", Some("ITSA- Bal Charge"), Some(10.34), Some(10.34), "2018-03-29")
    ),
    financialDetails = List(
      FinancialDetail("2019", Some("ITSA- Bal Charge"), Some(Seq(SubItem(Some("2019-05-15"))))),
      FinancialDetail("2020", Some("ITSA- Bal Charge"), Some(Seq(SubItem(Some("2019-05-18")))))
    )
  )

  def testFinancialDetailsModel(documentDescription: List[Option[String]],
                                mainType: List[Option[String]],
                                dueDate: List[Option[String]],
                                outstandingAmount: List[Option[BigDecimal]],
                                taxYear: String): FinancialDetailsModel =
    FinancialDetailsModel(
      documentDetails = List(
        DocumentDetail(taxYear, "1040000124", documentDescription.head, outstandingAmount.head, Some(43.21), "2018-03-29"),
        DocumentDetail(taxYear, "1040000125", documentDescription(1), outstandingAmount(1), Some(12.34), "2018-03-29")
      ),
      financialDetails = List(
        FinancialDetail(taxYear, mainType.head, Some(Seq(SubItem(dueDate.head)))),
        FinancialDetail(taxYear, mainType(1), Some(Seq(SubItem(dueDate(1)))))
      )
    )

  def testFinancialDetailsModelWithChargesOfSameType(documentDescription: List[Option[String]],
                                                     mainType: List[Option[String]],
                                                     dueDate: List[Option[String]],
                                                     outstandingAmount: List[Option[BigDecimal]],
                                                     taxYear: String): FinancialDetailsModel =
    FinancialDetailsModel(
      documentDetails = List(
        DocumentDetail(taxYear, "1040000123", documentDescription.head, outstandingAmount.head, Some(43.21), "2018-03-29"),
        DocumentDetail(taxYear, "1040000124", documentDescription(1), outstandingAmount(1), Some(12.34), "2018-03-29")
      ),
      financialDetails = List(
        FinancialDetail(taxYear, mainType.head, Some(Seq(SubItem(dueDate.head)))),
        FinancialDetail(taxYear, mainType(1), Some(Seq(SubItem(dueDate(1)))))
      )
    )

  def outstandingChargesModel(dueDate: String): OutstandingChargesModel = OutstandingChargesModel(
    List(OutstandingChargeModel("BCD", Some(dueDate), 123456789012345.67, 1234), OutstandingChargeModel("ACI", None, 12.67, 1234)))

  def outstandingChargesEmptyBCDModel(dueDate: String): OutstandingChargesModel = OutstandingChargesModel(
    List(OutstandingChargeModel("LATE", Some(dueDate), 123456789012345.67, 1234)))

  val outstandingChargesEmptyBCDModel: OutstandingChargesModel = outstandingChargesEmptyBCDModel(LocalDate.now().plusDays(30).toString)

  val outstandingChargesDueInMoreThan30Days: OutstandingChargesModel = outstandingChargesModel(LocalDate.now().plusDays(35).toString)

  val outstandingChargesOverdueData: OutstandingChargesModel = outstandingChargesModel(LocalDate.now().minusYears(1).minusDays(30).toString)

  val outstandingChargesDueIn30Days: OutstandingChargesModel = outstandingChargesModel(LocalDate.now().plusDays(30).toString)

  val financialDetailsDueInMoreThan30Days: FinancialDetailsModel = testFinancialDetailsModel(
    documentDescription = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    mainType = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    dueDate = List(Some(LocalDate.now().plusDays(45).toString), Some(LocalDate.now().plusDays(50).toString)),
    outstandingAmount = List(Some(50), Some(75)),
    taxYear = LocalDate.now().getYear.toString
  )

  val financialDetailsDueIn30Days: FinancialDetailsModel = testFinancialDetailsModel(
    documentDescription = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    mainType = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    dueDate = List(Some(LocalDate.now().toString), Some(LocalDate.now().toString)),
    outstandingAmount = List(Some(2000), Some(2000)),
    taxYear = LocalDate.now().getYear.toString
  )

  val financialDetailsOverdueData: FinancialDetailsModel = testFinancialDetailsModel(
    documentDescription = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    mainType = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    dueDate = List(Some(LocalDate.now().minusDays(15).toString), Some(LocalDate.now().minusDays(15).toString)),
    outstandingAmount = List(Some(2000), Some(2000)),
    taxYear = LocalDate.now().getYear.toString
  )

  val financialDetailsWithMixedData1: FinancialDetailsModel = testFinancialDetailsModelWithChargesOfSameType(
    documentDescription = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    mainType = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    dueDate = List(Some(LocalDate.now().plusDays(35).toString), Some(LocalDate.now().minusDays(1).toString)),
    outstandingAmount = List(Some(50), Some(75)),
    taxYear = LocalDate.now().getYear.toString
  )

  val financialDetailsWithMixedData2: FinancialDetailsModel = testFinancialDetailsModelWithChargesOfSameType(
    documentDescription = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    mainType = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    dueDate = List(Some(LocalDate.now().plusDays(30).toString), Some(LocalDate.now().minusDays(1).toString)),
    outstandingAmount = List(Some(25), Some(50)),
    taxYear = LocalDate.now().getYear.toString
  )

  val financialDetailsDueIn30DaysWithAZeroOutstandingAmount: FinancialDetailsModel = testFinancialDetailsModel(
    documentDescription = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    mainType = List(Some("ITSA- POA 1"), Some("ITSA - POA 2")),
    dueDate = List(Some(LocalDate.now().plusDays(1).toString), Some(LocalDate.now().toString)),
    outstandingAmount = List(Some(100), Some(0)),
    taxYear = LocalDate.now().getYear.toString
  )

  val whatYouOweDataWithDataDueIn30Days: WhatYouOweChargesList = WhatYouOweChargesList(
    dueInThirtyDaysList = financialDetailsDueIn30Days.getAllDocumentDetailsWithDueDates,
    outstandingChargesModel = Some(outstandingChargesOverdueData)
  )

  val whatYouOweDataWithDataDueInMoreThan30Days: WhatYouOweChargesList = WhatYouOweChargesList(
    futurePayments = financialDetailsDueInMoreThan30Days.getAllDocumentDetailsWithDueDates,
    outstandingChargesModel = Some(outstandingChargesDueInMoreThan30Days)
  )

  val whatYouOweDataWithOverdueData: WhatYouOweChargesList = WhatYouOweChargesList(
    overduePaymentList = financialDetailsOverdueData.getAllDocumentDetailsWithDueDates,
    outstandingChargesModel = Some(outstandingChargesOverdueData)
  )

  val whatYouOweDataFullData: WhatYouOweChargesList = WhatYouOweChargesList(
    overduePaymentList = financialDetailsOverdueData.getAllDocumentDetailsWithDueDates,
    outstandingChargesModel = Some(outstandingChargesOverdueData)
  )

  val whatYouOweDataFullDataWithoutOutstandingCharges: WhatYouOweChargesList = WhatYouOweChargesList(
    overduePaymentList = financialDetailsOverdueData.getAllDocumentDetailsWithDueDates
  )

  val whatYouOweDataWithMixedData1: WhatYouOweChargesList = WhatYouOweChargesList(
    overduePaymentList = List(financialDetailsWithMixedData1.getAllDocumentDetailsWithDueDates(1)),
    dueInThirtyDaysList = List(),
    futurePayments = List(financialDetailsWithMixedData1.getAllDocumentDetailsWithDueDates.head),
    outstandingChargesModel = Some(OutstandingChargesModel(List()))
  )

  val whatYouOweDataWithMixedData2: WhatYouOweChargesList = WhatYouOweChargesList(
    overduePaymentList = List(financialDetailsWithMixedData2.getAllDocumentDetailsWithDueDates(1)),
    dueInThirtyDaysList = List(financialDetailsWithMixedData2.getAllDocumentDetailsWithDueDates.head),
    futurePayments = List(),
    outstandingChargesModel = Some(OutstandingChargesModel(List()))
  )

  val whatYouOweWithAZeroOutstandingAmount: WhatYouOweChargesList = WhatYouOweChargesList(
    dueInThirtyDaysList = List(financialDetailsDueIn30DaysWithAZeroOutstandingAmount.getAllDocumentDetailsWithDueDates.head),
    outstandingChargesModel = Some(outstandingChargesOverdueData)
  )

  val whatYouOweOutstandingChargesOnly: WhatYouOweChargesList = WhatYouOweChargesList(outstandingChargesModel = Some(outstandingChargesOverdueData))

  val whatYouOweNoChargeList: WhatYouOweChargesList = WhatYouOweChargesList(List.empty, List.empty, List.empty)

  val whatYouOweFinancialDetailsEmptyBCDCharge: WhatYouOweChargesList = WhatYouOweChargesList(outstandingChargesModel = Some(outstandingChargesEmptyBCDModel))

  val testInvalidFinancialDetailsJson: JsValue = Json.obj(
    "amount" -> "invalidAmount",
    "payMethod" -> "Payment by Card",
    "valDate" -> "2019-05-27"
  )

  val testFinancialDetailsErrorModelParsing: FinancialDetailsErrorModel = FinancialDetailsErrorModel(
    testErrorStatus, "Json Validation Error. Parsing FinancialDetails Data Response")

  val testFinancialDetailsErrorModel: FinancialDetailsErrorModel = FinancialDetailsErrorModel(testErrorStatus, testErrorMessage)


  val testFinancialDetailsNotFoundErrorModel: FinancialDetailsErrorModel = FinancialDetailsErrorModel(testErrorNotFoundStatus, testErrorMessage)

}