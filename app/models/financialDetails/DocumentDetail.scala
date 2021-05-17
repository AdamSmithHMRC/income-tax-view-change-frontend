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

package models.financialDetails

import play.api.Logger
import play.api.libs.json.{Format, Json}

import java.time.LocalDate

case class DocumentDetail(taxYear: String,
                          transactionId: String,
                          documentDescription: Option[String],
                          outstandingAmount: Option[BigDecimal],
                          originalAmount: Option[BigDecimal]) {

  val isPaid: Boolean = outstandingAmount match {
    case Some(amount) if amount == 0 => true
    case _ => false
  }

  val isPartPaid: Boolean = outstandingAmount.getOrElse[BigDecimal](0) != originalAmount.getOrElse[BigDecimal](0)

  def remainingToPay: BigDecimal = {
    if (isPaid) BigDecimal(0)
    else outstandingAmount.getOrElse(originalAmount.get)
  }

  def getChargePaidStatus: String = {
    if (isPaid) "paid"
    else if (isPartPaid) "part-paid"
    else "unpaid"
  }

  def getChargeTypeKey: String = documentDescription match {
    case Some("ITSA- POA 1") => "paymentOnAccount1.text" //todo: fix the actual document descriptions
    case Some("ITSA - POA 2") => "paymentOnAccount2.text"
    case Some("ITSA- Bal Charge") => "balancingCharge.text"
    case error =>
      Logger.error(s"[DocumentDetail][getChargeTypeKey] Missing or non-matching charge type: $error found")
      "unknownCharge"
  }

}

case class DocumentDetailWithDueDate(documentDetail: DocumentDetail, dueDate: Option[LocalDate]) {
  val isOverdue: Boolean = dueDate.exists(_ isBefore LocalDate.now)
}

object DocumentDetail {
  implicit val format: Format[DocumentDetail] = Json.format[DocumentDetail]
}