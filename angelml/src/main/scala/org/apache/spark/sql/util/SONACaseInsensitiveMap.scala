/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.util

/**
  * Builds a map in which keys are case insensitive. Input map can be accessed for cases where
  * case-sensitive information is required. The primary constructor is marked private to avoid
  * nested case-insensitive map creation, otherwise the keys in the original map will become
  * case-insensitive in this scenario.
  */
class SONACaseInsensitiveMap[T] private(val originalMap: Map[String, T]) extends Map[String, T]
  with Serializable {

  val keyLowerCasedMap = originalMap.map(kv => kv.copy(_1 = kv._1.toLowerCase))

  override def get(k: String): Option[T] = keyLowerCasedMap.get(k.toLowerCase)

  override def contains(k: String): Boolean = keyLowerCasedMap.contains(k.toLowerCase)

  override def +[B1 >: T](kv: (String, B1)): Map[String, B1] = {
    new SONACaseInsensitiveMap(originalMap + kv)
  }

  override def iterator: Iterator[(String, T)] = keyLowerCasedMap.iterator

  override def -(key: String): Map[String, T] = {
    new SONACaseInsensitiveMap(originalMap.filterKeys(!_.equalsIgnoreCase(key)))
  }
}

object SONACaseInsensitiveMap {
  def apply[T](params: Map[String, T]): SONACaseInsensitiveMap[T] = params match {
    case caseSensitiveMap: SONACaseInsensitiveMap[T] => caseSensitiveMap
    case _ => new SONACaseInsensitiveMap(params)
  }
}