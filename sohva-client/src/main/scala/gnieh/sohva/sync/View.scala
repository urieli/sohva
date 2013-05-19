/*
* This file is part of the sohva project.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*couch.http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package gnieh.sohva
package sync

import gnieh.sohva.async.{
  View => AView
}

import scala.concurrent._
import duration._

/** A view can be queried to get the result.
 *
 *  @author Lucas Satabin
 */
case class View[Key: Manifest, Value: Manifest, Doc: Manifest](wrapped: AView[Key, Value, Doc]) extends gnieh.sohva.View[Key, Value, Doc] {

  type Result[T] = T

  def synced[T](result: wrapped.Result[T]): T = Await.result(result, Duration.Inf) match {
    case Right(t) => t
    case Left((409, error)) =>
      throw new ConflictException(error)
    case Left((code, error)) =>
      throw new CouchException(code, error)
  }

  @inline
  def query(key: Option[Key] = None,
            keys: List[Key] = Nil,
            startkey: Option[Key] = None,
            startkey_docid: Option[String] = None,
            endkey: Option[Key] = None,
            endkey_docid: Option[String] = None,
            limit: Int = -1,
            stale: Option[String] = None,
            descending: Boolean = false,
            skip: Int = 0,
            group: Boolean = false,
            group_level: Int = -1,
            reduce: Boolean = true,
            include_docs: Boolean = false,
            inclusive_end: Boolean = true,
            update_seq: Boolean = false): ViewResult[Key, Value, Doc] =

    synced(wrapped.query(key = key,
      keys = keys,
      startkey = startkey,
      startkey_docid = startkey_docid,
      endkey = endkey,
      endkey_docid = endkey_docid,
      limit = limit,
      stale = stale,
      descending = descending,
      skip = skip,
      group = group,
      group_level = group_level,
      reduce = reduce,
      include_docs = include_docs,
      inclusive_end = inclusive_end,
      update_seq = update_seq))

}