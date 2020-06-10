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

package controllers

import assets.BaseTestConstants._
import assets.BusinessDetailsTestConstants._
import assets.EstimatesTestConstants._
import assets.FinancialTransactionsTestConstants._
import assets.IncomeSourceDetailsTestConstants._
import assets.Messages
import audit.AuditingService
import config.{FrontendAppConfig, ItvcErrorHandler, ItvcHeaderCarrierForPartialsConverter}
import controllers.predicates.{NinoPredicate, SessionTimeoutPredicate}
import mocks.controllers.predicates.{MockAuthenticationPredicate, MockIncomeSourceDetailsPredicate}
import mocks.services.{MockCalculationService, MockFinancialTransactionsService}
import models.incomeSourceDetails.IncomeSourceDetailsModel
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.test.Helpers._
import testUtils.TestSupport

class TaxYearsControllerSpec extends TestSupport with MockCalculationService with MockFinancialTransactionsService
  with MockAuthenticationPredicate with MockIncomeSourceDetailsPredicate {

  object TestTaxYearsController extends TaxYearsController()(
    app.injector.instanceOf[FrontendAppConfig],
    app.injector.instanceOf[MessagesApi],
    ec,
    app.injector.instanceOf[SessionTimeoutPredicate],
    MockAuthenticationPredicate,
    app.injector.instanceOf[NinoPredicate],
    MockIncomeSourceDetailsPredicate,
    mockCalculationService,
    app.injector.instanceOf[ItvcHeaderCarrierForPartialsConverter],
    app.injector.instanceOf[ItvcErrorHandler],
    app.injector.instanceOf[AuditingService],
    mockFinancialTransactionsService
  )

  lazy val messages = new Messages.Calculation(testYear)

  "The TestYearsController.viewTaxYears action" when {

    "called with an authenticated HMRC-MTD-IT user" which {

      "successfully retrieves Business only income from the Income Sources predicate" when {
        "the getAllFinancialTransactions brings back an instance of FinancialTransactionsErrorModel" should {

          lazy val result = TestTaxYearsController.viewTaxYears(fakeRequestWithActiveSession)
          lazy val document = result.toHtmlDocument
          lazy val messages = Messages.TaxYears

          "return an ISE (500)" in {
            setupMockGetIncomeSourceDetails(testMtdUserNino)(IncomeSourceDetailsModel(List(business1, business2018), None))
            mockGetAllLatestCalcSuccess()
            mockGetAllFinancialTransactions(List(
              2017 -> financialTransactionsModel("2018-5-30", Some(1000)),
              2018 -> financialTransactionsErrorModel
            ))
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }

        "the getAllFinancialTransactions brings back an instance of FinancialTransactionsModel" should {

          lazy val result = TestTaxYearsController.viewTaxYears(fakeRequestWithActiveSession)
          lazy val document = result.toHtmlDocument
          lazy val messages = Messages.TaxYears

          "return status OK (200)" in {
            setupMockGetIncomeSourceDetails(testMtdUserNino)(IncomeSourceDetailsModel(List(business1, business2018), None))
            mockGetAllLatestCalcSuccess()
            mockGetAllFinancialTransactions(List(
              2017 -> financialTransactionsModel("2018-5-30", Some(1000)),
              2018 -> financialTransactionsModel("2018-3-6", Some(0))
            ))
            status(result) shouldBe Status.OK
          }
          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }
          "render the Tax Years sub-page" in {
            document.title shouldBe messages.title
          }
        }
      }

      "successfully retrieves income sources, but the list returned from the service has a not found error model" should {
        lazy val result = TestTaxYearsController.viewTaxYears(fakeRequestWithActiveSession)

        "return status OK (200)" in {
          setupMockGetIncomeSourceDetails(testMtdUserNino)(businessIncome2018and2019)
          mockGetAllLatestCalcSuccessOneNotFound()
          mockGetAllFinancialTransactions(List(
            2017 -> financialTransactionsModel("2018-3-6", Some(1000)),
            2018 -> financialTransactionsModel("2019-3-6", Some(0))
          ))

          status(result) shouldBe Status.OK
        }
      }

      "successfully retrieves income sources, but the list returned from the service has an internal server error model" should {

        lazy val result = TestTaxYearsController.viewTaxYears(fakeRequestWithActiveSession)

        "return an ISE (500)" in {
          setupMockGetIncomeSourceDetails(testMtdUserNino)(businessIncome2018and2019)
          mockGetAllLatestCrystallisedCalcWithError()
          mockGetAllFinancialTransactions(List(
            2017 -> financialTransactionsModel("2018-3-6", Some(1000)),
            2018 -> financialTransactionsModel("2019-3-6", Some(0))
          ))

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }

    "Called with an Unauthenticated User" should {

      "return redirect SEE_OTHER (303)" in {
        setupMockAuthorisationException()
        val result = TestTaxYearsController.viewTaxYears(fakeRequestWithActiveSession)
        status(result) shouldBe Status.SEE_OTHER
      }
    }
  }
}