/*
 * Copyright 2017 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import connectors.{BusinessDetailsConnector, PropertyDetailsConnector}
import models._
import play.api.Logger
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class IncomeSourceDetailsService @Inject()( val businessDetailsConnector: BusinessDetailsConnector,
                                            val propertyDetailsConnector: PropertyDetailsConnector) {

  def getIncomeSourceDetails(nino: String)(implicit hc: HeaderCarrier): Future[IncomeSourcesResponseModel] = {
    for {
      businessDetails <- businessDetailsConnector.getBusinessList(nino)
      propertyDetails <- propertyDetailsConnector.getPropertyDetails(nino)
    } yield (businessDetails, propertyDetails) match {
      case (_: BusinessDetailsErrorModel, _) => IncomeSourcesError
      case (_, _:PropertyDetailsModel) => IncomeSourcesError
      case (x, y) => createIncomeSourcesModel(x, y)
    }
  }

  private def createIncomeSourcesModel(business: BusinessListResponseModel, property: PropertyDetailsResponseModel) = {
    val businessModel = business match {
      case x: BusinessIncomeModel => Some(x)
      case _ => None
    }
    val propertyModel = property match {
      case x: PropertyIncomeModel => Some(x)
      case _ => None
    }
    IncomeSourcesModel(businessModel, propertyModel)
  }
}
