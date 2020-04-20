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

package views

import assets.CalcBreakdownTestConstants
import assets.Messages.{DeductionBreakdown, IncomeBreakdown}
import enums.Estimate
import models.calculation.CalcDisplayModel
import org.jsoup.nodes.Element
import testUtils.ViewSpec
import views.html.deductionBreakdown

class DeductionBreakdownViewSpec extends ViewSpec {

  "The deduction breakdown view" when {

    "provided with a calculation without tax deductions for the 2017 tax year" should {
      val taxYear = 2017

      lazy val view = deductionBreakdown(CalcDisplayModel("", 1,
        CalcBreakdownTestConstants.calculationNoBillModel,
        Estimate), taxYear)

      "have the correct title" in new Setup(view) {
        document title() shouldBe DeductionBreakdown.title
      }

      "have the correct back link" in new Setup(view) {
        content hasBackLinkTo controllers.routes.CalculationController.renderCalculationPage(taxYear).url
      }

      "have the correct heading" in new Setup(view) {
        content hasPageHeading DeductionBreakdown.heading(taxYear)
        content.h1.select(".heading-secondary").text() shouldBe DeductionBreakdown.subHeading(taxYear)
      }

      "have the correct guidance" in new Setup(view) {
        val guidance: Element = content.select("p").get(1)
        guidance.text() shouldBe DeductionBreakdown.guidance(taxYear)
        guidance hasCorrectLink(DeductionBreakdown.guidanceLink,
          "https://www.gov.uk/guidance/find-software-thats-compatible-with-making-tax-digital-for-income-tax")
      }

      "have an deduction table" which {

        "has only one table row" in new Setup(view) {
          content hasTableWithCorrectSize 1
        }

        "has a total line with a zero value" in new Setup(view) {
          val row: Element = content.table.select("tr").first()
          row.select("td").first().text() shouldBe DeductionBreakdown.total
          row.select("td").last().text() shouldBe "£0"
        }
      }
    }

    "provided with a calculation with all tax deductions for the 2018 tax year" should {
      val taxYear = 2018

      lazy val view = deductionBreakdown(CalcDisplayModel("", 1,
        CalcBreakdownTestConstants.calculationAllDeductionSources,
        Estimate), taxYear)

      "have the correct title" in new Setup(view) {
        document title() shouldBe DeductionBreakdown.title
      }

      "have the correct back link" in new Setup(view) {
        content hasBackLinkTo controllers.routes.CalculationController.renderCalculationPage(taxYear).url
      }

      "have the correct heading" in new Setup(view) {
        content hasPageHeading DeductionBreakdown.heading(taxYear)
        content.h1.select(".heading-secondary").text() shouldBe DeductionBreakdown.subHeading(taxYear)
      }

      "have the correct guidance" in new Setup(view) {
        val guidance: Element = content.select("p").get(1)
        guidance.text() shouldBe DeductionBreakdown.guidance(taxYear)
        guidance hasCorrectLink(DeductionBreakdown.guidanceLink,
          "https://www.gov.uk/guidance/find-software-thats-compatible-with-making-tax-digital-for-income-tax")
      }

      "have an deduction table" which {

        "has all three table rows" in new Setup(view) {
          content hasTableWithCorrectSize 3
        }

        "has a personal allowance line with the correct value" in new Setup(view) {
          val row: Element = content.table.select("tr").get(0)
          row.select("td").first().text() shouldBe DeductionBreakdown.personalAllowance
          row.select("td").last().text() shouldBe "£11,500"
        }

        "has a gift of investments and property to charity line with the correct value" in new Setup(view) {
          val row: Element = content.table.select("tr").get(1)
          row.select("td").first().text() shouldBe DeductionBreakdown.giftOfInvestmentsAndPropertyToCharity
          row.select("td").last().text() shouldBe "£10,000"
        }


        "has a total deductions line with the correct value" in new Setup(view) {
          val row: Element = content.table.select("tr").get(2)
          row.select("td").first().text() shouldBe DeductionBreakdown.total
          row.select("td").last().text() shouldBe "£21,500"
        }
      }
    }
  }
}
