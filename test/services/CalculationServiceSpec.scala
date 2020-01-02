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

package services

import assets.BaseTestConstants._
import assets.CalcBreakdownTestConstants._
import assets.EstimatesTestConstants._
import mocks.connectors.MockIncomeTaxViewChangeConnector
import mocks.services.MockCalculationService
import models.calculation._
import play.api.http.Status
import testUtils.TestSupport

class CalculationServiceSpec extends TestSupport with MockIncomeTaxViewChangeConnector with MockCalculationService {

  object TestCalculationService extends CalculationService(
    mockIncomeTaxViewChangeConnector,
    frontendAppConfig
  )

  private trait CalculationDataApiEnabled {
    frontendAppConfig.features.calcDataApiEnabled(true)
  }

  private trait CalculationDataApiDisabled {
    frontendAppConfig.features.calcDataApiEnabled(false)
  }

  "The CalculationService.getCalculationData method" when {

    "successful responses are returned from the CalculationDataConnector" should {

      "return a correctly formatted CalculationData model" in {
        setUpLatestCalculationResponse(testNino, testYear)(testCalcModelSuccess.copy(calculationDataModel = Some(calculationDataSuccessModel)))

        await(TestCalculationService.getCalculationDetail(testNino, testYear)) shouldBe calculationDisplaySuccessModel(calculationDataSuccessModel)
      }
    }

    "an Error Response is returned from the CalculationConnector" should {

      "return none" in {
        setUpLatestCalculationResponse(testNino, testYear)(errorCalculationModel)
        await(TestCalculationService.getCalculationDetail(testNino, testYear)) shouldBe CalcDisplayError
      }
    }

    "no calculation data is returned from the CalculationDataConnector" should {

      "return a correctly formatted CalcDisplayModel model with calcDataModel = None" in {
        setUpLatestCalculationResponse(testNino, testYear)(testCalcModelSuccess)

        await(TestCalculationService.getCalculationDetail(testNino, testYear)) shouldBe calculationDisplayNoBreakdownModel
      }
    }
  }

  "The CalculationService.getAllLatestCalculations method" when {

    object TestCalculationService extends CalculationService(
      mockIncomeTaxViewChangeConnector,
      frontendAppConfig
    )

    "when the  Calculation Data api feature switch is disabled" should {

      "passed an ordered list of years" should {

        "for a list of Estimates" should {

          "return a list of CalculationResponseModelWithYear models" in new CalculationDataApiDisabled {
            setUpLatestCalculationResponse(testNino, testYear)(testCalcModelEstimate)
            setUpLatestCalculationResponse(testNino, testYearPlusOne)(testCalcModelEstimate)
            await(TestCalculationService.getAllLatestCalculations(testNino, List(testYear, testYearPlusOne))) shouldBe lastTaxCalcWithYearList
          }
        }

        "for a list of Bills" should {

          "return a list of CalculationResponseModelWithYear bills models" in new CalculationDataApiDisabled {
            setUpLatestCalculationResponse(testNino, testYear)(testCalcModelCrystallised)
            setUpLatestCalculationResponse(testNino, testYearPlusOne)(testCalcModelCrystallised)
            await(TestCalculationService.getAllLatestCalculations(testNino, List(testYear, testYearPlusOne))) shouldBe lastTaxCalcWithYearCrystallisedList
          }
        }
      }

      "passed an empty list of Ints" in new CalculationDataApiDisabled {
        await(TestCalculationService.getAllLatestCalculations(testNino, List())) shouldBe List()
      }
    }
  }

  "The CalculationService.getLatestCalculation method" when {

    "successful response is returned from the CalculationDataConnector" should {

      "return a CalculationModel" in {
        setUpLatestCalculationResponse(testNino, testYear)(testCalcModelCrystallised)
        await(TestCalculationService.getLatestCalculation(testNino, testYear)) shouldBe CalculationModel(
          testTaxCalculationId,
          Some(543.21),
          Some(testTimeStampString),
          Some(true),
          Some(123.45),
          Some(987.65),
          Some(CalculationDataModel(
            None, 0.0, 123.45,
            AnnualAllowances(0,0),
            0, 0, 0,
            IncomeReceivedModel(0, 0, 0, 0),
            SavingsAndGainsModel(0, 0, List()),
            DividendsModel(0, 0, List()),
            GiftAidModel(0, 0, 0),
            NicModel(0, 0),
            None
          ))
        )

      }

      "error response is returned from the CalculationDataConnector" should {
        "return a CalculationErrorModel" in {
          setUpLatestCalculationResponse(testNino, testYear)(errorCalculationModel)
          await(TestCalculationService.getLatestCalculation(testNino, testYear)) shouldBe CalculationErrorModel(
            Status.INTERNAL_SERVER_ERROR, "Internal server error")
        }
      }
    }
  }
}
