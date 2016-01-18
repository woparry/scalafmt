package org

import scala.annotation.tailrec
import scala.meta.Tree
import scala.meta.tokens.Token

package object scalafmt {

  // TODO(olafur) Move these elsewhere.
  @tailrec final def childOf(child: Tree, tree: Tree): Boolean =
    child == tree || (child.parent match {
      case Some(parent) => childOf(parent, tree)
      case _ => false
    })

  def childOf(tok: Token, tree: Tree, owners: Map[Token, Tree]): Boolean =
    childOf(owners(tok), tree)

}