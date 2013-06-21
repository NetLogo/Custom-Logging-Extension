package org.nlogo.extensions

package object logging {
  type KV     = (String, String)
  type KVs    = Seq[KV]
  type Filter = KV => Boolean
}
