/*
 * Copyright 2019 HM Revenue & Customs
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

import javax.inject.Inject

import audit.AuditingService
import auth.MtdItUser
import config.{FrontendAppConfig, ItvcErrorHandler, ItvcHeaderCarrierForPartialsConverter}
import controllers.predicates.{AuthenticationPredicate, IncomeSourceDetailsPredicate, NinoPredicate, SessionTimeoutPredicate}
import models.financialTransactions.{FinancialTransactionsErrorModel, FinancialTransactionsModel, FinancialTransactionsResponseModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.FinancialTransactionsService
import services.CalculationService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class PaymentDueController @Inject()(val checkSessionTimeout: SessionTimeoutPredicate,
                                     val authenticate: AuthenticationPredicate,
                                     val retrieveNino: NinoPredicate,
                                     val retrieveIncomeSources: IncomeSourceDetailsPredicate,
                                     val financialTransactionsService: FinancialTransactionsService,
                                     val itvcHeaderCarrierForPartialsConverter: ItvcHeaderCarrierForPartialsConverter,
                                     val itvcErrorHandler: ItvcErrorHandler,
                                     val auditingService: AuditingService,
                                     implicit val config: FrontendAppConfig,
                                     implicit val messagesApi: MessagesApi
                                    ) extends FrontendController with I18nSupport {

  def hasFinancialTransactionsError(transactionModels: List[FinancialTransactionsResponseModel]): Boolean = {
    transactionModels.exists(_.isInstanceOf[FinancialTransactionsErrorModel])
  }


  val viewPaymentsDue: Action[AnyContent] = (checkSessionTimeout andThen authenticate andThen retrieveNino andThen retrieveIncomeSources).async {
    implicit user =>
      financialTransactionsService.getAllUnpaidFinancialTransactions.map {
        case transactions if hasFinancialTransactionsError(transactions) =>
          itvcErrorHandler.showInternalServerError()
        case transactions: List[FinancialTransactionsModel] => Ok(views.html.paymentDue(transactions))
      }

  }


}