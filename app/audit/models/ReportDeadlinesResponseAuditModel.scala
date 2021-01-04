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

package audit.models

import models.reportDeadlines.ReportDeadlineModel
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class ReportDeadlinesResponseAuditModel(mtditid: String,
                                             nino: String,
                                             incomeSourceId: String,
                                             reportDeadlines: List[ReportDeadlineModel]) extends ExtendedAuditModel {

  override val transactionName: String = "report-deadlines-response"
  override val auditType: String = "reportDeadlinesResponse"

  private case class AuditDetail(mtditid: String,
                                 nino: String,
                                 incomeSourceId: String,
                                 reportDeadlines: List[ReportDeadlineModel])

  private implicit val auditDetailWrites: Writes[AuditDetail] = (
    (__ \ "mtditid").write[String] and
      (__ \ "nino").write[String] and
      (__ \ "incomeSourceId").write[String] and
      (__ \ "reportDeadlines").write[List[ReportDeadlineModel]](Writes.list[ReportDeadlineModel](ReportDeadlineModel.auditWrites))
    )(unlift(AuditDetail.unapply))

  override val detail: JsValue = Json.toJson(
    AuditDetail(
      mtditid,
      nino,
      incomeSourceId,
      reportDeadlines
    )
  )
}