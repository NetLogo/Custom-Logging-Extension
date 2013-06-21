package org.nlogo.extensions.logging

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 6/21/13
 * Time: 4:44 PM
 */

protected trait Selector {
  def select(pairs: KVs, comparator: Filter) : KVs
}

protected trait GreedySelector extends Selector {
  override def select(pairs: KVs, comparator: Filter) : KVs = pairs filterNot comparator
}

protected trait LazySelector extends Selector {
  override def select(pairs: KVs, comparator: Filter) : KVs = pairs filter comparator
}
